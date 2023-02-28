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
 * RemoveContainers.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.QuickInfoHelper;

import java.util.Arrays;
import java.util.List;

/**
 * For removing containers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RemoveContainers
  extends AbstractDockerCommandWithOptions
  implements DockerCommandWithProgrammaticArguments {

  private static final long serialVersionUID = 34785283711877518L;

  /** whether to force the removal. */
  protected boolean m_Force;

  /** the additional arguments. */
  protected String[] m_AdditionalArguments;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For removing containers ('docker container stop').\n"
      + "For more information see:\n"
      + "https://docs.docker.com/engine/reference/commandline/container_rm/";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.insert(
      m_OptionManager.size() - 2,
      "force", "force",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "force", m_Force, (m_Force ? "force" : "normal"));
    result += ", " + super.getQuickInfo();

    return result;
  }

  /**
   * Sets whether to force the removal.
   *
   * @param value	true if to force
   */
  public void setForce(boolean value) {
    m_Force = value;
    reset();
  }

  /**
   * Returns whether to force the removal.
   *
   * @return		true if to force
   */
  public boolean getForce() {
    return m_Force;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forceTipText() {
    return "If enabled, the removal is forced (uses SIGKILL).";
  }

  /**
   * Sets the additional arguments to append to the command.
   *
   * @param value	the arguments
   */
  @Override
  public void setAdditionalArguments(String[] value) {
    m_AdditionalArguments = value;
  }

  /**
   * Returns the additional arguments to append to the command.
   *
   * @return	the arguments
   */
  @Override
  public String[] getAdditionalArguments() {
    return m_AdditionalArguments;
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
    result.add("container");
    result.add("rm");
    if (m_Force)
      result.add("--force");
    result.addAll(Arrays.asList(getActualOptions()));
    if (m_AdditionalArguments != null)
      result.addAll(Arrays.asList(m_AdditionalArguments));

    return result;
  }
}
