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
 * ImportContent.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Imports the content of the specified file. By default, the content gets escaped to make it valid LaTeX.
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
 * <pre>-import &lt;adams.core.io.PlaceholderFile&gt; (property: import)
 * &nbsp;&nbsp;&nbsp;The file to import the content from.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-escape &lt;boolean&gt; (property: escape)
 * &nbsp;&nbsp;&nbsp;If enabled, the content of the import file get escaped to make it valid 
 * &nbsp;&nbsp;&nbsp;LaTeX.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImportContent
  extends AbstractCodeGenerator {

  private static final long serialVersionUID = 101642148012049382L;

  /** the file to import. */
  protected PlaceholderFile m_Import;

  /** whether to escape the content. */
  protected boolean m_Escape;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Imports the content of the specified file. By default, the content gets escaped to make it valid LaTeX.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "import", "import",
      new PlaceholderFile());

    m_OptionManager.add(
      "escape", "escape",
      true);
  }

  /**
   * Sets the file to import the content.
   *
   * @param value	the file
   */
  public void setImport(PlaceholderFile value) {
    m_Import = value;
    reset();
  }

  /**
   * Returns the file to import the content.
   *
   * @return		the file
   */
  public PlaceholderFile getImport() {
    return m_Import;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String importTipText() {
    return "The file to import the content from.";
  }

  /**
   * Sets whether to escape the content of the import file.
   *
   * @param value	true if to escape
   */
  public void setEscape(boolean value) {
    m_Escape = value;
    reset();
  }

  /**
   * Returns whether to escape the content of the import file.
   *
   * @return		true if to escape
   */
  public boolean getEscape() {
    return m_Escape;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String escapeTipText() {
    return "If enabled, the content of the import file get escaped to make it valid LaTeX.";
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
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "import", m_Import, "import: ");
    result += QuickInfoHelper.toString(this, "escape", (m_Escape ? "escaped" : "unescaped"));

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
    List<String> 	lines;

    result  = new StringBuilder();
    lines = FileUtils.loadFromFile(m_Import);
    for (String line: lines) {
      if (m_Escape)
	line = escape(line);
      result.append(line);
      result.append("\n");
    }

    return result.toString();
  }
}
