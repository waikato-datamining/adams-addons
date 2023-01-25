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
 * RunWithArgs.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import java.util.Arrays;
import java.util.List;

/**
 * TODO: What this class does.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RunWithArgs
  extends Run
  implements DockerCommandWithProgrammaticArguments {

  private static final long serialVersionUID = -3410076523444950983L;

  /** the additional arguments. */
  protected String[] m_AdditionalArguments;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return super.globalInfo()
      + "\nAppends the additional arguments (received as input).";
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
   * Assembles the command to run. Docker executable gets added separately.
   *
   * @return		the command
   */
  @Override
  protected List<String> buildCommand() {
    List<String> 	result;

    result = super.buildCommand();
    if (m_AdditionalArguments != null)
      result.addAll(Arrays.asList(m_AdditionalArguments));

    return result;
  }
}
