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
import weka.classifiers.functions.PyroProxy;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Dummy, does nothing.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NullCommunicationProcessor
  extends AbstractCommunicationProcessor {

  private static final long serialVersionUID = -704870273976903924L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, does nothing.";
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
    throw new NotImplementedException();
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
    throw new NotImplementedException();
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
    throw new NotImplementedException();
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
    throw new NotImplementedException();
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
   * @param predictions	the prediction to parse
   * @return		the class distribution
   * @throws Exception	if conversion fails
   */
  @Override
  protected double[][] doParsePredictions(PyroProxy owner, Object predictions) throws Exception {
    throw new NotImplementedException();
  }
}
