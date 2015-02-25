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
 * AbstractHeatmapCrop.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.filter.heatmapcrop;

import adams.core.option.AbstractOptionHandler;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

import java.awt.Point;

/**
 * Ancestor for algorithsm that crop heatmaps.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHeatmapCrop
  extends AbstractOptionHandler {

  /** the left coordinate of the crop in the original heatmap. */
  public final static String CROP_LEFT = "Crop.Left";

  /** the right coordinate of the crop in the original heatmap. */
  public final static String CROP_TOP = "Crop.Top";

  /** the right coordinate of the crop in the original heatmap. */
  public final static String CROP_RIGHT = "Crop.Right";

  /** the bottom coordinate of the crop in the original heatmap. */
  public final static String CROP_BOTTOM = "Crop.Bottom";

  /** the top-left corner. */
  protected Point m_TopLeft;

  /** the bottom-right corner. */
  protected Point m_BottomRight;

  /**
   * Checks whether the heatmap can be processed, throws an {@link IllegalArgumentException}
   * if not.
   * <p/>
   * Default implementation only checks whether a heatmap is present.
   *
   * @param map		the heatmap to check.
   */
  protected void check(Heatmap map) {
    if (map == null)
      throw new IllegalStateException("No heatmap provided!");
  }

  /**
   * Hook method before the crop happens.
   * <p/>
   * Default method initializes the top-left and bottom-right corners to
   * map dimensions.
   *
   * @param map		the heatmap to crop
   */
  protected void preCrop(Heatmap map) {
    m_TopLeft     = new Point(0, 0);
    m_BottomRight = new Point(map.getWidth(), map.getHeight());
    if (isLoggingEnabled())
      getLogger().info("mapWidth=" + map.getWidth() + ", mapHeight=" + map.getHeight());
  }

  /**
   * Performs the actual cropping.
   *
   * @param map		the map to crop
   * @return		the cropped heatmap
   */
  protected abstract Heatmap doCrop(Heatmap map);

  /**
   * Hook method after the crop happened.
   * <p/>
   * Sets the crop coordinates.
   *
   * @param map		the cropped heatmap
   * @see		#m_TopLeft
   * @see		#m_BottomRight
   */
  protected void postCrop(Heatmap map) {
    Report	report;

    report = map.getReport();

    report.addField(new Field(CROP_LEFT,   DataType.NUMERIC));
    report.addField(new Field(CROP_TOP,    DataType.NUMERIC));
    report.addField(new Field(CROP_RIGHT,  DataType.NUMERIC));
    report.addField(new Field(CROP_BOTTOM, DataType.NUMERIC));

    report.setNumericValue(CROP_LEFT,   m_TopLeft.getX());
    report.setNumericValue(CROP_TOP,    m_TopLeft.getY());
    report.setNumericValue(CROP_RIGHT,  m_BottomRight.getX());
    report.setNumericValue(CROP_BOTTOM, m_BottomRight.getY());
  }

  /**
   * Crops the given heatmap.
   *
   * @param map		the map to crop
   * @return		the cropped heatmap
   */
  public Heatmap crop(Heatmap map) {
    Heatmap	result;

    check(map);
    preCrop(map);
    result = doCrop(map);
    postCrop(result);

    return result;
  }
}
