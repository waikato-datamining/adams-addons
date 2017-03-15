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
 * BlockSize.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Inserts the code from the base generator inside a block with the specified font size.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-var-expansion &lt;boolean&gt; (property: noVariableExpansion)
 * &nbsp;&nbsp;&nbsp;If enabled, variable expansion gets skipped.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-generator &lt;adams.doc.latex.generator.AbstractCodeGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;Generates the code for the differently sized block.
 * &nbsp;&nbsp;&nbsp;default: adams.doc.latex.generator.Verbatim
 * </pre>
 * 
 * <pre>-font-size &lt;tiny|scriptsize|footnotesize|small|normalsize|large|Large|LARGE|huge|Huge&gt; (property: fontSize)
 * &nbsp;&nbsp;&nbsp;The font size to use for the encapsulated block.
 * &nbsp;&nbsp;&nbsp;default: normalsize
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BlockSize
  extends AbstractMetaCodeGenerator {

  private static final long serialVersionUID = -2504232052630130162L;

  /**
   * The available size.
   */
  public enum FontSize {
    tiny,
    scriptsize,
    footnotesize,
    small,
    normalsize,
    large,
    Large,
    LARGE,
    huge,
    Huge,
  }

  /** the font size. */
  protected FontSize m_FontSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Inserts the code from the base generator inside a block with the specified font size.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "font-size", "fontSize",
      FontSize.normalsize);
  }

  /**
   * Returns the default code generator to use.
   *
   * @return		the default
   */
  protected AbstractCodeGenerator getDefaultGenerator() {
    return new Verbatim();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String generatorTipText() {
    return "Generates the code for the differently sized block.";
  }

  /**
   * Sets the font size for the block.
   *
   * @param value	the size
   */
  public void setFontSize(FontSize value) {
    m_FontSize = value;
    reset();
  }

  /**
   * Returns the font size for the block.
   *
   * @return		the size
   */
  public FontSize getFontSize() {
    return m_FontSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontSizeTipText() {
    return "The font size to use for the encapsulated block.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "fontSize", m_FontSize, "size: ");
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
    result.append("{\\").append(m_FontSize.toString()).append("\n");
    result.append(m_Generator.generate());
    ensureTrailingNewLine(result);
    result.append("}\n");

    return result.toString();
  }
}
