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
 * RemoveImages.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.docker.SimpleDockerHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * For removing images.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RemoveImages
  extends AbstractDockerCommandWithOptions
  implements DockerCommandWithProgrammaticArguments {

  private static final long serialVersionUID = 34785283711877518L;

  /** the additional arguments. */
  protected String[] m_AdditionalArguments;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For removing images ('docker iamge rm').\n"
      + "For more information see:\n"
      + "https://docs.docker.com/engine/reference/commandline/image_rm/";
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
    List<String> result;

    result = new ArrayList<>();
    result.add("image");
    result.add("rm");
    result.addAll(Arrays.asList(getActualOptions()));
    if (m_AdditionalArguments != null)
      result.addAll(Arrays.asList(m_AdditionalArguments));
    log(result);

    return SimpleDockerHelper.command(m_Connection.getAcualBinary(), result);
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
