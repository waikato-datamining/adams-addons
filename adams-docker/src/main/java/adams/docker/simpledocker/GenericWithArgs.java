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
 * GenericWithArgs.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Executes the specified docker command with the provided options feeding in the specified parameters.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class GenericWithArgs
  extends Generic
  implements DockerCommandWithProgrammaticArguments{

  private static final long serialVersionUID = -3235247889827794116L;

  /** the additional arguments. */
  protected String[] m_AdditionalArguments;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the specified docker command with the provided options. Appends the additional arguments (received as input)."
      + "Use non-blocking mode for long-running commands.";
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

    result = new ArrayList<>();
    result.add(m_Command);
    result.addAll(Arrays.asList(getActualOptions()));
    if (m_AdditionalArguments != null)
      result.addAll(Arrays.asList(m_AdditionalArguments));

    return result;
  }
}
