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
 * Rat.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.Pausable;
import adams.core.Properties;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.Variables;
import adams.core.base.BaseRegExp;
import adams.core.logging.LoggingLevel;
import adams.db.LogEntry;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateListener;
import adams.event.RatStateEvent;
import adams.event.RatStateListener;
import adams.flow.container.ErrorContainer;
import adams.flow.control.AtomicExecution;
import adams.flow.control.Breakpoint;
import adams.flow.control.LocalScopeSubProcess;
import adams.flow.control.ScopeHandler.ScopeHandling;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorPath;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorUser;
import adams.flow.core.Compatibility;
import adams.flow.core.ErrorHandler;
import adams.flow.core.InputConsumer;
import adams.flow.core.LazySetupSupporter;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.PauseStateHandler;
import adams.flow.core.PauseStateManager;
import adams.flow.core.QueueHelper;
import adams.flow.core.RatMode;
import adams.flow.core.RatState;
import adams.flow.core.Token;
import adams.flow.execution.debug.AbstractScopeRestriction;
import adams.flow.execution.debug.DebugScopeRestrictionHandler;
import adams.flow.execution.debug.SubFlowRestriction;
import adams.flow.standalone.rats.RatRunnable;
import adams.flow.standalone.rats.input.DummyInput;
import adams.flow.standalone.rats.input.RatInput;
import adams.flow.standalone.rats.log.AbstractLogObjectGenerator;
import adams.flow.standalone.rats.log.LogEntryGenerator;
import adams.flow.standalone.rats.output.DummyOutput;
import adams.flow.standalone.rats.output.RatOutput;
import adams.gui.flow.tree.Node;

