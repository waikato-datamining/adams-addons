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
 * DockerDirectoryMapping.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import java.io.File;

/**
 * TODO: What this class does.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DockerDirectoryMapping
  extends AbstractBaseString {

  private static final long serialVersionUID = 9003132865953252981L;

  /**
   * Initializes the directory mapping with a default value.
   */
  public DockerDirectoryMapping() {
    this("/local:/container");
  }

  /**
   * Uses the specified mapping.
   *
   * @param mapping		the mapping (local dir : container dir)
   */
  public DockerDirectoryMapping(String mapping) {
    super(mapping);
  }

  /**
   * Initializes the mapping with the provided dirs.
   *
   * @param localDir		the local directory
   * @param containerDir	the container directory
   */
  public DockerDirectoryMapping(File localDir, File containerDir) {
    this(localDir.getAbsolutePath(), containerDir.getAbsolutePath());
  }

  /**
   * Initializes the mapping with the provided dirs.
   *
   * @param localDir		the local directory
   * @param containerDir	the container directory
   */
  public DockerDirectoryMapping(String localDir, String containerDir) {
    this(localDir + ":" + containerDir);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    boolean	result;
    String[]	parts;

    result = value.contains(":");

    if (result) {
      parts  = value.split(":");
      result = (parts.length == 2);
    }

    return result;
  }

  /**
   * Returns the "local" part of the mapping.
   *
   * @return		the local part
   */
  public String localDir() {
    return getValue().split(":")[0];
  }

  /**
   * Returns the "container" part of the mapping.
   *
   * @return		the container part
   */
  public String containerDir() {
    return getValue().split(":")[1];
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return the tool tip
   */
  @Override
  public String getTipText() {
    return "For mapping a host directory into a container, format: <abs. local dir>:<abs. container dir>";
  }
}
