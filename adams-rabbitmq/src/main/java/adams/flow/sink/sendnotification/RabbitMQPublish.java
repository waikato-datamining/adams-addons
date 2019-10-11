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
 * RabbitMQPublish.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.sendnotification;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import adams.core.net.rabbitmq.send.StringConverter;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.RabbitMQConnection;
import com.rabbitmq.client.Channel;

/**
 * Publishes the incoming message using the specified exchange or queue.
 * Normally, when using an exchange, leave queue empty, and when using a queue,
 * leave the exchange empty.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQPublish
  extends AbstractNotification {

  private static final long serialVersionUID = -5909332155508918514L;

  /** the name of the exchange. */
  protected String m_Exchange;

  /** the name of the queue. */
  protected String m_Queue;

  /** the connection in use. */
  protected transient RabbitMQConnection m_Connection;

  /** the channel action to use. */
  protected transient Channel m_Channel;
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Publishes the incoming message using the specified exchange or queue.\n"
      + "Normally, when using an exchange, leave queue empty, and when using a queue, leave the exchange empty.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "exchange", "exchange",
      "");

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
    String	result;

    result = QuickInfoHelper.toString(this, "exchange", (m_Exchange.isEmpty() ? "-empty-" : m_Exchange), "exchange: ");
    result += QuickInfoHelper.toString(this, "queue", (m_Queue.isEmpty() ? "-empty-" : m_Queue), ", queue: ");

    return result;
  }

  /**
   * Sets the name of the exchange.
   *
   * @param value	the name
   */
  public void setExchange(String value) {
    m_Exchange = value;
    reset();
  }

  /**
   * Returns the name of the exchange.
   *
   * @return 		the name
   */
  public String getExchange() {
    return m_Exchange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String exchangeTipText() {
    return "The name of the exchange.";
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
   * Hook method before attempting to send the message.
   *
   * @param msg		the message to send
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(String msg) {
    String	result;

    result = super.check(msg);

    if (result == null) {
      if (m_Connection == null) {
	m_Connection = (RabbitMQConnection) ActorUtils.findClosestType(m_FlowContext, RabbitMQConnection.class);
	if (m_Connection == null)
	  result = "No " + RabbitMQConnection.class.getName() + " actor found!";
      }
    }

    if (result == null) {
      if (m_Channel == null) {
	m_Channel = m_Connection.createChannel();
	if (m_Channel == null)
	  result = "Failed to create a channel!";
      }
    }

    return result;
  }

  /**
   * Sends the notification.
   *
   * @param msg		the message to send
   * @return		null if successfully sent, otherwise error message
   */
  @Override
  protected String doSendNotification(String msg) {
    String			result;
    MessageCollection 		errors;
    byte[]			data;
    StringConverter		conv;

    result = null;

    // convert data
    errors = new MessageCollection();
    conv   = new StringConverter();
    data   = conv.convert(msg, errors);
    if (!errors.isEmpty())
      result = errors.toString();

    // send data
    if (result == null) {
      try {
	m_Channel.basicPublish(m_Exchange, m_Queue, null, data);
      }
      catch (Exception e) {
        result = LoggingHelper.handleException(this, "Failed to publish data (exchange=" + m_Exchange + ", queue=" + m_Queue + ")!", e);
      }
    }

    return result;
  }
}
