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

import adams.core.command.AsyncCapableExternalCommand;
import adams.flow.standalone.SimpleDockerConnection;

/**
 * Interface for docker commands.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface DockerCommand
  extends AsyncCapableExternalCommand {

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
}
