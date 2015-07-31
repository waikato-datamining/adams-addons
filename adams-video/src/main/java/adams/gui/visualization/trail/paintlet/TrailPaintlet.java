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
 * TrailPaintlet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.trail.paintlet;

import adams.gui.visualization.image.paintlet.Paintlet;
import adams.gui.visualization.trail.TrailPanel;

/**
 * Interface for trail paintlets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface TrailPaintlet
  extends Paintlet {

  /**
   * Sets the panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  public void setPanel(TrailPanel value);

  /**
   * Returns the underlying trail panel.
   *
   * @return		the panel, null if not set
   */
  public TrailPanel getTrailPanel();
}
