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
 * LowestTemperature.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap.overlay;

import adams.data.filter.HeatmapCentroid;
import adams.data.heatmap.Heatmap;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Overlays the heatmap with the points that have the lowest temperature.
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
 * <pre>-tolerance &lt;double&gt; (property: tolerance)
 * &nbsp;&nbsp;&nbsp;The tolerance to apply to values to still consider them 'highest' temperature.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10480 $
 * @see HeatmapCentroid
 */
public class LowestTemperature
  extends AbstractSingleColorHeatmapOverlay {

  /** for serialization. */
  private static final long serialVersionUID = -2945211815191636810L;

  /** the tolerance in temperature to consider still "highest" temperature. */
  protected double m_Tolerance;

  /** the list of points with highest temperature. */
  protected List<Point> m_Points;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Overlays the heatmap with the points that have the lowest temperature.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "tolerance", "tolerance",
      0.0, 0.0, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Points = null;
  }

  /**
   * Sets the tolerance.
   *
   * @param value 	the tolerance
   */
  public void setTolerance(double value) {
    if (value >= 0.0) {
      m_Tolerance = value;
      reset();
    }
    else {
      getLogger().warning("Tolerance must be >= 0, provided: " + value);
    }
  }

  /**
   * Returns the tolerance.
   *
   * @return 		the tolerance
   */
  public double getTolerance() {
    return m_Tolerance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String toleranceTipText() {
    return "The tolerance to apply to values to still consider them 'lowest' temperature.";
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected synchronized void doImageChanged(PaintPanel panel) {
    m_Points = null;
  }

  /**
   * Locates the points with the highest temperature.
   *
   * @param map         the heatmap to use
   */
  protected void locate(Heatmap map) {
    double lowest;
    int		x;
    int		y;

    lowest = map.getMin();
    m_Points = new ArrayList<>();
    for (y = 0; y < map.getHeight(); y++) {
      for (x = 0; x < map.getWidth(); x++) {
        if (map.get(y, x) <= lowest - m_Tolerance)
          m_Points.add(new Point(x, y));
      }
    }
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

    if (m_Points == null)
      locate(m_HeatmapPanel.getHeatmap());

    g.setColor(m_Color);
    for (Point p: m_Points) {
      x = (int) p.getX();
      y = (int) p.getY();
      g.drawLine(x, y, x, y);
    }
  }
}
