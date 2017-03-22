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
 * Image.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Inserts the specified image.
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
 * <pre>-path-type &lt;ABSOLUTE|BASENAME|SUPPLIED_DIR&gt; (property: pathType)
 * &nbsp;&nbsp;&nbsp;Determines how to process the file name.
 * &nbsp;&nbsp;&nbsp;default: ABSOLUTE
 * </pre>
 * 
 * <pre>-supplied-dir &lt;java.lang.String&gt; (property: suppliedDir)
 * &nbsp;&nbsp;&nbsp;The directory name to use instead.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-remove-extension &lt;boolean&gt; (property: removeExtension)
 * &nbsp;&nbsp;&nbsp;If enabled, removes the extension from the filename.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-image &lt;adams.core.io.PlaceholderFile&gt; (property: image)
 * &nbsp;&nbsp;&nbsp;The image to insert.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-options &lt;java.lang.String&gt; (property: options)
 * &nbsp;&nbsp;&nbsp;The options (if any) for the image.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Image
  extends AbstractFileReferencingCodeGenerator {

  private static final long serialVersionUID = 101642148012049382L;

  /** the image to insert. */
  protected PlaceholderFile m_Image;

  /** optional parameters for the image. */
  protected String m_Options;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Inserts the specified image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image", "image",
      new PlaceholderFile());

    m_OptionManager.add(
      "options", "options",
      "");
  }

  /**
   * Sets the image to insert.
   *
   * @param value	the image
   */
  public void setImage(PlaceholderFile value) {
    m_Image = value;
    reset();
  }

  /**
   * Returns the image to insert.
   *
   * @return		the image
   */
  public PlaceholderFile getImage() {
    return m_Image;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageTipText() {
    return "The image to insert.";
  }

  /**
   * Sets the options to use for the image.
   *
   * @param value	the options
   */
  public void setOptions(String value) {
    m_Options = value;
    reset();
  }

  /**
   * Returns the options to use for the image.
   *
   * @return		the options
   */
  public String getOptions() {
    return m_Options;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionsTipText() {
    return "The options (if any) for the image.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "image", m_Image, "image: ");
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
    result.append("\\includegraphics");
    if (!m_Options.isEmpty())
      result.append("[").append(m_Options).append("]");
    result.append("{").append(processFile(m_Image)).append("}\n");

    return result.toString();
  }
}
