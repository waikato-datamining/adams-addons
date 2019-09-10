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

package adams.flow.standalone.rats.output;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.net.rabbitmq.RabbitMQHelper;
import adams.core.net.rabbitmq.send.AbstractConverter;
import adams.core.net.rabbitmq.send.StringConverter;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.RabbitMQConnection;
import com.rabbitmq.client.Channel;

/**
 <!-- globalinfo-start -->
 * Publishes the incoming data using the specified exchange or queue.<br>
 * Normally, when using an exchange, leave queue empty, and when using a queue, leave the exchange empty.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
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
 * <pre>-converter &lt;adams.core.net.rabbitmq.send.AbstractConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The converter to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.net.rabbitmq.send.StringConverter
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQPublish
  extends AbstractRatOutput {

  private static final long serialVersionUID = -6705175917581201250L;

  /** the name of the exchange. */
  protected String m_Exchange;

  /** the name of the queue. */
  protected String m_Queue;

  /** the converter. */
  protected AbstractConverter m_Converter;

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
    return "Publishes the incoming data using the specified exchange or queue.\n"
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

    m_OptionManager.add(
      "converter", "converter",
      new StringConverter());
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
   * Returns the type of data that gets accepted.
   *
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    return m_Converter.accepts();
  }

  /**
   * Initializes the transmission.
   *
   * @return		null if successfully initialized, otherwise error message
   */
  @Override
  public String initTransmission() {
    String	result;

    result = super.initTransmission();

    if (result == null) {
      m_Connection = (RabbitMQConnection) ActorUtils.findClosestType(m_Owner, RabbitMQConnection.class);
      if (m_Connection == null)
	result = "No " + RabbitMQConnection.class.getName() + " actor found!";
    }

    return result;
  }

  /**
   * Performs the actual transmission.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String			result;
    MessageCollection 		errors;
    byte[]			data;

    result = null;

    m_Converter.setFlowContext(getOwner());

    if (m_Channel == null) {
      m_Channel = m_Connection.createChannel();
      if (m_Channel == null)
	result = "Failed to create a channel!";
    }

    // convert data
    data = null;
    if (result == null) {
      errors = new MessageCollection();
      data = m_Converter.convert(m_Input, errors);
      if (!errors.isEmpty())
	result = errors.toString();
    }

    // send data
    if (result == null) {
      try {
	m_Channel.basicPublish(m_Exchange, m_Queue, null, data);
      }
      catch (Exception e) {
        result = handleException("Failed to publish data (exchange=" + m_Exchange + ", queue=" + m_Queue + ")!", e);
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
    m_Channel = null;
    super.cleanUp();
  }
}
