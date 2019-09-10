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
 * TwitterListener.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.input;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.net.rabbitmq.RabbitMQHelper;
import adams.core.net.rabbitmq.receive.AbstractConverter;
import adams.core.net.rabbitmq.receive.StringConverter;
import adams.flow.container.RabbitMQConsumptionContainer;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.RabbitMQChannelAction;
import adams.flow.standalone.RabbitMQConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 <!-- globalinfo-start -->
 * Consumes data it receives and forwards it.<br>
 * It either binds to the specified exchange (if non-empty and ignores the queue name), or it listens to the specified queue.<br>
 * When using an exchange, this one must be declared via the adams.flow.standalone.RabbitMQChannelAction standalone.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-max-buffer &lt;int&gt; (property: maxBuffer)
 * &nbsp;&nbsp;&nbsp;The maximum number of items to buffer.
 * &nbsp;&nbsp;&nbsp;default: 65535
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-exchange &lt;java.lang.String&gt; (property: exchange)
 * &nbsp;&nbsp;&nbsp;The name of the exchange.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-queue &lt;java.lang.String&gt; (property: queue)
 * &nbsp;&nbsp;&nbsp;The name of the queue.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-converter &lt;adams.core.net.rabbitmq.receive.AbstractConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The converter to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.net.rabbitmq.receive.StringConverter
 * </pre>
 *
 * <pre>-limit &lt;int&gt; (property: limit)
 * &nbsp;&nbsp;&nbsp;The limit for data objects received; use &lt;= 0 for unlimited size.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-output-container &lt;boolean&gt; (property: outputContainer)
 * &nbsp;&nbsp;&nbsp;If enabled, outputs the data along side any properties in a adams.flow.container.RabbitMQConsumptionContainer.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQConsume
  extends AbstractBufferedRatInput {

  private static final long serialVersionUID = 7627032152241150448L;

  /** the name of the exchange. */
  protected String m_Exchange;

  /** the name of the queue. */
  protected String m_Queue;

  /** the converter. */
  protected AbstractConverter m_Converter;

  /** the limit of the queue (<= 0 is unlimited). */
  protected int m_Limit;

  /** whether to output a container. */
  protected boolean m_OutputContainer;

  /** the current connection. */
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
    return "Consumes data it receives and forwards it.\n"
      + "It either binds to the specified exchange (if non-empty and ignores the queue name), "
      + "or it listens to the specified queue.\n"
      + "When using an exchange, this one must be declared via the " + Utils.classToString(RabbitMQChannelAction.class) + " standalone.";
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

    m_OptionManager.add(
      "converter", "converter",
      new StringConverter());

    m_OptionManager.add(
      "limit", "limit",
      -1, -1, null);

    m_OptionManager.add(
      "output-container", "outputContainer",
      false);
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
   * Sets the limit for data objects received.
   *
   * @param value	the limit, <=0 for unlimited
   */
  public void setLimit(int value) {
    if (value <= 0)
      value = -1;
    m_Limit = value;
    reset();
  }

  /**
   * Returns the limit for data objects received.
   *
   * @return		the limit, <=0 is unlimited
   */
  public int getLimit() {
    return m_Limit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String limitTipText() {
    return "The limit for data objects received; use <= 0 for unlimited size.";
  }

  /**
   * Sets whether to output a container with the data alongside any properties.
   *
   * @param value 	true if to output the container
   */
  public void setOutputContainer(boolean value) {
    m_OutputContainer = value;
    reset();
  }

  /**
   * Returns whether to output a container with the data alongside any properties.
   *
   * @return 		true if to output the container
   */
  public boolean getOutputContainer() {
    return m_OutputContainer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputContainerTipText() {
    return
      "If enabled, outputs the data along side any properties in a "
	+ Utils.classToString(RabbitMQConsumptionContainer.class) + ".";
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
    result += QuickInfoHelper.toString(this, "converter", m_Converter, ", converter: ");
    result += QuickInfoHelper.toString(this, "limit", m_Limit, ", limit: ");
    result += QuickInfoHelper.toString(this, "outputContainer", m_OutputContainer, "container", ", ");

    return result;
  }

  /**
   * Returns the type of data this scheme generates.
   *
   * @return		the type of data
   */
  @Override
  public Class generates() {
    if (m_OutputContainer)
      return RabbitMQConsumptionContainer.class;
    else
      return m_Converter.generates();
  }

  /**
   * Initializes the reception.
   *
   * @return		null if successfully initialized, otherwise error message
   */
  @Override
  public String initReception() {
    String	result;

    result = super.initReception();

    if (result == null) {
      m_Connection = (RabbitMQConnection) ActorUtils.findClosestType(m_Owner, RabbitMQConnection.class);
      if (m_Connection == null)
	result = "No " + RabbitMQConnection.class.getName() + " actor found!";
    }

    return result;
  }

  /**
   * Performs the actual reception of data.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    String		result;
    DeliverCallback 	deliverCallback;
    String 		queue;

    result = null;

    m_Converter.setFlowContext(getOwner());

    if (m_Channel == null) {
      m_Channel = m_Connection.createChannel();
      if (m_Channel == null)
	result = "Failed to create a channel!";
    }

    queue           = "";
    deliverCallback = null;
    if (result == null) {
      m_Buffer.clear();

      // callback
      deliverCallback = (consumerTag, delivery) -> {
	byte[] data = delivery.getBody();
	MessageCollection errors = new MessageCollection();
	Object output = m_Converter.convert(data, errors);
	if (m_OutputContainer)
	  m_Buffer.add(new RabbitMQConsumptionContainer(output, delivery.getProperties()));
	else
	  m_Buffer.add(output);
      };

      // determine queue name
      if (m_Exchange.isEmpty()) {
	queue = m_Queue;
      }
      else {
	try {
	  queue = m_Channel.queueDeclare().getQueue();
	  m_Connection.addAutoCreatedQueue(queue);
	  m_Channel.queueBind(queue, m_Exchange, "");
	}
	catch (Exception e) {
	  result = handleException("Failed to bind queue to exchange!", e);
	}
      }
    }

    // consume
    if (result == null) {
      try {
	m_Channel.basicConsume(queue, true, deliverCallback, consumerTag -> {});
      }
      catch (Exception e) {
	result = handleException("Failed to consume data!", e);
      }
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    RabbitMQHelper.closeQuietly(m_Channel);
    m_Channel    = null;
    m_Connection = null;
    super.cleanUp();
  }
}
