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
 * Circles.java
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
 * TODO: What class does.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Circles
  extends AbstractTrailPaintlet
  implements AntiAliasingSupporter {

  /** the diameter of the circle. */
  protected int m_Diameter;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  @Override
  public String globalInfo() {
    return "Simple paints the trail steps as circles.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "diameter", "diameter",
	    7, 1, null);

    m_OptionManager.add(
      "anti-aliasing-enabled", "antiAliasingEnabled",
      GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));
  }

  /**
   * Sets the circle diameter.
   *
   * @param value	the diameter
   */
  public void setDiameter(int value) {
    m_Diameter = value;
    memberChanged();
  }

  /**
   * Returns the diameter of the circle.
   *
   * @return		the diameter
   */
  public int getDiameter() {
    return m_Diameter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String diameterTipText() {
    return "The diameter of the circle in pixels.";
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
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   */
  @Override
  public void performPaint(Graphics g) {
    Trail 		trail;
    List<Step> 		points;
    Step		curr;
    int			currX;
    int			currY;
    int			i;

    if (getTrailPanel() == null)
      return;

    trail  = getTrailPanel().getTrail();
    points = trail.toList();

    // paint all points
    g.setColor(m_Color);
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

    for (i = 0; i < points.size(); i++) {
      curr = points.get(i);

      // determine coordinates
      currX = (int) curr.getX();
      currY = (int) curr.getY();

      currX -= (m_Diameter / 2);
      currY -= (m_Diameter / 2);

      // draw circle
      g.drawOval(currX, currY, m_Diameter - 1, m_Diameter - 1);
    }
  }
}
