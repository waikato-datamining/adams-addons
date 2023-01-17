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
 * AbstractStdErrProcessing.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker.stderrprocessing;

import adams.core.option.AbstractOptionHandler;
import adams.docker.simpledocker.DockerCommand;

/**
 * Ancestor for processing the Docker command output received on stderr.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractStdErrProcessing
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 7977194867431996321L;

  /** the owning command. */
  protected DockerCommand m_Owner;

  /**
   * Configures the handler.
   *
   * @param owner 	the owning command
   * @return 		null if successfully setup, otherwise error message
   */
  public String setUp(DockerCommand owner) {
    if (owner == null)
      return "No owner set!";

    m_Owner = owner;

    return null;
  }

  /**
   * Processes the stderr output received when in async mode.
   *
   * @param output	the output to process
   */
  public abstract void processAsync(String output);

  /**
   * Processes the stderr output received when in blocking mode.
   *
   * @param output	the output to process
   */
  public abstract void processBlocking(String output);
}
