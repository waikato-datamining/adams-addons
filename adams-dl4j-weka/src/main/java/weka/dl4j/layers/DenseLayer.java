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
 *    DenseLayer.java
 *    Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */
package weka.dl4j.layers;

import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.distribution.Distribution;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.impl.ActivationCube;
import org.nd4j.linalg.activations.impl.ActivationELU;
import org.nd4j.linalg.activations.impl.ActivationHardSigmoid;
import org.nd4j.linalg.activations.impl.ActivationHardTanH;
import org.nd4j.linalg.activations.impl.ActivationIdentity;
import org.nd4j.linalg.activations.impl.ActivationLReLU;
import org.nd4j.linalg.activations.impl.ActivationRReLU;
import org.nd4j.linalg.activations.impl.ActivationReLU;
import org.nd4j.linalg.activations.impl.ActivationSELU;
import org.nd4j.linalg.activations.impl.ActivationSigmoid;
import org.nd4j.linalg.activations.impl.ActivationSoftPlus;
import org.nd4j.linalg.activations.impl.ActivationSoftSign;
import org.nd4j.linalg.activations.impl.ActivationSoftmax;
import org.nd4j.linalg.activations.impl.ActivationTanH;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.OptionMetadata;
import weka.dl4j.distribution.NormalDistribution;
import weka.gui.ProgrammaticProperty;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;

/**
 * A version of DeepLearning4j's DenseLayer that implements WEKA option handling.
 *
 * @author Christopher Beckham
 * @author Eibe Frank
 *
 * @version $Revision: 11711 $
 */
public class DenseLayer extends org.deeplearning4j.nn.conf.layers.DenseLayer implements OptionHandler, Serializable {

  // The serial version ID used when serializing this class
  protected static final long serialVersionUID = -6905917800811990400L;

  /**
   * Global info.
   *
   * @return string describing this class.
   */
  public String globalInfo() {
    return "A densely connected layer from DeepLearning4J.";
  }

  /**
   * Constructor for setting some defaults.
   */
  public DenseLayer() {
    setLayerName("Hidden layer");
    setActivationFunction("relu");
    setWeightInit(WeightInit.XAVIER);
    setDist(new NormalDistribution());
    setUpdater(Updater.NESTEROVS);
    setLearningRate(0.01);
    setBiasLearningRate(getLearningRate());
    setMomentum(0.9);
    setBiasInit(1.0);
    setAdamMeanDecay(0.9);
    setAdamVarDecay(0.999);
    setEpsilon(1e-6);
    setRmsDecay(0.95);
  }

  @OptionMetadata(
    displayName = "layer name",
    description = "The name of the layer (default = Hidden Layer).",
    commandLineParamName = "name", commandLineParamSynopsis = "-name <string>",
    displayOrder = 1)
  public String getLayerName() {
    return this.layerName;
  }
  public void setLayerName(String layerName) {
    this.layerName = layerName;
  }

  @OptionMetadata(
    displayName = "name of activation function",
    description = "The name of the activation function (default = relu; options are softmax,logsoftmax,maxout,identity,abs,cos,elu,exp,log,pow,sin,acos,asin,atan,ceil,relu,sign,sqrt,step,tanh,floor,round,hardtanh,timesoneminus,negative,softplus,softsign,leakyrelu,stabilize,sigmoid).",
    commandLineParamName = "activation", commandLineParamSynopsis = "-activation <string>",
    displayOrder = 2)
  public String getActivationFunction() {
    if (this.getActivationFn() instanceof ActivationTanH){
      return("tanh");
    }
    if (this.getActivationFn() instanceof ActivationReLU){
      return("relu");
    }
    if (this.getActivationFn() instanceof ActivationSoftmax){
      return("softmax");
    }
    if (this.getActivationFn() instanceof ActivationSigmoid){
      return("sigmoid");
    }
    if (this.getActivationFn() instanceof ActivationLReLU){
      return("lrelu");
    }
    if (this.getActivationFn() instanceof ActivationSoftSign){
      return("softsign");
    }
    if (this.getActivationFn() instanceof ActivationHardTanH){
      return("hardtanh");
    }
    if (this.getActivationFn() instanceof ActivationRReLU){
      return("rrelu");
    }
    if (this.getActivationFn() instanceof ActivationIdentity){
      return("identity");
    }
    if (this.getActivationFn() instanceof ActivationSoftPlus){
      return("softplus");
    }
    if (this.getActivationFn() instanceof ActivationELU){
      return("elu");
    }
    if (this.getActivationFn() instanceof ActivationSELU){
      return("selu");
    }
    if (this.getActivationFn() instanceof ActivationHardSigmoid){
      return("hardsigmoid");
    }
    if (this.getActivationFn() instanceof ActivationCube){
      return("cube");
    }

    return("sigmoid"); // default
  }

