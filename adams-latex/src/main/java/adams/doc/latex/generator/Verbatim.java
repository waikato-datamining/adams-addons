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
 * Verbatim.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

import adams.core.base.BaseText;

/**
 <!-- globalinfo-start -->
 * Outputs the specified statements in a verbatim block. Variables get expanded.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If enabled, the code generation gets skipped.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-no-var-expansion &lt;boolean&gt; (property: noVariableExpansion)
 * &nbsp;&nbsp;&nbsp;If enabled, variable expansion gets skipped.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-suppress-trailing-space &lt;boolean&gt; (property: suppressTrailingSpace)
 * &nbsp;&nbsp;&nbsp;If enabled, adds a '%' to suppress the trailing space.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-statements &lt;adams.core.base.BaseText&gt; (property: statements)
 * &nbsp;&nbsp;&nbsp;The statements to insert as verbatim block.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Verbatim
  extends AbstractEnvironmentWithNoTrailingSpace {

  private static final long serialVersionUID = 7225514457280622837L;

  /** the statements to add in verbatim. */
  protected BaseText m_Statements;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the specified statements in a verbatim block. Variables get expanded.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "statements", "statements",
      new BaseText());
  }

  /**
   * Sets the statements to insert as verbatim.
   *
   * @param value	the statements
   */
  public void setStatements(BaseText value) {
    m_Statements = value;
    reset();
  }

  /**
   * Returns the statements to insert as verbatim.
   *
   * @return		the statements
   */
  public BaseText getStatements() {
    return m_Statements;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statementsTipText() {
    return "The statements to insert as verbatim block.";
  }

  /**
   * Returns the list of required LaTeX packages for this code generator.
   *
   * @return		the packages
   */
  public String[] getRequiredPackages() {
    return new String[0];
  }

  /**
   * Generates the actual code.
   *
   * @return		the generated code
   */
  @Override
  protected String doGenerate() {
    StringBuilder	result;

    result = new StringBuilder();
    result.append("\\begin{verbatim}\n");
    result.append(expand(m_Statements.getValue()));
    ensureTrailingNewLine(result);
    result.append("\\end{verbatim}");
    if (m_SuppressTrailingSpace)
      result.append("%");
    result.append("\n");

    return result.toString();
  }
}
