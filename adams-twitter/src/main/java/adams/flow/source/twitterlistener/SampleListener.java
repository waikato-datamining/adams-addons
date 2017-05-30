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
 * SampleListener.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.twitterlistener;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.util.logging.Level;

/**
 * Listener for twitter sample stream.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13567 $
 */
public class SampleListener
  extends AbstractListener
  implements StatusListener {

  private static final long serialVersionUID = 5406360301457780558L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs status updates obtained from the 'garden hose'.";
  }

  /**
   * Starts the listening.
   */
  public void startExecution() {
    try {
      m_Twitter.addListener(this);
      m_Twitter.sample();
      m_Listening = true;
    }
    catch (Exception e) {
      m_Twitter.removeListener(this);
      getLogger().log(Level.SEVERE, "Failed to start listener!", e);
    }
  }

  /**
   * Removes the listener.
   */
  @Override
  protected void removeListener() {
    m_Twitter.removeListener(this);
  }

  /**
   * When receiving a status.
   *
   * @param status	the status
   */
  @Override
  public void onStatus(Status status) {
    if (m_Listening && !m_Paused) {
      if ((getMaxStatusUpdates() > 0) && (m_Count >= getMaxStatusUpdates()))
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
}
