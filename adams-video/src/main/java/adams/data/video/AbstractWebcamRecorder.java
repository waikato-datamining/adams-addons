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
 * AbstractWebcamRecorder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.video;

/**
 * Ancestor for webcam recorders.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWebcamRecorder
  extends AbstractVideoRecorder {

  private static final long serialVersionUID = -4437891144873073171L;

  /** the ID of the webcam. */
  protected String m_ID;

  /** the width of the screen portion to grab (-1 = remainder). */
  protected int m_Width;

  /** the height of the screen portion to grab (-1 = remainder). */
  protected int m_Height;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "id", "ID",
      "");

    m_OptionManager.add(
      "width", "width",
      320, 1, null);

    m_OptionManager.add(
      "height", "height",
      240, 1, null);
  }

  /**
   * Sets the ID of the webcam to use.
   *
   * @param value	the ID
   */
  public void setID(String value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the ID of the webcam to use.
   *
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String IDTipText() {
    return "The ID of the webcam to use (use empty string for the default one).";
  }

  /**
   * Sets the width of the webcam.
   *
   * @param value	the width
   */
  public void setWidth(int value) {
    if (getOptionManager().isValid("width", value)) {
      m_Width = value;
      reset();
    }
  }

  /**
   * Returns the width of the webcam.
   *
   * @return		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the webcam.";
  }

  /**
   * Sets the height of the webcam.
   *
   * @param value	the height
   */
  public void setHeight(int value) {
    if (getOptionManager().isValid("height", value)) {
      m_Height = value;
      reset();
    }
  }

  /**
   * Returns the height of the webcam.
   *
   * @return		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the webcam.";
  }
}