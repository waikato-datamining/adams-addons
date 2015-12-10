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
 * Rectangle.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.filter.heatmapcrop;

import adams.data.heatmap.Heatmap;

/**
 <!-- globalinfo-start -->
 * Crops the map to the specified rectangle x and y (both 1-based), width and height.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The row of the top-left corner, the starting point of the rectangle (1-based
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The column of the top-left corner, the starting point of the rectangle (
 * &nbsp;&nbsp;&nbsp;1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the rectangle.
 * &nbsp;&nbsp;&nbsp;default: 240
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the rectangle.
 * &nbsp;&nbsp;&nbsp;default: 320
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Rectangle
  extends AbstractHeatmapCrop {

  private static final long serialVersionUID = 8109859053628417241L;

  /** the row to start (top-left). */
  protected int m_X;

  /** the column to start (top-left). */
  protected int m_Y;

  /** the height of the submap. */
  protected int m_Height;

  /** the width of the submap. */
  protected int m_Width;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Crops the map to the specified rectangle x and y (both 1-based), width and height.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"x", "x",
	1, 1, null);

    m_OptionManager.add(
	"y", "y",
	1, 1, null);

    m_OptionManager.add(
	"height", "height",
	240, 1, null);

    m_OptionManager.add(
	"width", "width",
	320, 1, null);
  }

  /**
   * Sets the row to start the rectangle from.
   *
   * @param value 	the row (top-left corner)
   */
  public void setX(int value) {
    if (getOptionManager().isValid("x", value)) {
      m_X = value;
      reset();
    }
  }

  /**
   * Returns the row start the rectangle from.
   *
   * @return 		the row (top-left corner)
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
  public String xTipText() {
    return "The row of the top-left corner, the starting point of the rectangle (1-based).";
  }

  /**
   * Sets the column to start the rectangle from.
   *
   * @param value 	the column (top-left corner)
   */
  public void setY(int value) {
    if (getOptionManager().isValid("y", value)) {
      m_Y = value;
      reset();
    }
  }

  /**
   * Returns the column start the rectangle from.
   *
   * @return 		the column (top-left corner)
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
  public String yTipText() {
    return "The column of the top-left corner, the starting point of the rectangle (1-based).";
  }

  /**
   * Sets the height of the rectangle.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    if (getOptionManager().isValid("height", value)) {
      m_Height = value;
      reset();
    }
  }

  /**
   * Returns the height of the rectangle.
   *
   * @return 		the height
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
    return "The height of the rectangle.";
  }

  /**
   * Sets the width of the rectangle.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    if (getOptionManager().isValid("width", value)) {
      m_Width = value;
      reset();
    }
  }

  /**
   * Returns the width of the submap.
   *
   * @return 		the width
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
    return "The width of the rectangle.";
  }

  /**
   * Performs the actual cropping.
   *
   * @param map		the map to crop
   * @return		the cropped heatmap
   */
  @Override
  protected Heatmap doCrop(Heatmap map) {
    return map.submap(m_Y - 1, m_X - 1, m_Height, m_Width);
  }
}
