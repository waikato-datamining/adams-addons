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

/**
 * RatPlague.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.flow.control.StorageName;
import adams.flow.core.MutableActorHandler;
import adams.flow.standalone.rats.input.DeQueue;
import adams.flow.standalone.rats.input.DummyInput;
import adams.flow.standalone.rats.input.InputPolling;
import adams.flow.standalone.rats.output.DummyOutput;
import adams.flow.standalone.rats.output.EnQueue;

/**
 <!-- globalinfo-start -->
 * Replaces itself at runtime with a copy of itself, as many times as there are input queues.
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
 * &nbsp;&nbsp;&nbsp;default: RatPlague
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
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
 * &nbsp;&nbsp;&nbsp;The actors for transforming the data obtained by the receiver before sending
 * &nbsp;&nbsp;&nbsp;it to the transmitter.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-log &lt;adams.flow.core.CallableActorReference&gt; (property: log)
 * &nbsp;&nbsp;&nbsp;The name of the callable log actor to use (logging disabled if actor not
 * &nbsp;&nbsp;&nbsp;found).
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 *
 * <pre>-scope-handling-variables &lt;EMPTY|COPY|SHARE&gt; (property: scopeHandlingVariables)
 * &nbsp;&nbsp;&nbsp;Defines how variables are handled in the local scope; whether to start with
 * &nbsp;&nbsp;&nbsp;empty set, a copy of the outer scope variables or share variables with the
 * &nbsp;&nbsp;&nbsp;outer scope.
 * &nbsp;&nbsp;&nbsp;default: EMPTY
 * </pre>
 *
 * <pre>-propagate-variables &lt;boolean&gt; (property: propagateVariables)
 * &nbsp;&nbsp;&nbsp;If enabled, variables that match the specified regular expression get propagated
 * &nbsp;&nbsp;&nbsp;to the outer scope.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-variables-regexp &lt;adams.core.base.BaseRegExp&gt; (property: variablesRegExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that variable names must match in order to get propagated.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-scope-handling-storage &lt;EMPTY|COPY|SHARE&gt; (property: scopeHandlingStorage)
 * &nbsp;&nbsp;&nbsp;Defines how storage is handled in the local scope; whether to start with
 * &nbsp;&nbsp;&nbsp;empty set, a (deep) copy of the outer scope storage or share the storage
 * &nbsp;&nbsp;&nbsp;with the outer scope.
 * &nbsp;&nbsp;&nbsp;default: EMPTY
 * </pre>
 *
 * <pre>-propagate-storage &lt;boolean&gt; (property: propagateStorage)
 * &nbsp;&nbsp;&nbsp;If enabled, storage items which names match the specified regular expression
 * &nbsp;&nbsp;&nbsp;get propagated to the outer scope.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-storage-regexp &lt;adams.core.base.BaseRegExp&gt; (property: storageRegExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the names of storage items must match in order
 * &nbsp;&nbsp;&nbsp;to get propagated.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-flow-error-queue &lt;adams.flow.control.StorageName&gt; (property: flowErrorQueue)
 * &nbsp;&nbsp;&nbsp;The name of the (optional) queue in internal storage to feed with flow errors.
 * &nbsp;&nbsp;&nbsp;default: flowerrors
 * </pre>
 *
 * <pre>-send-error-queue &lt;adams.flow.control.StorageName&gt; (property: sendErrorQueue)
 * &nbsp;&nbsp;&nbsp;The name of the (optional) queue in internal storage to feed with send errors.
 * &nbsp;&nbsp;&nbsp;default: senderrors
 * </pre>
 *
 * <pre>-show-in-control &lt;boolean&gt; (property: showInControl)
 * &nbsp;&nbsp;&nbsp;If enabled, this Rat will be displayed in the adams.flow.standalone.RatControl
 * &nbsp;&nbsp;&nbsp;control panel.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-wait-poll &lt;int&gt; (property: waitPoll)
 * &nbsp;&nbsp;&nbsp;The number of milli-seconds to wait before polling again.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-input &lt;adams.flow.control.StorageName&gt; [-input ...] (property: input)
 * &nbsp;&nbsp;&nbsp;The names of the input queues in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-output &lt;adams.flow.control.StorageName&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The name of the output queue in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: queue
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RatPlague
  extends Rat {

  /** for serialization. */
  private static final long serialVersionUID = -154461277343021604L;

  /** the waiting period in msec before polling again. */
  protected int m_WaitPoll;

  /** the names of the input queues in the internal storage. */
  protected StorageName[] m_Input;

  /** the name of the output queue in the internal storage. */
  protected StorageName m_Output;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Replaces itself at runtime with a copy of itself, as many times as there are input queues.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.removeByProperty("receiver");
    m_OptionManager.removeByProperty("transmitter");

    m_OptionManager.add(
	    "wait-poll", "waitPoll",
	    100, 0, null);

    m_OptionManager.add(
	    "input", "input",
	    new StorageName[0]);

    m_OptionManager.add(
	    "output", "output",
	    new StorageName("queue"));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    // removed as options, hence manual initialization
    // for setUp() method to succeed
    setReceiver(new DummyInput());
    setTransmitter(new DummyOutput());
  }

  /**
   * Sets the number of milli-seconds to wait before polling.
   *
   * @param value	the number of milli-seconds
   */
  public void setWaitPoll(int value) {
    if (value >= 0) {
      m_WaitPoll = value;
      reset();
    }
    else {
      getLogger().warning("Number of milli-seconds to wait must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the number of milli-seconds to wait before polling again.
   *
   * @return		the number of milli-seconds
   */
  public int getWaitPoll() {
    return m_WaitPoll;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitPollTipText() {
    return "The number of milli-seconds to wait before polling again.";
  }

  /**
   * Sets the names for the input queues in the internal storage.
   *
   * @param value	the names
   */
  public void setInput(StorageName[] value) {
    m_Input = value;
    reset();
  }

  /**
   * Returns the names for the input queues in the internal storage.
   *
   * @return		the names
   */
  public StorageName[] getInput() {
    return m_Input;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputTipText() {
    return "The names of the input queues in the internal storage.";
  }

  /**
   * Sets the name for the output queues in the internal storage.
   *
   * @param value	the name
   */
  public void setOutput(StorageName value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the name of the input queue in the internal storage.
   *
   * @return		the name
   */
  public StorageName getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return "The name of the output queue in the internal storage.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "waitPoll", m_WaitPoll, "wait poll: ");
    result += QuickInfoHelper.toString(this, "input", m_Input, ", input: ");
    result += QuickInfoHelper.toString(this, "output", m_Output, ", output: ");

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    Rat			rat;
    int			i;
    int			index;
    int			n;
    InputPolling	polling;
    EnQueue		enqueue;
    DeQueue		dequeue;
    MutableActorHandler parent;

    result = super.setUp();

    if (result == null) {
      if (m_Input.length == 0)
	result = "No input queues defined!";
      else if (!(getParent() instanceof MutableActorHandler))
	result = "Parent is not a " + MutableActorHandler.class.getName() + "!";
    }

    if (result == null) {
      index  = index();
      parent = (MutableActorHandler) getParent();
      for (i = 0; i < m_Input.length; i++) {
	rat = new Rat();
	rat.setName(getName() + "-" + m_Input[i].getValue());
	dequeue = new DeQueue();
	dequeue.setStorageName(m_Input[i]);
	polling = new InputPolling();
	polling.setWaitPoll(m_WaitPoll);
	polling.setInput(dequeue);
	rat.setReceiver(polling);
	enqueue = new EnQueue();
	enqueue.setStorageName(m_Output);
	rat.setTransmitter(enqueue);
	rat.setLog(getLog());
	rat.setScopeHandlingVariables(getScopeHandlingVariables());
	rat.setPropagateVariables(getPropagateVariables());
	rat.setVariablesRegExp(getVariablesRegExp());
	rat.setScopeHandlingStorage(getScopeHandlingStorage());
	rat.setPropagateStorage(getPropagateStorage());
	rat.setStorageRegExp(getStorageRegExp());
	rat.setSendErrorQueue(getSendErrorQueue());
	rat.setShowInControl(getShowInControl());

	rat.removeAll();
	for (n = 0; n < size(); n++)
	  rat.add(get(n).shallowCopy());

	rat.setVariables(getVariables());
	if (i == 0)
	  parent.set(index, rat);
	else
	  parent.add(index + i, rat);
      }
      result = parent.setUp();
      setParent(null);
      cleanUp();
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
    return null;
  }
}