import java.util.HashSet;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Defines a single reception&#47;transmission setup.
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
 * &nbsp;&nbsp;&nbsp;default: Rat
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
 * <pre>-receiver &lt;adams.flow.standalone.rats.input.RatInput&gt; (property: receiver)
 * &nbsp;&nbsp;&nbsp;The receiver to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.input.DummyInput
 * </pre>
 * 
 * <pre>-perform-lazy-setup &lt;boolean&gt; (property: performLazySetup)
 * &nbsp;&nbsp;&nbsp;If enabled, initializing the sub-actors will only occurring the first time
 * &nbsp;&nbsp;&nbsp;the rat gets executed (ie the input triggers); use with 'wrapUpAfterExecution'
 * &nbsp;&nbsp;&nbsp; to save memory; gets disabled if actors contain a Breakpoint.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors for transforming the data obtained by the receiver before sending
 * &nbsp;&nbsp;&nbsp;it to the transmitter.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-transmitter &lt;adams.flow.standalone.rats.output.RatOutput&gt; (property: transmitter)
 * &nbsp;&nbsp;&nbsp;The transmitter to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.output.DummyOutput
 * </pre>
 *
 * <pre>-log &lt;adams.flow.core.CallableActorReference&gt; (property: log)
 * &nbsp;&nbsp;&nbsp;The name of the callable log actor to use (logging disabled if actor not
 * &nbsp;&nbsp;&nbsp;found).
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 *
 * <pre>-log-generator &lt;adams.flow.standalone.rats.log.AbstractLogObjectGenerator&gt; (property: logGenerator)
 * &nbsp;&nbsp;&nbsp;The generator for turning the error message into the required log object.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.log.LogEntryGenerator
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
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
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
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-flow-error-queue &lt;adams.flow.control.StorageName&gt; (property: flowErrorQueue)
 * &nbsp;&nbsp;&nbsp;The name of the (optional) queue in internal storage to feed with flow errors;
 * &nbsp;&nbsp;&nbsp; Forwards the original data received as payload in an adams.flow.container.ErrorContainer
 * &nbsp;&nbsp;&nbsp;alongside the error message.
 * &nbsp;&nbsp;&nbsp;default: flowerrors
 * </pre>
 *
 * <pre>-send-error-queue &lt;adams.flow.control.StorageName&gt; (property: sendErrorQueue)
 * &nbsp;&nbsp;&nbsp;The name of the (optional) queue in internal storage to feed with send errors;
 * &nbsp;&nbsp;&nbsp; Forwards the original data received as payload in an adams.flow.container.ErrorContainer
 * &nbsp;&nbsp;&nbsp;alongside the error message.
 * &nbsp;&nbsp;&nbsp;default: senderrors
 * </pre>
 *
 * <pre>-suppress-errors &lt;boolean&gt; (property: suppressErrors)
 * &nbsp;&nbsp;&nbsp;If enabled, errors are suppressed and only forwarded to the log actor.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-show-in-control &lt;boolean&gt; (property: showInControl)
 * &nbsp;&nbsp;&nbsp;If enabled, this Rat will be displayed in the adams.flow.standalone.RatControl
 * &nbsp;&nbsp;&nbsp;control panel.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-initial-state &lt;PAUSED|RUNNING&gt; (property: initialState)
 * &nbsp;&nbsp;&nbsp;The initial state of the Rat actor.
 * &nbsp;&nbsp;&nbsp;default: RUNNING
 * </pre>
 *
 * <pre>-mode &lt;CONTINUOUS|MANUAL&gt; (property: mode)
 * &nbsp;&nbsp;&nbsp;The mode the Rat actor is run in.
 * &nbsp;&nbsp;&nbsp;default: CONTINUOUS
 * </pre>
 *
 * <pre>-finish-before-stopping &lt;boolean&gt; (property: finishBeforeStopping)
 * &nbsp;&nbsp;&nbsp;If enabled, actor first finishes processing all data before stopping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stopping-timeout &lt;int&gt; (property: stoppingTimeout)
 * &nbsp;&nbsp;&nbsp;The timeout in milliseconds when waiting for actors to finish (&lt;= 0 for
 * &nbsp;&nbsp;&nbsp;infinity; see 'finishBeforeStopping').
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-wrapup-after-execution &lt;boolean&gt; (property: wrapUpAfterExecution)
 * &nbsp;&nbsp;&nbsp;Whether to wrapUp the actors after execution to save memory; only available
 * &nbsp;&nbsp;&nbsp;in conjunction with 'performLazySetup'.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Rat
  extends AbstractStandaloneGroupItem<Rats>
  implements MutableActorHandler, CallableActorUser, Pausable,
             FlowPauseStateListener, DebugScopeRestrictionHandler,
             AtomicExecution, LazySetupSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -154461277343021604L;

  /** the default log queue name. */
  public final static String DEFAULT_LOG = "unknown";

  /** the default flow errors queue name. */
  public final static String DEFAULT_FLOWERRORS = "flowerrors";

  /** the default send errors queue name. */
  public final static String DEFAULT_SENDERRORS = "senderrors";

  /** whether to perform lazy initialization of sub-actors. */
  protected boolean m_PerformLazySetup;

  /** the receiver to use. */
  protected RatInput m_Receiver;
  
  /** the actors for transforming the data. */
  protected LocalScopeSubProcess m_Actors;
  
  /** the transmitter to use. */
  protected RatOutput m_Transmitter;

  /** the callable name. */
  protected CallableActorReference m_Log;

  /** the callable log actor. */
  protected Actor m_LogActor;

  /** the generator for the log objects. */
  protected AbstractLogObjectGenerator m_LogGenerator;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the runnable doing the work. */
  protected RatRunnable m_Runnable;

  /** the name of the (optional) queue in internal storage for sending flow error to. */
  protected StorageName m_FlowErrorQueue;

  /** the name of the (optional) queue in internal storage for sending send error to. */
  protected StorageName m_SendErrorQueue;
  
  /** whether to show in {@link RatControl}. */
  protected boolean m_ShowInControl;

  /** whether the rat is currently being stopped. */
  protected boolean m_Stopping;

  /** whether errors a suppressed (only forwarded to log actor). */
  protected boolean m_SuppressErrors;

  /** the initial state. */
  protected RatState m_InitialState;

  /** the mode. */
  protected RatMode m_Mode;

  /** whether to wrap up after executing the actors. */
  protected boolean m_WrapUpAfterExecution;

  /** the state listeners. */
  protected Set<RatStateListener> m_StateListeners;

  /** whether lazy initialization has been performed. */
  protected boolean m_LazySetupPeformed;

  /** whether a Breakpoint actor is part of the sub-flow. */
  protected boolean m_BreakpointPresent;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines a single reception/transmission setup.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "receiver", "receiver",
      new DummyInput());

    m_OptionManager.add(
      "perform-lazy-setup", "performLazySetup",
      false);

    m_OptionManager.add(
      "actor", "actors",
      new Actor[0]);

    m_OptionManager.add(
      "transmitter", "transmitter",
      new DummyOutput());

    m_OptionManager.add(
      "log", "log",
      new CallableActorReference(DEFAULT_LOG));

    m_OptionManager.add(
      "log-generator", "logGenerator",
      new LogEntryGenerator());

    m_OptionManager.add(
      "scope-handling-variables", "scopeHandlingVariables",
      ScopeHandling.EMPTY);

    m_OptionManager.add(
      "propagate-variables", "propagateVariables",
      false);

    m_OptionManager.add(
      "variables-regexp", "variablesRegExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "scope-handling-storage", "scopeHandlingStorage",
      ScopeHandling.EMPTY);

    m_OptionManager.add(
      "propagate-storage", "propagateStorage",
      false);

    m_OptionManager.add(
      "storage-regexp", "storageRegExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "flow-error-queue", "flowErrorQueue",
      new StorageName(DEFAULT_FLOWERRORS));

    m_OptionManager.add(
      "send-error-queue", "sendErrorQueue",
      new StorageName(DEFAULT_SENDERRORS));

    m_OptionManager.add(
      "suppress-errors", "suppressErrors",
      true);

    m_OptionManager.add(
      "show-in-control", "showInControl",
      false);

    m_OptionManager.add(
      "initial-state", "initialState",
      RatState.RUNNING);

    m_OptionManager.add(
      "mode", "mode",
      RatMode.CONTINUOUS);

    m_OptionManager.add(
      "finish-before-stopping", "finishBeforeStopping",
      false);

    m_OptionManager.add(
      "stopping-timeout", "stoppingTimeout",
      -1, -1, null);

    m_OptionManager.add(
      "stopping-warning-interval", "stoppingWarningInterval",
      10000, -1, null);

    m_OptionManager.add(
      "wrapup-after-execution", "wrapUpAfterExecution",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Actors = new LocalScopeSubProcess();
    m_Actors.setParent(this);

    m_LazySetupPeformed = false;
    m_BreakpointPresent = false;

    m_Helper         = new CallableActorHelper();
    m_StateListeners = new HashSet<>();
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public synchronized void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_Actors.setLoggingLevel(value);
  }

  /**
   * Sets the receiver to use.
   *
   * @param value	the receiver
   */
  public void setReceiver(RatInput value) {
    m_Receiver = value;
    m_Receiver.setOwner(this);
    reset();
  }

  /**
   * Returns the receiver to use.
   *
   * @return		the receiver
   */
  public RatInput getReceiver() {
    return m_Receiver;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String receiverTipText() {
    return "The receiver to use.";
  }

  /**
   * Sets whether to perform a lazy setup, ie when first executed.
   *
   * @param value	true if to perform lazy setup
   */
  public void setPerformLazySetup(boolean value) {
    m_PerformLazySetup = value;
    reset();
  }

  /**
   * Returns whether to perform a lazy setup, ie when first executed.
   *
   * @return		true if to perform lazy setup
   */
  public boolean getPerformLazySetup() {
    return m_PerformLazySetup;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String performLazySetupTipText() {
    return
      "If enabled, initializing the sub-actors will only occurring the first "
	+ "time the rat gets executed (ie the input triggers); use with "
	+ "'wrapUpAfterExecution' to save memory; gets disabled if actors "
	+ "contain a Breakpoint.";
  }

  /**
   * Returns the handler of the actors.
   * 
   * @return		the handler
   */
  public LocalScopeSubProcess getActorHandler() {
    return m_Actors;
  }
  
  /**
   * Sets the flow items for this sequence.
   *
   * @param value 	the sequence items
   */
  public void setActors(Actor[] value) {
    m_Actors.setActors(value);
    reset();
  }

  /**
   * Returns the flow items of this sequence.
   *
   * @return 		the sequence items
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
    return 
	"The actors for transforming the data obtained by the receiver "
	+ "before sending it to the transmitter.";
  }

  /**
   * Sets the transmitter to use.
   *
   * @param value	the transmitter
   */
  public void setTransmitter(RatOutput value) {
    m_Transmitter = value;
    m_Transmitter.setOwner(this);
    reset();
  }

  /**
   * Returns the transmitter to use.
   *
   * @return		the transmitter
   */
  public RatOutput getTransmitter() {
    return m_Transmitter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String transmitterTipText() {
    return "The transmitter to use.";
  }

  /**
   * Sets the name of the callable log actor to use.
   *
   * @param value 	the callable name
   */
  public void setLog(CallableActorReference value) {
    m_Log = value;
    reset();
  }

  /**
   * Returns the name of the callable log actor in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getLog() {
    return m_Log;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logTipText() {
    return "The name of the callable log actor to use (logging disabled if actor not found).";
  }

  /**
   * Sets the generator for turning error messages into log objects.
   *
   * @param value 	the generator
   */
  public void setLogGenerator(AbstractLogObjectGenerator value) {
    m_LogGenerator = value;
    reset();
  }

  /**
   * Returns the generator for turning error messages into log objects.
   *
   * @return 		the generator
   */
  public AbstractLogObjectGenerator getLogGenerator() {
    return m_LogGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logGeneratorTipText() {
    return "The generator for turning the error message into the required log object.";
  }

  /**
   * Sets how to handle variables into the local scope.
   * 
   * @param value	the scope handling
   */
  public void setScopeHandlingVariables(ScopeHandling value) {
    m_Actors.setScopeHandlingVariables(value);
    reset();
  }
  
  /**
   * Returns how variables are handled in the local scope.
   * 
   * @return		the scope handling
   */
  public ScopeHandling getScopeHandlingVariables() {
    return m_Actors.getScopeHandlingVariables();
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeHandlingVariablesTipText() {
    return m_Actors.scopeHandlingVariablesTipText(); 
  }

  /**
   * Sets whether to propagate variables from the local to the outer scope.
   * 
   * @param value	if true then variables get propagated
   */
  public void setPropagateVariables(boolean value) {
    m_Actors.setPropagateVariables(value);
    reset();
  }
  
  /**
   * Returns whether to propagate variables from the local to the outer scope.
   * 
   * @return		true if variables get propagated
   */
  public boolean getPropagateVariables() {
    return m_Actors.getPropagateVariables();
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propagateVariablesTipText() {
    return "If enabled, variables that match the specified regular expression get propagated to the outer scope.";
  }

  /**
   * Sets the regular expression that variable names must match to get
   * propagated.
   * 
   * @param value	the expression
   */
  public void setVariablesRegExp(BaseRegExp value) {
    m_Actors.setVariablesRegExp(value);
    reset();
  }
  
  /**
   * Returns the regular expression that variable names must match to get
   * propagated.
   * 
   * @return		the expression
   */
  public BaseRegExp getVariablesRegExp() {
    return m_Actors.getVariablesRegExp();
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variablesRegExpTipText() {
    return "The regular expression that variable names must match in order to get propagated.";
  }

  /**
   * Sets how to handle storage in the local scope.
   * 
   * @param value	the scope handling
   */
  public void setScopeHandlingStorage(ScopeHandling value) {
    m_Actors.setScopeHandlingStorage(value);
    reset();
  }
  
  /**
   * Returns how storage is handled in the local scope.
   * 
   * @return		the scope handling
   */
  public ScopeHandling getScopeHandlingStorage() {
    return m_Actors.getScopeHandlingStorage();
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeHandlingStorageTipText() {
    return m_Actors.scopeHandlingStorageTipText();
  }

  /**
   * Sets whether to propagate storage items from the local to the outer scope.
   * 
   * @param value	if true then storage items get propagated
   */
  public void setPropagateStorage(boolean value) {
    m_Actors.setPropagateStorage(value);
    reset();
  }
  
  /**
   * Returns whether to propagate storage items from the local to the outer scope.
   * 
   * @return		true if storage items get propagated
   */
  public boolean getPropagateStorage() {
    return m_Actors.getPropagateStorage();
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propagateStorageTipText() {
    return "If enabled, storage items which names match the specified regular expression get propagated to the outer scope.";
  }

  /**
   * Sets the regular expression that storage item names must match to get
   * propagated.
   * 
   * @param value	the expression
   */
  public void setStorageRegExp(BaseRegExp value) {
    m_Actors.setStorageRegExp(value);
    reset();
  }
  
  /**
   * Returns the regular expression that storage item names must match to get
   * propagated.
   * 
   * @return		the expression
   */
  public BaseRegExp getStorageRegExp() {
    return m_Actors.getStorageRegExp();
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageRegExpTipText() {
    return "The regular expression that the names of storage items must match in order to get propagated.";
  }

  /**
   * Sets the name for the queue in internal storage to feed with flow errors.
   *
   * @param value	the name
   * @see		#queueFlowError(Object, String)
   * @see		ErrorContainer
   */
  public void setFlowErrorQueue(StorageName value) {
    m_FlowErrorQueue = value;
    reset();
  }

  /**
   * Returns the name for the queue in internal storage to feed with flow errors.
   *
   * @return		the name
   * @see		#queueFlowError(Object, String)
   * @see		ErrorContainer
   */
  public StorageName getFlowErrorQueue() {
    return m_FlowErrorQueue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String flowErrorQueueTipText() {
    return
      "The name of the (optional) queue in internal storage to feed with flow errors; "
	+ "Forwards the original data received as payload in an " + ErrorContainer.class.getName()
	+ " alongside the error message.";
  }

  /**
   * Sets the name for the queue in internal storage to feed with send errors.
   *
   * @param value	the name
   * @see		#queueSendError(Object, String)
   * @see		ErrorContainer
   */
  public void setSendErrorQueue(StorageName value) {
    m_SendErrorQueue = value;
    reset();
  }

  /**
   * Returns the name for the queue in internal storage to feed with send errors.
   *
   * @return		the name
   * @see		#queueSendError(Object, String)
   * @see		ErrorContainer
   */
  public StorageName getSendErrorQueue() {
    return m_SendErrorQueue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sendErrorQueueTipText() {
    return
      "The name of the (optional) queue in internal storage to feed with send errors; "
	+ "Forwards the original data received as payload in an " + ErrorContainer.class.getName()
	+ " alongside the error message.";
  }

  /**
   * Sets whether to suppress errors and only forward them to the log actor.
   * 
   * @param value	true if to suppress errors
   * @see		#setLog(CallableActorReference)
   */
  public void setSuppressErrors(boolean value) {
    m_SuppressErrors = value;
    reset();
  }
  
  /**
   * Returns whether suppress errors and only forward them to the log actor.
   * 
   * @return		true if to suppress errors
   * @see		#setLog(CallableActorReference)
   */
  public boolean getSuppressErrors() {
    return m_SuppressErrors;
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suppressErrorsTipText() {
    return "If enabled, errors are suppressed and only forwarded to the log actor.";
  }

  /**
   * Sets whether to show in RatControl.
   *
   * @param value	true if to show in RatControl
   */
  public void setShowInControl(boolean value) {
    m_ShowInControl = value;
    reset();
  }

  /**
   * Returns whether to show in RatControl.
   *
   * @return		true if to show in RatControl
   */
  public boolean getShowInControl() {
    return m_ShowInControl;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showInControlTipText() {
    return "If enabled, this Rat will be displayed in the " + RatControl.class.getName() + " control panel.";
  }

  /**
   * Sets the initial state of the Rat actor.
   *
   * @param value	the state
   */
  public void setInitialState(RatState value) {
    m_InitialState = value;
    reset();
  }

  /**
   * Returns the initial state of the Rat actor.
   *
   * @return		the state
   */
  public RatState getInitialState() {
    return m_InitialState;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initialStateTipText() {
    return "The initial state of the Rat actor.";
  }

  /**
   * Sets the mode the Rat is run in.
   *
   * @param value	the mode
   */
  public void setMode(RatMode value) {
    m_Mode = value;
    reset();
  }

  /**
   * Returns the mode the Rat is run in.
   *
   * @return		the mode
   */
  public RatMode getMode() {
    return m_Mode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modeTipText() {
    return "The mode the Rat actor is run in.";
  }

  /**
   * Sets whether to finish processing before stopping execution.
   *
   * @param value	if true then actor finishes processing first
   */
  public void setFinishBeforeStopping(boolean value) {
    m_Actors.setFinishBeforeStopping(value);
    reset();
  }

  /**
   * Returns whether to finish processing before stopping execution.
   *
   * @return		true if actor finishes processing first
   */
  public boolean getFinishBeforeStopping() {
    return m_Actors.getFinishBeforeStopping();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finishBeforeStoppingTipText() {
    return m_Actors.finishBeforeStoppingTipText();
  }

  /**
   * Sets the timeout for waiting for the sub-flow to stop.
   *
   * @param value	timeout in milliseconds (<= 0 for infinity)
   */
  public void setStoppingTimeout(int value) {
    m_Actors.setStoppingTimeout(value);
    reset();
  }

  /**
   * Returns the timeout for waiting for the sub-flow to stop.
   *
   * @return		timeout in milliseconds (<= 0 for infinity)
   */
  public int getStoppingTimeout() {
    return m_Actors.getStoppingTimeout();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stoppingTimeoutTipText() {
    return m_Actors.stoppingTimeoutTipText();
  }

  /**
   * Sets the interval for outputting warnings if the sub-flow hasn't stopped yet (and no stopping timeout set).
   *
   * @param value	interval in milliseconds (<= 0 no warning)
   */
  public void setStoppingWarningInterval(int value) {
    m_Actors.setStoppingWarningInterval(value);
    reset();
  }

  /**
   * Returns the interval for outputting warnings if the sub-flow hasn't stopped yet (and no stopping timeout set).
   *
   * @return		interval in milliseconds (<= 0 no warning)
   */
  public int getStoppingWarningInterval() {
    return m_Actors.getStoppingWarningInterval();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stoppingWarningIntervalTipText() {
    return m_Actors.stoppingWarningIntervalTipText();
  }

  /**
   * Sets whether to wrap up the actors after execution. Only available
   * in conjunction with {@link #m_PerformLazySetup}.
   *
   * @param value	if true then actors get wrapped up after execution
   */
  public void setWrapUpAfterExecution(boolean value) {
    m_WrapUpAfterExecution = value;
    reset();
  }

  /**
   * Returns whether to wrap up the actors after execution. Only available
   * in conjunction with {@link #m_PerformLazySetup}.
   *
   * @return		true if actors get wrapped up after execution
   */
  public boolean getWrapUpAfterExecution() {
    return m_WrapUpAfterExecution;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String wrapUpAfterExecutionTipText() {
    return "Whether to wrapUp the actors after execution to save memory; only available in conjunction with 'performLazySetup'.";
  }

  /**
   * Returns the additional information.
   *
   * @return		the additional information
   */
  @Override
  public String getAdditionalInformation() {
    String	result;

    result = super.getAdditionalInformation();

    result += "\n\nRat input/output:\n";
    result += "-receiver generates: " + Utils.classToString(getReceiver().generates()) + "\n";
    result += "-transmitter accepts: " + Utils.classesToString(getTransmitter().accepts()) + "\n";

    return result;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "receiver", m_Receiver, "in: ");
    if (!(m_Transmitter instanceof DummyOutput))
      result += QuickInfoHelper.toString(this, "transmitter", m_Transmitter, ", out: ");
    if (!m_Log.getValue().equals(DEFAULT_LOG))
      result += QuickInfoHelper.toString(this, "log", m_Log, ", log: ");
    if (!m_FlowErrorQueue.getValue().equals(DEFAULT_FLOWERRORS))
      result += QuickInfoHelper.toString(this, "flowErrorQueue", m_FlowErrorQueue, ", flow errors: ");
    if (!m_SendErrorQueue.getValue().equals(DEFAULT_SENDERRORS))
      result += QuickInfoHelper.toString(this, "sendErrorQueue", m_SendErrorQueue, ", send errors: ");
    result += QuickInfoHelper.toString(this, "showInControl", m_ShowInControl, "control", ", ");

    result += ", variables [";
    result += QuickInfoHelper.toString(this, "scopeHandlingVariables", getScopeHandlingVariables(), "scope: ");
    result += QuickInfoHelper.toString(this, "propagateVariables", getPropagateVariables(), "propagate", ", ");
    if (getPropagateVariables())
      result += QuickInfoHelper.toString(this, "variablesRegExp", getVariablesRegExp(), ", regexp: ");
    result += "]";
    
    result += ", storage [";
    result += QuickInfoHelper.toString(this, "scopeHandlingStorage", getScopeHandlingStorage(), "scope: ");
    result += QuickInfoHelper.toString(this, "propagateStorage", getPropagateStorage(), "propagate", ", ");
    if (getPropagateStorage())
      result += QuickInfoHelper.toString(this, "storageRegExp", getStorageRegExp(), ", regexp: ");
    result += "]";

    return result;
  }
  
  /**
   * Sets the parent of this actor, e.g., the group it belongs to.
   *
   * @param value	the new parent
   */
  @Override
  public void setParent(Actor value) {
    super.setParent(value);
    m_Actors.setParent(null);
    m_Actors.setParent(value);
    m_Actors.setName(getName());
  }
  
  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return m_Actors.getActorHandlerInfo();
  }

  /**
   * Sets the error handler to use for handling errors in the flow.
   *
   * @param value	the error handler
   */
  public void setErrorHandler(ErrorHandler value) {
    super.setErrorHandler(value);
    if (m_Actors != null)
      m_Actors.setErrorHandler(value);
  }

  /**
   * Handles the given error message with the flow that this actor belongs to,
   * if the flow has error logging turned on. Might stop the flow as well.
   *
   * @param source	the source of the error
   * @param type	the type of error
   * @param msg		the error message to log
   * @return		null if {@link #m_SuppressErrors}, otherwise the error message
   */
  @Override
  public String handleError(Actor source, String type, String msg) {
    Object		logObj;

    if (m_LogActor != null) {
      logObj = m_LogGenerator.generate(this, source, type, msg);
      ((InputConsumer) m_LogActor).input(new Token(logObj));
      m_LogActor.execute();
    }

    source.getLogger().severe(msg);

    if (m_StopFlowOnError)
      stopIfNecessary();

    if (m_SuppressErrors)
      return null;
    else
      return msg;
  }

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything is fine, otherwise the error
   */
  @Override
  public String check() {
    return m_Actors.check();
  }

  /**
   * Returns the size of the group.
   *
   * @return		the size
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
   * @return		null if successful, otherwise error message
   */
  @Override
  public String set(int index, Actor actor) {
    return m_Actors.set(index, actor);
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
   * Returns the first non-skipped actor.
   *
   * @return		the first 'active' actor, null if none available
   */
  @Override
  public Actor firstActive() {
    return m_Actors.firstActive();
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  @Override
  public Actor lastActive() {
    return m_Actors.lastActive();
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   * @return		null if successful, otherwise error message
   */
  @Override
  public String add(Actor actor) {
    if (actor == this)
      throw new IllegalArgumentException("Cannot add itself!");
    return m_Actors.add(actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   * @return		null if successful, otherwise error message
   */
  @Override
  public String add(int index, Actor actor) {
    if (actor == this)
      throw new IllegalArgumentException("Cannot add itself!");
    return m_Actors.add(index, actor);
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  @Override
  public Actor remove(int index) {
    return m_Actors.remove(index);
  }

  /**
   * Removes all actors.
   */
  @Override
  public void removeAll() {
    m_Actors.removeAll();
  }

  /**
   * Returns the internal local scope transformer.
   *
   * @return		the actor, null if not available
   */
  public LocalScopeSubProcess getLocalScope() {
    return m_Actors;
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
    m_Actors.setVariables(value);
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getLog());
  }

  /**
   * Checks whether a reference to the callable actor is currently available.
   *
   * @return		true if a reference is available
   * @see		#getCallableActor()
   */
  public boolean hasCallableActor() {
    return (m_LogActor != null);
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  public Actor getCallableActor() {
    return m_LogActor;
  }

  /**
   * Returns whether a breakpoint is present.
   *
   * @return		true if present
   */
  public boolean isBreakpointPresent() {
    return m_BreakpointPresent;
  }

  /**
   * Logs an error message if a valid callable log actor has been set up.
   * 
   * @param msg		the message to log
   * @param id		an optional ID of the data token that failed in the web service
   */
  public void log(String msg, String id) {
    LogEntry		log;
    Properties		props;
    String		result;

    if ((id != null) && (id.trim().length() == 0))
      id = null;
    
    // just log to console if not log actor
    if (m_LogActor == null) {
      getLogger().severe("LOG: " + ((id == null) ? "" : (id + " - ")) + msg);
      return;
    }
    
    // generate log container
    props = new Properties();
    props.setProperty(LogEntry.KEY_ERRORS, msg);
    if (id != null)
      props.setProperty(LogEntry.KEY_ID, id);

    log = new LogEntry();
    log.setType("Rat");
    log.setSource(getFullName());
    log.setStatus(LogEntry.STATUS_NEW);
    log.setMessage(props);
    
    try {
      synchronized(m_LogActor) {
	((InputConsumer) m_LogActor).input(new Token(log));
	result = m_LogActor.execute();
      }
      if (result != null)
	getLogger().severe("Failed to log message:\n" + log + "\n" + result);
    }
    catch (Exception e) {
      handleException("Failed to log message:\n" + log, e);
    }
  }
  
  /**
   * Creates an {@link ErrorContainer} with the provided data and puts it
   * in the {@link #m_SendErrorQueue} (if the queue is available).
   * 
   * @param payload	the payload to forward
   * @param error	the associated error
   * @return		true if successfully queued
   * @see		ErrorContainer
   */
  public boolean queueSendError(Object payload, String error) {
    ErrorContainer	cont;
    
    if (!getStorageHandler().getStorage().has(m_SendErrorQueue))
      return false;
    
    cont = new ErrorContainer(payload, error, getFullName() + "#send");
    return QueueHelper.enqueue(this, m_SendErrorQueue, cont);
  }

  /**
   * Creates an {@link ErrorContainer} with the provided data and puts it
   * in the {@link #m_FlowErrorQueue} (if the queue is available).
   *
   * @param payload	the payload to forward
   * @param error	the associated error
   * @return		true if successfully queued
   * @see		ErrorContainer
   */
  public boolean queueFlowError(Object payload, String error) {
    ErrorContainer	cont;

    if (!getStorageHandler().getStorage().has(m_FlowErrorQueue))
      return false;

    cont = new ErrorContainer(payload, error, getFullName() + "#flow");
    return QueueHelper.enqueue(this, m_FlowErrorQueue, cont);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    Compatibility	comp;
    String		msg;
    HashSet<String>	variables;
    PauseStateManager 	manager;

    result = super.setUp();

    if (result == null) {
      m_BreakpointPresent = (!ActorUtils.enumerate(m_Actors, new Class[]{Breakpoint.class}).isEmpty());
      if (!m_PerformLazySetup || isBreakpointPresent())
	result = m_Actors.setUp();
    }
    
    if (result == null) {
      comp = new Compatibility();
      if (m_Actors.active() > 0) {
	if (!comp.isCompatible(new Class[]{m_Receiver.generates()}, m_Actors.accepts()))
	  result = "Receiver not compatible with actors: " 
	      + Utils.classToString(m_Receiver.generates()) 
	      + " != " 
	      + Utils.classesToString(m_Actors.accepts());
	if (result == null) {
	  if (!comp.isCompatible(m_Actors.generates(), m_Transmitter.accepts()))
	    result = "Actors not compatible with transmitter: " 
		+ Utils.classesToString(m_Actors.generates()) 
		+ " != " 
		+ Utils.classesToString(m_Transmitter.accepts());
	}
      }
      else {
	if (!comp.isCompatible(new Class[]{m_Receiver.generates()}, m_Transmitter.accepts()))
	  result = "Receiver not compatible with transmitter: " 
	      + Utils.classToString(m_Receiver.generates()) 
	      + " != " 
	      + Utils.classesToString(m_Transmitter.accepts());
      }
      if (result == null)
	result = m_Receiver.setUp();
      if (result == null)
	result = m_Transmitter.setUp();
    }
    
    if (result == null) {
      m_LogActor = findCallableActor();
      if (m_LogActor == null) {
	if (!m_Log.getValue().equals(CallableActorReference.UNKNOWN)) {
	  msg = "Couldn't find callable log actor '" + getLog() + "' - logging disabled!";
	  getLogger().info(msg);
	}
      }
      else {
	comp = new Compatibility();
	if (!comp.isCompatible(new Class[]{LogEntry.class}, ((InputConsumer) m_LogActor).accepts()))
	  result = "Log actor '" + getLog() + "' must accept " + LogEntry.class.getName() + "!";
	if (result == null) {
	  variables = findVariables(m_LogActor);
	  m_DetectedVariables.addAll(variables);
	  if (!m_DetectedVariables.isEmpty())
	    getVariables().addVariableChangeListener(this);
	}
      }
    }
    
    if (result == null) {
      // redirect error handling
      ActorUtils.updateErrorHandler(this, this, isLoggingEnabled());
      m_Actors.setErrorHandler(this);
    }

    if (result == null) {
      if (getRoot() instanceof PauseStateHandler) {
	manager = ((PauseStateHandler) getRoot()).getPauseStateManager();
	if (manager != null)
	  manager.addListener(this);
      }
    }

    return result;
  }

  /**
   * Returns whether lazy setup has been performed.
   *
   * @return		true if performed or breakpoint present (which disables lazy setup)
   * @see		#isBreakpointPresent()
   */
  public boolean hasLazySetupPerformed() {
    return m_LazySetupPeformed || isBreakpointPresent();
  }

  /**
   * Performs the lazy setup.
   *
   * @return		null if successful, otherwise error message
   */
  public String lazySetup() {
    m_LazySetupPeformed = true;
    return m_Actors.setUp();
  }

  /**
   * Starts the runnable.
   *
   * @return		null if successful, otherwise error message
   */
  public String startRunnable() {
    String		result;

    result = null;

    try {
      // re-setup actors after manual stop
      if (m_Actors.isStopped())
        m_Actors.setUp();
      // make sure we have all the latest variable values
      m_Receiver.initReception();
      m_Transmitter.initTransmission();

      // start runnable
      m_Runnable = new RatRunnable(this, (m_InitialState == RatState.PAUSED));
      m_Runnable.setLoggingLevel(getLoggingLevel());
      switch (m_InitialState) {
	case RUNNING:
	  // don't do anything
	  break;
	case PAUSED:
	  if (isLoggingEnabled())
	    getLogger().info("Started rat in paused mode");
	  break;
	default:
	  result = "Unhandled initial rat state: " + m_InitialState;
      }
      if (result == null)
	new Thread(m_Runnable).start();
    }
    catch (Exception e) {
      result = handleException("Failed to start!", e);
    }

    if (result == null)
      notifyRatStateListeners();

    return result;
  }

  /**
   * For performing variable checks.
   *
   * @return		always null
   */
  @Override
  protected String performVariableChecks() {
    return null;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    switch (m_Mode) {
      case CONTINUOUS:
        return startRunnable();
      case MANUAL:
        return null;
      default:
        throw new IllegalStateException("Unhandled rat mode: " + m_Mode);
    }
  }

  /**
   * Pauses the execution.
   */
  @Override
  public void pauseExecution() {
    if (m_Runnable != null)
      m_Runnable.pauseExecution();
    notifyRatStateListeners();
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  @Override
  public boolean isPaused() {
    return (m_Runnable != null) && m_Runnable.isPaused();
  }

  /**
   * Resumes the execution.
   */
  @Override
  public void resumeExecution() {
    if (m_Runnable != null)
      m_Runnable.resumeExecution();
    notifyRatStateListeners();
  }

  /**
   * Returns whwether the runnable is currently active.
   *
   * @return		true if active
   */
  public boolean isRunnableActive() {
    return (m_Runnable != null);
  }

  /**
   * Stops the runnable.
   */
  public void stopRunnable() {
    if (m_Runnable != null) {
      m_Runnable.stopExecution();
      while ((m_Runnable != null) && m_Runnable.isRunning()) {
	try {
	  synchronized(this) {
	    wait(10);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }
      wrapUpRunnable();
    }
  }

  /**
   * Nulls the runnable and notifies listeners.
   */
  public void wrapUpRunnable() {
    m_Runnable = null;
    notifyRatStateListeners();
  }

  /**
   * Stops the execution if necessary.
   */
  protected synchronized void stopIfNecessary() {
    if (m_Stopping)
      return;
    if (!isStopped()) {
      m_Stopping = true;
      m_Actors.stopExecution();
      stopRunnable();
      m_Stopping = false;
    }
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    if (m_Actors != null)
      m_Actors.flushExecution();
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    stopIfNecessary();
    super.stopExecution();
  }
  
  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_Runnable != null)
      stopIfNecessary();
    
    m_Actors.wrapUp();

    if (m_LogActor != null) {
      synchronized(m_LogActor) {
	m_LogActor.wrapUp();
      }
    }

    m_LazySetupPeformed = false;

    super.wrapUp();
  }

  /**
   * Gets called when the pause state of the flow changes.
   *
   * @param e		the event
   */
  @Override
  public void flowPauseStateChanged(FlowPauseStateEvent e) {
    switch (e.getType()) {
      case PAUSED:
	pauseExecution();
	break;
      case RESUMED:
	resumeExecution();
	break;
      default:
	throw new IllegalStateException("Unhandled pause state: " + e.getType());
    }
  }

  /**
   * Adds the state listener.
   *
   * @param l		the listener
   */
  public void addRatStateListener(RatStateListener l) {
    m_StateListeners.add(l);
  }

  /**
   * Removes the state listener.
   *
   * @param l		the listener
   */
  public void removeRatStateListener(RatStateListener l) {
    m_StateListeners.remove(l);
  }

  /**
   * Notifies all the listeners.
   */
  public void notifyRatStateListeners() {
    RatStateEvent	e;

    e = new RatStateEvent(this, this);
    for (RatStateListener l: m_StateListeners)
      l.ratStateChanged(e);
  }

  /**
   * Returns the scope restriction in place.
   *
   * @return		the scope restriction
   */
  public AbstractScopeRestriction getDebugScopeRestriction() {
    SubFlowRestriction  result;

    result = new SubFlowRestriction();
    result.setRoot(new ActorPath(getFullName()));

    return result;
  }

  /**
   * Returns the scope restriction in place.
   *
   * @param context	the actor's node
   * @return		the scope restriction
   */
  public AbstractScopeRestriction getDebugScopeRestriction(Node context) {
    SubFlowRestriction  result;

    result = new SubFlowRestriction();
    result.setRoot(new ActorPath(context.getFullName()));

    return result;
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    PauseStateManager	manager;

    m_Actors.cleanUp();
    m_StateListeners.clear();

    if (m_LogActor != null) {
      m_LogActor.cleanUp();
      m_LogActor = null;
    }

    if (getRoot() instanceof PauseStateHandler) {
      manager = ((PauseStateHandler) getRoot()).getPauseStateManager();
      if (manager != null)
	manager.removeListener(this);
    }

    super.cleanUp();
  }
}
