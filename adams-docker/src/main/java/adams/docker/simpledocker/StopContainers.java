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
 * StopContainers.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.QuickInfoHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * For stopping containers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class StopContainers
  extends AbstractDockerCommandWithOptions
  implements DockerCommandWithProgrammaticArguments {

  private static final long serialVersionUID = 34785283711877518L;

  /** for number of seconds to wait. */
  protected int m_Wait;

  /** the additional arguments. */
  protected String[] m_AdditionalArguments;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For stopping containers ('docker container stop').\n"
      + "For more information see:\n"
      + "https://docs.docker.com/engine/reference/commandline/container_stop/";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.insert(
      m_OptionManager.size() - 2,
      "wait", "wait",
      10, 0, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "wait", m_Wait, "wait: ");
    result += ", " + super.getQuickInfo();

    return result;
  }

  /**
   * Sets the number of seconds to wait before stopping.
   *
   * @param value	the number of seconds
   */
  public void setWait(int value) {
    if (getOptionManager().isValid("wait", value)) {
      m_Wait = value;
      reset();
    }
  }

  /**
   * Returns the number of seconds to wait before stopping.
   *
   * @return		the number of seconds
   */
  public int getWait() {
    return m_Wait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitTipText() {
    return "The number of seconds to wait before stopping the container(s).";
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
   * Whether the command is used in a blocking or async fashion.
   *
   * @return		true if blocking, false if async
   */
  @Override
  public boolean isUsingBlocking() {
    return true;
  }

  /**
   * Executes the command.
   *
   * @return		the result of the command, either a CommandResult or a String object (= error message)
   */
  @Override
  protected Object doBlockingExecute() {
    List<String> cmd;

    cmd = new ArrayList<>();
    cmd.add("container");
    cmd.add("stop");
    cmd.add("--force");
    cmd.add("--time");
    cmd.add("" + m_Wait);
    cmd.addAll(Arrays.asList(getActualOptions()));
    if (m_AdditionalArguments != null)
      cmd.addAll(Arrays.asList(m_AdditionalArguments));

    return doBlockingExecute(cmd);
  }

  /**
   * Returns the class of the output the command generates.
   *
   * @return		the type
   */
  @Override
  public Class generates() {
    return String.class;
  }
}
