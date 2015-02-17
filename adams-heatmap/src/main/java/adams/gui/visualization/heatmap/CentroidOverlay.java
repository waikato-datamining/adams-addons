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
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap;

import java.awt.Color;
import java.awt.Graphics;

import weka.core.Utils;
import adams.data.filter.HeatmapCentroid;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

/**
 <!-- globalinfo-start -->
 * Paints the centroid's location over the heatmap image.
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
 * <pre>-enabled (property: enabled)
 * &nbsp;&nbsp;&nbsp;If enabled, this overlay is painted over the image.
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
public class CentroidOverlay
  extends AbstractHeatmapOverlay {

  /** for serialization. */
  private static final long serialVersionUID = -2945211815191636810L;

  /** the calculated centroid. */
  protected double[] m_Centroid;

  /** the color to paint the centroid in. */
  protected Color m_Color;

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
   * Resets the overlay.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Centroid = null;
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
