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
 * Submap.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.heatmap.Heatmap;

/**
 <!-- globalinfo-start -->
 * Creates a submap, specified by the coordinates of the top-left corner (row and column), width and height.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Submap
  extends AbstractFilter<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = -6589416402799089054L;

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
    return
        "Creates a submap, specified by the coordinates of the top-left corner "
      + "(row and column), width and height.";
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
    if (value >= 0) {
      m_Row = value;
      reset();
    }
    else {
      getLogger().severe("Row must be >= 0, provided: " + value);
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
    if (value >= 0) {
      m_Column = value;
      reset();
    }
    else {
      getLogger().severe("Column must be >= 0, provided: " + value);
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
    if (value > 0) {
      m_Height = value;
      reset();
    }
    else {
      getLogger().severe("Height must be > 0, provided: " + value);
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
    if (value > 0) {
      m_Width = value;
      reset();
    }
    else {
      getLogger().severe("Width must be > 0, provided: " + value);
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
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected Heatmap processData(Heatmap data) {
    return data.submap(m_Row, m_Column, m_Height, m_Width);
  }
}
