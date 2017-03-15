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
 * MultiGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

/**
 <!-- globalinfo-start -->
 * Appends the output of the specified generators to the document.
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
 * <pre>-generator &lt;adams.doc.latex.generator.AbstractCodeGenerator&gt; [-generator ...] (property: generators)
 * &nbsp;&nbsp;&nbsp;The generators to use for appending the document.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiGenerator
  extends AbstractCodeGenerator {

  private static final long serialVersionUID = 7225514457280622837L;

  /** the generators to use. */
  protected AbstractCodeGenerator[] m_Generators;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Appends the output of the specified generators to the document.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generator", "generators",
      new AbstractCodeGenerator[0]);
  }

  /**
   * Sets the generators to use.
   *
   * @param value	the generators
   */
  public void setGenerators(AbstractCodeGenerator[] value) {
    m_Generators = value;
    reset();
  }

  /**
   * Returns the generators to use.
   *
   * @return		the generators
   */
  public AbstractCodeGenerator[] getGenerators() {
    return m_Generators;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorsTipText() {
    return "The generators to use for appending the document.";
  }

  /**
   * Generates the actual code.
   *
   * @return		the generated code
   */
  @Override
  protected String doGenerate() {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < m_Generators.length; i++) {
      m_Generators[i].setFlowContext(m_FlowContext);
      result.append(m_Generators[i].generate());
      ensureTrailingNewLine(result);
    }

    return result.toString();
  }
}
