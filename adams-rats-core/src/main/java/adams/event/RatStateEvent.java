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
 * RatStateEvent.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.event;

import adams.flow.standalone.Rat;

import java.util.EventObject;

/**
 * Gets sent if a rat state changes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RatStateEvent
  extends EventObject {

  private static final long serialVersionUID = -1667596669270360853L;

  /** the Rat that has changed. */
  protected Rat m_Rat;

  /**
   * Initializes event.
   *
   * @param source	the sender of the event
   * @param rat		the rat that changed
   */
  public RatStateEvent(Object source, Rat rat) {
    super(source);

    m_Rat = rat;
  }

  /**
   * Returns the rat that changed.
   *
   * @return		the rat
   */
  public Rat getRat() {
    return m_Rat;
  }
}
