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
 * Rat.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.Variables;
import adams.flow.control.SubProcess;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.Compatibility;
import adams.flow.core.InternalActorHandler;
import adams.flow.core.MutableActorHandler;
import adams.flow.standalone.rats.DummyInput;
import adams.flow.standalone.rats.DummyOutput;
import adams.flow.standalone.rats.RatInput;
import adams.flow.standalone.rats.RatOutput;
import adams.flow.standalone.rats.RatRunnable;

/**
 <!-- globalinfo-start -->
 * Defines a single reception&#47;transmission setup.
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-receiver &lt;adams.flow.standalone.rats.RatInput&gt; (property: receiver)
 * &nbsp;&nbsp;&nbsp;The receiver to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.DummyInput
 * </pre>
 * 
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors for transforming the data obtained by the receiver before sending 
 * &nbsp;&nbsp;&nbsp;it to the transmitter.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-transmitter &lt;adams.flow.standalone.rats.RatOutput&gt; (property: transmitter)
 * &nbsp;&nbsp;&nbsp;The transmitter to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.DummyOutput
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Rat
  extends AbstractStandaloneGroupItem
  implements MutableActorHandler, InternalActorHandler {

  /** for serialization. */
  private static final long serialVersionUID = -154461277343021604L;

  /** the receiver to use. */
  protected RatInput m_Receiver;
  
  /** the actors for transforming the data. */
  protected SubProcess m_Actors;
  
  /** the transmitter to use. */
  protected RatOutput m_Transmitter;
  
  /** the runnable doing the work. */
  protected RatRunnable m_Runnable;
  
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
	    "actor", "actors",
	    new AbstractActor[0]);

    m_OptionManager.add(
	    "transmitter", "transmitter",
	    new DummyOutput());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Actors = new SubProcess();
    m_Actors.setAllowEmpty(true);
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
   * Returns the handler of the actors.
   * 
   * @return		the handler
   */
  public SubProcess getActorHandler() {
    return m_Actors;
  }
  
  /**
   * Sets the flow items for this sequence.
   *
   * @param value 	the sequence items
   */
  public void setActors(AbstractActor[] value) {
    m_Actors.setActors(value);
    reset();
  }

  /**
   * Returns the flow items of this sequence.
   *
   * @return 		the sequence items
   */
  public AbstractActor[] getActors() {
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "receiver", m_Receiver, "receiver: ");
    result += QuickInfoHelper.toString(this, "transmitter", m_Transmitter, ", transmitter: ");
    
    return result;
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
  public AbstractActor get(int index) {
    return m_Actors.get(index);
  }

  /**
   * Sets the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to set at this position
   */
  @Override
  public void set(int index, AbstractActor actor) {
    m_Actors.set(index, actor);
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
  public AbstractActor firstActive() {
    return m_Actors.firstActive();
  }

  /**
   * Returns the last non-skipped actor.
   *
   * @return		the last 'active' actor, null if none available
   */
  @Override
  public AbstractActor lastActive() {
    return m_Actors.lastActive();
  }

  /**
   * Inserts the actor at the end.
   *
   * @param actor	the actor to insert
   */
  @Override
  public void add(AbstractActor actor) {
    m_Actors.add(actor);
  }

  /**
   * Inserts the actor at the given position.
   *
   * @param index	the position
   * @param actor	the actor to insert
   */
  @Override
  public void add(int index, AbstractActor actor) {
    m_Actors.add(index, actor);
  }

  /**
   * Removes the actor at the given position and returns the removed object.
   *
   * @param index	the position
   * @return		the removed actor
   */
  @Override
  public AbstractActor remove(int index) {
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
   * Updates the Variables instance in use.
   * <p/>
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
   * Returns the internal actor.
   *
   * @return		the actor, null if not available
   */
  public Actor getInternalActor() {
    return m_Actors;
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
    
    result = super.setUp();

    if (result == null) {
      m_Actors.setParent(this);
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
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    
    result = null;
    
    try {
      m_Runnable = new RatRunnable(this);
      m_Runnable.setLoggingLevel(getLoggingLevel());
      new Thread(m_Runnable).start();
    }
    catch (Exception e) {
      result = handleException("Failed to execute!", e);
    }
    
    return result;
  }
  
  /**
   * Stops the execution if necessary.
   */
  protected void stopIfNecessary() {
    if (!m_Stopped) {
      m_Receiver.stopExecution();
      m_Actors.stopExecution();
      m_Transmitter.stopExecution();
      if (m_Runnable != null) {
	m_Runnable.stopExecution();
	while (m_Runnable.isRunning()) {
	  try {
	    synchronized(this) {
	      wait(100);
	    }
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}
	m_Runnable = null;
      }
    }
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
    
    super.wrapUp();
  }
}
