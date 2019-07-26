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
 * RectangleBackground.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wordcloud;

import adams.core.MessageCollection;
import com.kennycason.kumo.bg.Background;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Generates a rectangular background.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RectangleBackground
  extends AbstractBackground {

  private static final long serialVersionUID = 2848272343570036328L;

  /** the x. */
  protected int m_X;

  /** the y. */
  protected int m_Y;

  /** the width. */
  protected int m_Width;

  /** the height. */
  protected int m_Height;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a rectangular background.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "x", "X",
      0, 0, null);

    m_OptionManager.add(
      "y", "Y",
      0, 0, null);

    m_OptionManager.add(
      "width", "width",
      600, 1, null);

    m_OptionManager.add(
      "height", "height",
      300, 1, null);
  }

  /**
   * Sets the x for the background.
   *
   * @param value	the x
   */
  public void setX(int value) {
    if (getOptionManager().isValid("X", value)) {
      m_X = value;
      reset();
    }
  }

  /**
   * Returns the x for the background.
   *
   * @return		the x
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
    return "The x for the background.";
  }

  /**
   * Sets the y for the background.
   *
   * @param value	the y
   */
  public void setY(int value) {
    if (getOptionManager().isValid("Y", value)) {
      m_Y = value;
      reset();
    }
  }

  /**
   * Returns the y for the background.
   *
   * @return		the y
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
    return "The y for the background.";
  }

  /**
   * Sets the width for the background.
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
   * Returns the width for the background.
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
    return "The width for the background.";
  }

  /**
   * Sets the height for the background.
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
   * Returns the height for the background.
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
    return "The height for the background.";
  }

  /**
   * Generates the background.
   *
   * @param errors 	for collecting errors
   * @return		the background, null if failed
   */
  @Override
  public Background generate(MessageCollection errors) {
    return new com.kennycason.kumo.bg.RectangleBackground(
      new Point(m_X, m_Y),
      new Dimension(m_Width, m_Height));
  }
}
