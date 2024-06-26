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
 * KillContainers.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.QuickInfoHelper;

import java.util.Arrays;
import java.util.List;

/**
 * For killing containers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class KillContainers
  extends AbstractDockerCommand
  implements DockerCommandWithProgrammaticArguments {

  private static final long serialVersionUID = 34785283711877518L;

  /** the signal to send. */
  protected String m_Signal;

  /** the additional arguments. */
  protected String[] m_AdditionalArguments;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For killing containers ('docker container kill').\n"
      + "For more information see:\n"
      + "https://docs.docker.com/engine/reference/commandline/container_kill/";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "signal", "signal",
      "KILL");
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
    result += QuickInfoHelper.toString(this, "signal", m_Signal, ", signal: ");

    return result;
  }

  /**
   * Sets the signal to use.
   *
   * @param value	the signal
   */
  public void setSignal(String value) {
    m_Signal = value;
    reset();
  }

  /**
   * Returns the signal to use.
   *
   * @return		the signal
   */
  public String getSignal() {
    return m_Signal;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String signalTipText() {
    return "The signal to send to the container processes.";
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
    result.add("kill");
    result.add("--signal");
    result.add("" + m_Signal);
    if (m_AdditionalArguments != null)
      result.addAll(Arrays.asList(m_AdditionalArguments));

    return result;
  }
}
