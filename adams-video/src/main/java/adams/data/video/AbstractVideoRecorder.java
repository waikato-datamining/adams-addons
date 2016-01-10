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

/**
 * AbstractVideoRecorder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.video;

/**
 * Ancestor for video recorders.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractVideoRecorder
  extends AbstractFileBaseRecorder {

  private static final long serialVersionUID = -4437891144873073171L;

  /** the frames per second. */
  protected int m_FramesPerSecond;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "frames-per-second", "framesPerSecond",
      25, 1, null);
  }

  /**
   * Sets the frames per second.
   *
   * @param value	the fps
   */
  public void setFramesPerSecond(int value) {
    if (getOptionManager().isValid("framesPerSecond", value)) {
      m_FramesPerSecond = value;
      reset();
    }
  }

  /**
   * Returns the frames per second.
   *
   * @return		the fps
   */
  public int getFramesPerSecond() {
    return m_FramesPerSecond;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String framesPerSecondTipText() {
    return "The frames per second to use for recording.";
  }
}