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
 * RabbitMQConnection.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.scripting.connection;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import adams.core.net.rabbitmq.RabbitMQHelper;
import adams.core.net.rabbitmq.connection.AbstractConnectionFactory;
import adams.core.net.rabbitmq.connection.GuestConnectionFactory;
import adams.core.net.rabbitmq.send.AbstractConverter;
import adams.core.net.rabbitmq.send.StringConverter;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.RemoteCommandWithResponse;
import adams.scripting.processor.RemoteCommandProcessor;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Uses RabbitMQ to send commands via a exchange or queue.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQConnection
  extends AbstractConnection {

  private static final long serialVersionUID = 7719866884762680511L;

  /** the connection to use. */
  protected AbstractConnectionFactory m_ConnectionFactory;

  /** the prefetch count. */
  protected int m_PrefetchCount;

  /** the name of the exchange. */
  protected String m_Exchange;

  /** the queue in use. */
  protected String m_Queue;

  /** the converter for sending. */
  protected AbstractConverter m_SendConverter;

  /** the connection. */
  protected transient com.rabbitmq.client.Connection m_Connection;

  /** the channel action to use. */
  protected transient Channel m_Channel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses a RabbitMQ queue to send commands.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "connection-factory", "connectionFactory",
      new GuestConnectionFactory());

    m_OptionManager.add(
      "prefetch-count", "prefetchCount",
      1, 0, null);

    m_OptionManager.add(
      "exchange", "exchange",
      "");

    m_OptionManager.add(
      "queue", "queue",
      "");

    m_OptionManager.add(
      "send-converter", "sendConverter",
      new StringConverter());
  }

  /**
   * Sets the connection factory to use.
   *
   * @param value	the factory
   */
  public void setConnectionFactory(AbstractConnectionFactory value) {
    m_ConnectionFactory = value;
    reset();
  }

  /**
   * Returns the connection factory to use.
   *
   * @return		the factory
   */
  public AbstractConnectionFactory getConnectionFactory() {
    return m_ConnectionFactory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String connectionFactoryTipText() {
    return "The connection factory to use.";
  }

  /**
   * Sets the maximum number of unacked jobs a client can pull off a queue.
   *
   * @param value	the count, 0 = unlimited, 1 = fair
   */
  public void setPrefetchCount(int value) {
    m_PrefetchCount = value;
    reset();
  }

  /**
   * Returns the maximum number of unacked jobs a client can pull off a queue.
   *
   * @return		the count, 0 = unlimited, 1 = fair
   */
  public int getPrefetchCount() {
    return m_PrefetchCount;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefetchCountTipText() {
    return "The number of un-acked jobs a client can pull off a queue; 0 = unlimited, 1 = fair.";
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
   * Sets the converter to use for sending.
   *
   * @param value	the converter
   */
  public void setSendConverter(AbstractConverter value) {
    m_SendConverter = value;
    reset();
  }

  /**
   * Returns the converter to use for sending.
   *
   * @return 		the converter
   */
  public AbstractConverter getSendConverter() {
    return m_SendConverter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String sendConverterTipText() {
    return "The converter to use for sending.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "connectionFactory", m_ConnectionFactory, "connection: ");
    result += QuickInfoHelper.toString(this, "prefetchCount", (m_PrefetchCount == 0 ? "unlimited" : "" + m_PrefetchCount), ", prefetch: ");
    result += QuickInfoHelper.toString(this, "exchange", (m_Exchange.isEmpty() ? "-empty-" : m_Exchange), ", exchange: ");
    result += QuickInfoHelper.toString(this, "queue", (m_Queue.isEmpty() ? "-empty-" : m_Queue), ", queue: ");
    result += QuickInfoHelper.toString(this, "sendConverter", m_SendConverter, ", send: ");

    return result;
  }

  /**
   * Starts up a RabbitMQ connection.
   *
   * @return		null if OK, otherwise error message
   */
  protected String connect() {
    String		result;
    ConnectionFactory 	factory;
    MessageCollection	errors;

    result = null;

    errors = new MessageCollection();
    factory = m_ConnectionFactory.generate(errors);
    if (!errors.isEmpty())
      return null;

    try {
      m_Connection = factory.newConnection();
      if (m_Connection == null)
	result = "Failed to connect to broker (" + m_ConnectionFactory + ")!";
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to connect to broker (" + m_ConnectionFactory + ")!", e);
    }

    if (result == null) {
      try {
	m_Channel = m_Connection.createChannel();
	if (m_Channel == null)
	  result = "Failed to create a channel!";
	else
	  m_Channel.basicQos(m_PrefetchCount);
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to create channel!", e);
      }
    }

    return result;
  }

  /**
   * Disconnects the FTP session, if necessary.
   */
  protected void disconnect() {
    RabbitMQHelper.closeQuietly(m_Channel);
    RabbitMQHelper.closeQuietly(m_Connection);
    m_Channel    = null;
    m_Connection = null;
  }

  /**
   * Returns the channel object.
   *
   * @return		the FTP client, null if failed to connect
   */
  protected Channel getChannel() {
    if (m_Channel == null)
      connect();

    return m_Channel;
  }

  /**
   * Sends the command to the specified sscripting engine.
   *
   * @param cmd		the command to send
   * @param processor 	for formatting/parsing
   * @return		null if successfully sent, otherwise error message
   */
  protected String doSend(RemoteCommand cmd, RemoteCommandProcessor processor) {
    String 		result;
    String 		msg;
    byte[] 		data;
    MessageCollection 	errors;

    result = null;

    if (m_Channel == null)
      result = connect();

    // assemble message
    msg = null;
    if (result == null) {
      if (cmd.isRequest()) {
	msg = cmd.assembleRequest(processor);
      }
      else {
	if (cmd instanceof RemoteCommandWithResponse)
	  msg = ((RemoteCommandWithResponse) cmd).assembleResponse(processor);
	else
	  result = "Remote command is not a response but flagged as such:\n" + cmd.toString();
      }
    }

    data   = null;
    errors = new MessageCollection();
    if (result == null) {
      if (msg == null)
	result = "Failed to assemble message: " + cmd;
      else
	data = m_SendConverter.convert(msg, errors);
    }

    if (data != null) {
      try {
	m_Channel.basicPublish(m_Exchange, m_Queue, null, data);
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to publish data (exchange=" + m_Exchange + ", queue=" + m_Queue + ")!", e);
      }
    }

    return result;
  }

  /**
   * Sends the request command.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doSendRequest(RemoteCommand cmd, RemoteCommandProcessor processor) {
    return doSend(cmd, processor);
  }

  /**
   * Sends the response command.
   *
   * @param cmd		the command to send
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doSendResponse(RemoteCommand cmd, RemoteCommandProcessor processor) {
    return doSend(cmd, processor);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    disconnect();
    super.cleanUp();
  }
}
