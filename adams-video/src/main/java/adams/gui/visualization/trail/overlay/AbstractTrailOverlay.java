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
 * AbstractTrailOverlay.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.trail.overlay;

import adams.gui.visualization.trail.TrailPanel;
import adams.gui.visualization.image.AbstractImageOverlay;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Graphics;

/**
 * Ancestor for trail overlays.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTrailOverlay
  extends AbstractImageOverlay {

  /** for serialization. */
  private static final long serialVersionUID = -8198433620642324789L;

  /** the trail panel. */
  protected TrailPanel m_TrailPanel;

  /**
   * Sets the trail panel this overlay is for.
   *
   * @param value	the panel
   */
  public void setTrailPanel(TrailPanel value) {
    m_TrailPanel = value;
    reset();
  }

  /**
   * Returns the trail panel this overlay is for.
   *
   * @return		the panel
   */
  public TrailPanel getTrailPanel() {
    return m_TrailPanel;
  }

  /**
   * Paints the overlay over the image.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  public void paintOverlay(PaintPanel panel, Graphics g) {
    if (m_TrailPanel != null)
      super.paintOverlay(panel, g);
    else
      getLogger().severe("Not trail panel set!");
  }
}
