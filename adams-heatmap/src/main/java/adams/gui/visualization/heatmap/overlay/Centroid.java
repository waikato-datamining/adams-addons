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
 * Centroid.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap.overlay;

import adams.core.Utils;
import adams.data.filter.HeatmapCentroid;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Graphics;

/**
 <!-- globalinfo-start -->
 * Paints the centroid's location over the heatmap image.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-enabled &lt;boolean&gt; (property: enabled)
 * &nbsp;&nbsp;&nbsp;If enabled, this overlay is painted over the image.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color for the overlay.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see adams.data.filter.HeatmapCentroid
 */
public class Centroid
  extends AbstractSingleColorHeatmapOverlay {

  /** for serialization. */
  private static final long serialVersionUID = -2945211815191636810L;

  /** the calculated centroid. */
  protected double[] m_Centroid;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paints the centroid's location over the heatmap image.";
  }

  /**
   * Resets the overlay.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Centroid = null;
  }

  /**
   * Calculates the centroid.
   */
  protected void calcCentroid() {
    HeatmapCentroid centroid;
    Heatmap	map;

    centroid      = new HeatmapCentroid();
    map           = centroid.filter(m_HeatmapPanel.getHeatmap());
    m_Centroid    = new double[2];
    m_Centroid[0] = map.getReport().getDoubleValue(new Field(HeatmapCentroid.CENTROID_X, DataType.NUMERIC));
    m_Centroid[1] = map.getReport().getDoubleValue(new Field(HeatmapCentroid.CENTROID_Y, DataType.NUMERIC));
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected synchronized void doImageChanged(PaintPanel panel) {
    m_Centroid = null;
  }

  /**
   * Paints the overlay over the image.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  protected synchronized void doPaintOverlay(PaintPanel panel, Graphics g) {
    int		x;
    int		y;

    if (m_Centroid == null)
      calcCentroid();

    if ((m_Centroid[0] == -1.0) || (m_Centroid[1] == -1.0)) {
      getLogger().severe("Cannot paint centroid: " + Utils.arrayToString(m_Centroid));
      return;
    }

    x = (int) Math.round(m_Centroid[0]);
    y = (int) Math.round(m_Centroid[1]);

    g.setColor(m_Color);
    g.drawLine(x, 0, x, panel.getOwner().getHeight() - 1);
    g.drawLine(0, y, panel.getOwner().getWidth() - 1, y);
  }
}
