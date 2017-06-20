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
 * DL4JConfiguratorToModel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.ml.dl4j.model.ModelConfigurator;
import org.deeplearning4j.nn.api.Model;

/**
 <!-- globalinfo-start -->
 * Generates a concrete model object from the incoming configurator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-input &lt;int&gt; (property: numInput)
 * &nbsp;&nbsp;&nbsp;The number of input nodes to use.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-num-output &lt;int&gt; (property: numOutput)
 * &nbsp;&nbsp;&nbsp;The number of output nodes to use.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JConfiguratorToModel
  extends AbstractConversion {

  private static final long serialVersionUID = -4013081305335693154L;

  /** the number of input nodes. */
  protected int m_NumInput;

  /** the number of output nodes. */
  protected int m_NumOutput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a concrete model object from the incoming configurator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-input", "numInput",
      1, 1, null);

    m_OptionManager.add(
      "num-output", "numOutput",
      1, 1, null);
  }

  /**
   * Sets the number of input nodes to use.
   *
   * @param value	the number of nodes
   */
  public void setNumInput(int value) {
    if (getOptionManager().isValid("numInput", value)) {
      m_NumInput = value;
      reset();
    }
  }

  /**
   * Returns the number of input nodes to use.
   *
   * @return 		the number of nodes
   */
  public int getNumInput() {
    return m_NumInput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String numInputTipText() {
    return "The number of input nodes to use.";
  }

  /**
   * Sets the number of output nodes to use.
   *
   * @param value	the number of nodes
   */
  public void setNumOutput(int value) {
    if (getOptionManager().isValid("numOutput", value)) {
      m_NumOutput = value;
      reset();
    }
  }

  /**
   * Returns the number of output nodes to use.
   *
   * @return 		the number of nodes
   */
  public int getNumOutput() {
    return m_NumOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String numOutputTipText() {
    return "The number of output nodes to use.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return ModelConfigurator.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Model.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    ModelConfigurator   conf;

    conf = (ModelConfigurator) m_Input;
    return conf.configureModel(m_NumInput, m_NumOutput);
  }
}
