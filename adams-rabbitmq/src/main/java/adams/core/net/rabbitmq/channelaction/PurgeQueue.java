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
 * PurgeQueue.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.channelaction;

import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import com.rabbitmq.client.Channel;

/**
 * Purges a queue.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PurgeQueue
  extends AbstractChannelAction {

  private static final long serialVersionUID = 4097038378479166882L;

  /** the name of the queue. */
  protected String m_Queue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Purges the specified queue.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "queue", "queue",
      "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "queue", (m_Queue.isEmpty() ? "-empty-" : m_Queue), "queue: ");
  }

  /**
   * Sets the name of the queue.
   *
   * @param value	the name
   */
  public void setQueue(String value) {
    m_Queue = value;
    reset();
  }

  /**
   * Returns the name of the queue.
   *
   * @return 		the name
   */
  public String getQueue() {
    return m_Queue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String queueTipText() {
    return "The name of the queue.";
  }

  /**
   * Performs the action.
   *
   * @param channel	the channel to operate on
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doPerformAction(Channel channel) {
    String	result;

    result = null;

    try {
      channel.queuePurge(m_Queue);
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to purge queue '" + m_Queue + "'!", e);
    }

    return result;
  }
}
