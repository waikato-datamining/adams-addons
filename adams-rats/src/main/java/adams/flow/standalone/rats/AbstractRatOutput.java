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
 * AbstractRatOutput.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import java.util.HashSet;
import java.util.Hashtable;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoSupporter;
import adams.core.ShallowCopySupporter;
import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.Variables;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;
import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;
import adams.flow.core.ActorVariablesFinder;
import adams.flow.core.QueueHelper;
import adams.flow.standalone.QueueInit;
import adams.flow.standalone.Rat;

/**
 * Ancestor for output transmitters.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRatOutput
  extends AbstractOptionHandler
  implements RatOutput, ShallowCopySupporter<AbstractRatOutput>, Stoppable, 
             QuickInfoSupporter, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -2633576389566678059L;

  /** the owner. */
  protected Rat m_Owner;

  /** whether the reception was stopped. */
  protected boolean m_Stopped;

  /** the logging prefix. */
  protected String m_LoggingPrefix;
  
  /** the input. */
  protected Object m_Input;

  /** the variable names that are used within this output. */
  protected HashSet<String> m_DetectedVariables;

  /** the variable names (referencing callable actors or storage) that are used within this output. */
  protected HashSet<String> m_DetectedObjectVariables;

  /** whether the output needs re-setting up because of modified variables. */
  protected HashSet<String> m_VariablesUpdated;

  /** for backing up the state of an actor. */
  protected Hashtable<String,Object> m_BackupState;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LoggingPrefix           = "";
    m_DetectedVariables       = null;
    m_DetectedObjectVariables = null;
    m_VariablesUpdated        = new HashSet<String>();
  }

  /**
   * Initializes the logger.
   */
  @Override
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(m_LoggingPrefix);
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }

  /**
   * Returns the full name of the receiver.
   * 
   * @return		the name
   */
  public String getFullName() {
    if (getOwner() != null)
      return getOwner().getFullName() + "$" + getClass().getSimpleName();
    else
      return "???$" + getClass().getSimpleName();
  }

  /**
   * Updates the prefix of the logger.
   */
  protected void updatePrefix() {
    if (getOwner() != null) {
      m_LoggingPrefix = getFullName();
      m_Logger        = null;
    }
  }

  /**
   * Outputs the stacktrace along with the message on stderr and returns a 
   * combination of both of them as string.
   * 
   * @param msg		the message for the exception
   * @param t		the exception
   * @return		the full error message (message + stacktrace)
   */
  protected String handleException(String msg, Throwable t) {
    return Utils.handleException(this, msg, t);
  }

  /**
   * Sets the actor the transmitter belongs to.
   * 
   * @param value	the owner
   */
  public void setOwner(Rat value) {
    m_Owner = value;
    updatePrefix();
  }

  /**
   * Returns the actor the transmitter belongs to.
   * 
   * @return		the owner
   */
  public Rat getOwner() {
    return m_Owner;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the type of data that gets accepted.
   * 
   * @return		the type of data
   */
  public abstract Class[] accepts();
  
  /**
   * Whether input can be supplied at the moment.
   * 
   * @return		true if input is accepted
   */
  @Override
  public boolean canInput() {
    return (m_Input == null);
  }
  
  /**
   * The data to transmit.
   * 
   * @param obj		the data
   */
  public void input(Object obj) {
    m_Input = obj;
  }

  /**
   * Returns the Variables instance to use.
   *
   * @return		the variables instance, null if not available
   */
  public Variables getVariables() {
    if (getOwner() != null)
      return getOwner().getVariables();
    else
      return null;
  }

  /**
   * Recursively finds all the variables used in this RatOutput.
   *
   * @return		the variables that were found
   */
  protected HashSet<String> findVariables() {
    ActorVariablesFinder	finder;
    HashSet<String>		result;

    getLogger().finest("Locating variables in " + getFullName() + "...");

    finder = new ActorVariablesFinder();
    getOptionManager().traverse(finder);
    result = finder.getResult();

    getLogger().finest("Found variables in " + getFullName() + " (" + result.size() + "): " + result);

    return result;
  }

  /**
   * Updates the detected variables.
   */
  protected void updateDetectedVariables() {
    getOptionManager().registerVariables();
    m_DetectedVariables = findVariables();

    // split off callable refs/storage refs
    m_DetectedObjectVariables = new HashSet<String>();
    for (String var: m_DetectedVariables) {
      if (getVariables().isObject(var))
	m_DetectedObjectVariables.add(var);
    }
    m_DetectedVariables.removeAll(m_DetectedObjectVariables);

    if (m_DetectedVariables.size() > 0)
      getVariables().addVariableChangeListener(this);
  }

  /**
   * Hook method for performing checks at setup time.
   * 
   * @return		null if successful, otherwise error message
   */
  public String setUp() {
    String	result;

    result = null;
    
    if (m_Owner == null)
      result = "No owning actor set!";
    
    if (result == null) {
      updatePrefix();
      updateDetectedVariables();
    }
    
    return result;
  }

  /**
   * Hook method for performing checks.
   * <p/>
   * Default implementation does nothing.
   * 
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    return null;
  }
  
  /**
   * A simple waiting method.
   * 
   * @param msec	the maximum number of milli-seconds to wait, no waiting if 0
   */
  protected void doWait(int msec) {
    int		count;
    int		current;
    
    if (msec == 0)
      return;
    
    if (isLoggingEnabled())
      getLogger().fine("doWait: " + msec);
    
    count = 0;
    while ((count < msec) && !m_Stopped) {
      try {
	current = msec - 100;
	if (current <= 0)
	  current = msec;
	if (current > 100)
	  current = 100;
	synchronized(this) {
	  wait(current);
	}
	count += current;
      }
      catch (Exception e) {
	// ignored
      }
    }
  }
  
  /**
   * Returns the flow's queue.
   * 
   * @param name	the name of the queue
   * @return		the queue, null if not initialized
   * @see		QueueInit
   */
  protected StorageQueueHandler getQueue(StorageName name) {
    return QueueHelper.getQueue(getOwner(), name);
  }

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  public void variableChanged(VariableChangeEvent e) {
    if ((m_DetectedVariables == null) || (m_DetectedVariables.size() == 0))
      return;
    if (m_VariablesUpdated.contains(e.getName()))
      return;

    if (m_DetectedVariables.contains(e.getName()) && (e.getType() != Type.REMOVED)) {
      m_VariablesUpdated.add(e.getName());
      getLogger().info("Changes in variable '" + e.getName() + "'");
    }
  }

  /**
   * Checks whether a specified key is present in the current backup state.
   *
   * @param key		the key of the object to look for in the backup state
   * @return		true if key present
   */
  protected boolean isBackedUp(String key) {
    if (m_BackupState == null)
      return false;
    else
      return m_BackupState.containsKey(key);
  }

  /**
   * Removes the object with the specified key from the current backup state.
   *
   * @param key		the key of the object to remove from the backup state
   */
  protected void pruneBackup(String key) {
    if (m_BackupState == null)
      return;
    if (!m_BackupState.containsKey(key))
      return;
    m_BackupState.remove(key);
  }

  /**
   * Removes entries from the backup.
   * <p/>
   * Default implementation does nothing.
   *
   * @see		#reset()
   */
  protected void pruneBackup() {
  }

  /**
   * Backs up the current state of the actor before update the variables.
   * <p/>
   * Default implementation only returns an empty hashtable.
   *
   * @return		the backup
   * @see		#updateVariables()
   * @see		#restoreState(Hashtable)
   */
  protected Hashtable<String,Object> backupState() {
    return new Hashtable<String,Object>();
  }

  /**
   * Restores the state of the actor before the variables got updated.
   * <p/>
   * Default implementation does nothing.
   *
   * @param state	the backup of the state to restore from
   * @see		#updateVariables()
   * @see		#backupState()
   */
  protected void restoreState(Hashtable<String,Object> state) {
  }

  /**
   * Gets called when the actor needs to be re-setUp when a variable changes.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String updateVariables() {
    String		result;
    Variables		backup;

    if (isLoggingEnabled()) {
      getLogger().info(
	  "Attempting updating variables (" + getOptionManager().getVariables().hashCode() + "): " 
	      + m_VariablesUpdated + "/" + m_DetectedObjectVariables);
    }

    // obtain the new value(s)
    m_BackupState = backupState();
    // outer variables
    getOptionManager().setQuiet(true);
    getOptionManager().updateVariableValues(true);
    // inner variables
    backup = getOptionManager().getVariables();
    getOptionManager().setVariables(getOwner().getInternalActor().getVariables());
    getOptionManager().updateVariableValues(true);
    getOptionManager().setVariables(backup);
    getOptionManager().setQuiet(false);

    // re-initialize the actor
    result = setUp();
    if (result == null)
      restoreState(m_BackupState);

    if (isLoggingEnabled()) {
      getLogger().info(
	  "Finished updating variables " + m_VariablesUpdated + "/" + m_DetectedObjectVariables + ": " 
	      + ((result == null) ? "successful" : result));
    }

    m_BackupState = null;

    m_VariablesUpdated.clear();

    return result;
  }

  /**
   * Performs the actual transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  protected abstract String doTransmit();
  
  /**
   * Performs the transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  public String transmit() {
    String	result;
    
    m_Stopped = false;

    // do we need to re-setup the output, due to changes in variables?
    if (    (m_VariablesUpdated.size() > 0) 
         || (m_DetectedVariables != null) 
         || (m_DetectedObjectVariables.size() > 0) ) {
      updateVariables();
    }
    
    result = check();
    if (result == null) {
      result  = doTransmit();
      m_Input = null;
    }
    
    return result;
  }
  
  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractRatOutput shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractRatOutput shallowCopy(boolean expand) {
    return (AbstractRatOutput) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    getOptionManager().deregisterVariables();
    if (m_DetectedVariables != null) {
      m_DetectedVariables.clear();
      m_DetectedVariables = null;
    }
    if (m_DetectedObjectVariables != null) {
      m_DetectedObjectVariables.clear();
      m_DetectedObjectVariables = null;
    }
    
    m_Stopped = true;
    
    if (isLoggingEnabled())
      getLogger().info("Stopped");
  }

  /**
   * Returns whether the transmitter has been stopped.
   * 
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_Input = null;
  }
}
