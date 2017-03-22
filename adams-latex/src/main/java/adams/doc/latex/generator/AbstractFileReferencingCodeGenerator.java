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
 * AbstractFileReferencingCodeGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.doc.latex.generator;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

import java.io.File;

/**
 * Ancestor for code generators that deal with files and need to have control
 * over the filename being added to the LaTeX document.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFileReferencingCodeGenerator
  extends AbstractCodeGenerator {

  private static final long serialVersionUID = -2904853840565190465L;

  /**
   * Determines how to process the file path.
   */
  public enum PathType {
    ABSOLUTE,
    BASENAME,
    SUPPLIED_DIR,
  }

  /** how to process the file path. */
  protected PathType m_PathType;

  /** the supplied directory. */
  protected String m_SuppliedDir;

  /** whether to remove the extension. */
  protected boolean m_RemoveExtension;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "path-type", "pathType",
      getDefaultPathType());

    m_OptionManager.add(
      "supplied-dir", "suppliedDir",
      getDefaultSuppliedDir());

    m_OptionManager.add(
      "remove-extension", "removeExtension",
      getDefaultRemoveExtension());
  }

  /**
   * Returns the default path type to use.
   *
   * @return		the default
   */
  protected PathType getDefaultPathType() {
    return PathType.ABSOLUTE;
  }

  /**
   * Sets how to process the file name.
   *
   * @param value	the type
   */
  public void setPathType(PathType value) {
    m_PathType = value;
    reset();
  }

  /**
   * Returns how to process the file name.
   *
   * @return		the type
   */
  public PathType getPathType() {
    return m_PathType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pathTypeTipText() {
    return "Determines how to process the file name.";
  }

  /**
   * Returns the default for the supplied directory.
   *
   * @return		the default
   */
  protected String getDefaultSuppliedDir() {
    return "";
  }

  /**
   * Sets the directory to use instead.
   *
   * @param value	the directory
   */
  public void setSuppliedDir(String value) {
    m_SuppliedDir = value;
    reset();
  }

  /**
   * Returns the directory to use instead.
   *
   * @return		the directory
   */
  public String getSuppliedDir() {
    return m_SuppliedDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suppliedDirTipText() {
    return "The directory name to use instead.";
  }

  /**
   * Returns the default for removing the extension.
   *
   * @return		the default
   */
  protected boolean getDefaultRemoveExtension() {
    return false;
  }

  /**
   * Sets whether to remove the file extension.
   *
   * @param value	true if to remove
   */
  public void setRemoveExtension(boolean value) {
    m_RemoveExtension = value;
    reset();
  }

  /**
   * Returns whether to remove the file extension.
   *
   * @return		true if to remove
   */
  public boolean getRemoveExtension() {
    return m_RemoveExtension;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String removeExtensionTipText() {
    return "If enabled, removes the extension from the filename.";
  }

  /**
   * Processes the file according to the path type.
   *
   * @param file	the file to process
   * @return		the processed file name
   */
  protected String processFile(PlaceholderFile file) {
    String	result;

    switch (m_PathType) {
      case ABSOLUTE:
	result = file.getAbsolutePath();
	break;
      case BASENAME:
	result = file.getName();
	break;
      case SUPPLIED_DIR:
	result = m_SuppliedDir + (m_SuppliedDir.endsWith(File.separator) ? "" : File.separator) + file.getName();
	break;
      default:
	throw new IllegalStateException("Unhandled path type: " + m_PathType);
    }

    if (m_RemoveExtension)
      result = FileUtils.replaceExtension(result, "");

    return result;
  }
}
