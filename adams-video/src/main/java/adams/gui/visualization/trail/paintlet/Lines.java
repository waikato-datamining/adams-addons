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
 * Lines.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.trail.paintlet;

import adams.data.trail.Step;
import adams.data.trail.Trail;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;

import java.awt.Graphics;
import java.util.List;

/**
 * Connects the steps with lines.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Lines
  extends AbstractTrailPaintlet
  implements AntiAliasingSupporter {

  private static final long serialVersionUID = -2398989327548617860L;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  @Override
  public String globalInfo() {
    return "Connects the step locations with lines.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "anti-aliasing-enabled", "antiAliasingEnabled",
      GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_AntiAliasingEnabled = value;
    memberChanged();
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return m_AntiAliasingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String antiAliasingEnabledTipText() {
    return "If enabled, uses anti-aliasing for drawing circles.";
  }

  /**
   * Paints the given data.
   *
   * @param g		the graphics context to use for painting
   * @param trail	the data to paint
   */
  @Override
  public void paintData(Graphics g, Trail trail) {
    List<Step> 		points;
    Step		prev;
    Step		curr;
    int			prevX;
    int			prevY;
    int			currX;
    int			currY;
    int			i;

    points = trail.toList();

    // paint all points
    g.setColor(m_Color);
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

    for (i = 1; i < points.size(); i++) {
      prev = points.get(i - 1);
      curr = points.get(i);

      // determine coordinates
      prevX = (int) prev.getX();
      prevY = (int) prev.getY();
      currX = (int) curr.getX();
      currY = (int) curr.getY();

      // draw line
      g.drawLine(prevX, prevY, currX, currY);
    }
  }
}
