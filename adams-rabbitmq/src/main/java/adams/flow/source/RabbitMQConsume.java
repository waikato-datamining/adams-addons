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
 * RabbitMQConsume.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.ClassCrossReference;
import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.net.rabbitmq.RabbitMQHelper;
import adams.core.net.rabbitmq.receive.AbstractConverter;
import adams.core.net.rabbitmq.receive.StringConverter;
import adams.data.conversion.RabbitMQEnvelopeToMap;
import adams.flow.container.RabbitMQConsumptionContainer;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.sink.RabbitMQMessageDeliveryAction;
import adams.flow.standalone.RabbitMQChannelAction;
import adams.flow.standalone.RabbitMQConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.Hashtable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Consumes data it receives and forwards it.<br>
 * It either binds to the specified exchange (if non-empty and ignores the queue name), or it listens to the specified queue.<br>
 * When using an exchange, this one must be declared via the adams.flow.standalone.RabbitMQChannelAction standalone.<br>
 * When not automatically acknowledging messages, the delivery tag must be retrieved from the delivery envelope (enabled to output container) and manually acknowledge using adams.flow.sink.RabbitMQAck.<br>
 * <br>
 * See also:<br>
 * adams.data.conversion.RabbitMQEnvelopeToMap<br>
 * adams.flow.sink.RabbitMQAck
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: RabbitMQConsume
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
 * <pre>-auto-ack &lt;boolean&gt; (property: autoAck)
 * &nbsp;&nbsp;&nbsp;If enabled, jobs are automatically acknowledged (= flagged as successfully
 * &nbsp;&nbsp;&nbsp;processed); otherwise the delivery tag has get extracted with adams.data.conversion.RabbitMQEnvelopeToMap
 * &nbsp;&nbsp;&nbsp;and manually acknowledged using adams.flow.sink.RabbitMQAck.
 * &nbsp;&nbsp;&nbsp;default: true
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
  extends AbstractSource
  implements ClassCrossReference {

  private static final long serialVersionUID = -7073183797972945731L;

  /** the key for storing the collected data in the backup. */
  public final static String BACKUP_DATA = "data";

  /** the name of the exchange. */
  protected String m_Exchange;

  /** the name of the queue. */
  protected String m_Queue;

  /** whether to automatically acknowledge jobs. */
  protected boolean m_AutoAck;

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

  /** the data that has been received. */
  protected ArrayBlockingQueue<Object> m_Data;

  /** the internal timeout interval for polling the queue in msec. */
  protected int m_PollTimeout;

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
      + "When using an exchange, this one must be declared via the "
      + Utils.classToString(RabbitMQChannelAction.class) + " standalone.\n"
      + "When not automatically acknowledging messages, the delivery tag must be "
      + "retrieved from the delivery envelope (enabled to output container) "
      + "and manually acknowledge using " + Utils.classToString(RabbitMQMessageDeliveryAction.class) + ".";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{
      RabbitMQEnvelopeToMap.class,
      RabbitMQMessageDeliveryAction.class,
    };
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
      "auto-ack", "autoAck",
      true);

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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Data        = null;
    m_PollTimeout = 100;
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Data = null;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_DATA);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_Data != null)
      result.put(BACKUP_DATA, m_Data);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_DATA)) {
      m_Data = (ArrayBlockingQueue<Object>) state.get(BACKUP_DATA);
      state.remove(BACKUP_DATA);
    }

    super.restoreState(state);
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
    result += QuickInfoHelper.toString(this, "autoAck", m_AutoAck, "auto-ack", ", ");
    result += QuickInfoHelper.toString(this, "converter", m_Converter, ", converter: ");
    result += QuickInfoHelper.toString(this, "limit", m_Limit, ", limit: ");
    result += QuickInfoHelper.toString(this, "outputContainer", m_OutputContainer, "container", ", ");

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
   * Sets whether to automatically acknowledge jobs (= successfully processed).
   *
   * @param value	true if automatically acknowledge
   * @see		RabbitMQEnvelopeToMap
   * @see                RabbitMQMessageDeliveryAction
   */
  public void setAutoAck(boolean value) {
    m_AutoAck = value;
    reset();
  }

  /**
   * Returns whether to automatically acknowledge jobs (= successfully processed).
   *
   * @return 		true if automatically acknowledge
   * @see		RabbitMQEnvelopeToMap
   * @see                RabbitMQMessageDeliveryAction
   */
  public boolean getAutoAck() {
    return m_AutoAck;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String autoAckTipText() {
    return "If enabled, jobs are automatically acknowledged (= flagged as "
      + "successfully processed); otherwise the delivery tag has get "
      + "extracted with " + Utils.classToString(RabbitMQEnvelopeToMap.class) + " "
      + "and manually acknowledged using " + Utils.classToString(RabbitMQMessageDeliveryAction.class) + ".";
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
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] generates() {
    if (m_OutputContainer)
      return new Class[]{RabbitMQConsumptionContainer.class};
    else
      return new Class[]{m_Converter.generates()};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;

    result = super.setUp();

    if (result == null) {
      m_Connection = (RabbitMQConnection) ActorUtils.findClosestType(this, RabbitMQConnection.class);
      if (m_Connection == null)
	result = "No " + RabbitMQConnection.class.getName() + " actor found!";
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    DeliverCallback 	deliverCallback;
    String 		queue;

    result = null;

    m_Converter.setFlowContext(this);

    if (m_Channel == null) {
      m_Channel = m_Connection.createChannel();
      if (m_Channel == null)
	result = "Failed to create a channel!";
    }

    queue           = "";
    deliverCallback = null;
    if (result == null) {
      // ensure queue is cleared
      if (m_Data == null)
	m_Data = new ArrayBlockingQueue<>((m_Limit < 1 ? 65536 : m_Limit));
      m_Data.clear();

      // callback
      deliverCallback = (consumerTag, delivery) -> {
	byte[] data = delivery.getBody();
	MessageCollection errors = new MessageCollection();
	Object output = m_Converter.convert(data, errors);
	if (m_OutputContainer)
	  m_Data.add(new RabbitMQConsumptionContainer(output, delivery.getProperties(), delivery.getEnvelope(), m_Channel));
	else
	  m_Data.add(output);
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
	m_Channel.basicConsume(queue, m_AutoAck, deliverCallback, consumerTag -> {});
      }
      catch (Exception e) {
	result = handleException("Failed to consume data!", e);
      }
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   * <br>
   * Always returns true once executed and not stopped.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return m_Executed && !isStopped();
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;
    Object	data;

    result = null;
    data   = null;
    while (!isStopped() && (data == null)) {
      try {
	data = m_Data.poll(m_PollTimeout, TimeUnit.MILLISECONDS);
      }
      catch (Exception e) {
	if (isLoggingEnabled())
	  getLogger().log(Level.INFO, "Exception while polling", e);
      }
    }
    if (data != null)
      result = new Token(data);

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    RabbitMQHelper.closeQuietly(m_Channel);
    m_Channel    = null;
    m_Connection = null;

    super.wrapUp();
  }
}
