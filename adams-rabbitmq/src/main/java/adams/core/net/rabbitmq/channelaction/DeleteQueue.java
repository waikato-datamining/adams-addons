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
 * DeleteQueue.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.channelaction;

import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import com.rabbitmq.client.Channel;

/**
 * Deletes a queue.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DeleteQueue
  extends AbstractChannelActionWithWait {

  private static final long serialVersionUID = 4097038378479166882L;

  /** the name of the queue. */
  protected String m_Queue;

  /** whether the queue gets only deleted if not used. */
  protected boolean m_IfUnused;

  /** whether the queue gets only deleted if empty. */
  protected boolean m_IfEmpty;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Deletes the specified queue.";
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

    m_OptionManager.add(
      "if-unused", "ifUnused",
      false);

    m_OptionManager.add(
      "if-empty", "ifEmpty",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "queue", (m_Queue.isEmpty() ? "-empty-" : m_Queue), "queue: ");
    result += QuickInfoHelper.toString(this, "ifUnused", m_IfUnused, "if unused", ", ");
    result += QuickInfoHelper.toString(this, "ifEmpty", m_IfEmpty, "if empty", ", ");
    result += QuickInfoHelper.toString(this, "wait", m_Wait, "wait", ", ");

    return result;
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
   * Sets whether to delete the queue only if not used.
   *
   * @param value	true if durable
   */
  public void setIfUnused(boolean value) {
    m_IfUnused = value;
    reset();
  }

  /**
   * Returns whether to delete the queue only if not used.
   *
   * @return 		true if durable
   */
  public boolean getIfUnused() {
    return m_IfUnused;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String ifUnusedTipText() {
    return "If enabled, only gets deleted if not used.";
  }

  /**
   * Sets whether to delete the queue only if empty.
   *
   * @param value	true if only if empty
   */
  public void setIfEmpty(boolean value) {
    m_IfEmpty = value;
    reset();
  }

  /**
   * Returns whether to delete the queue only if empty.
   *
   * @return 		true if only if empty
   */
  public boolean getIfEmpty() {
    return m_IfEmpty;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String ifEmptyTipText() {
    return "If enabled, only gets deleted if empty.";
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
      if (m_Wait)
	channel.queueDelete(m_Queue, m_IfUnused, m_IfEmpty);
      else
	channel.queueDeleteNoWait(m_Queue, m_IfUnused, m_IfEmpty);
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to delete queue '" + m_Queue + "'!", e);
    }

    return result;
  }
}
