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
    result += QuickInfoHelper.toString(this, "removeContainer", m_RemoveContainer, "remove container", ", ");

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
    if (m_RemoveContainer)
      result.add("--rm");
    for (DockerDirectoryMapping mapping: m_Connection.getDirMappings()) {
      result.add("-v");
      result.add(mapping.getValue());
    }
    result.addAll(Arrays.asList(getActualOptions()));

    return result;
  }
}
