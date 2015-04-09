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
 * AbstractSingleColorHeatmapOverlay.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap.overlay;

import java.awt.Color;

/**
 * Ancestor for overlays that just use a single color.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10480 $
 */
public abstract class AbstractSingleColorHeatmapOverlay
  extends AbstractHeatmapOverlay {

  /** for serialization. */
  private static final long serialVersionUID = -2945211815191636810L;

  /** the color to paint the centroid in. */
  protected Color m_Color;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"color", "color",
	Color.RED);
  }

  /**
   * Sets the color for the overlay.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color for the overlay.
   *
   * @return 		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color for the overlay.";
  }
}
