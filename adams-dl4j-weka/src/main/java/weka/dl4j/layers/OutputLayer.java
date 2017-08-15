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
 *    OutputLayer.java
 *    Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */
package weka.dl4j.layers;

import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.impl.ActivationIdentity;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.impl.LossMSE;
import weka.dl4j.distribution.NormalDistribution;

import java.io.Serializable;

/**
 * A version of DeepLearning4j's OutputLayer that implements WEKA option handling.
 *
 * @author Christopher Beckham
 * @author Eibe Frank
 *
 * @version $Revision: 11711 $
 */
public class OutputLayer extends org.deeplearning4j.nn.conf.layers.OutputLayer implements Serializable {

  private static final long serialVersionUID = 139321786136127207L;

  /**
   * Global info.
   *
   * @return string describing this class.
   */
  public String globalInfo() {
    return "An output layer from DeepLearning4J.";
  }

  /**
   * Constructor for setting some defaults.
   */
  public OutputLayer() {
    setLayerName("Output layer");
    setActivationFn(new ActivationIdentity());
    setWeightInit(WeightInit.XAVIER);
    setDist(new NormalDistribution());
    setIUpdater(new Nesterovs());
    setLossFn(new LossMSE());
    setLearningRate(0.01);
    setBiasLearningRate(getLearningRate());
    setBiasInit(1.0);
  }
}