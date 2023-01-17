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
 * ExecContainer.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Performs the 'docker container exec' command in either blocking or async fashion.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ExecContainer
  extends AbstractAsyncCapableDockerCommandWithOptions {

  private static final long serialVersionUID = -681107300094757081L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs the 'docker container exec' command in either blocking or async fashion.\n"
      + "The latter is useful for long-running commands as it supports incremental output.\n"
      + "For more information see:\n"
      + "https://docs.docker.com/engine/reference/commandline/container_exec/";
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
	result = "No options provided! Minimum is the ID of a docker container!";
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
    result.add("container");
    result.add("exec");
    result.addAll(Arrays.asList(getActualOptions()));

    return result;
  }
}
