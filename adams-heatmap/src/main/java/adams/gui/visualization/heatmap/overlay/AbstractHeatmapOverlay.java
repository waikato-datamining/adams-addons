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
 * CentroidOverlay.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap.overlay;

import adams.gui.visualization.heatmap.HeatmapPanel;
import adams.gui.visualization.image.AbstractImageOverlay;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Graphics;

/**
 * Ancestor for heatmap image overlays.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHeatmapOverlay
  extends AbstractImageOverlay {

  /** for serialization. */
  private static final long serialVersionUID = -8198433620642324789L;

  /** the heatmap panel. */
  protected HeatmapPanel m_HeatmapPanel;

  /**
   * Sets the heatmap panel this overlay is for.
   *
   * @param value	the panel
   */
  public void setHeatmapPanel(HeatmapPanel value) {
    m_HeatmapPanel = value;
    reset();
  }

  /**
   * Returns the heatmap panel this overlay is for.
   *
   * @return		the panel
   */
  public HeatmapPanel getHeatmapPanel() {
    return m_HeatmapPanel;
  }

  /**
   * Paints the overlay over the image.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  public void paintOverlay(PaintPanel panel, Graphics g) {
    if (m_HeatmapPanel != null)
      super.paintOverlay(panel, g);
    else
      getLogger().severe("Not heatmap panel set!");
  }
}
