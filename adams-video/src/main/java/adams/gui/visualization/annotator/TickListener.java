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
 * TickListener.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;

/**
 * Listener interface for tick events.
 *
 * @author sjb90
 * @version $Revision$
 */
public interface TickListener {

  /**
   * Called by the object the listener is registered with to aleart the Ticklistener that a tick has happened
   * @param e an event that contains information about the tick
   */
  void tickHappened(TickEvent e);

  long getInterval();
}
