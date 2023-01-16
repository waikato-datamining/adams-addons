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
 * Run.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.QuickInfoHelper;
import adams.core.base.DockerDirectoryMapping;
import adams.core.management.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Performs the 'docker run' command in either blocking or async fashion.
 * The latter is useful for long-running commands as it supports incremental output.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Run
  extends AbstractAsyncCapableDockerCommandWithOptions {

  private static final long serialVersionUID = -681107300094757081L;

  /** whether to remove the container after execution. */
  protected boolean m_RemoveContainer;

  /** whether to run in user context (-u $(id -u):$(id -g)). */
  protected boolean m_RunAsUser;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs the 'docker run' command in either blocking or async fashion.\n"
      + "The latter is useful for long-running commands as it supports incremental output.\n"
      + "For more information see:\n"
      + "https://docs.docker.com/engine/reference/commandline/run/";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.insert(
      m_OptionManager.size() - 2,
      "remove-container", "removeContainer",
      false);

    m_OptionManager.insert(
      m_OptionManager.size() - 2,
      "run-as-user", "runAsUser",
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

    result = QuickInfoHelper.toString(this, "runAsUser", (m_RunAsUser ? "user" : "root"), "run as: ");
    result += QuickInfoHelper.toString(this, "removeContainer", m_RemoveContainer, "remove container", ", ");
    result += ", " + super.getQuickInfo();

    return result;
  }

  /**
   * Sets whether to remove the container after execution.
   *
   * @param value	true if to remove
   */
  public void setRemoveContainer(boolean value) {
    m_RemoveContainer = value;
    reset();
  }

  /**
   * Returns whether to remove the container after execution.
   *
   * @return		true if to remove
   */
  public boolean getRemoveContainer() {
    return m_RemoveContainer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String removeContainerTipText() {
    return "If enabled, the --rm flag for removing the container is added to the command.";
  }

  /**
   * Sets whether to run the container as the current user rather than root.
   *
   * @param value	true if to run as user
   */
  public void setRunAsUser(boolean value) {
    m_RunAsUser = value;
    reset();
  }

  /**
   * Returns whether to run the container as the current user rather than root.
   *
   * @return		true if to run as user
   */
  public boolean getRunAsUser() {
    return m_RunAsUser;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String runAsUserTipText() {
    return "If enabled, the container is run as the current user using the '-u' option.";
  }

  /**
   * Hook method for performing checks before executing the command.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check() {
    String	result;
    String[]	options;

    result = super.check();

    if (result == null) {
      options = getActualOptions();
      if (options.length == 0)
	result = "No options provided! Minimum is the name of a docker image!";
    }

    return result;
  }

  /**
   * Assembles the command to run. Docker executable gets added separately.
   *
   * @return		the command
   */
  @Override
  protected List<String> buildCommand() {
    List<String> 	result;

    result = new ArrayList<>();
    result.add("run");
    for (DockerDirectoryMapping mapping: m_Connection.getDirMappings()) {
      result.add("-v");
      result.add(mapping.getValue());
    }
    if (m_RemoveContainer)
      result.add("--rm");
    if (m_RunAsUser) {
      result.add("-u");
      result.add(User.getUserID() + ":" + User.getGroupID());
    }
    result.addAll(Arrays.asList(getActualOptions()));

    return result;
  }
}
