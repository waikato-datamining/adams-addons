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
 * AbstractControlState.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.ratcontrol;

import adams.core.Pausable;
import adams.core.logging.LoggingObject;
import adams.flow.core.Actor;
import adams.flow.standalone.RatControl;
import adams.flow.standalone.Rats;

/**
 * Ancestor for control states.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractControlState<T extends Actor & Pausable>
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -5965060223206287867L;

  /** the owner. */
  protected RatControl m_Owner;

  /** the rats group this belongs to. */
  protected Rats m_Group;

  /** the actor to manage. */
  protected T m_Actor;

  /** the button for pausing/resuming. */
  protected boolean m_PauseResume;

  /**
   * Sets the RatControl actor this control belongs to.
   *
   * @param value	the owner
   */
  public void setOwner(RatControl value) {
    m_Owner = value;
  }

  /**
   * Returns the RatControl actor this control belongs to.
   *
   * @return		the owner
   */
  public RatControl getOwner() {
    return m_Owner;
  }

  /**
   * Sets the Rats groups this control belongs to.
   *
   * @param value	the group
   */
  public void setGroup(Rats value) {
    m_Group = value;
  }

  /**
   * Returns the Rats group this control belongs to.
   *
   * @return		the group
   */
  public Rats getGroup() {
    return m_Group;
  }

  /**
   * Sets the actor to manage.
   *
   * @param value	the actor
   */
  public void setActor(T value) {
    m_Actor = value;
  }

  /**
   * Returns the actor in use.
   *
   * @return		the actor
   */
  public T getActor() {
    return m_Actor;
  }

  /**
   * Pauses the rat.
   */
  public void pause() {
    if (m_Actor == null)
      return;
    m_Actor.pauseExecution();
  }

  /**
   * Resumes the rat.
   */
  public void resume() {
    if (m_Actor == null)
      return;
    m_Actor.resumeExecution();
  }

  /**
   * Pauses/resumes the rat.
   */
  public void pauseOrResume() {
    if (m_Actor == null)
      return;
    if (m_Actor.isPaused())
      resume();
    else
      pause();
  }

  /**
   * Sets the "pauseable" state of the control panel.
   *
   * @param value	true if to enable
   */
  public void setPausable(boolean value) {
    m_PauseResume = value;
  }

  /**
   * Returns whether the control panel is enabled.
   *
   * @return		true if enabled
   */
  public boolean isPausable() {
    return m_PauseResume;
  }
}
