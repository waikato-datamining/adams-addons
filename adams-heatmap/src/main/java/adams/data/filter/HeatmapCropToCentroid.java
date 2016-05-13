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
 * CropToCentroid.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.filter.heatmapcrop.CropToCentroid;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;

/**
 <!-- globalinfo-start -->
 * Generates a cropped heatmap centered around the centroid calculated on the pre-filtered data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-filter &lt;adams.data.filter.AbstractFilter&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;Pre-filters the data, .
 * &nbsp;&nbsp;&nbsp;default: adams.data.filter.Threshold
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the cropped region.
 * &nbsp;&nbsp;&nbsp;default: 240
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the cropped region.
 * &nbsp;&nbsp;&nbsp;default: 320
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-centroid &lt;adams.data.filter.Centroid&gt; (property: centroid)
 * &nbsp;&nbsp;&nbsp;The centroid filter to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.filter.Centroid
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@Deprecated
public class HeatmapCropToCentroid
  extends AbstractPreFilter<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = 2270876952032422552L;

  /** the left coordinate of the crop in the original heatmap. */
  public final static String CROP_LEFT = "Crop.Left";

  /** the right coordinate of the crop in the original heatmap. */
  public final static String CROP_TOP = "Crop.Top";

  /** the right coordinate of the crop in the original heatmap. */
  public final static String CROP_RIGHT = "Crop.Right";

  /** the bottom coordinate of the crop in the original heatmap. */
  public final static String CROP_BOTTOM = "Crop.Bottom";

  /** the height of the crop region around the centroid. */
  protected int m_Height;

  /** the width of the crop region around the centroid. */
  protected int m_Width;

  /** the centroid filter. */
  protected HeatmapCentroid m_Centroid;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates a cropped heatmap centered around the centroid "
      + "calculated on the pre-filtered data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"height", "height",
	240, 1, null);

    m_OptionManager.add(
	"width", "width",
	320, 1, null);

    m_OptionManager.add(
	"centroid", "centroid",
	new HeatmapCentroid());
  }

  /**
   * Returns the default pre-filter to use.
   *
   * @return		the default
   */
  @Override
  protected Filter getDefaultFilter() {
    return new HeatmapThreshold();
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
    return "The height of the cropped region.";
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
    return "The width of the cropped region.";
  }

  /**
   * Sets the centroid filter.
   *
   * @param value 	the filter
   */
  public void setCentroid(HeatmapCentroid value) {
    m_Centroid = value;
    reset();
  }

  /**
   * Returns the centroid filter.
   *
   * @return 		the filter
   */
  public HeatmapCentroid getCentroid() {
    return m_Centroid;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String centroidTipText() {
    return "The centroid filter to use.";
  }

  /**
   * Performs the actual filtering, using the pre-filtered data to manipulate
   * the original data.
   *
   * @param filtered	the pref-filtered data
   * @param original	the original input data
   * @return		the final data
   */
  @Override
  protected Heatmap processData(Heatmap filtered, Heatmap original) {
    Heatmap	result;
    HeatmapCentroid centroid;
    Heatmap	centered;
    int		c_x;
    int		c_y;

    // calculate centroid
    centroid = (HeatmapCentroid) m_Centroid.shallowCopy(true);
    centered = centroid.filter(filtered);
    centroid.destroy();

    c_x = (int) Math.round(centered.getReport().getDoubleValue(new Field(HeatmapCentroid.CENTROID_X, DataType.NUMERIC)));
    c_y = (int) Math.round(centered.getReport().getDoubleValue(new Field(HeatmapCentroid.CENTROID_Y, DataType.NUMERIC)));
    if (isLoggingEnabled())
      getLogger().info("Centroid location (y,x): " + c_y + "," + c_x);

    result = CropToCentroid.crop(original, c_y, c_x, m_Height, m_Width);

    return result;
  }
}
