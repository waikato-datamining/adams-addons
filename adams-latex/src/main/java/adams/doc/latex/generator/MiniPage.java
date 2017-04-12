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
 * MiniPage.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Inserts a minipage environment.
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
 * <pre>-generator &lt;adams.doc.latex.generator.CodeGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;Generates the code for the minipage.
 * &nbsp;&nbsp;&nbsp;default: adams.doc.latex.generator.Figure -generator adams.doc.latex.generator.Image
 * </pre>
 * 
 * <pre>-suppress-trailing-space &lt;boolean&gt; (property: suppressTrailingSpace)
 * &nbsp;&nbsp;&nbsp;If enabled, adds a '%' to suppress the trailing space.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-position &lt;java.lang.String&gt; (property: position)
 * &nbsp;&nbsp;&nbsp;The optional position parameter.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-width &lt;java.lang.String&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width for the minipage.
 * &nbsp;&nbsp;&nbsp;default: 0.5\\linewidth
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MiniPage
  extends AbstractMetaCodeGeneratorWithNoTrailingSpace {

  private static final long serialVersionUID = -2504232052630130162L;

  /** the optional position. */
  protected String m_Position;

  /** the width. */
  protected String m_Width;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Inserts a minipage environment.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "position", "position",
      "");

    m_OptionManager.add(
      "width", "width",
      "0.5\\linewidth");
  }

  /**
   * Returns the default code generator to use.
   *
   * @return		the default
   */
  protected CodeGenerator getDefaultGenerator() {
    return new Figure();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String generatorTipText() {
    return "Generates the code for the minipage.";
  }

  /**
   * Sets the (optional) position to use.
   *
   * @param value	the position
   */
  public void setPosition(String value) {
    m_Position = value;
    reset();
  }

  /**
   * Returns the (optional) position to use.
   *
   * @return		the position
   */
  public String getPosition() {
    return m_Position;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String positionTipText() {
    return "The optional position parameter.";
  }

  /**
   * Sets the width.
   *
   * @param value	the width
   */
  public void setWidth(String value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the width.
   *
   * @return		the width
   */
  public String getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width for the minipage.";
  }

  /**
   * Returns the list of required LaTeX packages for this code generator.
   *
   * @return		the packages
   */
  public String[] getRequiredPackages() {
    return new String[]{"minipage"};
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "width", (m_Width.isEmpty() ? "-missing-" : m_Width), ", width: ");

    return result;
  }

  /**
   * Hook method for performing checks.
   * <br>
   * Will raise an {@link IllegalStateException} if no width provided.
   */
  @Override
  protected void check() {
    super.check();

    if (m_Width.isEmpty())
      throw new IllegalStateException("No width provided!");
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
    result.append("\\begin{minipage}");
    if (!m_Position.isEmpty())
      result.append("[").append(m_Position).append("]");
    result.append("{").append(m_Width).append("}");
    result.append("\n");
    result.append("  ").append(m_Generator.generate());
    ensureTrailingNewLine(result);
    result.append("\\end{minipage}");
    if (m_SuppressTrailingSpace)
      result.append("%");
    result.append("\n");

    return result.toString();
  }
}
