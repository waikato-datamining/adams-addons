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
 * AbstractTrailPaintlet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.trail.paintlet;

import adams.gui.visualization.core.AbstractStrokePaintlet;

import java.awt.Color;

/**
 * Ancestor for trail paintlets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTrailPaintlet
  extends AbstractStrokePaintlet {

  private static final long serialVersionUID = 8036940792107897639L;

  /** Color of the stroke for the paintlet */
  protected Color m_Color;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"color", "color", Color.BLACK);
  }

  /**
   * Set the stroke color for the paintlet.
   *
   * @param value	Color of the stroke
   */
  public void setColor(Color value) {
    m_Color = value;
    memberChanged();
  }

  /**
   * Get the stroke color for the paintlet.
   *
   * @return		Color of the stroke
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
    return "The stroke color.";
  }
}
