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
 * DL4JYamlToModel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.ml.dl4j.model.ModelType;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

/**
 <!-- globalinfo-start -->
 * Converts a YAML string into a deeplearning4j model.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-type &lt;MULTI_LAYER_NETWORK|COMPUTATION_GRAPH&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of model to instantiate.
 * &nbsp;&nbsp;&nbsp;default: MULTI_LAYER_NETWORK
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JYamlToModel
  extends AbstractConversion {

  private static final long serialVersionUID = -4013081305335693154L;

  /** the model type. */
  protected ModelType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a YAML string into a deeplearning4j model.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      ModelType.MULTI_LAYER_NETWORK);
  }

  /**
   * Sets the model type to instantiate.
   *
   * @param value	the type
   */
  public void setType(ModelType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the model type to instantiate.
   *
   * @return 		the type
   */
  public ModelType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of model to instantiate.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return String.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    switch (m_Type) {
      case MULTI_LAYER_NETWORK:
	return MultiLayerNetwork.class;
      case COMPUTATION_GRAPH:
	return ComputationGraph.class;
      default:
	throw new IllegalStateException("Unhanded model type: " + m_Type);
    }
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    String	yaml;

    yaml = (String) m_Input;

    switch (m_Type) {
      case MULTI_LAYER_NETWORK:
	return new MultiLayerNetwork(MultiLayerConfiguration.fromYaml(yaml));
      case COMPUTATION_GRAPH:
	return new ComputationGraph(ComputationGraphConfiguration.fromYaml(yaml));
      default:
	throw new IllegalStateException("Unhanded model type: " + m_Type);
    }
  }
}
