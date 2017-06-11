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
 * SimpleMultiLayerNetwork.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.model;

import adams.core.Randomizable;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 <!-- globalinfo-start -->
 * A simple multilayer network, adapted from the iris example:<br>
 * https:&#47;&#47;github.com&#47;deeplearning4j&#47;dl4j-examples&#47;blob&#47;ba219bbbdfd2b377f30559330a173ace508b1758&#47;dl4j-examples&#47;src&#47;main&#47;java&#47;org&#47;deeplearning4j&#47;examples&#47;dataExamples&#47;CSVExample.java
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-iterations &lt;int&gt; (property: numIterations)
 * &nbsp;&nbsp;&nbsp;The number of iterations to perform.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-learning-rate &lt;double&gt; (property: learningRate)
 * &nbsp;&nbsp;&nbsp;The learning rate to use.
 * &nbsp;&nbsp;&nbsp;default: 0.1
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the weight initialization.
 * &nbsp;&nbsp;&nbsp;default: 6
 * </pre>
 * 
 * <pre>-use-regularization &lt;boolean&gt; (property: useRegularization)
 * &nbsp;&nbsp;&nbsp;If enabled, regularization is used.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-l2 &lt;double&gt; (property: l2)
 * &nbsp;&nbsp;&nbsp;The L2 value.
 * &nbsp;&nbsp;&nbsp;default: 1.0E-4
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-hidden-nodes &lt;int&gt; (property: hiddenNodes)
 * &nbsp;&nbsp;&nbsp;The number of hidden nodes.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-activation &lt;CUBE|ELU|HARDSIGMOID|HARDTANH|IDENTITY|LEAKYRELU|RELU|RRELU|SIGMOID|SOFTMAX|SOFTPLUS|SOFTSIGN|TANH&gt; (property: activation)
 * &nbsp;&nbsp;&nbsp;The activation to use.
 * &nbsp;&nbsp;&nbsp;default: TANH
 * </pre>
 * 
 * <pre>-weight-init &lt;DISTRIBUTION|ZERO|SIGMOID_UNIFORM|UNIFORM|XAVIER|XAVIER_UNIFORM|XAVIER_FAN_IN|XAVIER_LEGACY|RELU|RELU_UNIFORM|VI|SIZE|NORMALIZED&gt; (property: weightInit)
 * &nbsp;&nbsp;&nbsp;The weight init to use.
 * &nbsp;&nbsp;&nbsp;default: XAVIER
 * </pre>
 * 
 * <pre>-output-activation &lt;CUBE|ELU|HARDSIGMOID|HARDTANH|IDENTITY|LEAKYRELU|RELU|RRELU|SIGMOID|SOFTMAX|SOFTPLUS|SOFTSIGN|TANH&gt; (property: outputActivation)
 * &nbsp;&nbsp;&nbsp;The activation to use for the output layer.
 * &nbsp;&nbsp;&nbsp;default: SOFTMAX
 * </pre>
 * 
 * <pre>-output-loss-function &lt;MSE|L1|EXPLL|XENT|MCXENT|RMSE_XENT|SQUARED_LOSS|RECONSTRUCTION_CROSSENTROPY|NEGATIVELOGLIKELIHOOD|CUSTOM|COSINE_PROXIMITY|HINGE|SQUARED_HINGE|KL_DIVERGENCE|MEAN_ABSOLUTE_ERROR|L2|MEAN_ABSOLUTE_PERCENTAGE_ERROR|MEAN_SQUARED_LOGARITHMIC_ERROR|POISSON&gt; (property: outputLossFunction)
 * &nbsp;&nbsp;&nbsp;The loss function to use for the output layer.
 * &nbsp;&nbsp;&nbsp;default: NEGATIVELOGLIKELIHOOD
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleMultiLayerNetwork
  extends AbstractModelConfigurator
  implements Randomizable {

  private static final long serialVersionUID = -4915929902612899539L;

  /** the number of iterations. */
  protected int m_NumIterations;

  /** the learning rate. */
  protected double m_LearningRate;

  /** the seed value. */
  protected long m_Seed;

  /** whether to use regularization. */
  protected boolean m_UseRegularization;

  /** the L2. */
  protected double m_L2;

  /** the number of nodes in the hidden layer. */
  protected int m_HiddenNodes;

  /** the activation function. */
  protected Activation m_Activation;

  /** the weight init of the hidden layer. */
  protected WeightInit m_WeightInit;

  /** the activation function of the outut layer. */
  protected Activation m_OutputActivation;

  /** the loss function of the output layer. */
  protected LossFunctions.LossFunction m_OutputLossFunction;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui1e-6
   */
  @Override
  public String globalInfo() {
    return
      "A simple multilayer network, adapted from the iris example:\n"
	+ "https://github.com/deeplearning4j/dl4j-examples/blob/ba219bbbdfd2b377f30559330a173ace508b1758/dl4j-examples/src/main/java/org/deeplearning4j/examples/dataExamples/CSVExample.java";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-iterations", "numIterations",
      1, 1, null);

    m_OptionManager.add(
      "learning-rate", "learningRate",
      0.1, 0.0, null);

    m_OptionManager.add(
      "seed", "seed",
      6L);

    m_OptionManager.add(
      "use-regularization", "useRegularization",
      true);

    m_OptionManager.add(
      "l2", "l2",
      1e-4, 0.0, null);

    m_OptionManager.add(
      "hidden-nodes", "hiddenNodes",
      3, 1, null);

    m_OptionManager.add(
      "activation", "activation",
      Activation.TANH);

    m_OptionManager.add(
      "weight-init", "weightInit",
      WeightInit.XAVIER);

    m_OptionManager.add(
      "output-activation", "outputActivation",
      Activation.SOFTMAX);

    m_OptionManager.add(
      "output-loss-function", "outputLossFunction",
      LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD);
  }

  /**
   * Sets the number of iterations to perform.
   *
   * @param value	the iterations
   */
  public void setNumIterations(int value) {
    if (getOptionManager().isValid("numIterations", value)) {
      m_NumIterations = value;
      reset();
    }
  }

  /**
   * Returns the number of iterations to perform.
   *
   * @return 		the iterations
   */
  public int getNumIterations() {
    return m_NumIterations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String numIterationsTipText() {
    return "The number of iterations to perform.";
  }

  /**
   * Sets the learning rate.
   *
   * @param value	the rate
   */
  public void setLearningRate(double value) {
    if (getOptionManager().isValid("learningRate", value)) {
      m_LearningRate = value;
      reset();
    }
  }

  /**
   * Returns the learning rate.
   *
   * @return 		the rate
   */
  public double getLearningRate() {
    return m_LearningRate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String learningRateTipText() {
    return "The learning rate to use.";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    if (getOptionManager().isValid("seed", value)) {
      m_Seed = value;
      reset();
    }
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value for the weight initialization.";
  }

  /**
   * Sets whether to use regularization.
   *
   * @param value	true if to use regularization
   */
  public void setUseRegularization(boolean value) {
    m_UseRegularization = value;
    reset();
  }

  /**
   * Returns whether to use regularization.
   *
   * @return  		true if to use regularization
   */
  public boolean getUseRegularization() {
    return m_UseRegularization;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useRegularizationTipText() {
    return "If enabled, regularization is used.";
  }

  /**
   * Sets L2.
   *
   * @param value	L2
   */
  public void setL2(double value) {
    if (getOptionManager().isValid("l2", value)) {
      m_L2 = value;
      reset();
    }
  }

  /**
   * Returns L2.
   *
   * @return  		L2
   */
  public double getL2() {
    return m_L2;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String l2TipText() {
    return "The L2 value.";
  }

  /**
   * Sets the number of hidden nodes.
   *
   * @param value	the number of nodes
   */
  public void setHiddenNodes(int value) {
    m_HiddenNodes= value;
    reset();
  }

  /**
   * Returns the number of hidden nodes.
   *
   * @return  		the number of nodes
   */
  public int getHiddenNodes() {
    return m_HiddenNodes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hiddenNodesTipText() {
    return "The number of hidden nodes.";
  }

  /**
   * Sets the activation.
   *
   * @param value	the activation
   */
  public void setActivation(Activation value) {
    m_Activation = value;
    reset();
  }

  /**
   * Returns the activation.
   *
   * @return  		the activation
   */
  public Activation getActivation() {
    return m_Activation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String activationTipText() {
    return "The activation to use.";
  }

  /**
   * Sets the hidden weight init.
   *
   * @param value	the weight init
   */
  public void setWeightInit(WeightInit value) {
    m_WeightInit = value;
    reset();
  }

  /**
   * Returns the hidden weight init.
   *
   * @return  		the weight init
   */
  public WeightInit getWeightInit() {
    return m_WeightInit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String weightInitTipText() {
    return "The weight init to use.";
  }

  /**
   * Sets the output activation.
   *
   * @param value	the activation
   */
  public void setOutputActivation(Activation value) {
    m_OutputActivation = value;
    reset();
  }

  /**
   * Returns the output activation.
   *
   * @return  		the activation
   */
  public Activation getOutputActivation() {
    return m_OutputActivation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputActivationTipText() {
    return "The activation to use for the output layer.";
  }

  /**
   * Sets the output loss function.
   *
   * @param value	the function
   */
  public void setOutputLossFunction(LossFunctions.LossFunction value) {
    m_OutputLossFunction = value;
    reset();
  }

  /**
   * Returns the output loss function.
   *
   * @return  		the function
   */
  public LossFunctions.LossFunction getOutputLossFunction() {
    return m_OutputLossFunction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputLossFunctionTipText() {
    return "The loss function to use for the output layer.";
  }

  /**
   * Configures the actual {@link Model} and returns it.
   *
   * @param numInput	the number of input nodes
   * @param numOutput	the number of output nodes
   * @return		the model
   */
  protected Model doConfigureModel(int numInput, int numOutput) {
    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
      .seed(m_Seed)
      .iterations(m_NumIterations)
      .activation(m_Activation)
      .weightInit(m_WeightInit)
      .learningRate(m_LearningRate)
      .regularization(m_UseRegularization)
      .l2(m_L2)
      .list()
      .layer(0, new DenseLayer.Builder()
        .nIn(numInput)
        .nOut(m_HiddenNodes)
        .build())
      .layer(1, new DenseLayer.Builder()
        .nIn(m_HiddenNodes)
        .nOut(m_HiddenNodes)
        .build())
      .layer(2, new OutputLayer.Builder(m_OutputLossFunction)
        .activation(m_OutputActivation)
        .nIn(m_HiddenNodes)
        .nOut(numOutput)
        .build())
      .backprop(true)
      .pretrain(false)
      .build();

    return new MultiLayerNetwork(conf);
  }
}
