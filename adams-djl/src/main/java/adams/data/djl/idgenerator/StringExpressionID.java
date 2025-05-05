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
 * StringExpressionID.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.djl.idgenerator;

import adams.parser.StringExpressionText;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * Expands any variables in the supplied string expression, evaluates it and returns the result as ID.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class StringExpressionID
  extends AbstractIDGenerator {

  private static final long serialVersionUID = -8875224901669801709L;

  /** the expression to use as ID. */
  protected StringExpressionText m_ID;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Expands any variables in the supplied string expression, evaluates it and returns the result as ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "id", "ID",
      new StringExpressionText("djl"));
  }

  /**
   * Sets the ID.
   *
   * @param value 	the ID
   */
  public void setID(StringExpressionText value) {
    m_ID = value;
    reset();
  }

  /**
   * Gets the ID.
   *
   * @return 		the ID
   */
  public StringExpressionText getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String IDTipText() {
    return "The string expression for generating the ID to use, can contain variables.";
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
    String	exp;
    String 	result;

    exp = m_FlowContext.getVariables().expand(m_ID.getValue());
    try {
      result = adams.parser.StringExpression.evaluate(exp, new HashMap());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to evaluate string expression: " + exp, e);
      result = exp;
    }
    return result;
  }
}
