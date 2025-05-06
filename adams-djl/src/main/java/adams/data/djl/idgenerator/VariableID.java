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
 * VariableID.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.djl.idgenerator;

import adams.core.base.BaseString;

/**
 * Expands any variables in the supplied string and returns that as ID.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class VariableID
  extends AbstractIDGenerator {

  private static final long serialVersionUID = -8875224901669801709L;

  /** the text to use as ID. */
  protected BaseString m_ID;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Expands any variables in the supplied string and returns that as ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "id", "ID",
      new BaseString("djl"));
  }

  /**
   * Sets the ID.
   *
   * @param value 	the ID
   */
  public void setID(BaseString value) {
    m_ID = value;
    reset();
  }

  /**
   * Gets the ID.
   *
   * @return 		the ID
   */
  public BaseString getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String IDTipText() {
    return "The ID to use, can contain variables.";
  }

  /**
   * Returns whether flow context is required.
   *
   * @return		true if required
   */
  @Override
  protected boolean requiresFlowContext() {
    return true;
  }

  /**
   * Generates the ID.
   *
   * @return the ID
   */
  @Override
  public String generate() {
    return m_FlowContext.getVariables().expand(m_ID.getValue());
  }
}