  public void setActivationFunction(String activationFunction) {
    if(activationFunction.equals("tanh")){
      this.setActivationFn(new ActivationTanH());
      return;
    }
    if(activationFunction.equals("lrelu")){
      this.setActivationFn(new ActivationLReLU());
      return;
    }
    if(activationFunction.equals("softmax")){
      this.setActivationFn(new ActivationSoftmax());
      return;
    }
    if(activationFunction.equals("sigmoid")){
      this.setActivationFn(new ActivationSigmoid());
      return;
    }
    if(activationFunction.equals("relu")){
      this.setActivationFn(new ActivationReLU());
      return;
    }
    if(activationFunction.equals("softsign")){
      this.setActivationFn(new ActivationSoftSign());
      return;
    }
    if(activationFunction.equals("hardtanh")){
      this.setActivationFn(new ActivationHardTanH());
      return;
    }
    if(activationFunction.equals("rrelu")){
      this.setActivationFn(new ActivationRReLU());
      return;
    }
    if(activationFunction.equals("identity")){
      this.setActivationFn(new ActivationIdentity());
      return;
    }
    if(activationFunction.equals("softplus")){
      this.setActivationFn(new ActivationSoftPlus());
      return;
    }
    if(activationFunction.equals("elu")){
      this.setActivationFn(new ActivationELU());
      return;
    }
    if(activationFunction.equals("selu")){
      this.setActivationFn(new ActivationSELU());
      return;
    }
    if(activationFunction.equals("hardsigmoid")){
      this.setActivationFn(new ActivationHardSigmoid());
      return;
    }
    if(activationFunction.equals("cube")){
      this.setActivationFn(new ActivationCube());
      return;
    }
    // default
    this.setActivationFn(new ActivationSigmoid());

  }

  @OptionMetadata(
    displayName = "weight initialization method",
    description = "The method for weight initialization (default = XAVIER).",
    commandLineParamName = "weightInit", commandLineParamSynopsis = "-weightInit <specification>",
    displayOrder = 3)
  public WeightInit getWeightInit() {
    return this.weightInit;
  }
  public void setWeightInit(WeightInit weightInit) {
    this.weightInit = weightInit;
  }

  @OptionMetadata(
    displayName = "bias initialization",
    description = "The bias initialization (default = 1.0).",
    commandLineParamName = "biasInit", commandLineParamSynopsis = "-biasInit <double>",
    displayOrder = 4)
  public double getBiasInit() {
    return this.biasInit;
  }
  public void setBiasInit(double biasInit) {
    this.biasInit = biasInit;
  }

  @OptionMetadata(
    displayName = "distribution",
    description = "The distribution (default = NormalDistribution(1e-3, 1)).",
    commandLineParamName = "dist", commandLineParamSynopsis = "-dist <specification>",
    displayOrder = 5)
  public Distribution getDist() {
    return this.dist;
  }
  public void setDist(Distribution dist) {
    this.dist = dist;
  }

  @OptionMetadata(
    displayName = "learning rate",
    description = "The learning rate (default = 0.01).",
    commandLineParamName = "lr", commandLineParamSynopsis = "-lr <double>",
    displayOrder = 6)
  public double getLearningRate() {
    return this.learningRate;
  }
  public void setLearningRate(double learningRate) {
    this.learningRate = learningRate;
  }

  @OptionMetadata(
    displayName = "bias learning rate",
    description = "The bias learning rate (default = 0.01).",
    commandLineParamName = "blr", commandLineParamSynopsis = "-blr <double>",
    displayOrder = 7)
  public double getBiasLearningRate() {
    return this.biasLearningRate;
  }
  public void setBiasLearningRate(double biasLearningRate) {
    this.biasLearningRate = biasLearningRate;
  }

  @OptionMetadata(
    displayName = "learning rate schedule",
    description = "The learning rate schedule.",
    commandLineParamName = "lrSchedule", commandLineParamSynopsis = "-lrSchedule <specification>",
    displayOrder = 8)
  public Map<Integer, Double> getLearningRateSchedule() {
    return this.learningRateSchedule;
  }
  public void setLearningRateSchedule(Map<Integer, Double> learningRateSchedule) {
    this.learningRateSchedule = learningRateSchedule;
  }

  @OptionMetadata(
    displayName = "L1",
    description = "The L1 parameter (default = 0).",
    commandLineParamName = "L1", commandLineParamSynopsis = "-L1 <double>",
    displayOrder = 11)
  public double getL1() {
    return this.l1;
  }
  public void setL1(double l1) {
    this.l1 = l1;
  }

  @OptionMetadata(
    displayName = "L2",
    description = "The L2 parameter (default = 0).",
    commandLineParamName = "L2", commandLineParamSynopsis = "-L2 <double>",
    displayOrder = 12)
  public double getL2() {
    return this.l2;
  }
  public void setL2(double l2) {
    this.l2 = l2;
  }

  @OptionMetadata(
    displayName = "bias L1",
    description = "The bias L1 parameter (default = 0).",
    commandLineParamName = "biasL1", commandLineParamSynopsis = "-biasL1 <double>",
    displayOrder = 13)
  public double getBiasL1() {
    return this.l1Bias;
  }
  public void setBiasL1(double biasL1) {
    this.l1Bias = biasL1;
  }

