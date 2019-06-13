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
 * SimpleJsonCommunicationProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.wekapyroproxy;

import adams.core.exception.NotImplementedException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import weka.classifiers.functions.PyroProxy;
import weka.core.Instance;
import weka.core.Instances;

import java.io.StringReader;

/**
 * Turns Instances/Instance into simple JSON.
 * <br>
 * Train:
 * <pre>
 * {
 *   Model: <name>
 *   Train: {
 *     X:
 *       [...],  // single instance
 *       ...
 *     ],
 *     Y: [...]  // class values
 *   }
 * }
 * <pre>
 *
 * Predict (send):
 * <pre>
 * {
 *   Model: <name>
 *   Train: {
 *     X: [...],  // single instance
 *   }
 * }
 * <pre>
 *
 * Predict (receive):
 * <pre>
 * {
 *   Error: <only present if failed to make prediction>
 *   Prediction: [...]  // distribution array (single double for numeric class)
 * }
 * <pre>
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleJsonCommunicationProcessor
  extends AbstractCommunicationProcessor {

  private static final long serialVersionUID = -704870273976903924L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns Instances/Instance into simple JSON.";
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

    for (i = 0; i < data.numAttributes(); i++) {
      if (!data.attribute(i).isNumeric())
        throw new IllegalStateException("Attribute #" + (i+1) + " (" + data.attribute(i).name() + ") is not numeric!");
    }
  }

  /**
   * Converts a single Instance into a JSON array (excluding class).
   *
   * @param inst	the instance to convert
   * @return		the JSON array
   */
  protected JSONArray instanceToRow(Instance inst) {
    JSONArray 	result;
    int 	i;

    result = new JSONArray();
    for (i = 0; i < inst.numAttributes(); i++) {
      if (i == inst.classIndex())
        continue;
      result.add(inst.value(i));
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
  @Override
  protected Object doConvertDataset(PyroProxy owner, Instances data) throws Exception {
    JSONObject 		result;
    JSONObject		train;
    JSONArray 		x;
    JSONArray		y;
    int			i;

    result = new JSONObject();
    result.put("Model", owner.getModelName());
    train = new JSONObject();
    result.put("Train", train);
    x = new JSONArray();
    train.put("X", x);
    y = new JSONArray();
    train.put("y", y);
    for (i = 0; i < data.numInstances(); i++) {
      x.add(instanceToRow(data.instance(i)));
      y.add(data.instance(i).classValue());
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

    result = new JSONObject();
    result.put("Model", owner.getModelName());
    result.put("x", instanceToRow(inst));

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
