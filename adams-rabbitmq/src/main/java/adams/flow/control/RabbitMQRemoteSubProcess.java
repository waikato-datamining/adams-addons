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
 * RabbitMQRemoteSubProcess.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.control;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.container.EncapsulatedActorsContainer;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.EncapsulateActors;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import com.rabbitmq.client.DeliverCallback;

/**
 <!-- globalinfo-start -->
 * Encapsulates a sequence of actors to be executed remotely. The first actor must accept input and the last one must produce output.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
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
 * &nbsp;&nbsp;&nbsp;default: RabbitMQRemoteSubProcess
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
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to execute remotely.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; [-storage-name ...] (property: storageNames)
 * &nbsp;&nbsp;&nbsp;The (optional) storage items to transfer.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-variable-name &lt;adams.core.VariableName&gt; [-variable-name ...] (property: variableNames)
 * &nbsp;&nbsp;&nbsp;The (optional) variables to transfer.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-queue &lt;java.lang.String&gt; (property: queue)
 * &nbsp;&nbsp;&nbsp;The name of the queue.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-send-converter &lt;adams.core.net.rabbitmq.send.AbstractConverter&gt; (property: sendConverter)
 * &nbsp;&nbsp;&nbsp;The converter to use for sending.
 * &nbsp;&nbsp;&nbsp;default: adams.core.net.rabbitmq.send.BinaryConverter
 * </pre>
 *
 * <pre>-receive-converter &lt;adams.core.net.rabbitmq.receive.AbstractConverter&gt; (property: receiveConverter)
 * &nbsp;&nbsp;&nbsp;The converter to use for receiving data.
 * &nbsp;&nbsp;&nbsp;default: adams.core.net.rabbitmq.receive.BinaryConverter
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RabbitMQRemoteSubProcess
  extends AbstractRabbitMQControlActor
  implements InputConsumer, OutputProducer {

  private static final long serialVersionUID = 5816569944356142679L;

  /** the converter for receiving. */
  protected adams.core.net.rabbitmq.receive.AbstractConverter m_ReceiveConverter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Encapsulates a sequence of actors to be executed remotely. The first actor must accept "
	+ "input and the last one must produce output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "receive-converter", "receiveConverter",
      new adams.core.net.rabbitmq.receive.BinaryConverter());
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "receiveConverter", m_ReceiveConverter, ", receive: ");

    return result;
  }

  /**
   * Checks the tee actor before it is set.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param index	the index the actor gets set
   * @param actor	the actor to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkSubActor(int index, Actor actor) {
    if (ActorUtils.isSource(actor))
      return "You cannot add a source actor ('" + actor.getName() + "'/" + actor.getClass().getName() + ")!";

    return null;
  }

  /**
   * Checks the tee actor before it is set via the setTeeActor method.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkSubActors(Actor[] actors) {
    int		i;

    if (actors.length > 0) {
      for (i = 0; i < actors.length; i++) {
	if (actors[i].getSkip())
	  continue;
	if (!(actors[i] instanceof InputConsumer))
	  return "You need to provide an actor that processes input, '" + actors[i].getName() + "'/" + actors[i].getClass().getName() + " doesn't!";
      }
    }

    return null;
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo()
      .allowStandalones(false)
      .allowSource(false)
      .actorExecution(ActorExecution.SEQUENTIAL)
      .forwardsInput(true);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    if (active() > 0)
      return ((InputConsumer) firstActive()).accepts();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    if (active() > 0)
      return ((OutputProducer) lastActive()).generates();
    else
      return new Class[]{Unknown.class};
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
      if (!(firstActive() instanceof InputConsumer))
	result = "First actor ('" + firstActive().getName() + "') does not accept input!";
      else if (!(lastActive() instanceof OutputProducer))
	result = "Last actor ('" + lastActive().getName() + "') does not generate output!";
    }

    return result;
  }

  /**
   * Encapsulates the sub-flow.
   *
   * @return		the generated container
   */
  protected EncapsulatedActorsContainer encapsulate() {
    SubProcess	sub;
    int		i;

    // generate subflow
    sub = new SubProcess();
    sub.setName("source: " + getFullName());
    for (i = 0; i < size(); i++)
      sub.add(get(i).shallowCopy(false));
    sub.setParent(getParent());  // for access to storage
    sub.setVariables(getVariables());

    // encapsulate
    return EncapsulateActors.wrap(sub, m_VariableNames, m_StorageNames, m_InputToken.getPayload());
  }

  /**
   * Generates the callback for the data coming back.
   *
   * @return		the callback
   */
  protected DeliverCallback generateDeliverCallback() {
    DeliverCallback 	result;

    m_ReceiveConverter.setFlowContext(this);

    result = (consumerTag, delivery) -> {
      try {
	byte[] dataRec = delivery.getBody();
	MessageCollection errorsRec = new MessageCollection();
	Object objRec = m_ReceiveConverter.convert(dataRec, errorsRec);
	if (objRec != null) {
	  if (objRec instanceof EncapsulatedActorsContainer) {
	    EncapsulatedActorsContainer contRec = (EncapsulatedActorsContainer) objRec;
	    if (contRec.hasValue(EncapsulatedActorsContainer.VALUE_OUTPUT)) {
	      Object generated = contRec.getValue(EncapsulatedActorsContainer.VALUE_OUTPUT);
	      if (isLoggingEnabled())
		getLogger().info("Received: " + generated);
	      m_OutputToken = new Token(generated);
	    }
	    else {
	      getLogger().warning("Did not receive any generated output!");
	    }
	  }
	  else {
	    getLogger().severe("Expected " + Utils.classToString(EncapsulatedActorsContainer.class) + " but received " + Utils.classToString(objRec) + " back!");
	  }
	}
      }
      catch (Exception e) {
	handleException("Failed to process received data!", e);
      }
      finally {
	m_Processing = null;
      }
    };

    return result;
  }
}
