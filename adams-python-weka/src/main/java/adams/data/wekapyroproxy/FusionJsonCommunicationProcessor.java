/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * FusionJsonCommunicationProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.wekapyroproxy;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import com.github.fracpete.javautils.enumerate.Enumerated;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import weka.classifiers.functions.PyroProxy;
import weka.core.Instance;
import weka.core.Instances;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Turns Instances/Instance into fusion JSON.
 * <br>
 * Train:
 * <pre>
 * {
 *   "names": [
 *     <modelname>  // the model name
 *   ]
 *   "inputs": {
 *     "input1": [
 *         [...],  // single instance or class value
 *         ...
 *       ]
 *       ...,
 *     "input2": [
 *         [...],  // single instance or class value
 *         ...
 *       ]
 *       ...,
 *     ]
 *   }
 * }
 * <pre>
 *
 * Predict (send):
 * <pre>
 * {
 *   "names": [
 *     <modelname>  // the model name
 *   ]
 *   "inputs": {
 *     "input1": [
 *         [...],  // single instance
 *         ...
 *       ]
 *       ...,
 *     "input2": [
 *         [...],  // single instance
 *         ...
 *       ]
 *       ...,
 *     ]
 *   }
 * }
 * <pre>
 *
 * Predict (receive):
 * <pre>
 * {
 *   "error": <only present if failed to make prediction>
 *   "outputs": {
 *     "<modelname>": {
 *       "output1": [
 *         [...],  // single prediction
 *         ...
 *       ],
 *     }
 *   }
 * }
 * <pre>
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FusionJsonCommunicationProcessor
  extends AbstractCommunicationProcessor {

  private static final long serialVersionUID = -704870273976903924L;

  /** the regular expressions to identify fusion subsets. */
  protected BaseRegExp[] m_RegExps;

  /** the names for the fusion subsets. */
  protected BaseString[] m_Names;

  /** the mapping between fusion subset names and attribute indices. */
  protected Map<BaseString,TIntList> m_Mapping;

  /** the class attribute name. */
  protected String m_ClassAttName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns Instances/Instance into fusion JSON.\n"
      + "Uses regular expressions to identify the fusion subsets (incl the class attribute).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExps",
      new BaseRegExp[0]);

    m_OptionManager.add(
      "name", "names",
      new BaseString[0]);
  }

  /**
   * Sets the regular expression to apply to the attribute names for
   * identifying the fusion subsets (incl class).
   *
   * @param value 	the expressions
   */
  public void setRegExps(BaseRegExp[] value) {
    m_RegExps = value;
    m_Names   = (BaseString[]) Utils.adjustArray(m_Names, m_RegExps.length, new BaseString());
    reset();
  }

  /**
   * Returns the regular expression to apply to the attribute names for
   * identifying the fusion subsets (incl class).
   *
   * @return 		the expressions
   */
  public BaseRegExp[] getRegExps() {
    return m_RegExps;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpsTipText() {
    return "The regular expressions to use for identifying the fusion subsets (incl the class attribute).";
  }

  /**
   * Sets the names to use for the fusion subsets (corresponds to the subsets).
   *
   * @param value 	the names
   */
  public void setNames(BaseString[] value) {
    m_Names   = value;
    m_RegExps = (BaseRegExp[]) Utils.adjustArray(m_RegExps, m_Names.length, new BaseRegExp());
    reset();
  }

  /**
   * Returns the names to use for the fusion subsets (corresponds to the subsets).
   *
   * @return 		the names
   */
  public BaseString[] getNames() {
    return m_Names;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String namesTipText() {
    return "The names to use for the fusion subsets (corresponds to the subsets).";
  }

  /**
   * Performs the initialization.
   *
   * @param owner 	the owning classifier
   * @param data	the training data
   * @throws Exception	if initialization fails
   */
  @Override
  protected void doInitialize(PyroProxy owner, Instances data) throws Exception {
    int		i;

    if (m_RegExps.length != m_Names.length)
      throw new IllegalStateException("# of regexps and names differ: " + m_RegExps.length + " != " + m_Names.length);
    if (m_RegExps.length == 0)
      throw new IllegalStateException("No regular expressions defined!");

    if (data.classIndex() == -1)
      throw new IllegalStateException("No class attribute set!");
    m_ClassAttName = data.classAttribute().name();

    m_Mapping = new HashMap<>();
    for (BaseString name: m_Names)
      m_Mapping.put(name, new TIntArrayList());

    for (i = 0; i < data.numAttributes(); i++) {
      for (Enumerated<BaseRegExp> regExp: enumerate(m_RegExps)) {
        if (regExp.value.isMatch(data.attribute(i).name())) {
          m_Mapping.get(m_Names[regExp.index]).add(i);
	  if (!data.attribute(i).isNumeric())
	    throw new IllegalStateException("Attribute #" + (i+1) + " (" + data.attribute(i).name() + ") is not numeric!");
	}
      }
    }

    // check for empty mappings
    for (Enumerated<BaseString> name: enumerate(m_Names)) {
      if (m_Mapping.get(name.value).isEmpty())
        throw new IllegalStateException("Regular expression '" + m_RegExps[name.index] + "' (" + name.value + ") did not match any attributes!");
    }
  }

  /**
   * Converts a single Instance into a JSON array.
   *
   * @param inst	the instance to convert
   * @param name	the name of the mapping
   * @return		the JSON array
   */
  protected JSONArray instanceToRow(Instance inst, BaseString name) {
    JSONArray 	result;

    result = new JSONArray();
    for (int index: m_Mapping.get(name).toArray())
      result.add(inst.value(index));

    return result;
  }

  /**
   * Performs the initialization.
   *
   * @param owner 	the owning classifier
   * @param data	the training data
   * @throws Exception	if initialization fails
   */
  @Override
  protected Object doConvertDataset(PyroProxy owner, Instances data) throws Exception {
    JSONObject 		result;
    JSONObject 		inputs;
    JSONArray 		input;
    JSONArray		names;
    int			i;

    result = new JSONObject();
    names  = new JSONArray();
    names.add(owner.getModelName());
    result.put("names", names);
    result.put("names-are-tags", false);
    inputs = new JSONObject();
    result.put("inputs", inputs);
    for (BaseString name: m_Names) {
      input = new JSONArray();
      inputs.put(name.getValue(), input);
      for (i = 0; i < data.numInstances(); i++)
        input.add(instanceToRow(data.instance(i), name));
    }

    return result.toJSONString();
  }

  /**
   * Converts the instance into a different format.
   *
   * @param owner 	the owning classifier
   * @param inst	the instance to convert
   * @return		the generated data structure
   * @throws Exception	if conversion fails
   */
  @Override
  protected Object doConvertInstance(PyroProxy owner, Instance inst) throws Exception {
    JSONObject 		result;
    JSONObject		inputs;
    JSONArray		input;
    JSONArray		names;

    result = new JSONObject();
    names  = new JSONArray();
    names.add(owner.getModelName());
    result.put("names", names);
    result.put("names-are-tags", false);
    inputs = new JSONObject();
    result.put("inputs", inputs);
    for (BaseString name: m_Names) {
      input = new JSONArray();
      inputs.put(name.getValue(), input);
      input.add(instanceToRow(inst, name));
    }

    return result.toJSONString();
  }

  /**
   * Parses the prediction.
   *
   * @param owner 	the owning classifier
   * @param prediction	the prediction to parse
   * @return		the class distribution
   * @throws Exception	if conversion fails
   */
  @Override
  protected double[] doParsePrediction(PyroProxy owner, Object prediction) throws Exception {
    double[]		result;
    StringReader 	sreader;
    JSONParser 		parser;
    JSONObject 		parsed;
    JSONObject		outputs;
    JSONObject		model;
    JSONArray 		output;
    JSONArray 		outputElem;

    sreader = new StringReader((String) prediction);
    parser  = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    parsed  = (JSONObject) parser.parse(sreader);
    if (parsed.containsKey("error"))
      throw new Exception(parsed.getAsString("error"));
    outputs = (JSONObject) parsed.get("outputs");
    if (outputs == null)
      throw new IllegalStateException("Failed to retrieve key: outputs");
    model = (JSONObject) outputs.get(owner.getModelName());
    if (model == null)
      throw new IllegalStateException("Failed to retrieve model: " + owner.getModelName());
    output = (JSONArray) model.get(m_ClassAttName);
    if (output == null)
      throw new IllegalStateException("Failed to retrieve predictions for class attribute: " + m_ClassAttName);
    if (output.size() == 0)
      throw new IllegalStateException("Outer predictions array is empty!");
    outputElem = (JSONArray) output.get(0);
    if (outputElem.size() == 0)
      throw new IllegalStateException("Inner predictions array is empty!");
    result  = new double[]{((Number) outputElem.get(0)).doubleValue()};

    return result;
  }

  /**
   * Returns whether batch predictions are supported.
   *
   * @return		true if supported
   */
  public boolean supportsBatchPredictions() {
    return true;
  }

  /**
   * Parses the predictions.
   *
   * @param owner 	the owning classifier
   * @param predictions	the predictions to parse
   * @return		the class distribution
   * @throws Exception	if conversion fails
   */
  @Override
  protected double[][] doParsePredictions(PyroProxy owner, Object predictions) throws Exception {
    double[][]		result;
    StringReader 	sreader;
    JSONParser 		parser;
    JSONObject 		parsed;
    JSONObject		outputs;
    JSONObject		model;
    JSONArray 		output;
    JSONArray 		outputElem;
    int			i;

    sreader = new StringReader((String) predictions);
    parser  = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    parsed  = (JSONObject) parser.parse(sreader);
    if (parsed.containsKey("error"))
      throw new Exception(parsed.getAsString("error"));
    outputs = (JSONObject) parsed.get("outputs");
    if (outputs == null)
      throw new IllegalStateException("Failed to retrieve key: outputs");
    model = (JSONObject) outputs.get(owner.getModelName());
    if (model == null)
      throw new IllegalStateException("Failed to retrieve model: " + owner.getModelName());
    output = (JSONArray) model.get(m_ClassAttName);
    if (output == null)
      throw new IllegalStateException("Failed to retrieve predictions for class attribute: " + m_ClassAttName);
    if (output.size() == 0)
      throw new IllegalStateException("Outer predictions array is empty!");
    result = new double[output.size()][];
    for (i = 0; i < output.size(); i++) {
      outputElem = (JSONArray) output.get(i);
      if (outputElem.size() == 0)
	throw new IllegalStateException("Inner predictions array #" + (i+1) + " is empty!");
      result[i] = new double[]{((Number) outputElem.get(0)).doubleValue()};
    }

    return result;
  }
}
