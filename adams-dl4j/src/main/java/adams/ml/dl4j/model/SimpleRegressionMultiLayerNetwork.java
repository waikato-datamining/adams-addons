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
 * SimpleRegressionMultiLayerNetwork.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.model;

import adams.core.Randomizable;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 <!-- globalinfo-start -->
 * A simple multilayer network, adapted from this regression example:<br>
 * https:&#47;&#47;github.com&#47;deeplearning4j&#47;dl4j-examples&#47;blob&#47;bde80477139bbf74bea729f66e6dcd59944933ee&#47;dl4j-examples&#47;src&#47;main&#47;java&#47;org&#47;deeplearning4j&#47;examples&#47;recurrent&#47;regression&#47;SingleTimestepRegressionExample.java
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
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-learning-rate &lt;double&gt; (property: learningRate)
 * &nbsp;&nbsp;&nbsp;The learning rate to use.
 * &nbsp;&nbsp;&nbsp;default: 0.0015
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the weight initialization.
 * &nbsp;&nbsp;&nbsp;default: 140
 * </pre>
 *
 * <pre>-hidden-nodes &lt;int&gt; (property: hiddenNodes)
 * &nbsp;&nbsp;&nbsp;The number of hidden nodes.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-activation &lt;CUBE|ELU|HARDSIGMOID|HARDTANH|IDENTITY|LEAKYRELU|RELU|RRELU|SIGMOID|SOFTMAX|SOFTPLUS|SOFTSIGN|TANH&gt; (property: activation)
 * &nbsp;&nbsp;&nbsp;The activation to use.
 * &nbsp;&nbsp;&nbsp;default: TANH
 * </pre>
 *
 * <pre>-output-activation &lt;CUBE|ELU|HARDSIGMOID|HARDTANH|IDENTITY|LEAKYRELU|RELU|RRELU|SIGMOID|SOFTMAX|SOFTPLUS|SOFTSIGN|TANH&gt; (property: outputActivation)
 * &nbsp;&nbsp;&nbsp;The activation to use for the output layer.
 * &nbsp;&nbsp;&nbsp;default: IDENTITY
 * </pre>
 *
 * <pre>-weight-init &lt;DISTRIBUTION|ZERO|SIGMOID_UNIFORM|UNIFORM|XAVIER|XAVIER_UNIFORM|XAVIER_FAN_IN|XAVIER_LEGACY|RELU|RELU_UNIFORM|VI|SIZE|NORMALIZED&gt; (property: weightInit)
 * &nbsp;&nbsp;&nbsp;The weight init to use.
 * &nbsp;&nbsp;&nbsp;default: XAVIER
 * </pre>
 *
 * <pre>-optimization-algorithm &lt;LINE_GRADIENT_DESCENT|CONJUGATE_GRADIENT|HESSIAN_FREE|LBFGS|STOCHASTIC_GRADIENT_DESCENT&gt; (property: optimizationAlgorithm)
 * &nbsp;&nbsp;&nbsp;The optimization algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: STOCHASTIC_GRADIENT_DESCENT
 * </pre>
 *
 * <pre>-updater &lt;SGD|ADAM|ADADELTA|NESTEROVS|ADAGRAD|RMSPROP|NONE|CUSTOM&gt; (property: updater)
 * &nbsp;&nbsp;&nbsp;The updater to use.
 * &nbsp;&nbsp;&nbsp;default: NESTEROVS
 * </pre>
 *
 * <pre>-loss-function &lt;MSE|L1|EXPLL|XENT|MCXENT|RMSE_XENT|SQUARED_LOSS|RECONSTRUCTION_CROSSENTROPY|NEGATIVELOGLIKELIHOOD|CUSTOM|COSINE_PROXIMITY|HINGE|SQUARED_HINGE|KL_DIVERGENCE|MEAN_ABSOLUTE_ERROR|L2|MEAN_ABSOLUTE_PERCENTAGE_ERROR|MEAN_SQUARED_LOGARITHMIC_ERROR|POISSON&gt; (property: lossFunction)
 * &nbsp;&nbsp;&nbsp;The loss function to use.
 * &nbsp;&nbsp;&nbsp;default: MSE
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleRegressionMultiLayerNetwork
  extends AbstractModelConfigurator
  implements Randomizable {

  private static final long serialVersionUID = -4915929902612899539L;

  /** the number of iterations. */
  protected int m_NumIterations;

  /** the learning rate. */
  protected double m_LearningRate;

  /** the seed value. */
  protected long m_Seed;

  /** the number of nodes in the hidden layer. */
  protected int m_HiddenNodes;

  /** the activation function. */
  protected Activation m_Activation;

  /** the activation function of the outut layer. */
  protected Activation m_OutputActivation;

  /** the weight init of the hidden layer. */
  protected WeightInit m_WeightInit;

  /** the optimization algorithm. */
  protected OptimizationAlgorithm m_OptimizationAlgorithm;

  /** the updater. */
  protected Updater m_Updater;

  /** the loss function. */
  protected LossFunctions.LossFunction m_LossFunction;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui1e-6
   */
  @Override
  public String globalInfo() {
    return
      "A simple multilayer network, adapted from this regression example:\n"
	+ "https://github.com/deeplearning4j/dl4j-examples/blob/bde80477139bbf74bea729f66e6dcd59944933ee/dl4j-examples/src/main/java/org/deeplearning4j/examples/recurrent/regression/SingleTimestepRegressionExample.java";
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
      0.0015, 0.0, null);

    m_OptionManager.add(
      "seed", "seed",
      140L);

    m_OptionManager.add(
      "hidden-nodes", "hiddenNodes",
      10, 1, null);

    m_OptionManager.add(
      "activation", "activation",
      Activation.TANH);

    m_OptionManager.add(
      "output-activation", "outputActivation",
      Activation.IDENTITY);

    m_OptionManager.add(
      "weight-init", "weightInit",
      WeightInit.XAVIER);

    m_OptionManager.add(
      "optimization-algorithm", "optimizationAlgorithm",
      OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);

    m_OptionManager.add(
      "updater", "updater",
      Updater.NESTEROVS);

    m_OptionManager.add(
      "loss-function", "lossFunction",
      LossFunctions.LossFunction.MSE);
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
   * Sets the updater.
   *
   * @param value	the updater
   */
  public void setUpdater(Updater value) {
    m_Updater = value;
    reset();
  }

  /**
   * Returns the updater.
   *
   * @return  		the updater
   */
  public Updater getUpdater() {
    return m_Updater;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updaterTipText() {
    return "The updater to use.";
  }

  /**
   * Sets the loss function.
   *
   * @param value	the loss function
   */
  public void setLossFunction(LossFunctions.LossFunction value) {
    m_LossFunction = value;
    reset();
  }

  /**
   * Returns the loss function.
   *
   * @return  		the loss function
   */
  public LossFunctions.LossFunction getLossFunction() {
    return m_LossFunction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lossFunctionTipText() {
    return "The loss function to use.";
  }

  /**
   * Sets the optimization algorithm.
   *
   * @param value	the optimization algorithm
   */
  public void setOptimizationAlgorithm(OptimizationAlgorithm value) {
    m_OptimizationAlgorithm = value;
    reset();
  }

  /**
   * Returns the optimization algorithm.
   *
   * @return  		the optimization algorithm
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
    return "The optimization algorithm to use.";
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
      .optimizationAlgo(m_OptimizationAlgorithm)
      .iterations(m_NumIterations)
      .list()
      .layer(
	0,
	new DenseLayer.Builder()
	  .nIn(numInput)
	  .nOut(m_HiddenNodes)
	  .biasLearningRate(0.01)
	  .activation(m_Activation)
	  .learningRate(m_LearningRate)
	  .weightInit(m_WeightInit)
	  .updater(m_Updater)
	  .build())
      .layer(
	1,
	new OutputLayer.Builder(m_LossFunction)
	  .nIn(m_HiddenNodes)
	  .nOut(1)
	  .biasLearningRate(0.01)
	  .activation(m_OutputActivation)
	  .learningRate(m_LearningRate)
	  .weightInit(m_WeightInit)
	  .updater(m_Updater)
	  .build())
      .build();

    return new MultiLayerNetwork(conf);
  }
}
