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
import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.Stoppable;
import adams.core.logging.Logger;
import adams.core.net.TwitterHelper;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;
import twitter4j.Status;

/**
 * Ancestor for twitter stream listeners.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13567 $
 */
public abstract class AbstractListener
  extends AbstractOptionHandler
  implements Pausable, Stoppable, QuickInfoSupporter, FlowContextHandler {

  private static final long serialVersionUID = 5406360301457780558L;

  /** the maximum number of status updates to output. */
  protected int m_MaxStatusUpdates;

  /** the owner. */
  protected Actor m_FlowContext;

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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FlowContext = null;
    m_Count       = 0;
    m_Twitter     = null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
            "max-updates", "maxStatusUpdates",
            100, -1, null);
  }

  /**
   * Sets the maximum number of status updates to output.
   *
   * @param value	the maximum number
   */
  public void setMaxStatusUpdates(int value) {
    m_MaxStatusUpdates = value;
    reset();
  }

  /**
   * Returns the maximum number of status updates to output.
   *
   * @return		the maximum number
   */
  public int getMaxStatusUpdates() {
    return m_MaxStatusUpdates;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxStatusUpdatesTipText() {
    return "The maximum number of status updates to output; use <=0 for unlimited.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "maxStatusUpdates", ((m_MaxStatusUpdates <= 0) ? "unlimited " : "" + m_MaxStatusUpdates) + " status updates");
  }

  /**
   * Returns the logger in use.
   *
   * @return		the logger
   */
  @Override
  public synchronized Logger getLogger() {
    if (m_FlowContext != null)
      return m_FlowContext.getLogger();
    else
      return super.getLogger();
  }

  /**
   * Sets the flow context.
   *
   * @param value	the flow context
   */
  public void setFlowContext(Actor value) {
    if (value == null)
      throw new IllegalArgumentException("Flow context cannot be null!");

    m_FlowContext = value;
    m_Count       = 0;
    m_Twitter     = TwitterHelper.getTwitterStreamConnection(getFlowContext());
  }

  /**
   * Returns the flow context.
   *
   * @return		the flow context
   */
  public Actor getFlowContext() {
    return m_FlowContext;
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
   * Removes the listener.
   */
  protected abstract void removeListener();

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Listening = false;
    m_Paused    = false;
    removeListener();
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
      if (getFlowContext().isLoggingEnabled() && (m_Count % 100 == 0))
	getLogger().info("status updates: " + m_Count);
    }

    m_Next = null;

    return result;
  }
}
