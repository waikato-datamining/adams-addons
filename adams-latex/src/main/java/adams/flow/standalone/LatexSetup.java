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

/*
 * LatexSetup.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.io.PlaceholderDirectory;
import adams.doc.latex.LatexHelper;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Allows to configure LaTex and override the system-wide settings.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: LatexSetup
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-binaries-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: binariesDir)
 * &nbsp;&nbsp;&nbsp;The directory containing the LaTeX binaries.
 * &nbsp;&nbsp;&nbsp;default: &#47;usr&#47;bin
 * </pre>
 * 
 * <pre>-executable &lt;java.lang.String&gt; (property: executable)
 * &nbsp;&nbsp;&nbsp;The executable to use (no path).
 * &nbsp;&nbsp;&nbsp;default: pdflatex
 * </pre>
 * 
 * <pre>-bibtex &lt;java.lang.String&gt; (property: bibtex)
 * &nbsp;&nbsp;&nbsp;The bibtex executable to use (no path).
 * &nbsp;&nbsp;&nbsp;default: bibtex
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LatexSetup
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -1959430342987913960L;

  /** the path to the binaries. */
  protected PlaceholderDirectory m_BinariesDir;

  /** the executable. */
  protected String m_Executable;

  /** the bibtex executable. */
  protected String m_Bibtex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to configure LaTex and override the system-wide settings.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "binaries-dir", "binariesDir",
      LatexHelper.getBinariesDir());

    m_OptionManager.add(
      "executable", "executable",
      LatexHelper.getExecutable());

    m_OptionManager.add(
      "bibtex", "bibtex",
      LatexHelper.getBibtex());
  }

  /**
   * Sets the binaries directory to use.
   *
   * @param value	the dir
   */
  public void setBinariesDir(PlaceholderDirectory value) {
    m_BinariesDir = value;
    reset();
  }

  /**
   * Returns the binaries directory to use.
   *
   * @return		the dir
   */
  public PlaceholderDirectory getBinariesDir() {
    return m_BinariesDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binariesDirTipText() {
    return "The directory containing the LaTeX binaries.";
  }

  /**
   * Sets the executable to use (no path).
   *
   * @param value	the executable
   */
  public void setExecutable(String value) {
    m_Executable = value;
    reset();
  }

  /**
   * Returns the executable to use (no path).
   *
   * @return		the executable
   */
  public String getExecutable() {
    return m_Executable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String executableTipText() {
    return "The executable to use (no path).";
  }

  /**
   * Sets the bibtex executable to use (no path).
   *
   * @param value	the executable
   */
  public void setBibtex(String value) {
    m_Bibtex = value;
    reset();
  }

  /**
   * Returns the bibtex executable to use (no path).
   *
   * @return		the executable
   */
  public String getBibtex() {
    return m_Bibtex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bibtexTipText() {
    return "The bibtex executable to use (no path).";
  }

  /**
   * Returns the full path of the executable.
   *
   * @param exec	the executable (no path)
   * @return		the full path
   */
  public String executablePath(String exec) {
    return m_BinariesDir.getAbsolutePath() + File.separator + exec;
  }

  /**
   * Returns the full path of the executable.
   *
   * @return		the full path
   */
  public String executablePath() {
    return executablePath(getExecutable());
  }

  /**
   * Returns the full path of the bibtex executable.
   *
   * @return		the full path
   */
  public String bibtexPath() {
    return executablePath(getBibtex());
  }

  /**
   * Executes the flow item.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    if (!getBinariesDir().exists())
      return "LaTeX directory for binaries does not exist: " + m_BinariesDir;

    return null;
  }
}
