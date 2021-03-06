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
 * AbstractTrailViewerPlugin.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.trail.plugins;

import adams.gui.plugin.AbstractToolPlugin;
import adams.gui.visualization.trail.TrailPanel;

/**
 * Ancestor for plugins for the TrailViewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTrailViewerPlugin
  extends AbstractToolPlugin<TrailPanel> {

  /** for serialization. */
  private static final long serialVersionUID = -8139858776265449470L;

  /**
   * Performs the actual logging.
   *
   * @param msg		the message to log
   */
  protected void doLog(String msg) {
    m_CurrentPanel.log(msg);
  }
}
