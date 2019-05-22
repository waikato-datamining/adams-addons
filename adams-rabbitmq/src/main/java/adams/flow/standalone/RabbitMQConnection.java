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
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.net.rabbitmq.RabbitMQHelper;
import adams.core.net.rabbitmq.connection.AbstractConnectionFactory;
import adams.core.net.rabbitmq.connection.GuestConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Defines a connection to a RabbitMQ broker.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: RabbitMQConnection
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-connection-factory &lt;adams.core.net.rabbitmq.connection.AbstractConnectionFactory&gt; (property: connectionFactory)
 * &nbsp;&nbsp;&nbsp;The connection factory to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.net.rabbitmq.connection.GuestConnectionFactory
 * </pre>
 *
 * <pre>-prefetch-count &lt;int&gt; (property: prefetchCount)
 * &nbsp;&nbsp;&nbsp;The number of un-acked jobs a client can pull off a queue; 0 = unlimited,
 * &nbsp;&nbsp;&nbsp; 1 = fair.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQConnection
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -1726172998200420556L;

  /** the connection to use. */
  protected AbstractConnectionFactory m_ConnectionFactory;

  /** the prefetch count. */
  protected int m_PrefetchCount;

  /** the connection. */
  protected transient com.rabbitmq.client.Connection m_Connection;

  /** the auto-created queues that need to get deleted again. */
  protected List<String> m_AutoCreatedQueues;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines a connection to a RabbitMQ broker.";
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
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_AutoCreatedQueues = new ArrayList<>();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String  result;

    result = QuickInfoHelper.toString(this, "connectionFactory", m_ConnectionFactory);
    result += QuickInfoHelper.toString(this, "prefetchCount", (m_PrefetchCount == 0 ? "unlimited" : "" + m_PrefetchCount), ", prefetch: ");

    return result;
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
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    com.rabbitmq.client.Connection	conn;
    MessageCollection			errors;

    result = null;
    errors = new MessageCollection();
    conn   = retrieveConnection(errors);
    if ((conn == null) || !conn.isOpen()) {
      if (!errors.isEmpty())
	result = errors.toString();
      else
	result = "Failed to connect to broker (" + m_ConnectionFactory + ")!";
    }

    return result;
  }

  /**
   * Returns the database connection in use. Reconnects the database, to make
   * sure that the database connection is the correct one.
   *
   * @param errors 	for collecting errors, can be null
   * @return		the connection object
   */
  protected com.rabbitmq.client.Connection retrieveConnection(MessageCollection errors) {
    ConnectionFactory 	factory;

    if (errors == null)
      errors = new MessageCollection();
    factory = m_ConnectionFactory.generate(errors);
    if (!errors.isEmpty())
      return null;

    try {
      return factory.newConnection();
    }
    catch (Exception e) {
      errors.add("Failed to connect to broker (" + m_ConnectionFactory + ")!", e);
      handleException("Failed to connect to broker (" + m_ConnectionFactory + ")!", e);
      return null;
    }
  }

  /**
   * Returns the database connection in use. Reconnects the database, to make
   * sure that the database connection is the correct one.
   *
   * @return		the connection object
   */
  public com.rabbitmq.client.Connection getConnection() {
    if (m_Connection == null)
      m_Connection = retrieveConnection(null);
    return m_Connection;
  }

  /**
   * Creates a new channel and returns it.
   *
   * @return		the channel, null if failed to create or no connection available
   */
  public Channel createChannel() {
    return RabbitMQHelper.createChannel(this, getConnection(), m_PrefetchCount);
  }

  /**
   * Adds the queue to the list of queues that were automatically created
   * and need deleting when the flow wraps up.
   *
   * @param queue	the queue to add
   */
  public void addAutoCreatedQueue(String queue) {
    m_AutoCreatedQueues.add(queue);
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    RabbitMQHelper.closeQuietly(m_Connection, m_AutoCreatedQueues);
    m_Connection = null;

    super.wrapUp();
  }
}
