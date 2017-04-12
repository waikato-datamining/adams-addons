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
 * CodeGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

import adams.core.AdditionalInformationHandler;
import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;

/**
 * Interface for LaTeX code generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface CodeGenerator
  extends OptionHandler, FlowContextHandler, QuickInfoSupporter, AdditionalInformationHandler {

  /**
   * Sets whether the generator is skipped.
   *
   * @param value 	true if generation is to be skipped
   */
  public void setSkip(boolean value);

  /**
   * Returns whether the generator is skipped.
   *
   * @return 		true if generation is skipped
   */
  public boolean getSkip();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipTipText();

  /**
   * Sets whether to skip variable expansion.
   *
   * @param value	true if to skip
   */
  public void setNoVariableExpansion(boolean value);

  /**
   * Returns whether to skip variable expansion.
   *
   * @return		true if to skip
   */
  public boolean getNoVariableExpansion();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noVariableExpansionTipText();

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value);

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext();

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo();

  /**
   * Returns the list of required LaTeX packages for this code generator.
   *
   * @return		the packages
   */
  public String[] getRequiredPackages();

  /**
   * Returns the additional information.
   *
   * @return		the additional information, null or 0-length string for no information
   */
  public String getAdditionalInformation();

  /**
   * Generates the code.
   *
   * @return		the generated code
   */
  public String generate();
}
