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
 * Dl4jMlpClassifier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.model;

import adams.core.Randomizable;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import weka.dl4j.layers.DenseLayer;
import weka.dl4j.layers.OutputLayer;

/**
 <!-- globalinfo-start -->
 * Configures a network as used by weka.classifiers.functions.Dl4jMlpClassifier.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the model.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-optimization-algorithm &lt;LINE_GRADIENT_DESCENT|CONJUGATE_GRADIENT|HESSIAN_FREE|LBFGS|STOCHASTIC_GRADIENT_DESCENT&gt; (property: optimizationAlgorithm)
 * &nbsp;&nbsp;&nbsp;The optimization algorithm.
 * &nbsp;&nbsp;&nbsp;default: STOCHASTIC_GRADIENT_DESCENT
 * </pre>
 * 
 * <pre>-layer &lt;org.deeplearning4j.nn.conf.layers.Layer&gt; [-layer ...] (property: layers)
 * &nbsp;&nbsp;&nbsp;The layers specification.
 * &nbsp;&nbsp;&nbsp;default: weka.dl4j.layers.OutputLayer -activation identity -adamMeanDecay 0.9 -adamVarDecay 0.999 -biasInit 1.0 -biasL1 0.0 -biasL2 0.0 -blr 0.01 -dist \"weka.dl4j.distribution.NormalDistribution -mean 0.001 -std 1.0\" -dropout 0.0 -epsilon 1.0E-6 -gradientNormalization None -gradNormThreshold 1.0 -L1 0.0 -L2 0.0 -name \"Output layer\" -lr 0.01 -lossFn LossMSE() -momentum 0.9 -rho 0.0 -rmsDecay 0.95 -updater NESTEROVS -weightInit XAVIER
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Dl4jMlpClassifier
  extends AbstractModelConfigurator
  implements Randomizable {

  private static final long serialVersionUID = -1020010172973169754L;

  /** the seed. */
  protected long m_Seed;

  /** the optimization algorithm. */
  protected OptimizationAlgorithm m_OptimizationAlgorithm;

  /** The layers of the network. */
  protected Layer[] m_Layers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures a network as used by " + weka.classifiers.functions.Dl4jMlpClassifier.class.getName() + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "optimization-algorithm", "optimizationAlgorithm",
      OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);

    m_OptionManager.add(
      "layer", "layers",
      new Layer[]{new OutputLayer()});
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  @Override
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  @Override
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String seedTipText() {
    return "The seed value for the model.";
  }

  /**
   * Sets the optimzation algorithm.
   *
   * @param value	the algorithm
   */
  public void setOptimizationAlgorithm(OptimizationAlgorithm value) {
    m_OptimizationAlgorithm = value;
    reset();
  }

  /**
   * Returns the optimization algorithm.
   *
   * @return		the algorithm
   */
  public OptimizationAlgorithm getOptimizationAlgorithm() {
    return m_OptimizationAlgorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optimizationAlgorithmTipText() {
    return "The optimization algorithm.";
  }

  /**
   * Sets the layer specification.
   *
   * @param layers 	the layers
   */
  public void setLayers(Layer[] layers) {
    m_Layers = layers;
    reset();
  }

  /**
   * Returns the layer specification.
   *
   * @return		the layers
   */
  public Layer[] getLayers() {
    return m_Layers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layersTipText() {
    return "The layers specification.";
  }

  /**
   * Get the current number of units for a particular layer. Returns -1 for
   * anything that is not a DenseLayer or an OutputLayer.
   *
   * @param layer the layer
   * @return the number of units
   */
  protected int getNumUnits(Layer layer) {
    if (layer instanceof DenseLayer)
      return ((DenseLayer) layer).getNOut();
    else if (layer instanceof OutputLayer)
      return ((OutputLayer) layer).getNOut();
    return -1;
  }

  /**
   * Sets the number of incoming connections for the nodes in the given layer.
   *
   * @param layer the layer
   * @param numInputs the number of inputs
   */
  protected void setNumIncoming(Layer layer, int numInputs) {
    if (layer instanceof DenseLayer)
      ((DenseLayer) layer).setNIn(numInputs);
    else if (layer instanceof OutputLayer)
      ((OutputLayer) layer).setNIn(numInputs);
  }

  /**
   * Configures the actual {@link Model} and returns it.
   *
   * @param numInput	the number of input nodes
   * @param numOutput	the number of output nodes
   * @return		the model
   */
  @Override
  protected Model doConfigureModel(int numInput, int numOutput) {
    NeuralNetConfiguration.Builder 	builder;
    ListBuilder 			listbuilder;
    int 				i;

    builder = new NeuralNetConfiguration.Builder();
    builder.setOptimizationAlgo(getOptimizationAlgorithm());
    builder.setSeed(m_Seed);

    listbuilder = builder.list(getLayers());
    for (i = 0; i < m_Layers.length; i++) {

      // Is this the first hidden layer?
      if (i == 0)
	setNumIncoming(m_Layers[i], numInput);
      else
	setNumIncoming(m_Layers[i], getNumUnits(m_Layers[i - 1]));

      // Is this the output layer?
      if (i == m_Layers.length - 1)
	((OutputLayer) m_Layers[i]).setNOut(numOutput);
      listbuilder = listbuilder.layer(i, m_Layers[i]);
    }
    listbuilder = listbuilder
      .pretrain(false)
      .backprop(true);

    return new MultiLayerNetwork(listbuilder.build());
  }
}
