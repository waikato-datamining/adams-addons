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
 * AbstractScreenRecorder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.video;

/**
 * Ancestor for screen recorders.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScreenRecorder
  extends AbstractVideoRecorder {

  private static final long serialVersionUID = -4437891144873073171L;

  /** the X position of the screen portion to grab (0-based). */
  protected int m_X;

  /** the Y position of the screen portion to grab (0-based). */
  protected int m_Y;

  /** the width of the screen portion to grab (-1 = remainder). */
  protected int m_Width;

  /** the height of the screen portion to grab (-1 = remainder). */
  protected int m_Height;

  /** whether to capture the mouse cursor. */
  protected boolean m_CaptureMouse;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "x", "X",
      1, 1, null);

    m_OptionManager.add(
      "y", "Y",
      1, 1, null);

    m_OptionManager.add(
      "width", "width",
      -1, -1, null);

    m_OptionManager.add(
      "height", "height",
      -1, -1, null);
  }

  /**
   * Sets the X position of the screen portion (0-based).
   *
   * @param value	the X position
   */
  public void setX(int value) {
    if (getOptionManager().isValid("x", value)) {
      m_X = value;
      reset();
    }
  }

  /**
   * Returns the X position of the screen portion (0-based).
   *
   * @return		the Y position
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The X position on the screen (1-based).";
  }

  /**
   * Sets the Y position of the screen portion (0-based).
   *
   * @param value	the Y position
   */
  public void setY(int value) {
    if (getOptionManager().isValid("y", value)) {
      m_Y = value;
      reset();
    }
  }

  /**
   * Returns the Y position of the screen portion (0-based).
   *
   * @return		the Y position
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The Y position on the screen (1-based).";
  }

  /**
   * Sets the width of the screen portion (-1 = remainder).
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
   * Returns the width of the screen portion (-1 = remainder).
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
    return "The width of the rectangle (-1 = remainder).";
  }

  /**
   * Sets the height of the screen portion (-1 = remainder).
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
   * Returns the height of the screen portion (-1 = remainder).
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
    return "The height of the rectangle (-1 = remainder).";
  }

  /**
   * Sets whether to capture the mouse cursor.
   *
   * @param value	true if to capture
   */
  public void setCaptureMouse(boolean value) {
    m_CaptureMouse = value;
  }

  /**
   * Returns whether to capture the mouse cursor.
   *
   * @return		true if to capture
   */
  public boolean getCaptureMouse() {
    return m_CaptureMouse;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String captureMouseTipText() {
    return "If enabled, the mouse gets captured as well.";
  }
}