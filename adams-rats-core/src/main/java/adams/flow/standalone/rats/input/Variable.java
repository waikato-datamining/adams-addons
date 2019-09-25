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
 * Variable.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.core.Variables;

/**
 <!-- globalinfo-start -->
 * Outputs the specified variable, if possible
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-variable-name &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The name of variable to output.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Variable
  extends AbstractRatInput {

  /** for serialization. */
  private static final long serialVersionUID = 6942772195383207110L;

  /** the variable name. */
  protected VariableName m_VariableName;

  /** the item obtained from the queue. */
  protected Object m_Output;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Outputs the specified variable, if possible.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "variable-name", "variableName",
      new VariableName());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Output = null;
  }

  /**
   * Sets the name of the variable.
   *
   * @param value	the name
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the name of the variable.
   *
   * @return		the name
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNameTipText() {
    return "The name of variable to output.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "variableName", m_VariableName, "variable: ");
  }

  /**
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    Variables 	variables;

    m_Output = null;
    variables = getOwner().getVariables();
    if (variables.has(m_VariableName.getValue()))
      m_Output = variables.get(m_VariableName.getValue());

    return null;
  }

  /**
   * Checks whether any output can be collected.
   * 
   * @return		true if output available
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Output != null);
  }

  /**
   * Returns the received data.
   * 
   * @return		the data
   */
  @Override
  public Object output() {
    Object	result;
    
    result   = m_Output;
    m_Output = null;
    
    return result;
  }
}
