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
 * RabbitMQRemoteProcedureCall.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.UniqueIDs;
import adams.core.Utils;
import adams.core.net.rabbitmq.RabbitMQHelper;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.standalone.RabbitMQConnection;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Performs a remote procedure call via a RabbitMQ broker.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: RabbitMQRemoteProcedureCall
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
 * <pre>-queue &lt;java.lang.String&gt; (property: queue)
 * &nbsp;&nbsp;&nbsp;The name of the queue.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-send-converter &lt;adams.core.net.rabbitmq.send.AbstractConverter&gt; (property: sendConverter)
 * &nbsp;&nbsp;&nbsp;The converter to use for sending.
 * &nbsp;&nbsp;&nbsp;default: adams.core.net.rabbitmq.send.StringConverter
 * </pre>
 *
 * <pre>-receive-converter &lt;adams.core.net.rabbitmq.receive.AbstractConverter&gt; (property: receiveConverter)
 * &nbsp;&nbsp;&nbsp;The converter to use for receiving data.
 * &nbsp;&nbsp;&nbsp;default: adams.core.net.rabbitmq.receive.StringConverter
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQRemoteProcedureCall
  extends AbstractTransformer {

  private static final long serialVersionUID = 194761311376840744L;

  /** the name of the queue. */
  protected String m_Queue;

  /** the converter for sending. */
  protected adams.core.net.rabbitmq.send.AbstractConverter m_SendConverter;

  /** the converter for receiving. */
  protected adams.core.net.rabbitmq.receive.AbstractConverter m_ReceiveConverter;

  /** the connection in use. */
  protected transient RabbitMQConnection m_Connection;

  /** the channel action to use. */
  protected transient Channel m_Channel;

  /** the collected data. */
  protected List<Object> m_Data;

  /** for checking whether still processing received data. */
  protected transient Long m_Processing;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs a remote procedure call via a RabbitMQ broker.";
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
      "send-converter", "sendConverter",
      new adams.core.net.rabbitmq.send.StringConverter());

    m_OptionManager.add(
      "receive-converter", "receiveConverter",
      new adams.core.net.rabbitmq.receive.StringConverter());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Data = new ArrayList<>();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "queue", (m_Queue.isEmpty() ? "-empty-" : m_Queue), "queue: ");
    result += QuickInfoHelper.toString(this, "sendConverter", m_SendConverter, ", send: ");
    result += QuickInfoHelper.toString(this, "receiveConverter", m_ReceiveConverter, ", receive: ");

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
   * Sets the converter to use for sending.
   *
   * @param value	the converter
   */
  public void setSendConverter(adams.core.net.rabbitmq.send.AbstractConverter value) {
    m_SendConverter = value;
    reset();
  }

  /**
   * Returns the converter to use for sending.
   *
   * @return 		the converter
   */
  public adams.core.net.rabbitmq.send.AbstractConverter getSendConverter() {
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
   * Sets the converter to use for receiving.
   *
   * @param value	the converter
   */
  public void setReceiveConverter(adams.core.net.rabbitmq.receive.AbstractConverter value) {
    m_ReceiveConverter = value;
    reset();
  }

  /**
   * Returns the converter to use for receiving.
   *
   * @return 		the converter
   */
  public adams.core.net.rabbitmq.receive.AbstractConverter getReceiveConverter() {
    return m_ReceiveConverter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String receiveConverterTipText() {
    return "The converter to use for receiving data.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return m_SendConverter.accepts();
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{m_ReceiveConverter.generates()};
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
    String 		result;
    String 		callbackQueue;
    BasicProperties 	props;
    MessageCollection 	errorsSnd;
    byte[] 		dataSnd;
    DeliverCallback 	deliverCallback;

    result = null;
    m_Data.clear();
    m_SendConverter.setFlowContext(this);
    m_ReceiveConverter.setFlowContext(this);

    if (m_Channel == null) {
      m_Channel = m_Connection.createChannel();
      if (m_Channel == null)
        result = "Failed to create a channel!";
    }

    // convert input data
    dataSnd = null;
    if (result == null) {
      errorsSnd = new MessageCollection();
      dataSnd = m_SendConverter.convert(m_InputToken.getPayload(), errorsSnd);
      if (!errorsSnd.isEmpty())
        result = errorsSnd.toString();
    }

    // send
    callbackQueue = null;
    if (result == null) {
      try {
	callbackQueue = m_Channel.queueDeclare().getQueue();
	m_Connection.addAutoCreatedQueue(callbackQueue);

	props = new BasicProperties.Builder()
	  .replyTo(callbackQueue)
	  .build();

	m_Channel.basicPublish("", m_Queue, props, dataSnd);
      }
      catch (Exception e) {
        result = handleException("Failed to send data!", e);
      }
    }

    // receive
    if (result == null) {
      try {
	deliverCallback = (consumerTag, delivery) -> {
	  try {
	    byte[] dataRec = delivery.getBody();
	    MessageCollection errorsRec = new MessageCollection();
	    Object output = m_ReceiveConverter.convert(dataRec, errorsRec);
	    if (output != null)
	      m_Data.add(output);
	  }
	  catch (Exception e) {
	    handleException("Failed to process received data!", e);
	  }
	  finally {
	    m_Processing = null;
	  }
	};

	m_Processing = UniqueIDs.nextLong();
	m_Channel.basicConsume(callbackQueue, true, deliverCallback, consumerTag -> {});
	while (m_Processing != null) {
	  Utils.wait(this, 1000, 50);
	}
      }
      catch (Exception e) {
	result = handleException("Failed to receive data!", e);
      }
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Data.size() > 0);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    if (m_Data.size() > 0)
      result = new Token(m_Data.remove(0));
    else
      result = null;

    return result;
  }

  @Override
  public void stopExecution() {
    if (m_Processing != null)
      m_Processing = null;
    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    RabbitMQHelper.closeQuietly(m_Channel);
    m_Channel = null;
    super.wrapUp();
  }
}
