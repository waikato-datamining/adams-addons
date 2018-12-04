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
 * RatControlState.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.ratcontrol;

import adams.flow.standalone.Rat;

/**
 * Control state for {@link Rat} actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RatControlState
  extends AbstractControlState<Rat> {

  /** for serialization. */
  private static final long serialVersionUID = 4516229240505598425L;

  /** the button for stopping/starting. */
  protected boolean m_StopStart;

  /**
   * Stops the rat.
   *
   * @return		null if successful, otherwise error message
   */
  public String stop() {
    if (m_Actor == null)
      return null;

    if (m_Actor.isRunnableActive())
      m_Actor.stopRunnable();
    return null;
  }

  /**
   * Starts the rat.
   *
   * @return		null if successful, otherwise error message
   */
  public String start() {
    String	result;

    if (m_Actor == null)
      return null;

    result = null;
    if (!m_Actor.isRunnableActive())
      result = m_Actor.startRunnable();

    return result;
  }

  /**
   * Stops/starts the rat.
   *
   * @return		null if successful, otherwise error message
   */
  public String stopOrStart() {
    if (m_Actor == null)
      return null;

    if (m_Actor.isRunnableActive())
      return stop();
    else
      return start();
  }

  /**
   * Updates the state of the buttons.
   */
  public void updateButtons() {
    if (m_Actor == null)
      return;

    m_PauseResume = m_Actor.isRunnableActive();
  }

  /**
   * Sets the "stoppable" state of the control staet.
   *
   * @param value	true if to enable
   */
  public void setStoppable(boolean value) {
    m_StopStart = value;
  }

  /**
   * Returns whether the "stoppable" state of the control state is enabled.
   *
   * @return		true if enabled
   */
  public boolean isStoppable() {
    return m_StopStart;
  }
}
