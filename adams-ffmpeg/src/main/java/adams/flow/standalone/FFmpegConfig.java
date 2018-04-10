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
 * FFmpegConfig.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.management.OS;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FFmpegConfig
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -7106585852803101639L;

  /** the ffmpeg executable. */
  protected PlaceholderFile m_Executable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines the ffmpeg/avconv executable.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "executable", "executable",
      getDefaultExecutable());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "executable", m_Executable, ", executable: ");

    return result;
  }
  
  /**
   * Returns the default executable.
   * 
   * @return		the default
   */
  protected PlaceholderFile getDefaultExecutable() {
    // TODO search PATH?
    if (OS.isWindows()) {
      return new PlaceholderFile("ffmpeg.exe");
    }
    else {
      if (FileUtils.fileExists("/usr/bin/avconv"))
        return new PlaceholderFile("/usr/bin/avconv");
      else
        return new PlaceholderFile("/usr/bin/ffmpeg");
    }
  }

  /**
   * Sets the ffmpeg executable.
   *
   * @param value	the executable
   */
  public void setExecutable(PlaceholderFile value) {
    m_Executable = value;
    reset();
  }

  /**
   * Returns the ffmpeg executable.
   *
   * @return		the executable
   */
  public PlaceholderFile getExecutable() {
    return m_Executable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String executableTipText() {
    return "The avconv/ffmpeg executable.";
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    
    result = super.setUp();
    
    if (result == null) {
      if (m_Executable.isDirectory())
	result = "Executable points to a directory: " + m_Executable;
      else if (!m_Executable.exists())
	result = "Executable does not exist: " + m_Executable;
    }
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    return null;
  }
}
