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
 * Submap.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.filter.heatmapcrop;

import adams.data.heatmap.Heatmap;

/**
 <!-- globalinfo-start -->
 * Creates the submap using the specified row and column (both 0-based), width and height.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-row &lt;int&gt; (property: row)
 * &nbsp;&nbsp;&nbsp;The row of the top-left corner, the starting point of the submap (0-based
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-col &lt;int&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column of the top-left corner, the starting point of the submap (0-based
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the submap.
 * &nbsp;&nbsp;&nbsp;default: 240
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the submap.
 * &nbsp;&nbsp;&nbsp;default: 320
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Submap
  extends AbstractHeatmapCrop {

  private static final long serialVersionUID = 8109859053628417241L;

  /** the row to start (top-left). */
  protected int m_Row;

  /** the column to start (top-left). */
  protected int m_Column;

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
    return "Creates the submap using the specified row and column (both 0-based), width and height.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"row", "row",
	0, 0, null);

    m_OptionManager.add(
	"col", "column",
	0, 0, null);

    m_OptionManager.add(
	"height", "height",
	240, 1, null);

    m_OptionManager.add(
	"width", "width",
	320, 1, null);
  }

  /**
   * Sets the row to start the submap from.
   *
   * @param value 	the row (top-left corner)
   */
  public void setRow(int value) {
    if (getOptionManager().isValid("row", value)) {
      m_Row = value;
      reset();
    }
  }

  /**
   * Returns the row start the submap from.
   *
   * @return 		the row (top-left corner)
   */
  public int getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowTipText() {
    return "The row of the top-left corner, the starting point of the submap (0-based).";
  }

  /**
   * Sets the column to start the submap from.
   *
   * @param value 	the column (top-left corner)
   */
  public void setColumn(int value) {
    if (getOptionManager().isValid("column", value)) {
      m_Column = value;
      reset();
    }
  }

  /**
   * Returns the column start the submap from.
   *
   * @return 		the column (top-left corner)
   */
  public int getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The column of the top-left corner, the starting point of the submap (0-based).";
  }

  /**
   * Sets the height of the submap.
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
   * Returns the height start the submap from.
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
    return "The height of the submap.";
  }

  /**
   * Sets the width of the submap.
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
   * Returns the width start the submap from.
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
    return "The width of the submap.";
  }

  /**
   * Performs the actual cropping.
   *
   * @param map		the map to crop
   * @return		the cropped heatmap
   */
  @Override
  protected Heatmap doCrop(Heatmap map) {
    return map.submap(m_Row, m_Column, m_Height, m_Width);
  }
}
