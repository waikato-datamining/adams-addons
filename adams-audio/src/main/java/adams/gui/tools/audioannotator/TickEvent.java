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

/*
 * TickEvent.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.audioannotator;

import java.util.Date;

/**
 * A Tick event to alert listeners and pass the timestamp through to them.
 *
 * @author sjb90
 */
public class TickEvent {

  /** the timestamp */
  protected Date m_TimeStamp;

  /**
   * Constructor for the basic tick event
   * @param time the timestamp to be passed to the listener
   */
  public TickEvent(Date time) {
    m_TimeStamp = time;
  }

  /**
   * A getter for the timestamp
   * @return the timestamp
   */
  public Date getTimeStamp() {
    return m_TimeStamp;
  }

}
