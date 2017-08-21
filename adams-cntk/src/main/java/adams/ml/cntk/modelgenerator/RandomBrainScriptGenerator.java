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
 * RandomBrainScriptGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelgenerator;

import adams.core.Randomizable;
import adams.core.Utils;
import adams.core.base.BaseBoolean;
import adams.core.base.BaseDouble;
import adams.core.base.BaseInteger;
import adams.core.base.BaseText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Generates random networks, just using dense layers and a linear layer as output.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RandomBrainScriptGenerator
  extends AbstractBrainScriptModelGenerator
  implements Randomizable {

  private static final long serialVersionUID = 6117066358207451433L;

  /** the placeholder variable for the input dimension. */
  public final static String INPUT_DIM = "inputDim";

  /** the placeholder variable for the output dimension. */
  public final static String OUTPUT_DIM = "outputDim";

  /** the default network. */
  protected BaseText m_DefaultNetwork;

  /** the default dense layer. */
  protected BaseText m_DefaultDenseLayer;

  /** the default output layer. */
  protected BaseText m_DefaultOutputLayer;

  /** the seed value. */
  protected long m_Seed;

  /** the number of networks to generate. */
  protected int m_NumNetworks;

  /** the list of possible layers. */
  protected BaseInteger[] m_NumLayers;

  /** the list of possible nodes (per layer). */
  protected BaseInteger[] m_NumNodes;

  /** learning rate values. */
  protected BaseDouble[] m_LearningRate;

  /** using regularization. */
  protected BaseBoolean[] m_UseRegularization;

  /** L1 values. */
  protected BaseDouble[] m_L1;

  /** L2 values. */
  protected BaseDouble[] m_L2;

  /** the drop out. */
  protected BaseDouble[] m_DropOut;  // TODO add support for schedule

  /** the epochs for the learning rate schedule. */
  protected BaseInteger[] m_LearningRateScheduleEpochs;

  /** the learning rate divisors for the learning rate schedule. */
  protected BaseDouble[] m_LearningRateScheduleDivisors;

  /** whether to insert batchnorm layers. */
  protected boolean m_InsertBatchNormLayers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates random networks, just using dense layers and a linear layer as output.\n"
      + "Inserts the following variables for input and output dimensions:\n"
      + "- input: " + INPUT_DIM + "\n"
      + "- output: " + OUTPUT_DIM + "\n"
      + "\n"
      + getBrainScriptInfo();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "default-network", "defaultNetwork",
      new BaseText("SGD = {\n  #maxEpochs = 10\n  #minibatchSize = 200\n}"));

    m_OptionManager.add(
      "default-dense-layer", "defaultDenseLayer",
      new BaseText("DenseLayer {}"));

    m_OptionManager.add(
      "default-output-layer", "defaultOutputLayer",
      new BaseText("LinearLayer {" + OUTPUT_DIM + ", init=\"gaussian\" }"));

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "num-networks", "numNetworks",
      1, 1, null);

    m_OptionManager.add(
      "num-layers", "numLayers",
      new BaseInteger[0]);

    m_OptionManager.add(
      "num-nodes", "numNodes",
      new BaseInteger[0]);

    m_OptionManager.add(
      "learning-rate", "learningRate",
      new BaseDouble[0]);

    m_OptionManager.add(
      "use-regularization", "useRegularization",
      new BaseBoolean[0]);

    m_OptionManager.add(
      "l1", "L1",
      new BaseDouble[0]);

    m_OptionManager.add(
      "l2", "L2",
      new BaseDouble[0]);

    m_OptionManager.add(
      "drop-out", "dropOut",
      new BaseDouble[0]);

    m_OptionManager.add(
      "learning-rate-schedule-epochs", "learningRateScheduleEpochs",
      new BaseInteger[0]);

    m_OptionManager.add(
      "learning-rate-schedule-divisors", "learningRateScheduleDivisors",
      new BaseDouble[0]);

    m_OptionManager.add(
      "insert-batch-norm-layers", "insertBatchNormLayers",
      false);
  }

  /**
   * Sets the default network.
   *
   * @param value	the network
   */
  public void setDefaultNetwork(BaseText value) {
    m_DefaultNetwork = value;
    reset();
  }

  /**
   * Returns the default network.
   *
   * @return  		the network
   */
  public BaseText getDefaultNetwork() {
    return m_DefaultNetwork;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String defaultNetworkTipText() {
    return "The default network setup to use (minus layers).";
  }

  /**
   * Sets the default dense layer.
   *
   * @param value	the dense layer
   */
  public void setDefaultDenseLayer(BaseText value) {
    m_DefaultDenseLayer = value;
    reset();
  }

  /**
   * Returns the default dense layer.
   *
   * @return  		the dense layer
   */
  public BaseText getDefaultDenseLayer() {
    return m_DefaultDenseLayer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String defaultDenseLayerTipText() {
    return "The default dense layer setup to use (minus layers).";
  }

  /**
   * Sets the default output layer.
   *
   * @param value	the output layer
   */
  public void setDefaultOutputLayer(BaseText value) {
    m_DefaultOutputLayer = value;
    reset();
  }

  /**
   * Returns the default output layer.
   *
   * @return  		the output layer
   */
  public BaseText getDefaultOutputLayer() {
    return m_DefaultOutputLayer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String defaultOutputLayerTipText() {
    return "The default output layer setup to use.";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
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
    return "The seed value to use for initializing the random number generator";
  }

  /**
   * Sets the maximum number of networks to generate.
   *
   * @param value	the maximum
   */
  public void setNumNetworks(int value) {
    if (getOptionManager().isValid("numNetworks", value)) {
      m_NumNetworks = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of networks to generate.
   *
   * @return  		the maximum
   */
  public int getNumNetworks() {
    return m_NumNetworks;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numNetworksTipText() {
    return "The maximum number of networks to generate (duplicate networks will still get removed afterwards).";
  }

  /**
   * Sets the list of number of layers.
   *
   * @param value	the list
   */
  public void setNumLayers(BaseInteger[] value) {
    m_NumLayers = value;
    reset();
  }

  /**
   * Returns the list of number of layers.
   *
   * @return  		the list
   */
  public BaseInteger[] getNumLayers() {
    return m_NumLayers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numLayersTipText() {
    return "The list of layers to choose from.";
  }

  /**
   * Sets the list of node counts to choose from.
   *
   * @param value	the list
   */
  public void setNumNodes(BaseInteger[] value) {
    m_NumNodes = value;
    reset();
  }

  /**
   * Returns the list of node counts to choose from.
   *
   * @return  		the list
   */
  public BaseInteger[] getNumNodes() {
    return m_NumNodes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numNodesTipText() {
    return "The list of node counts to choose from.";
  }

  /**
   * Sets the list of learning rate values to choose from.
   *
   * @param value	the list
   */
  public void setLearningRate(BaseDouble[] value) {
    m_LearningRate = value;
    reset();
  }

  /**
   * Returns the list of learning rate values to choose from.
   *
   * @return  		the list
   */
  public BaseDouble[] getLearningRate() {
    return m_LearningRate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String learningRateTipText() {
    return "The list of learning rate values to choose from.";
  }

  /**
   * Sets the list of regularization flags to choose from.
   *
   * @param value	the list
   */
  public void setUseRegularization(BaseBoolean[] value) {
    m_UseRegularization = value;
    reset();
  }

  /**
   * Returns the list of regularization flags to choose from.
   *
   * @return  		the list
   */
  public BaseBoolean[] getUseRegularization() {
    return m_UseRegularization;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useRegularizationTipText() {
    return "The list of regularization flags to choose from.";
  }

  /**
   * Sets the list of L1 values to choose from.
   *
   * @param value	the list
   */
  public void setL1(BaseDouble[] value) {
    m_L1 = value;
    reset();
  }

  /**
   * Returns the list of L1 values to choose from.
   *
   * @return  		the list
   */
  public BaseDouble[] getL1() {
    return m_L1;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String L1TipText() {
    return "The list of L1 values to choose from.";
  }

  /**
   * Sets the list of L2 values to choose from.
   *
   * @param value	the list
   */
  public void setL2(BaseDouble[] value) {
    m_L2 = value;
    reset();
  }

  /**
   * Returns the list of L2 values to choose from.
   *
   * @return  		the list
   */
  public BaseDouble[] getL2() {
    return m_L2;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String L2TipText() {
    return "The list of L2 values to choose from.";
  }

  /**
   * Sets the list of drop out values to choose from.
   *
   * @param value	the list
   */
  public void setDropOut(BaseDouble[] value) {
    m_DropOut = value;
    reset();
  }

  /**
   * Returns the list of drop out values to choose from.
   *
   * @return  		the list
   */
  public BaseDouble[] getDropOut() {
    return m_DropOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dropOutTipText() {
    return "The list of drop out values to choose from.";
  }

  /**
   * Sets the epochs to use for the learning rate schedule; not used if empty.
   *
   * @param value	the list
   */
  public void setLearningRateScheduleEpochs(BaseInteger[] value) {
    m_LearningRateScheduleEpochs = value;
    reset();
  }

  /**
   * Returns the list of epochs to use for the learning rate schedule; not used if empty.
   *
   * @return  		the list
   */
  public BaseInteger[] getLearningRateScheduleEpochs() {
    return m_LearningRateScheduleEpochs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String learningRateScheduleEpochsTipText() {
    return "The list of epochs for the learning rate schedule; requires learning rate to be specified (for initial value); not used if empty.";
  }

  /**
   * Sets the divisors to use for the learning rate schedule; not used if empty.
   *
   * @param value	the list
   */
  public void setLearningRateScheduleDivisors(BaseDouble[] value) {
    m_LearningRateScheduleDivisors = value;
    reset();
  }

  /**
   * Returns the list of divisors to use for the learning rate schedule; not used if empty.
   *
   * @return  		the list
   */
  public BaseDouble[] getLearningRateScheduleDivisors() {
    return m_LearningRateScheduleDivisors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String learningRateScheduleDivisorsTipText() {
    return
      "The list of divisors for calculating learning rate used in the learning "
	+ "rate schedule to choose from; each subsequent value is calculated by "
	+ "dividing the previous one by the chosen divisor.";
  }

  /**
   * Sets whether to insert batchnorm layers after each layer.
   *
   * @param value	true if to insert
   */
  public void setInsertBatchNormLayers(boolean value) {
    m_InsertBatchNormLayers = value;
    reset();
  }

  /**
   * Returns whether to insert batchnorm layers after each layer.
   *
   * @return  		true if to insert
   */
  public boolean getInsertBatchNormLayers() {
    return m_InsertBatchNormLayers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String insertBatchNormLayersTipText() {
    return "If enabled, batchnorm layers get inserted after each layer (except output layer).";
  }

  /**
   * Picks an array element randomly if more than one element, otherwise
   * just returns the single element.
   *
   * @param rand	the random number generator to use for picking the element
   * @param array	the array to pick the element from
   * @return		the chosen element
   */
  protected Object pick(Random rand, Object array) {
    if (Array.getLength(array) == 1)
      return Array.get(array, 0);
    else
      return Array.get(array, rand.nextInt(Array.getLength(array)));
  }

  /**
   * Generates the actual models.
   *
   * @param numInput	the number of input nodes
   * @param numOutput	the number of output nodes
   * @return		the models
   */
  @Override
  protected List<String> doGenerate(int numInput, int numOutput) {
    List<String>	result;
    Set<String> 	generated;
    Random 		rand;
    int			i;
    int			n;
    int			numLayers;
    String		conf;
    StringBuilder	model;
    String		layer;
    List<String>	layers;
    List<String>	names;
    double		lr;
    int			priorEpoch;
    List<String>	lrSchedule;

    result    = new ArrayList<>();
    rand      = new Random(m_Seed);

    while (result.size() < m_NumNetworks) {
      conf = m_DefaultNetwork.getValue();

      // regularization
      if (m_UseRegularization.length > 0) {
	if (((BaseBoolean) pick(rand, m_UseRegularization)).booleanValue()) {
	  if (m_L1.length > 0)
	    conf = BrainScriptHelper.addParam(conf, "  L1RegWeight = " + pick(rand, m_L1), true);
	  if (m_L2.length > 0)
	    conf = BrainScriptHelper.addParam(conf, "  L2RegWeight = " + pick(rand, m_L2), true);
	}
      }

      // drop-type/-out
      if (m_DropOut.length > 0)
	conf = BrainScriptHelper.addParam(conf, "  dropoutRate = " + pick(rand, m_DropOut), true);

      // learning rates?
      if (m_LearningRate.length > 0) {
	// learning rates schedule?
	if (m_LearningRateScheduleEpochs.length > 0) {
	  lr         = ((BaseDouble) pick(rand, m_LearningRate)).doubleValue();
	  priorEpoch = 0;
	  lrSchedule = new ArrayList<>();
	  for (n = 0; n < m_LearningRateScheduleEpochs.length; n++) {
	    lr /= ((BaseDouble) pick(rand, m_LearningRateScheduleDivisors)).doubleValue();
	    if (n < m_LearningRateScheduleEpochs.length - 1)
	      lrSchedule.add(lr + "*" + (m_LearningRateScheduleEpochs[n].intValue() - priorEpoch));
	    else
	      lrSchedule.add("" + lr);
	    priorEpoch = m_LearningRateScheduleEpochs[n].intValue();
	  }
	  conf = BrainScriptHelper.addParam(conf, "  learningRatesPerSample = " + Utils.flatten(lrSchedule, ":"), true);
	}
	else {
	  conf = BrainScriptHelper.addParam(conf, "  learningRatesPerSample = " + pick(rand, m_LearningRate), true);
	}
      }

      // layers
      layers    = new ArrayList<>();
      names     = new ArrayList<>();
      numLayers = ((BaseInteger) pick(rand, m_NumLayers)).intValue();
      if (isLoggingEnabled())
        getLogger().info("# layers: " + numLayers);
      for (i = 0; i < numLayers; i++) {
        if (i == numLayers - 1) {
          layer = m_DefaultOutputLayer.getValue();
          names.add("ol");
        }
        else {
          layer = m_DefaultDenseLayer.getValue();
          names.add("dl" + i);
          if (i == 0)
	    layer = BrainScriptHelper.setOutDim(layer, INPUT_DIM);
          else
	    layer = BrainScriptHelper.setOutDim(layer, ((BaseInteger) pick(rand, m_NumNodes)).intValue());
        }

	layers.add(layer);
	if (m_InsertBatchNormLayers && (i < numLayers - 1)) {
	  layers.add("BatchNormalizationLayer");
	  names.add("bnl" + i);
	}
      }

      model = new StringBuilder();
      model.append("BrainScriptNetworkBuilder = {\n");
      model.append("  ").append(INPUT_DIM).append(" = ").append("" + numInput).append("\n");
      model.append("  ").append(OUTPUT_DIM).append(" = ").append("" + numOutput).append("\n");
      model.append("\n");
      model.append("  ").append("model (features) {\n");
      for (n = 0; n < layers.size(); n++) {
        model.append("    ").append(names.get(n)).append(" = ").append(layers.get(n));
        if (n == 0)
          model.append(" (features)");
        else
          model.append(" (").append(names.get(n-1)).append(")");
        model.append("\n");
      }
      model.append("  ").append("}.ol\n");
      model.append("\n");
      model.append("  # inputs\n");
      model.append("  features = Input {inputDim}\n");
      model.append("  labels = Input {labels}\n");
      model.append("  \n");
      model.append("  # apply model to outputs\n");
      model.append("  ol = model (features)\n");
      model.append("  \n");
      model.append("  # define regression loss\n");
      model.append("  diff = labels - ol\n");
      model.append("  sqerr = ReduceSum (diff.*diff, axis=1)\n");
      model.append("  rmse = Sqrt (sqerr)\n");
      model.append("  \n");
      model.append("  # declare special nodes\n");
      model.append("  featureNodes    = (features)\n");
      model.append("  labelNodes      = (labels)\n");
      model.append("  criterionNodes  = (rmse)\n");
      model.append("  evaluationNodes = (rmse)\n");
      model.append("  outputNodes     = (ol)\n");
      model.append("\n");
      model.append("}\n");
      model.append("\n");
      model.append(conf);

      result.add(model.toString());
    }

    // remove duplicate networks
    generated = new HashSet<>();
    i         = 0;
    while (i < result.size()) {
      if (generated.contains(result.get(i))) {
        result.remove(i);
      }
      else {
        generated.add(result.get(i));
        i++;
      }
    }

    return result;
  }
}
