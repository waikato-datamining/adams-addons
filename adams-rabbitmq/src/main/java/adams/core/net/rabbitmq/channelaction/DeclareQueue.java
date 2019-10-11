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
 * DeclareQueue.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.channelaction;

import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import com.rabbitmq.client.Channel;

/**
 * Declares a queue.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DeclareQueue
  extends AbstractChannelActionWithWait {

  private static final long serialVersionUID = 4097038378479166882L;

  /** the name of the queue. */
  protected String m_Queue;

  /** whether the queue survives a server restart. */
  protected boolean m_Durable;

  /** declaring an exclusive queue (restricted to this connection). */
  protected boolean m_Exclusive;

  /** declaring an autodelete queue (server will delete it when no longer in use). */
  protected boolean m_AutoDelete;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Declares the specified queue.";
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
      "durable", "durable",
      false);

    m_OptionManager.add(
      "exclusive", "exclusive",
      false);

    m_OptionManager.add(
      "auto-delete", "autoDelete",
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
    result += QuickInfoHelper.toString(this, "durable", m_Durable, "durable", ", ");
    result += QuickInfoHelper.toString(this, "exclusive", m_Exclusive, "exclusive", ", ");
    result += QuickInfoHelper.toString(this, "autoDelete", m_AutoDelete, "auto-delete", ", ");
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
   * Sets whether declaring a durable queue (the queue will survive a server restart).
   *
   * @param value	true if durable
   */
  public void setDurable(boolean value) {
    m_Durable = value;
    reset();
  }

  /**
   * Returns whether declaring a durable queue (the queue will survive a server restart).
   *
   * @return 		true if durable
   */
  public boolean getDurable() {
    return m_Durable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String durableTipText() {
    return "If enabled, declaring a durable queue (the queue will survive a server restart).";
  }

  /**
   * Sets whether declaring an exclusive queue (restricted to this connection).
   *
   * @param value	true if exclusive
   */
  public void setExclusive(boolean value) {
    m_Exclusive = value;
    reset();
  }

  /**
   * Returns whether declaring an exclusive queue (restricted to this connection).
   *
   * @return 		true if exclusive
   */
  public boolean getExclusive() {
    return m_Exclusive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String exclusiveTipText() {
    return "If enabled, declaring an exclusive queue (restricted to this connection).";
  }

  /**
   * Sets whether declaring an autodelete queue (server will delete it when no longer in use).
   *
   * @param value	true if auto-delete
   */
  public void setAutoDelete(boolean value) {
    m_AutoDelete = value;
    reset();
  }

  /**
   * Returns whether declaring an autodelete queue (server will delete it when no longer in use).
   *
   * @return 		true if auto-delete
   */
  public boolean getAutoDelete() {
    return m_AutoDelete;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String autoDeleteTipText() {
    return "If enabled, declaring an autodelete queue (server will delete it when no longer in use).";
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
        channel.queueDeclare(m_Queue, m_Durable, m_Exclusive, m_AutoDelete, null);
      else
        channel.queueDeclareNoWait(m_Queue, m_Durable, m_Exclusive, m_AutoDelete, null);
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to declare queue '" + m_Queue + "'!", e);
    }

    return result;
  }
}
