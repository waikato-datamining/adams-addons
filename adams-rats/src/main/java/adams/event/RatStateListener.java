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
 * RatStateListener.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.event;

/**
 * Interface for classes that listen to Rat state changes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RatStateListener {

  /**
   * Gets called in case the state of a Rat actor changes.
   *
   * @param e		the event
   */
  public void ratStateChanged(RatStateEvent e);
}
