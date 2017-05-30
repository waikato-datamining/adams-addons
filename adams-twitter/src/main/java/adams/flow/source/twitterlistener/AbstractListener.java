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
 * AbstractListener.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.twitterlistener;

import adams.core.Pausable;
import adams.core.QuickInfoSupporter;
import adams.core.Stoppable;
import adams.core.logging.Logger;
import adams.core.net.TwitterHelper;
import adams.core.option.AbstractOptionHandler;
import adams.flow.source.TwitterListener;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.util.logging.Level;

/**
 * Ancestor for twitter stream listeners.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13567 $
 */
public abstract class AbstractListener
  extends AbstractOptionHandler
  implements Pausable, Stoppable, StatusListener, QuickInfoSupporter {

  private static final long serialVersionUID = 5406360301457780558L;

  /** the owner. */
  protected TwitterListener m_Owner;

  /** for accessing the twitter streaming API. */
  protected transient twitter4j.TwitterStream m_Twitter;

  /** the counter for tweets. */
  protected int m_Count;

  /** the next available Status. */
  protected Status m_Next;

  /** the listener is paused. */
  protected boolean m_Paused;

  /** whether the listener is running. */
  protected boolean m_Listening;

  /**
   * Initializes the listener.
   */
  public AbstractListener() {
    super();

    m_Owner   = null;
    m_Count   = 0;
    m_Twitter = null;
  }

  /**
   * Returns the logger in use.
   *
   * @return		the logger
   */
  @Override
  public synchronized Logger getLogger() {
    if (m_Owner != null)
      return m_Owner.getLogger();
    else
      return super.getLogger();
  }

  /**
   * Sets the owner.
   *
   * @param value	the owning actor
   */
  public void setOwner(TwitterListener value) {
    if (value == null)
      throw new IllegalArgumentException("Owner cannot be null!");

    m_Owner   = value;
    m_Count   = 0;
    m_Twitter = TwitterHelper.getTwitterStreamConnection(getOwner());
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public TwitterListener getOwner() {
    return m_Owner;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Starts the listening.
   */
  public abstract void startExecution();

  /**
   * Returns whether the listener is active.
   *
   * @return		true if active
   */
  public boolean isListening() {
    return m_Listening;
  }

  /**
   * Pauses the execution (if still listening).
   */
  @Override
  public void pauseExecution() {
    if (m_Listening)
      m_Paused = true;
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  @Override
  public boolean isPaused() {
    return m_Paused;
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    m_Paused = false;
  }

  /**
   * When receiving a status.
   *
   * @param status	the status
   */
  @Override
  public void onStatus(Status status) {
    if (m_Listening && !m_Paused) {
      if ((getOwner().getMaxStatusUpdates() > 0) && (m_Count >= getOwner().getMaxStatusUpdates()))
	stopExecution();
      else
	m_Next = status;
    }
  }

  /**
   * Ignored.
   *
   * @param statusDeletionNotice
   */
  @Override
  public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
  }

  /**
   * Ignored.
   *
   * @param i
   */
  @Override
  public void onTrackLimitationNotice(int i) {
  }

  /**
   * Ignored.
   *
   * @param l
   * @param l1
   */
  @Override
  public void onScrubGeo(long l, long l1) {
  }

  /**
   * Outputs a stall warning.
   *
   * @param stallWarning	the warning
   */
  @Override
  public void onStallWarning(StallWarning stallWarning) {
    getLogger().warning(stallWarning.toString());
  }

  /**
   * Gets called if an exception is encountered.
   *
   * @param e			the exception
   */
  @Override
  public void onException(Exception e) {
    // TODO stop listening?
    getLogger().log(Level.SEVERE, "Exception encountered while listening!", e);
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Listening = false;
    m_Paused    = false;
    m_Twitter.removeListener(this);
    try {
      m_Twitter.shutdown();
    }
    catch (Exception e) {
      // ignored
    }
    m_Twitter.cleanUp();
  }

  /**
   * Returns whether there is another update available.
   *
   * @return		true if another update available
   */
  public boolean hasNext() {
    return m_Listening || (m_Next != null);
  }

  /**
   * Retrieves the next status update.
   *
   * @return		the next status
   */
  public Status next() {
   Status	result;
    int	count;

    result = null;

    count = 0;
    while (result == null) {
      result = m_Next;
      count++;

      if (result == null) {
	if (m_Listening) {
	  try {
	    synchronized(this) {
	      wait(50);
	    }
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}
	else {
	  break;
	}
      }

      // problem with obtaining data?
      if (count == 100)
	break;
    }

    // only increment counter when the status update was actually used
    if (result != null) {
      m_Count++;
      if (getOwner().isLoggingEnabled() && (m_Count % 100 == 0))
	getLogger().info("status updates: " + m_Count);
    }

    m_Next = null;

    return result;
  }
}
