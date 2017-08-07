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

/**
 * DL4JMultiLayerNetworkProvider.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import weka.core.Instances;

/**
 * Interface for classes that allow access to a MultiLayerNetwork isntance.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface DL4JMultiLayerNetworkProvider {

  /**
   * Returns the network.
   *
   * @return		the network, null if none available
   */
  public MultiLayerNetwork getMultiLayerNetwork();

  /**
   * Sets the trained network.
   *
   * @param model	the trained network to use
   * @see		#setTrainingData(Instances)
   */
  public void setTrainedMultiLayerNetwork(MultiLayerNetwork model);

  /**
   * Sets the data used to train the network.
   *
   * @param data	the training data
   * @see		#setTrainedMultiLayerNetwork(MultiLayerNetwork)
   */
  public void setTrainingData(Instances data);
}
