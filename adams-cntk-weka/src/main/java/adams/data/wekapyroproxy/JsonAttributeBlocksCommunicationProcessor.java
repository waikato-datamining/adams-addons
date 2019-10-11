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
 * JsonAttributeBlocksCommunicationProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.wekapyroproxy;

import adams.core.Utils;
import adams.core.exception.NotImplementedException;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import weka.classifiers.functions.PyroProxy;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts the Instance into JSON, generating blocks using the inputs defined
 * by the JSON file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JsonAttributeBlocksCommunicationProcessor
  extends AbstractCommunicationProcessor {

  private static final long serialVersionUID = -3873591854625889387L;

  /** the JSON file with the attribute ranges. */
  protected PlaceholderFile m_DataDescriptionFile;

  /** the data description. */
  protected transient Map<String,TIntList> m_DataDescription;

  /** the filter. */
  protected transient Filter m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts the Instance into JSON, generating blocks using the inputs defined by the JSON file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "data-description-file", "dataDescriptionFile",
      new PlaceholderFile());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_DataDescription = null;
    m_Filter          = null;
  }

  /**
   * Sets the data description JSON file.
   *
   * @param value 	the file
   */
  public void setDataDescriptionFile(PlaceholderFile value) {
    m_DataDescriptionFile = value;
    reset();
  }

  /**
   * Returns the data description JSON file.
   *
   * @return 		the file
   */
  public PlaceholderFile getDataDescriptionFile() {
    return m_DataDescriptionFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataDescriptionFileTipText() {
    return "The JSON file containing the dataset desription.";
  }

  /**
   * Loads the data description on demand.
   *
   * @return		null if successful, otherwise error message
   */
  protected String loadDataDescription() {
    String		result;
    JSONParser		parser;
    FileReader 		freader;
    BufferedReader 	breader;
    JSONObject		obj;
    JSONObject		ranges;
    String		attsStr;
    TIntArrayList	atts;

    result = null;
    freader = null;
    breader = null;
    try {
      freader = new FileReader(m_DataDescriptionFile.getAbsolutePath());
      breader = new BufferedReader(freader);
      parser  = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
      obj     = (JSONObject) parser.parse(breader);

      // ranges
      ranges  = (JSONObject) obj.get("AttributeRanges");
      m_DataDescription = new HashMap<>();
      for (String key: ranges.keySet()) {
	attsStr = ranges.getAsString(key);
	atts    = new TIntArrayList();
	for (String att: attsStr.split(","))
	  atts.add(Integer.parseInt(att) - 1);
	m_DataDescription.put(key, atts);
      }

      // filter
      m_Filter = (Filter) OptionUtils.forAnyCommandLine(Filter.class, obj.getAsString("Filter"));
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to read JSON file: " + m_DataDescriptionFile, e);
    }
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(freader);
    }

    return result;
  }

  /**
   * Hook method for performing checks before initializing.
   *
   * @param data	the Instances to check
   * @return		null if successful, otherwise the error message
   */
  @Override
  protected String checkInitialize(Instances data) {
    String	result;

    result = super.checkInitialize(data);

    if (result == null) {
      if (!m_DataDescriptionFile.exists())
        result = "Data description JSON file does not exist: " + m_DataDescriptionFile;
      else if (m_DataDescriptionFile.isDirectory())
        result = "Data description JSON file points to a directory: " + m_DataDescriptionFile;
      else
        result = loadDataDescription();
    }

    return result;
  }

  /**
   * Performs the initialization.
   *
   * @param owner 	the owning classifier
   * @param data	the training data
   * @throws Exception	if initialization fails
   */
  protected void doInitialize(PyroProxy owner, Instances data) throws Exception {
    m_Filter.setInputFormat(data);
    Filter.useFilter(data, m_Filter);
  }

  /**
   * Hook method for performing checks before building.
   *
   * @param data	the Instances to check
   * @return		null if successful, otherwise the error message
   */
  protected String checkDataset(Instances data) {
    String	result;

    result = super.checkDataset(data);

    if (result == null) {
      if (!m_DataDescriptionFile.exists())
        result = "Data description JSON file does not exist: " + m_DataDescriptionFile;
      else if (m_DataDescriptionFile.isDirectory())
        result = "Data description JSON file points to a directory: " + m_DataDescriptionFile;
      else
        result = loadDataDescription();
    }

    return result;
  }

  /**
   * Performs the dataset conversion.
   *
   * @param owner 	the owning classifier
   * @param data	the dataset to convert
   * @return		the converted dataset
   * @throws Exception	if build fails
   */
  protected Object doConvertDataset(PyroProxy owner, Instances data) throws Exception {
    throw new NotImplementedException();
  }

  /**
   * Hook method for performing checks before converting the Instance.
   *
   * @param inst	the Instance to check
   * @return		null if successful, otherwise the error message
   */
  @Override
  protected String checkInstance(Instance inst) {
    String	result;

    result = super.checkInstance(inst);

    if (result == null) {
      if (!m_DataDescriptionFile.exists())
        result = "Data description JSON file does not exist: " + m_DataDescriptionFile;
      else if (m_DataDescriptionFile.isDirectory())
        result = "Data description JSON file points to a directory: " + m_DataDescriptionFile;
      else
        result = loadDataDescription();
    }

    return result;
  }

  /**
   * Converts the instance into a string.
   *
   * @param owner 	the owning classifier
   * @param inst	the instance to convert
   * @return		the generated data structure
   */
  @Override
  protected Object doConvertInstance(PyroProxy owner, Instance inst) throws Exception {
    String		result;
    JSONObject		data;
    JSONObject		blocks;
    TIntList		atts;
    StringBuilder	values;

    // filter data
    if (!m_Filter.isFirstBatchDone()) {
      m_Filter.setInputFormat(inst.dataset());
      Filter.useFilter(inst.dataset(), m_Filter);
    }
    m_Filter.input(inst);
    m_Filter.batchFinished();
    inst = m_Filter.output();

    data = new JSONObject();
    data.put("Model", owner.getModelName());
    blocks = new JSONObject();
    data.put("Blocks", blocks);
    for (String block: m_DataDescription.keySet()) {
      atts   = m_DataDescription.get(block);
      values = new StringBuilder();
      for (int index: atts.toArray()) {
        if (values.length() > 0)
          values.append(",");
        values.append("" + inst.value(index));
      }
      blocks.put(block, values.toString());
    }
    result = data.toJSONString();

    return result;
  }

  /**
   * Hook method for performing checks before parsing the prediction.
   *
   * @param prediction	the prediction to check
   * @return		null if successful, otherwise the error message
   */
  @Override
  protected String checkPrediction(Object prediction) {
    String	result;

    result = super.checkPrediction(prediction);

    if (result == null) {
      if (!(prediction instanceof String))
        result = "Expected prediction to be a string, instead received: " + Utils.classToString(prediction);
    }

    return result;
  }

  /**
   * Parses the prediction.
   *
   * @param owner 	the owning classifier
   * @param prediction	the prediction to parse
   * @return		the generated data structure
   * @throws Exception	if conversion fails
   */
  protected double[] doParsePrediction(PyroProxy owner, Object prediction) throws Exception {
    double[]		result;
    StringReader	sreader;
    JSONParser		parser;
    JSONObject		pred;
    JSONArray 		array;
    int			i;

    sreader = new StringReader((String) prediction);
    parser  = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    pred    = (JSONObject) parser.parse(sreader);
    if (pred.containsKey("Error"))
      throw new Exception(pred.getAsString("Error"));
    array   = (JSONArray) pred.get("Prediction");
    result  = new double[array.size()];
    for (i = 0; i < array.size(); i++)
      result[i] = ((Number) array.get(i)).doubleValue();

    return result;
  }

  /**
   * Returns whether batch predictions are supported.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsBatchPredictions() {
    return false;
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
    throw new NotImplementedException();
  }
}
