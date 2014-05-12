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

import adams.core.CleanUpHandler;
import adams.core.QuickInfoSupporter;
import adams.core.ShallowCopySupporter;
import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;
import adams.flow.core.AbstractActor;
import adams.flow.standalone.QueueInit;

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
  protected AbstractActor m_Owner;

  /** whether the reception was stopped. */
  protected boolean m_Stopped;

  /** the logging prefix. */
  protected String m_LoggingPrefix;
  
  /** the input. */
  protected Object m_Input;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LoggingPrefix = "";
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
  public void setOwner(AbstractActor value) {
    m_Owner = value;
    updatePrefix();
  }

  /**
   * Returns the actor the transmitter belongs to.
   * 
   * @return		the owner
   */
  public AbstractActor getOwner() {
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
   * Hook method for performing checks at setup time.
   * 
   * @return		null if successful, otherwise error message
   */
  public String setUp() {
    String	result;

    result = null;
    
    if (m_Owner == null)
      result = "No owning actor set!";
    
    if (result == null)
      updatePrefix();
    
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
    return (StorageQueueHandler) getOwner().getStorageHandler().getStorage().get(name);
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
    result    = check();
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
