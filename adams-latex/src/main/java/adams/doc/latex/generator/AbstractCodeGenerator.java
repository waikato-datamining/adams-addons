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
 * AbstractCodeGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

import adams.core.AdditionalInformationHandler;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.conversion.EscapeLatexCharacters;
import adams.data.conversion.EscapeLatexCharacters.Characters;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;

/**
 * Ancestor for LaTeX code generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCodeGenerator
  extends AbstractOptionHandler
  implements FlowContextHandler, QuickInfoSupporter, AdditionalInformationHandler {

  private static final long serialVersionUID = -590133419718559795L;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** whether to skip the transformation and just forward the token. */
  protected boolean m_Skip;

  /** whether to not expand variables. */
  protected boolean m_NoVariableExpansion;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "skip", "skip",
      false);

    m_OptionManager.add(
      "no-var-expansion", "noVariableExpansion",
      false);
  }

  /**
   * Sets whether the generator is skipped.
   *
   * @param value 	true if generation is to be skipped
   */
  public void setSkip(boolean value) {
    m_Skip = value;
    reset();
  }

  /**
   * Returns whether the generator is skipped.
   *
   * @return 		true if generation is skipped
   */
  public boolean getSkip() {
    return m_Skip;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipTipText() {
    return "If enabled, the code generation gets skipped.";
  }

  /**
   * Sets whether to skip variable expansion.
   *
   * @param value	true if to skip
   */
  public void setNoVariableExpansion(boolean value) {
    m_NoVariableExpansion = value;
    reset();
  }

  /**
   * Returns whether to skip variable expansion.
   *
   * @return		true if to skip
   */
  public boolean getNoVariableExpansion() {
    return m_NoVariableExpansion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noVariableExpansionTipText() {
    return "If enabled, variable expansion gets skipped.";
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the list of required LaTeX packages for this code generator.
   *
   * @return		the packages
   */
  public abstract String[] getRequiredPackages();

  /**
   * Returns the additional information.
   *
   * @return		the additional information, null or 0-length string for no information
   */
  public String getAdditionalInformation() {
    StringBuilder	result;
    String[]		packages;

    packages = getRequiredPackages();
    if (packages.length == 0)
      return null;

    result = new StringBuilder();
    result.append("Required package(s):\n");
    for (String pkg: packages)
      result.append("\\usepackage{" + pkg + "}\n");

    return result.toString();
  }

  /**
   * Hook method for performing checks.
   * <br>
   * Will raise an {@link IllegalStateException} if check fails.
   */
  protected void check() {
    if ((m_FlowContext == null) && !m_NoVariableExpansion)
      throw new IllegalStateException("No flow context set!");
  }

  /**
   * Escapes all characters.
   *
   * @param s		the string to escape
   * @return		the escaped string
   */
  protected String escape(String s) {
    EscapeLatexCharacters	conv;

    conv = new EscapeLatexCharacters();
    conv.setCharacters(Characters.values());
    conv.setInput(s);
    conv.convert();

    return (String) conv.getOutput();
  }

  /**
   * Expands the variables in the string, unless it is skipped.
   *
   * @param s		the string to expand
   * @return		the (potentially) expanded string
   * @see		#getNoVariableExpansion()
   */
  protected String expand(String s) {
    if (getNoVariableExpansion())
      return s;
    if (m_FlowContext == null)
      return s;
    return m_FlowContext.getVariables().expand(s);
  }

  /**
   * Expands and then escapes the string.
   *
   * @param s		the string to expand/escaped
   * @return		the modified string
   */
  protected String expandEscape(String s) {
    return escape(expand(s));
  }

  /**
   * Ensures that a trailing newline is present.
   *
   * @param s		the string to check/modify
   * @return		the (potentially) appended string
   */
  protected String ensureTrailingNewLine(String s) {
    if (s.charAt(s.length() - 1) != '\n')
      s = s + "\n";
    return s;
  }

  /**
   * Ensures that a trailing newline is present.
   *
   * @param s		the StringBuilder to check/modify
   */
  protected void ensureTrailingNewLine(StringBuilder s) {
    if (s.charAt(s.length() - 1) != '\n')
      s.append("\n");
  }

  /**
   * Generates the actual code.
   *
   * @return		the generated code
   */
  protected abstract String doGenerate();

  /**
   * Generates the code.
   *
   * @return		the generated code
   */
  public String generate() {
    if (getSkip())
      return "";

    check();
    return doGenerate();
  }
}
