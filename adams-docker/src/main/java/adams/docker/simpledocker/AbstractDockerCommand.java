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
 * AbstractDockerCommand.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.Utils;
import adams.core.command.AbstractAsyncCapableExternalCommand;
import adams.core.management.CommandResult;
import adams.docker.SimpleDockerHelper;
import adams.flow.standalone.SimpleDockerConnection;

import java.util.List;

/**
 * Ancestor for docker commands.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDockerCommand
  extends AbstractAsyncCapableExternalCommand
  implements DockerCommand {

  private static final long serialVersionUID = -3060945925413859934L;

  /** the docker connection. */
  protected transient SimpleDockerConnection m_Connection;

  /**
   * Sets the docker connection to use.
   *
   * @param value	the connection
   */
  @Override
  public void setConnection(SimpleDockerConnection value) {
    m_Connection = value;
  }

  /**
   * Returns the docker connection in use.
   *
   * @return		the connection, null if none set
   */
  @Override
  public SimpleDockerConnection getConnection() {
    return m_Connection;
  }

  /**
   * Hook method for performing checks before executing the command.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    String	result;

    result = super.check();

    if (result == null) {
      if (m_Connection == null)
	result = "No docker connection available! Missing " + Utils.classToString(SimpleDockerConnection.class) + " standalone?";
    }

    return result;
  }

  /**
   * Assembles the command to run.
   *
   * @return		the command
   */
  protected List<String> buildCommand() {
    List<String>  result;

    result = super.buildCommand();
    if (!isUsingBlocking())
      result.add(m_Connection.getAcualBinary());

    return result;
  }

  /**
   * Executes the specified command in blocking fashion.
   *
   * @param cmd		the command to execute
   * @return		the generated output
   */
  protected CommandResult doBlockingExecute(List<String> cmd) {
    log(cmd);
    m_LastCommand = cmd.toArray(new String[0]);
    return SimpleDockerHelper.command(m_Connection.getAcualBinary(), cmd);
  }

  /**
   * Executes the command.
   *
   * @return		the result of the command, either a CommandResult or a String object (= error message)
   */
  @Override
  protected Object doBlockingExecute() {
    return doBlockingExecute(buildCommand());
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Connection = null;
    super.cleanUp();
  }
}
