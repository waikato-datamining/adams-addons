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
 *    ConvolutionLayer.java
 *    Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */
package weka.dl4j.layers;

import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.impl.ActivationIdentity;
import org.nd4j.linalg.learning.config.Nesterovs;
import weka.dl4j.distribution.NormalDistribution;

import java.io.Serializable;

/**
 * A version of DeepLearning4j's ConvolutionLayer that implements WEKA option handling.
 *
 * @author Christopher Beckham
 * @author Eibe Frank
 *
 * @version $Revision: 11711 $
 */
public class ConvolutionLayer extends org.deeplearning4j.nn.conf.layers.ConvolutionLayer implements Serializable {

  /** The ID used to serialize this class. */
  private static final long serialVersionUID = 6905344091980568487L;

  /**
   * Global info.
   *
   * @return string describing this class.
   */
  public String globalInfo() {
    return "A convolution layer from DeepLearning4J.";
  }

  /**
   * Constructor for setting some defaults.
   */
  public ConvolutionLayer() {
    setLayerName("Convolution layer");
    setActivationFn(new ActivationIdentity());
    setWeightInit(WeightInit.XAVIER);
    setDist(new NormalDistribution());
    setIUpdater(new Nesterovs());
    setLearningRate(0.01);
    setBiasLearningRate(getLearningRate());
    setMomentum(0.9);
    setBiasInit(1.0);
    setAdamMeanDecay(0.9);
    setAdamVarDecay(0.999);
    setEpsilon(1e-6);
    setRmsDecay(0.95);
    setConvolutionMode(ConvolutionMode.Truncate);
    setKernelSize(new int[] {5, 5});
    setStride(new int[] {1, 1});
    setPadding(new int[] {0, 0});
    this.cudnnAlgoMode = ConvolutionLayer.AlgoMode.PREFER_FASTEST;
  }
}