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
 * RabbitMQScriptingEngine.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.scripting.engine;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.net.rabbitmq.RabbitMQHelper;
import adams.core.net.rabbitmq.channelaction.AbstractChannelAction;
import adams.core.net.rabbitmq.channelaction.NoAction;
import adams.core.net.rabbitmq.connection.AbstractConnectionFactory;
import adams.core.net.rabbitmq.connection.GuestConnectionFactory;
import adams.core.net.rabbitmq.receive.AbstractConverter;
import adams.core.net.rabbitmq.receive.StringConverter;
import adams.multiprocess.PausableFixedThreadPoolExecutor;
import adams.scripting.command.RemoteCommand;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * RabbitMQ implementation of scripting engine for remote commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQScriptingEngine
  extends AbstractScriptingEngineWithJobQueue {

  private static final long serialVersionUID = -3763240773922918567L;

  /** the connection to use. */
  protected AbstractConnectionFactory m_ConnectionFactory;

  /** the prefetch count. */
  protected int m_PrefetchCount;

  /** the action to execute. */
  protected AbstractChannelAction m_Action;

  /** the name of the exchange. */
  protected String m_Exchange;

  /** the name of the queue. */
  protected String m_Queue;

  /** the converter. */
  protected AbstractConverter m_Converter;

  /** the current connection. */
  protected transient Connection m_Connection;

  /** the channel action to use. */
  protected transient Channel m_Channel;

  /** the data that has been received. */
  protected ArrayBlockingQueue<Object> m_Data;

  /** the ack tags that have been received. */
  protected ArrayBlockingQueue<Long> m_Tags;

  /** the internal timeout interval for polling the queue in msec. */
  protected int m_PollTimeout;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Listens for commands using a RabbitMQ exchange or queue.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Data        = null;
    m_Tags        = null;
    m_PollTimeout = 100;
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
      "action", "action",
      new NoAction());

    m_OptionManager.add(
      "exchange", "exchange",
      "");

    m_OptionManager.add(
      "queue", "queue",
      "");

    m_OptionManager.add(
      "converter", "converter",
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
   * Sets the action to run.
   *
   * @param value	the action
   */
  public void setAction(AbstractChannelAction value) {
    m_Action = value;
    reset();
  }

  /**
   * Returns the action to run.
   *
   * @return 		the action
   */
  public AbstractChannelAction getAction() {
    return m_Action;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String actionTipText() {
    return "The channel action to execute.";
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
   * Sets the converter to use.
   *
   * @param value	the converter
   */
  public void setConverter(AbstractConverter value) {
    m_Converter = value;
    reset();
  }

  /**
   * Returns the converter to use.
   *
   * @return 		the converter
   */
  public AbstractConverter getConverter() {
    return m_Converter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String converterTipText() {
    return "The converter to use.";
  }

  /**
   * Closes the connection.
   */
  protected void close() {
    RabbitMQHelper.closeQuietly(m_Connection);
    RabbitMQHelper.closeQuietly(m_Channel);
    m_Channel    = null;
    m_Connection = null;
  }

  /**
   * Starts up a RabbitMQ connection.
   *
   * @return		null if OK, otherwise error message
   */
  protected String connect() {
    String		result;
    ConnectionFactory factory;
    MessageCollection errors;

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

    if (result == null)
      result = m_Action.performAction(m_Channel);

    return result;
  }

  /**
   * Handles the received object.
   *
   * @param data	the data received
   * @param tag		the tag to use for the ack
   */
  protected void handleMessage(Object data, Long tag) {
    boolean		ack;
    RemoteCommand 	cmd;
    MessageCollection	errors;
    String		msg;

    ack = false;

    // instantiate command
    errors = new MessageCollection();
    cmd    = m_CommandProcessor.parse("" + data, errors);

    if (cmd != null) {
      // permitted?
      if (!m_PermissionHandler.permitted(cmd)) {
	m_RequestHandler.requestRejected(cmd, "Not permitted!");
	return;
      }

      // handle command
      msg = m_CommandHandler.handle(cmd, m_CommandProcessor);
      if (msg != null)
	getLogger().severe("Failed to handle command:\n" + msg);
      else
        ack = true;
    }
    else {
      if (!errors.isEmpty())
	getLogger().severe("Failed to parse command:\n" + errors.toString());
      else
	getLogger().severe("Failed to parse command:\n" + data);
    }

    if (ack && (tag != null)) {
      try {
	m_Channel.basicAck(tag, false);
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Failed to send ack!", e);
      }
    }
  }

  /**
   * Executes the scripting engine.
   *
   * @return		error message in case of failure to start up or run,
   * 			otherwise null
   */
  @Override
  protected String doExecute() {
    String		result;
    DeliverCallback 	deliverCallback;
    String 		queue;
    Object		data;
    Long		tag;

    m_Paused  = false;
    m_Stopped = false;
    result    = connect();

    queue           = "";
    deliverCallback = null;
    if (result == null) {
      // ensure queue is cleared
      if (m_Data == null) {
	m_Data = new ArrayBlockingQueue<>(65536);
	m_Tags = new ArrayBlockingQueue<>(65536);
      }
      m_Data.clear();
      m_Tags.clear();

      // callback
      deliverCallback = (consumerTag, delivery) -> {
	byte[] recv = delivery.getBody();
	MessageCollection errors = new MessageCollection();
	Object output = m_Converter.convert(recv, errors);
	m_Tags.add(delivery.getEnvelope().getDeliveryTag());
	m_Data.add(output);
      };

      // determine queue name
      if (m_Exchange.isEmpty()) {
	queue = m_Queue;
      }
      else {
	try {
	  queue = m_Channel.queueDeclare().getQueue();
	  m_Channel.queueBind(queue, m_Exchange, "");
	}
	catch (Exception e) {
	  result = LoggingHelper.handleException(this, "Failed to bind queue to exchange!", e);
	}
      }
    }

    // wait for connections
    if (m_Channel != null) {
      // start up job queue
      m_Executor = new PausableFixedThreadPoolExecutor(m_MaxConcurrentJobs);

      while (!m_Stopped) {
	while (m_Paused && !m_Stopped) {
	  Utils.wait(this, this, 1000, 50);
	}

	try {
	  m_Channel.basicConsume(queue, false, deliverCallback, consumerTag -> {});
	}
	catch (Exception e) {
	  result = LoggingHelper.handleException(this, "Failed to consume data!", e);
	}

	data = null;
	while (!isStopped() && (data == null)) {
	  try {
	    data = m_Data.poll(m_PollTimeout, TimeUnit.MILLISECONDS);
	    if (data != null) {
	      tag = m_Tags.poll();
	      if (tag == null)
	        getLogger().severe("No tag for ack received");
	      handleMessage(data, tag);
	    }
	  }
	  catch (Exception e) {
	    if (isLoggingEnabled())
	      getLogger().log(Level.INFO, "Exception while polling", e);
	  }
	}
      }
    }

    close();

    if ((m_Executor != null) && !m_Executor.isTerminated()) {
      getLogger().info("Shutting down job queue...");
      m_Executor.shutdown();
      while (!m_Executor.isTerminated())
	Utils.wait(this, 1000, 100);
      getLogger().info("Job queue shut down");
    }

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    close();
  }

  /**
   * Starts the scripting engine from commandline.
   *
   * @param args  	additional options for the scripting engine
   */
  public static void main(String[] args) {
    runScriptingEngine(RabbitMQScriptingEngine.class, args);
  }
}
