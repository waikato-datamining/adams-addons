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
 * DockerContainer.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker;

import java.io.Serializable;
import java.util.Objects;

/**
 * Simple container for docker containers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DockerContainer
  implements Serializable, Comparable<DockerContainer> {

  private static final long serialVersionUID = -687099907771835571L;

  /** the container ID. */
  protected String m_ContainerID;

  /** the image. */
  protected String m_Image;

  /** the command. */
  protected String m_Command;

  /** when the image was created. */
  protected String m_Created;

  /** the status of the container. */
  protected String m_Status;

  /** the ports. */
  protected String m_Ports;

  /** the names. */
  protected String m_Names;

  /**
   * Initializes the container.
   *
   * @param containerID	the container ID (hash)
   * @param command	the command
   * @param image	the image
   * @param created	the created string
   * @param status	the status
   * @param ports	the ports
   * @param names 	the names
   */
  public DockerContainer(String containerID, String image, String command, String created, String status, String ports, String names) {
    m_ContainerID = containerID;
    m_Image       = image;
    m_Command     = command;
    m_Created     = created;
    m_Status      = status;
    m_Ports       = ports;
    m_Names       = names;
  }

  /**
   * Returns the container ID.
   *
   * @return		the ID
   */
  public String getContainerID() {
    return m_ContainerID;
  }

  /**
   * Returns the image.
   *
   * @return		the image
   */
  public String getImage() {
    return m_Image;
  }

  /**
   * Returns the command.
   *
   * @return		the command
   */
  public String getCommand() {
    return m_Command;
  }

  /**
   * Returns the created information.
   *
   * @return		the created information
   */
  public String getCreated() {
    return m_Created;
  }

  /**
   * Returns the status.
   *
   * @return		the status
   */
  public String getStatus() {
    return m_Status;
  }

  /**
   * Returns the ports.
   *
   * @return		the ports
   */
  public String getPorts() {
    return m_Ports;
  }

  /**
   * Returns the names.
   *
   * @return		the names
   */
  public String getNames() {
    return m_Names;
  }

  /**
   * Compares itself with the other image.
   *
   * @param o	the other image
   * @return	the comparison result
   */
  @Override
  public int compareTo(DockerContainer o) {
    int		result;

    result = getContainerID().compareTo(o.getContainerID());
    if (result == 0)
      result = getImage().compareTo(o.getImage());
    if (result == 0)
      result = getCommand().compareTo(o.getCommand());
    if (result == 0)
      result = getCreated().compareTo(o.getCreated());
    if (result == 0)
      result = getStatus().compareTo(o.getStatus());
    if (result == 0)
      result = getPorts().compareTo(o.getPorts());
    if (result == 0)
      result = getNames().compareTo(o.getNames());

    return result;
  }

  /**
   * Checks whether the objects are equal.
   *
   * @param o		the object to compare with
   * @return		true if the same content
   * @see		#compareTo(DockerContainer)
   */
  @Override
  public boolean equals(Object o) {
    return (o instanceof DockerContainer) && (compareTo((DockerContainer) o) == 0);
  }

  /**
   * Generates a hash code.
   *
   * @return		the hash
   */
  @Override
  public int hashCode() {
    return Objects.hash(m_ContainerID, m_Image, m_Command, m_Created, m_Status, m_Ports, m_Names);
  }
}
