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
 * Pull.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.QuickInfoHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pulls the specified image.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Pull
  extends AbstractAsyncCapableDockerCommandWithOptions
  implements AsyncCapableDockerCommand {

  private static final long serialVersionUID = -3235247889827794116L;

  /** the image to pull. */
  protected String m_Image;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Pulls the specified image."
      + "Use non-blocking mode for better progress updates.\n"
      + "For more information see:\n"
      + "https://docs.docker.com/engine/reference/commandline/pull/";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.insert(
      m_OptionManager.size() - 2,
      "image", "image",
      "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "image", m_Image, "image: ");
    result += ", " + super.getQuickInfo();

    return result;
  }

  /**
   * Sets the image to pull.
   *
   * @param value	the image
   */
  public void setImage(String value) {
    m_Image = value;
    reset();
  }

  /**
   * Returns the image to pull.
   *
   * @return		the image
   */
  public String getImage() {
    return m_Image;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageTipText() {
    return "The name of the image to pull.";
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
    result.add("pull");
    result.addAll(Arrays.asList(getActualOptions()));
    result.add(m_Image);

    return result;
  }
}
