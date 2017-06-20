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
 * RandomDl4jMlpClassifierNetworks.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.modelgenerator;

import adams.core.Randomizable;
import adams.core.base.BaseBoolean;
import adams.core.base.BaseDouble;
import adams.core.base.BaseInteger;
import adams.core.option.OptionUtils;
import adams.ml.dl4j.model.Dl4jMlpClassifier;
import adams.ml.dl4j.model.Dl4jMlpClassifier.DropType;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import weka.dl4j.layers.DenseLayer;
import weka.dl4j.layers.OutputLayer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Generates random {@link Dl4jMlpClassifier} networks.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Dl4jMlpClassifier
 */
public class RandomDl4jMlpClassifierNetworks
  extends AbstractModelGenerator
  implements Randomizable {

  private static final long serialVersionUID = -3934375675313822523L;

  /** the default network. */
  protected Dl4jMlpClassifier m_DefaultNetwork;

  /** the default dense layer. */
  protected DenseLayer m_DefaultDenseLayer;

  /** the default output layer. */
  protected OutputLayer m_DefaultOutputLayer;

  /** the seed value. */
  protected long m_Seed;

  /** the number of networks to generate. */
  protected int m_NumNetworks;

  /** the list of possible layers. */
  protected BaseInteger[] m_NumLayers;

  /** the list of possible nodes (per layer). */
  protected BaseInteger[] m_NumNodes;

  /** using regularization (per layer). */
  protected BaseBoolean[] m_UseRegularization;

  /** L1 values (per layer). */
  protected BaseDouble[] m_L1;

  /** L2 values (per layer). */
  protected BaseDouble[] m_L2;

  /** the drop type (per layer). */
  protected DropType[] m_DropType;

  /** the drop out (per layer). */
  protected BaseDouble[] m_DropOut;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates random " + Dl4jMlpClassifier.class.getName() + " networks.\n"
      + "Randomly selects items from the supplied parameter lists if more than item supplied.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "default-network", "defaultNetwork",
      new Dl4jMlpClassifier());

    m_OptionManager.add(
      "default-dense-layer", "defaultDenseLayer",
      new DenseLayer());

    m_OptionManager.add(
      "default-output-layer", "defaultOutputLayer",
      new OutputLayer());

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
      "use-regularization", "useRegularization",
      new BaseBoolean[0]);

    m_OptionManager.add(
      "l1", "L1",
      new BaseDouble[0]);

    m_OptionManager.add(
      "l2", "L2",
      new BaseDouble[0]);

    m_OptionManager.add(
      "drop-type", "dropType",
      new DropType[0]);

    m_OptionManager.add(
      "drop-out", "dropOut",
      new BaseDouble[0]);
  }

  /**
   * Sets the default network.
   *
   * @param value	the network
   */
  public void setDefaultNetwork(Dl4jMlpClassifier value) {
    m_DefaultNetwork = value;
    reset();
  }

  /**
   * Returns the default network.
   *
   * @return  		the network
   */
  public Dl4jMlpClassifier getDefaultNetwork() {
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
  public void setDefaultDenseLayer(DenseLayer value) {
    m_DefaultDenseLayer = value;
    reset();
  }

  /**
   * Returns the default dense layer.
   *
   * @return  		the dense layer
   */
  public DenseLayer getDefaultDenseLayer() {
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
  public void setDefaultOutputLayer(OutputLayer value) {
    m_DefaultOutputLayer = value;
    reset();
  }

  /**
   * Returns the default output layer.
   *
   * @return  		the output layer
   */
  public OutputLayer getDefaultOutputLayer() {
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
   * Sets the drop type values to choose from.
   *
   * @param value	the list
   */
  public void setDropType(DropType[] value) {
    m_DropType = value;
    reset();
  }

  /**
   * Returns the list of drop type values to choose from.
   *
   * @return  		the list
   */
  public DropType[] getDropType() {
    return m_DropType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dropTypeTipText() {
    return "The list of drop type values to choose from.";
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
   * Hook method for performing checks.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null) {
      if (m_NumLayers.length == 0)
	result = "List of layers is empty!";
      else if (m_NumNodes.length == 0)
	result = "List of nodes is empty!";
      else if (m_UseRegularization.length == 0)
	result = "List of regularization flags is empty!";
      else if (m_L1.length == 0)
	result = "List of L1 values is empty!";
      else if (m_L2.length == 0)
	result = "List of L2 values is empty!";
      else if (m_DropType.length == 0)
	result = "List of drop types is empty!";
      else if (m_DropOut.length == 0)
	result = "List of drop out values is empty!";
    }

    return result;
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
  protected List<Model> doGenerate(int numInput, int numOutput) {
    List<Model>		result;
    Set<String> 	generated;
    String		yaml;
    Random 		rand;
    int			i;
    Dl4jMlpClassifier	conf;
    Layer[]		layers;

    result    = new ArrayList<>();
    rand      = new Random(m_Seed);

    while (result.size() < m_NumNetworks) {
      conf = (Dl4jMlpClassifier) OptionUtils.shallowCopy(m_DefaultNetwork);
      conf.setUseRegularization(((BaseBoolean) pick(rand, m_UseRegularization)).booleanValue());
      conf.setL1(((BaseDouble) pick(rand, m_L1)).doubleValue());
      conf.setL2(((BaseDouble) pick(rand, m_L2)).doubleValue());
      conf.setDropType((DropType) pick(rand, m_DropType));
      conf.setDropOut(((BaseDouble) pick(rand, m_DropOut)).doubleValue());

      layers = new Layer[((BaseInteger) pick(rand, m_NumLayers)).intValue()];
      if (isLoggingEnabled())
	getLogger().info("# layers: " + layers.length);
      for (i = 0; i < layers.length; i++) {
	if (i == layers.length - 1) {
	  layers[i] = (OutputLayer) OptionUtils.shallowCopy(m_DefaultOutputLayer);
	  layers[i].setLayerName("output-" + i);
	}
	else {
	  layers[i] = (DenseLayer) OptionUtils.shallowCopy(m_DefaultDenseLayer);
	  layers[i].setLayerName("dense-" + i);
	  ((DenseLayer) layers[i]).setNOut(((BaseInteger) pick(rand, m_NumNodes)).intValue());
	}
	if (conf.getUseRegularization()) {
	  layers[i].setL1(((BaseDouble) pick(rand, m_L1)).doubleValue());
	  layers[i].setL2(((BaseDouble) pick(rand, m_L2)).doubleValue());
	}
      }
      conf.setLayers(layers);

      result.add(conf.configureModel(numInput, numOutput));
    }

    // remove duplicate networks
    generated = new HashSet<>();
    i         = 0;
    while (i < result.size()) {
      yaml = ((MultiLayerNetwork) result.get(i)).getLayerWiseConfigurations().toYaml();
      if (generated.contains(yaml)) {
	result.remove(i);
      }
      else {
	generated.add(yaml);
	i++;
      }
    }

    return result;
  }
}