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
 * ListRequiredLatexPackages.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.processor;

import adams.core.option.AbstractOption;
import adams.core.option.OptionTraversalPath;
import adams.doc.latex.generator.CodeGenerator;

/**
 <!-- globalinfo-start -->
 * Generates a list of all the packages required by the adams.doc.latex.generator.CodeGenerator-derived LaTeX generators.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListRequiredLatexPackages
  extends AbstractListingProcessor {

  private static final long serialVersionUID = -6340700367008421185L;

  /** whether to generate latex code. */
  protected boolean m_GenerateUsePackageStatements;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a list of all the packages required by the " + CodeGenerator.class.getName() + "-derived LaTeX generators.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generate-usepackage-statements", "generateUsePackageStatements",
      false);
  }

  /**
   * Sets whether to generate latex code.
   *
   * @param value 	true if to generate latex
   */
  public void setGenerateUsePackageStatements(boolean value) {
    m_GenerateUsePackageStatements = value;
    reset();
  }

  /**
   * Returns whether to generate latex code.
   *
   * @return 		true if to generate latex
   */
  public boolean getGenerateUsePackageStatements() {
    return m_GenerateUsePackageStatements;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generateUsePackageStatementsTipText() {
    return "If enabled, generates 'usepackage' LaTeX statements.";
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Required LaTeX packages";
  }

  /**
   * Checks whether the object is valid and should be added to the list.
   *
   * @param option	the current option
   * @param obj		the object to check
   * @param path	the traversal path of properties
   * @return		true if valid
   */
  protected boolean isValid(AbstractOption option, Object obj, OptionTraversalPath path) {
    return (obj instanceof CodeGenerator);
  }

  /**
   * Returns the string representation of the object that is added to the list.
   *
   * @param option	the current option
   * @param obj		the object to turn into a string
   * @param path	the traversal path of properties
   * @return		the string representation, null if to ignore the item
   */
  protected String[] objectToStrings(AbstractOption option, Object obj, OptionTraversalPath path) {
    String[]	result;
    int		i;

    result = ((CodeGenerator) obj).getRequiredPackages();
    if (m_GenerateUsePackageStatements) {
      for (i = 0; i < result.length; i++)
	result[i] = "\\usepackage{" + result[i] + "}";
    }

    return result;
  }

  /**
   * Returns whether the list generates a string array per object or not.
   *
   * @return		true if multiple items get generated
   * @see		#objectToString(AbstractOption, Object, OptionTraversalPath)
   * @see		#objectToStrings(AbstractOption, Object, OptionTraversalPath)
   */
  protected boolean generatesMultipleItems() {
    return true;
  }

  /**
   * Returns whether the list should be sorted.
   *
   * @return		true if the list should get sorted
   */
  @Override
  protected boolean isSortedList() {
    return true;
  }

  /**
   * Returns whether the list should not contain any duplicates.
   *
   * @return		true if the list contains no duplicates
   */
  @Override
  protected boolean isUniqueList() {
    return true;
  }

  /**
   * Returns the header to use in the dialog, i.e., the one-liner that
   * explains the output.
   *
   * @return		the header, null if no header available
   */
  @Override
  protected String getHeader() {
    return "Required LaTeX packages:";
  }
}
