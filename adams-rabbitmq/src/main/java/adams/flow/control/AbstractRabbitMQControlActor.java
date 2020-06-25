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
import adams.core.UniqueIDs;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.Variables;
import adams.flow.container.EncapsulatedActorsContainer;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.Token;
import adams.flow.sink.Null;
import adams.flow.standalone.RabbitMQConnection;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.Hashtable;

/**
 * Ancestor for RabbitMQ control actors with sub-flows.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRabbitMQControlActor
  extends AbstractControlActor
  implements MutableActorHandler {

  private static final long serialVersionUID = 5816569944356142679L;

  /** the key for storing the input token in the backup. */
  public final static String BACKUP_INPUT = "input";

  /** the key for storing the output token in the backup. */
  public final static String BACKUP_OUTPUT = "output";

  /** the current input token. */
  protected transient Token m_InputToken;

  /** the current output token. */
  protected transient Token m_OutputToken;

  /** the actors. */
  protected Sequence m_Actors;

  /** the storage items to transmit. */
  protected StorageName[] m_StorageNames;

  /** the variables to transmit. */
  protected VariableName[] m_VariableNames;

  /** the name of the queue. */
  protected String m_Queue;

  /** the converter for sending. */
  protected adams.core.net.rabbitmq.send.AbstractConverter m_SendConverter;

  /** the connection in use. */
  protected transient RabbitMQConnection m_Connection;

  /** the channel action to use. */
  protected transient Channel m_Channel;

  /** for checking whether still processing received data. */
  protected transient Long m_Processing;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "actor", "actors",
      new Actor[0]);

    m_OptionManager.add(
      "storage-name", "storageNames",
      new StorageName[0]);

    m_OptionManager.add(
      "variable-name", "variableNames",
      new VariableName[0]);

    m_OptionManager.add(
      "queue", "queue",
      "");

    m_OptionManager.add(
      "send-converter", "sendConverter",
      new adams.core.net.rabbitmq.send.BinaryConverter());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Actors = new Sequence();
    m_Actors.setActors(new Actor[]{new Null()});
  }

  /**
   * Sets the actor to execute remotely.
   *
   * @param value	the actor
   */
  public void setActors(Actor[] value) {
    String	msg;

    msg = checkSubActors(value);
    if (msg == null) {
      m_Actors.setActors(value);
      reset();
      updateParent();
    }
    else {
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Returns the actors to execute remotely.
   *
   * @return		the actors
   */
  public Actor[] getActors() {
    return m_Actors.getActors();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorsTipText() {
    return "The actors to execute remotely.";
  }

  /**
   * Sets the names of the storage items to transfer.
   *
   * @param value	the storage names
   */
  public void setStorageNames(StorageName[] value) {
    m_StorageNames = value;
    reset();
  }

  /**
   * Returns the names of the storage items to transfer.
   *
   * @return		the storage names
   */
  public StorageName[] getStorageNames() {
    return m_StorageNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String storageNamesTipText() {
    return "The (optional) storage items to transfer.";
  }

  /**
   * Sets the names of the variables to transfer.
   *
   * @param value	the variable names
   */
  public void setVariableNames(VariableName[] value) {
    m_VariableNames = value;
    reset();
  }

  /**
   * Returns the names of the variables to transfer.
   *
   * @return		the variable names
   */
  public VariableName[] getVariableNames() {
    return m_VariableNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String variableNamesTipText() {
    return "The (optional) variables to transfer.";
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    value  = super.getQuickInfo();
    result = QuickInfoHelper.toString(this, "queue", (m_Queue.isEmpty() ? "-empty-" : m_Queue), "queue: ");
    result += QuickInfoHelper.toString(this, "sendConverter", m_SendConverter, ", send: ");
    if (value != null)
      result += ", " + value;

    return result;
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

    if (m_InputToken != null)
      result.put(BACKUP_INPUT, m_InputToken);
    if (m_OutputToken != null)
      result.put(BACKUP_OUTPUT, m_OutputToken);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INPUT)) {
      m_InputToken = (Token) state.get(BACKUP_INPUT);
      state.remove(BACKUP_INPUT);
    }

    if (state.containsKey(BACKUP_OUTPUT)) {
      m_OutputToken = (Token) state.get(BACKUP_OUTPUT);
      state.remove(BACKUP_OUTPUT);
    }

    super.restoreState(state);
  }

  /**
   * Updates the parent of all actors in this group.
   */
  @Override
  protected void updateParent() {
    m_Actors.setName(getName());
    m_Actors.setParent(null);
    m_Actors.setParent(getParent());
  }

  /**
   * Updates the Variables instance in use.
   * <br><br>
   * Use with caution!
   *
   * @param value	the instance to use
   */
  @Override
  protected void forceVariables(Variables value) {
    super.forceVariables(value);
    m_Actors.forceVariables(value);
  }

  /**
   * Returns the size of the group.
   *
   * @return		always 1
   */
  @Override
  public int size() {
    return m_Actors.size();
  }

  /**
   * Returns the actor at the given position.
   *
   * @param index	the position
   * @return		the actor
   */
  @Override
  public Actor get(int index) {
    return m_Actors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   * @return		null if everything is fine, otherwise the error
   * @see		#checkSubActor(int, Actor)
   */
  @Override
  public String set(int index, Actor actor) {
    String result;

    result = checkSubActor(index, actor);
    if (result == null) {
      m_Actors.set(index, actor);
      reset();
      updateParent();
    }

    return result;
  }

  /**
   * Returns the index of the actor.
   *
   * @param actor	the name of the actor to look for
   * @return		the index of -1 if not found
   */
  @Override
  public int indexOf(String actor) {
    return m_Actors.indexOf(actor);
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   * @return		null if everything is fine, otherwise the error
   */
  @Override
  public String add(Actor actor) {
    return add(size(), actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   * @return		null if everything is fine, otherwise the error
   * @see		#checkSubActor(int, Actor)
   */
  @Override
  public String add(int index, Actor actor) {
    String result;

    if (actor == this)
      throw new IllegalArgumentException("Cannot add itself!");

    result = checkSubActor(index, actor);
    if (result == null) {
      m_Actors.add(index, actor);
      reset();
      updateParent();
    }

    return result;
  }

  /**
   * Checks the sub-flow actor before it is set.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param index	the index the actor gets set
   * @param actor	the actor to check
   * @return		null if accepted, otherwise error message
   */
  protected abstract String checkSubActor(int index, Actor actor);

  /**
   * Checks the sub-flow  actor before it is set via the setSubActor method.
   * Returns an error message if the actor is not acceptable, null otherwise.
   *
   * @param actors	the actors to check
   * @return		null if accepted, otherwise error message
   */
  protected abstract String checkSubActors(Actor[] actors);

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  public Actor remove(int index) {
    Actor	result;

    result = m_Actors.remove(index);
    reset();

    return result;
  }

  /**
   * Removes all actors.
   */
  public void removeAll() {
    m_Actors.removeAll();
    reset();
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public abstract ActorHandlerInfo getActorHandlerInfo();

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   * @see		#m_InputToken
   */
  public void input(Token token) {
    m_InputToken  = token;
    m_OutputToken = null;
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  public boolean hasInput() {
    return (m_InputToken != null);
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  public Token currentInput() {
    return m_InputToken;
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
   * Encapsulates the sub-flow.
   *
   * @return		the generated container
   */
  protected abstract EncapsulatedActorsContainer encapsulate();

  /**
   * Generates the callback for the data coming back.
   *
   * @return		the callback
   */
  protected abstract DeliverCallback generateDeliverCallback();

  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    EncapsulatedActorsContainer	cont;
    String 			callbackQueue;
    BasicProperties 		props;
    MessageCollection 		errorsSnd;
    byte[] 			dataSnd;
    DeliverCallback 		deliverCallback;

    result = null;
    m_SendConverter.setFlowContext(this);

    if (m_Channel == null) {
      m_Channel = m_Connection.createChannel();
      if (m_Channel == null)
	result = "Failed to create a channel!";
    }

    if (result == null) {
      cont = encapsulate();

      // convert input data
      errorsSnd = new MessageCollection();
      dataSnd = m_SendConverter.convert(cont, errorsSnd);
      if (!errorsSnd.isEmpty())
	result = errorsSnd.toString();

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
	  result = handleException("Failed to send flow container!", e);
	}
      }

      // receive
      if (result == null) {
	try {
	  deliverCallback = generateDeliverCallback();
	  m_Processing = UniqueIDs.nextLong();
	  m_Channel.basicConsume(callbackQueue, true, deliverCallback, consumerTag -> {});
	  while (m_Processing != null) {
	    Utils.wait(this, 1000, 50);
	  }
	}
	catch (Exception e) {
	  result = handleException("Failed to receive flow container!", e);
	}
      }
    }

    return result;
  }

  /**
   * Post-execute hook.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		#m_Executed
   */
  @Override
  protected String postExecute() {
    String	result;

    result = super.postExecute();

    if (isStopped())
      m_OutputToken = null;

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String execute() {
    String	result;

    result = super.execute();

    if (m_Skip)
      m_OutputToken = m_InputToken;

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return (m_OutputToken != null);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    Token	result;

    result        = m_OutputToken;
    m_OutputToken = null;
    m_InputToken  = null;

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_InputToken  = null;
    m_OutputToken = null;

    super.wrapUp();
  }
}
