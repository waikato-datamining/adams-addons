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
 * AbstractBufferedImageBasedHeatmapThreshold.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.filter.heatmapthreshold;

import adams.data.conversion.HeatmapToBufferedImage;
import adams.data.conversion.HeatmapToBufferedImageConversion;
import adams.data.heatmap.Heatmap;
import adams.data.image.BufferedImageContainer;

import java.awt.image.BufferedImage;

/**
 * Ancestor of threshold algorithms that use a {@link java.awt.image.BufferedImage}
 * as basis for their calculation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBufferedImageBasedHeatmapThreshold
  extends AbstractHeatmapThreshold {

  private static final long serialVersionUID = -1205777458822555932L;

  /** for generating the BufferedImage. */
  protected HeatmapToBufferedImageConversion m_Conversion;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"conversion", "conversion",
	new HeatmapToBufferedImage());
  }

  /**
   * Sets the conversion to use for turning the heatmap into a BufferedImage.
   *
   * @param value 	the conversion
   */
  public void setConversion(HeatmapToBufferedImageConversion value) {
    m_Conversion = value;
    reset();
  }

  /**
   * Returns the conversion to use for turning the heatmap into a BufferedImage.
   *
   * @return 		the conversion
   */
  public HeatmapToBufferedImageConversion getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return "The conversion to use for turning the heatmap into a BufferedImage.";
  }

  /**
   * Performs the actual calculation of the threshold using the image.
   *
   * @param map		the original heatmap
   * @param img		the generated image to use
   * @return		the threshold
   */
  protected abstract double doCalcThreshold(Heatmap map, BufferedImage img);

  /**
   * Performs the actual calculation of the threshold.
   *
   * @param map		the map to base the calculation on
   * @return		the threshold
   */
  @Override
  protected double doCalcThreshold(Heatmap map) {
    BufferedImage	img;
    String		msg;

    m_Conversion.setInput(map);
    msg = m_Conversion.convert();
    if (msg != null)
      throw new IllegalStateException("Failed to convert Heatmap into BufferedImage: " + msg);
    else
      img = ((BufferedImageContainer) m_Conversion.getOutput()).getImage();
    m_Conversion.cleanUp();

    return doCalcThreshold(map, img);
  }
}
