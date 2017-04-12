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
 * MultiCol.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Inserts a 'multicol' environment, providing multiple columns and an optional preface across the columns.
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
 * &nbsp;&nbsp;&nbsp;Generates the code for the columns.
 * &nbsp;&nbsp;&nbsp;default: adams.doc.latex.generator.CustomStatements
 * </pre>
 * 
 * <pre>-preface &lt;adams.doc.latex.generator.CodeGenerator&gt; (property: preface)
 * &nbsp;&nbsp;&nbsp;The optional code generator for the preface; use adams.doc.latex.generator.Dummy 
 * &nbsp;&nbsp;&nbsp;to skip.
 * &nbsp;&nbsp;&nbsp;default: adams.doc.latex.generator.Dummy
 * </pre>
 * 
 * <pre>-num-columns &lt;int&gt; (property: numColumns)
 * &nbsp;&nbsp;&nbsp;The number of columns to use.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiCol
  extends AbstractMetaCodeGenerator {

  private static final long serialVersionUID = -2504232052630130162L;

  /** optional preface code generator. */
  protected CodeGenerator m_Preface;

  /** the number of columns. */
  protected int m_NumColumns;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Inserts a 'multicol' environment, providing multiple columns and an optional preface across the columns.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "preface", "preface",
      new Dummy());

    m_OptionManager.add(
      "num-columns", "numColumns",
      2, 1, null);
  }

  /**
   * Returns the default code generator to use.
   *
   * @return		the default
   */
  protected CodeGenerator getDefaultGenerator() {
    return new CustomStatements();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String generatorTipText() {
    return "Generates the code for the columns.";
  }

  /**
   * Sets the optional preface code generator.
   *
   * @param value	the preface
   */
  public void setPreface(CodeGenerator value) {
    m_Preface = value;
    reset();
  }

  /**
   * Returns the preface code generator.
   *
   * @return		the preface
   */
  public CodeGenerator getPreface() {
    return m_Preface;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefaceTipText() {
    return "The optional code generator for the preface; use " + Dummy.class.getName() + " to skip.";
  }

  /**
   * Sets the number of columns to use.
   *
   * @param value	the columns
   */
  public void setNumColumns(int value) {
    if (getOptionManager().isValid("numColumns", value)) {
      m_NumColumns = value;
      reset();
    }
  }

  /**
   * Returns the number of columns to use.
   *
   * @return		the columns
   */
  public int getNumColumns() {
    return m_NumColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numColumnsTipText() {
    return "The number of columns to use.";
  }

  /**
   * Returns the list of required LaTeX packages for this code generator.
   *
   * @return		the packages
   */
  public String[] getRequiredPackages() {
    return new String[]{"wrapfig"};
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "numColumns", m_NumColumns, ", #cols: ");

    return result;
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
    result.append("\\begin{multicols}{").append("" + m_NumColumns).append("}\n");
    if (!(m_Preface instanceof Dummy)) {
      result.append("[\n");
      result.append(m_Preface.generate());
      result.append("]\n");
    }
    result.append(m_Generator.generate());
    ensureTrailingNewLine(result);
    result.append("\\end{multicols}\n");

    return result.toString();
  }
}
