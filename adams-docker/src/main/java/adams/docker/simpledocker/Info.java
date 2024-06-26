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
 * Info.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.QuickInfoHelper;

import java.util.List;

/**
 * Outputs information about the docker environment ('docker info').
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Info
  extends AbstractDockerCommand {

  private static final long serialVersionUID = 34785283711877518L;

  /** for custom formatting. */
  protected String m_FormatString;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs information about the docker environment ('docker info').\n"
      + "For more information see:\n"
      + "https://docs.docker.com/engine/reference/commandline/info/";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "format-string", "formatString",
      "");
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
    result += QuickInfoHelper.toString(this, "formatString", m_FormatString, ", format: ");

    return result;
  }

  /**
   * Sets the format string for the output.
   *
   * @param value	the format
   */
  public void setFormatString(String value) {
    m_FormatString = value;
    reset();
  }

  /**
   * Returns the format string for the output.
   *
   * @return		the format
   */
  public String getFormatString() {
    return m_FormatString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatStringTipText() {
    return "The custom Go template format string for the output, ignored if empty.";
  }

  /**
   * Assembles the command to run.
   *
   * @return		the command
   */
  @Override
  protected List<String> buildCommand() {
    List<String> result;

    result = super.buildCommand();
    result.add("info");
    if (!m_FormatString.isEmpty()) {
      result.add("-f");
      result.add(m_FormatString);
    }

    return result;
  }
}
