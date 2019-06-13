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
 * AbstractCommunicationProcessor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.wekapyroproxy;

import adams.core.option.AbstractOptionHandler;
import weka.classifiers.functions.PyroProxy;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Ancestor for classes processing the communication to/fro Pyro proxy models.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractCommunicationProcessor
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 3766046746032503304L;

  /**
   * Hook method for performing checks before initializing.
   *
   * @param data	the Instances to check
   * @return		null if successful, otherwise the error message
   */
  protected String checkInitialize(Instances data) {
    if (data == null)
      return "No Instances provided!";
    return null;
  }

  /**
   * Performs the initialization.
   *
   * @param owner 	the owning classifier
   * @param data	the training data
   * @throws Exception	if initialization fails
   */
  protected abstract void doInitialize(PyroProxy owner, Instances data) throws Exception;

  /**
   * Converts the instance into a different format.
   *
   * @param owner 	the owning classifier
   * @param data	the training data
   * @throws Exception	if check or initialization fails
   */
  public void initialize(PyroProxy owner, Instances data) throws Exception {
    String	msg;

    msg = checkInitialize(data);
    if (msg != null)
      throw new Exception(msg);
    doInitialize(owner, data);
  }

  /**
   * Hook method for performing checks before converting a dataset.
   *
   * @param data	the dataset to check
   * @return		null if successful, otherwise the error message
   */
  protected String checkDataset(Instances data) {
    if (data == null)
      return "No Instances provided!";
    return null;
  }

  /**
   * Performs the dataset conversion.
   *
   * @param owner 	the owning classifier
   * @param data	the dataset to convert
   * @return		the converted dataset
   * @throws Exception	if build fails
   */
  protected abstract Object doConvertDataset(PyroProxy owner, Instances data) throws Exception;

  /**
   * Performs the dataset conversion.
   *
   * @param owner 	the owning classifier
   * @param data	the dataset to convert
   * @return		the converted dataset
   * @throws Exception	if check or build fails
   */
  public Object convertDataset(PyroProxy owner, Instances data) throws Exception {
    String	msg;

    msg = checkDataset(data);
    if (msg != null)
      throw new Exception(msg);
    return doConvertDataset(owner, data);
  }

  /**
   * Hook method for performing checks before converting the Instance.
   *
   * @param inst	the Instance to check
   * @return		null if successful, otherwise the error message
   */
  protected String checkInstance(Instance inst) {
    if (inst == null)
      return "No Instance provided!";
    return null;
  }

  /**
   * Converts the instance into a different format.
   *
   * @param owner 	the owning classifier
   * @param inst	the instance to convert
   * @return		the generated data structure
   * @throws Exception	if conversion fails
   */
  protected abstract Object doConvertInstance(PyroProxy owner, Instance inst) throws Exception;

  /**
   * Converts the instance into a different format.
   *
   * @param owner 	the owning classifier
   * @param inst	the instance to convert
   * @return		the generated data structure
   * @throws Exception	if check or conversion fails
   */
  public Object convertInstance(PyroProxy owner, Instance inst) throws Exception {
    String	msg;

    msg = checkInstance(inst);
    if (msg != null)
      throw new Exception(msg);
    return doConvertInstance(owner, inst);
  }

  /**
   * Hook method for performing checks before parsing the prediction.
   *
   * @param prediction	the prediction to check
   * @return		null if successful, otherwise the error message
   */
  protected String checkPrediction(Object prediction) {
    if (prediction == null)
      return "No prediction provided!";
    return null;
  }

  /**
   * Parses the prediction.
   *
   * @param owner 	the owning classifier
   * @param prediction	the prediction to parse
   * @return		the class distribution
   * @throws Exception	if conversion fails
   */
  protected abstract double[] doParsePrediction(PyroProxy owner, Object prediction) throws Exception;

  /**
   * Parses the prediction.
   *
   * @param owner 	the owning classifier
   * @param prediction	the prediction to parse
   * @return		the class distribution
   * @throws Exception	if check or conversion fails
   */
  public double[] parsePrediction(PyroProxy owner, Object prediction) throws Exception {
    String	msg;

    msg = checkPrediction(prediction);
    if (msg != null)
      throw new Exception(msg);
    return doParsePrediction(owner, prediction);
  }

  /**
   * Returns whether batch predictions are supported.
   *
   * @return		true if supported
   */
  public abstract boolean supportsBatchPredictions();

  /**
   * Hook method for performing checks before parsing the predictions.
   *
   * @param predictions	the prediction to check
   * @return		null if successful, otherwise the error message
   */
  protected String checkPredictions(Object predictions) {
    if (predictions == null)
      return "No prediction provided!";
    return null;
  }

  /**
   * Parses the predictions.
   *
   * @param owner 	the owning classifier
   * @param predictions	the predictions to parse
   * @return		the class distribution
   * @throws Exception	if conversion fails
   */
  protected abstract double[][] doParsePredictions(PyroProxy owner, Object predictions) throws Exception;

  /**
   * Parses the predictions.
   *
   * @param owner 	the owning classifier
   * @param predictions	the predictions to parse
   * @return		the class distribution
   * @throws Exception	if check or conversion fails
   */
  public double[][] parsePredictions(PyroProxy owner, Object predictions) throws Exception {
    String	msg;

    msg = checkPrediction(predictions);
    if (msg != null)
      throw new Exception(msg);
    return doParsePredictions(owner, predictions);
  }
}
