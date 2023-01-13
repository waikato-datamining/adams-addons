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
 * DockerCommand.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.QuickInfoSupporter;
import adams.core.StoppableWithFeedback;
import adams.core.logging.LoggingSupporter;
import adams.core.option.OptionHandler;
import adams.flow.standalone.SimpleDockerConnection;

/**
 * Interface for docker commands.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface DockerCommand
  extends OptionHandler, QuickInfoSupporter, StoppableWithFeedback, LoggingSupporter {

  /**
   * Sets the docker connection to use.
   *
   * @param value	the connection
   */
  public void setConnection(SimpleDockerConnection value);

  /**
   * Returns the docker connection in use.
   *
   * @return		the connection, null if none set
   */
  public SimpleDockerConnection getConnection();

  /**
   * Returns the class of the output the command generates.
   *
   * @return		the type
   */
  public Class generates();

  /**
   * Whether the command is used in a blocking or async fashion.
   *
   * @return		true if blocking, false if async
   */
  public boolean isUsingBlocking();

  /**
   * Executes the command.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute();

  /**
   * Returns whether the command was executed.
   *
   * @return		true if executed
   */
  public boolean isExecuted();

  /**
   * Returns whether the command is currently running.
   *
   * @return		true if running
   */
  public boolean isRunning();

  /**
   * Returns whether the command finished.
   *
   * @return		true if finished
   */
  public boolean isFinished();

  /**
   * Whether there is any pending output.
   *
   * @return		true if output pending
   */
  public boolean hasOutput();

  /**
   * Returns the next line in the output.
   *
   * @return		the line, null if none available
   */
  public Object output();
}