  @OptionMetadata(
    displayName = "bias L2",
    description = "The bias L2 parameter (default = 0).",
    commandLineParamName = "biasL2", commandLineParamSynopsis = "-biasL2 <double>",
    displayOrder = 14)
  public double getBiasL2() {
    return this.l2Bias;
  }
  public void setBiasL2(double biasL2) {
    this.l2Bias = biasL2;
  }

  @OptionMetadata(
    displayName = "dropout parameter",
    description = "The dropout parameter (default = 0).",
    commandLineParamName = "dropout", commandLineParamSynopsis = "-dropout <double>",
    displayOrder = 15)
  public double getDropOut() {
    return this.dropOut;
  }
  public void setDropOut(double dropOut) {
    this.dropOut = dropOut;
  }

  @OptionMetadata(
    displayName = "updater for stochastic gradient descent",
    description = "The updater for stochastic gradient descent (default NESTEROVS).",
    commandLineParamName = "updater", commandLineParamSynopsis = "-updater <speficiation>",
    displayOrder = 16)
  public Updater getUpdater() {
    return this.updater;
  }
  public void setUpdater(Updater updater) {
    this.updater = updater;
  }

  @OptionMetadata(
    displayName = "ADADELTA's rho parameter",
    description = "ADADELTA's rho parameter (default = 0).",
    commandLineParamName = "rho", commandLineParamSynopsis = "-rho <double>",
    displayOrder = 17)
  public double getRho() {
    return this.rho;
  }
  public void setRho(double rho) {
    this.rho = rho;
  }

  @OptionMetadata(
    displayName = "ADADELTA's epsilon parameter",
    description = "ADADELTA's epsilon parameter (default = 1e-6).",
    commandLineParamName = "epsilon", commandLineParamSynopsis = "-epsilon <double>",
    displayOrder = 18)
  public double getEpsilon() {
    return this.epsilon;
  }
  public void setEpsilon(double epsilon) {
    this.epsilon = epsilon;
  }

  @OptionMetadata(
    displayName = "RMSPROP's RMS decay parameter",
    description = "RMSPROP's RMS decay parameter (default = 0.95).",
    commandLineParamName = "rmsDecay", commandLineParamSynopsis = "-rmsDecay <double>",
    displayOrder = 19)
  public double getRmsDecay() {
    return this.rmsDecay;
  }
  public void setRmsDecay(double rmsDecay) {
    this.rmsDecay = rmsDecay;
  }

  @OptionMetadata(
    displayName = "ADAM's mean decay parameter",
    description = "ADAM's mean decay parameter (default 0.9).",
    commandLineParamName = "adamMeanDecay", commandLineParamSynopsis = "-adamMeanDecay <double>",
    displayOrder = 20)
  public double getAdamMeanDecay() { return this.adamMeanDecay; }
  public void setAdamMeanDecay(double adamMeanDecay) {
    this.adamMeanDecay = adamMeanDecay;
  }

  @OptionMetadata(
    displayName = "ADAMS's var decay parameter",
    description = "ADAM's var decay parameter (default 0.999).",
    commandLineParamName = "adamVarDecay", commandLineParamSynopsis = "-adamVarDecay <double>",
    displayOrder = 21)
  public double getAdamVarDecay() {
    return this.adamVarDecay;
  }
  public void setAdamVarDecay(double adamVarDecay) {
    this.adamVarDecay = adamVarDecay;
  }

  @OptionMetadata(
    displayName = "gradient normalization method",
    description = "The gradient normalization method (default = None).",
    commandLineParamName = "gradientNormalization", commandLineParamSynopsis = "-gradientNormalization <specification>",
    displayOrder = 22)
  public GradientNormalization getGradientNormalization() {
    return this.gradientNormalization;
  }
  public void setGradientNormalization(GradientNormalization gradientNormalization) {
    this.gradientNormalization = gradientNormalization;
  }

  @OptionMetadata(
    displayName = "gradient normalization threshold",
    description = "The gradient normalization threshold (default = 1).",
    commandLineParamName = "gradNormThreshold", commandLineParamSynopsis = "-gradNormThreshold <double>",
    displayOrder = 23)
  public double getGradientNormalizationThreshold() {
    return this.gradientNormalizationThreshold;
  }
  public void setGradientNormalizationThreshold(double gradientNormalizationThreshold) {
    this.gradientNormalizationThreshold = gradientNormalizationThreshold;
  }

  @ProgrammaticProperty
  public int getNIn() { return super.getNIn(); }
  public void setNIn(int nIn) {
    this.nIn = nIn;
  }

  @OptionMetadata(
    displayName = "number of units",
    description = "The number of units.",
    commandLineParamName = "nOut", commandLineParamSynopsis = "-nOut <int>",
    displayOrder = 25)
  public int getNOut() { return super.getNOut(); }
  public void setNOut(int nOut) {
    this.nOut = nOut;
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {

    return Option.listOptionsForClass(this.getClass()).elements();
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {

    return Option.getOptions(this, this.getClass());
  }

  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {

    Option.setOptions(options, this, this.getClass());
  }
}