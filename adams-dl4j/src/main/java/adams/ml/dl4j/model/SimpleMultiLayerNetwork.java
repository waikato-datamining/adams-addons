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
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.model;

import adams.core.Randomizable;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RBM;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 <!-- globalinfo-start -->
 * A simple multilayer network, adapted from the iris flow tutorial:<br>
 * http:&#47;&#47;deeplearning4j.org&#47;iris-flower-dataset-tutorial
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
 * <pre>-learning-rate &lt;float&gt; (property: learningRate)
 * &nbsp;&nbsp;&nbsp;The learning rate to use.
 * &nbsp;&nbsp;&nbsp;default: 1.0E-6
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the weight initialization.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-optimization-algorithm &lt;LINE_GRADIENT_DESCENT|CONJUGATE_GRADIENT|HESSIAN_FREE|LBFGS|STOCHASTIC_GRADIENT_DESCENT&gt; (property: optimizationAlgorithm)
 * &nbsp;&nbsp;&nbsp;The optimization algorithm.
 * &nbsp;&nbsp;&nbsp;default: CONJUGATE_GRADIENT
 * </pre>
 * 
 * <pre>-use-regularization &lt;boolean&gt; (property: useRegularization)
 * &nbsp;&nbsp;&nbsp;If enabled, regularization is used.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-l1 &lt;double&gt; (property: l1)
 * &nbsp;&nbsp;&nbsp;The L1 value.
 * &nbsp;&nbsp;&nbsp;default: 0.1
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-l2 &lt;double&gt; (property: l2)
 * &nbsp;&nbsp;&nbsp;The L2 value.
 * &nbsp;&nbsp;&nbsp;default: 2.0E-4
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-use-drop-connect &lt;boolean&gt; (property: useDropConnect)
 * &nbsp;&nbsp;&nbsp;If enabled, drop-connect is used.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-hidden-nodes &lt;int&gt; (property: hiddenNodes)
 * &nbsp;&nbsp;&nbsp;The number of hidden nodes.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-hidden-activation &lt;java.lang.String&gt; (property: hiddenActivation)
 * &nbsp;&nbsp;&nbsp;The activation to use for the hidden layer; eg relu (rectified linear), 
 * &nbsp;&nbsp;&nbsp;tanh, sigmoid, softmax, hardtanh, leakyrelu, maxout, softsign, softplus.
 * &nbsp;&nbsp;&nbsp;default: relu
 * </pre>
 * 
 * <pre>-hidden-loss-function &lt;MSE|EXPLL|XENT|MCXENT|RMSE_XENT|SQUARED_LOSS|RECONSTRUCTION_CROSSENTROPY|NEGATIVELOGLIKELIHOOD|CUSTOM&gt; (property: hiddenLossFunction)
 * &nbsp;&nbsp;&nbsp;The loss function to use for the hidden layer.
 * &nbsp;&nbsp;&nbsp;default: RMSE_XENT
 * </pre>
 * 
 * <pre>-hidden-weight-init &lt;DISTRIBUTION|NORMALIZED|SIZE|UNIFORM|VI|ZERO|XAVIER|RELU&gt; (property: hiddenWeightInit)
 * &nbsp;&nbsp;&nbsp;The weight init to use for the hidden layer.
 * &nbsp;&nbsp;&nbsp;default: XAVIER
 * </pre>
 * 
 * <pre>-hidden-updater &lt;SGD|ADAM|ADADELTA|NESTEROVS|ADAGRAD|RMSPROP|NONE|CUSTOM&gt; (property: hiddenUpdater)
 * &nbsp;&nbsp;&nbsp;The updater to use for the hidden layer.
 * &nbsp;&nbsp;&nbsp;default: ADAGRAD
 * </pre>
 * 
 * <pre>-hidden-drop-out &lt;double&gt; (property: hiddenDropOut)
 * &nbsp;&nbsp;&nbsp;The drop-out to use for the hidden layer.
 * &nbsp;&nbsp;&nbsp;default: 0.5
 * </pre>
 * 
 * <pre>-output-activation &lt;java.lang.String&gt; (property: outputActivation)
 * &nbsp;&nbsp;&nbsp;The activation to use for the output layer; eg relu (rectified linear), 
 * &nbsp;&nbsp;&nbsp;tanh, sigmoid, softmax, hardtanh, leakyrelu, maxout, softsign, softplus.
 * &nbsp;&nbsp;&nbsp;default: softmax
 * </pre>
 * 
 * <pre>-output-loss-function &lt;MSE|EXPLL|XENT|MCXENT|RMSE_XENT|SQUARED_LOSS|RECONSTRUCTION_CROSSENTROPY|NEGATIVELOGLIKELIHOOD|CUSTOM&gt; (property: outputLossFunction)
 * &nbsp;&nbsp;&nbsp;The loss function to use for the output layer.
 * &nbsp;&nbsp;&nbsp;default: MCXENT
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
  protected float m_LearningRate;

  /** the seed value. */
  protected long m_Seed;

  /** the optimization algorithm. */
  protected OptimizationAlgorithm m_OptimizationAlgorithm;

  /** whether to use regularization. */
  protected boolean m_UseRegularization;

  /** the L1. */
  protected double m_L1;

  /** the L2. */
  protected double m_L2;

  /** whether to use drop-connect. */
  protected boolean m_UseDropConnect;

  /** the number of nodes in the hidden layer. */
  protected int m_HiddenNodes;

  /** the activation function of the hidden layer. */
  protected String m_HiddenActivation;

  /** the loss function of the hidden layer. */
  protected LossFunctions.LossFunction m_HiddenLossFunction;

  /** the weight init of the hidden layer. */
  protected WeightInit m_HiddenWeightInit;

  /** the updater of the hidden layer. */
  protected Updater m_HiddenUpdater;

  /** the drop-out of the hidden layer. */
  protected double m_HiddenDropOut;

  /** the activation function of the outut layer. */
  protected String m_OutputActivation;

  /** the loss function of the output layer. */
  protected LossFunctions.LossFunction m_OutputLossFunction;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "A simple multilayer network, adapted from the iris flow tutorial:\n"
	+ "http://deeplearning4j.org/iris-flower-dataset-tutorial";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-iterations", "numIterations",
      1000, 1, null);

    m_OptionManager.add(
      "learning-rate", "learningRate",
      1e-6f, 0.0f, null);

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "optimization-algorithm", "optimizationAlgorithm",
      OptimizationAlgorithm.CONJUGATE_GRADIENT);

    m_OptionManager.add(
      "use-regularization", "useRegularization",
      true);

    m_OptionManager.add(
      "l1", "l1",
      1e-1, 0.0, null);

    m_OptionManager.add(
      "l2", "l2",
      2e-4, 0.0, null);

    m_OptionManager.add(
      "use-drop-connect", "useDropConnect",
      true);

    m_OptionManager.add(
      "hidden-nodes", "hiddenNodes",
      3, 1, null);

    m_OptionManager.add(
      "hidden-activation", "hiddenActivation",
      "relu");

    m_OptionManager.add(
      "hidden-loss-function", "hiddenLossFunction",
      LossFunctions.LossFunction.RMSE_XENT);

    m_OptionManager.add(
      "hidden-weight-init", "hiddenWeightInit",
      WeightInit.XAVIER);

    m_OptionManager.add(
      "hidden-updater", "hiddenUpdater",
      Updater.ADAGRAD);

    m_OptionManager.add(
      "hidden-drop-out", "hiddenDropOut",
      0.5);

    m_OptionManager.add(
      "output-activation", "outputActivation",
      "softmax");

    m_OptionManager.add(
      "output-loss-function", "outputLossFunction",
      LossFunctions.LossFunction.MCXENT);
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
  public void setLearningRate(float value) {
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
  public float getLearningRate() {
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
   * Sets the optimization algorithm.
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
   * @return  		the algorithm
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
   * Sets L1.
   *
   * @param value	L1
   */
  public void setL1(double value) {
    if (getOptionManager().isValid("l1", value)) {
      m_L1 = value;
      reset();
    }
  }

  /**
   * Returns L1.
   *
   * @return  		L1
   */
  public double getL1() {
    return m_L1;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String l1TipText() {
    return "The L1 value.";
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
   * Sets whether to use dropConnect.
   *
   * @param value	true if to use dropConnect
   */
  public void setUseDropConnect(boolean value) {
    m_UseDropConnect = value;
    reset();
  }

  /**
   * Returns whether to use dropConnect.
   *
   * @return  		true if to use dropConnect
   */
  public boolean getUseDropConnect() {
    return m_UseDropConnect;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useDropConnectTipText() {
    return "If enabled, drop-connect is used.";
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
   * Sets the hidden activation.
   *
   * @param value	the activation
   */
  public void setHiddenActivation(String value) {
    m_HiddenActivation = value;
    reset();
  }

  /**
   * Returns the hidden activation.
   *
   * @return  		the activation
   */
  public String getHiddenActivation() {
    return m_HiddenActivation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hiddenActivationTipText() {
    return "The activation to use for the hidden layer; eg relu (rectified linear), tanh, sigmoid, softmax, hardtanh, leakyrelu, maxout, softsign, softplus.";
  }

  /**
   * Sets the hidden loss function.
   *
   * @param value	the function
   */
  public void setHiddenLossFunction(LossFunctions.LossFunction value) {
    m_HiddenLossFunction = value;
    reset();
  }

  /**
   * Returns the hidden loss function.
   *
   * @return  		the function
   */
  public LossFunctions.LossFunction getHiddenLossFunction() {
    return m_HiddenLossFunction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hiddenLossFunctionTipText() {
    return "The loss function to use for the hidden layer.";
  }

  /**
   * Sets the hidden weight init.
   *
   * @param value	the weight init
   */
  public void setHiddenWeightInit(WeightInit value) {
    m_HiddenWeightInit = value;
    reset();
  }

  /**
   * Returns the hidden weight init.
   *
   * @return  		the weight init
   */
  public WeightInit getHiddenWeightInit() {
    return m_HiddenWeightInit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hiddenWeightInitTipText() {
    return "The weight init to use for the hidden layer.";
  }

  /**
   * Sets the hidden updater.
   *
   * @param value	the updater
   */
  public void setHiddenUpdater(Updater value) {
    m_HiddenUpdater = value;
    reset();
  }

  /**
   * Returns the hidden updater.
   *
   * @return  		the updater
   */
  public Updater getHiddenUpdater() {
    return m_HiddenUpdater;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hiddenUpdaterTipText() {
    return "The updater to use for the hidden layer.";
  }

  /**
   * Sets the hidden drop-out.
   *
   * @param value	the drop-out
   */
  public void setHiddenDropOut(double value) {
    if (getOptionManager().isValid("hiddenDropOut", value)) {
      m_HiddenDropOut = value;
      reset();
    }
  }

  /**
   * Returns the hidden drop-out.
   *
   * @return  		the drop-out
   */
  public double getHiddenDropOut() {
    return m_HiddenDropOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hiddenDropOutTipText() {
    return "The drop-out to use for the hidden layer.";
  }

  /**
   * Sets the output activation.
   *
   * @param value	the activation
   */
  public void setOutputActivation(String value) {
    m_OutputActivation = value;
    reset();
  }

  /**
   * Returns the output activation.
   *
   * @return  		the activation
   */
  public String getOutputActivation() {
    return m_OutputActivation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputActivationTipText() {
    return "The activation to use for the output layer; eg relu (rectified linear), tanh, sigmoid, softmax, hardtanh, leakyrelu, maxout, softsign, softplus.";
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
      .seed(m_Seed) // Locks in weight initialization for tuning
      .iterations(m_NumIterations) // # training iterations predict/classify & backprop
      .learningRate(m_LearningRate) // Optimization step size
      .optimizationAlgo(m_OptimizationAlgorithm) // Backprop to calculate gradients
      .l1(m_L1)
      .regularization(m_UseRegularization)
      .l2(m_L2)
      .useDropConnect(m_UseDropConnect)
      .list()
      .layer(0, new RBM.Builder(RBM.HiddenUnit.RECTIFIED, RBM.VisibleUnit.GAUSSIAN)
	  .nIn(numInput) // # input nodes
	  .nOut(m_HiddenNodes) // # fully connected hidden layer nodes. Add list if multiple layers.
	  .weightInit(m_HiddenWeightInit) // Weight initialization
	  .k(1) // # contrastive divergence iterations
	  .activation(m_HiddenActivation) // Activation function type
	  .lossFunction(m_HiddenLossFunction) // Loss function type
	  .updater(m_HiddenUpdater)
	  .dropOut(m_HiddenDropOut)
	  .build()
      ) // NN layer type
      .layer(1, new OutputLayer.Builder(m_OutputLossFunction)
	  .nIn(m_HiddenNodes) // # input nodes
	  .nOut(numOutput) // # output nodes
	  .activation(m_OutputActivation)
	  .build()
      ) // NN layer type
      .build();

    return new MultiLayerNetwork(conf);
  }
}
