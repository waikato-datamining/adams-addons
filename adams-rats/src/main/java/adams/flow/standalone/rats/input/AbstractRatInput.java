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
 * AbstractRatInput.java
 * Copyright (C) 2014-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoSupporter;
import adams.core.ShallowCopySupporter;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;
import adams.flow.core.QueueHelper;
import adams.flow.standalone.QueueInit;
import adams.flow.standalone.Rat;

/**
 * Ancestor for input receivers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRatInput
  extends AbstractOptionHandler
  implements RatInput, ShallowCopySupporter<AbstractRatInput>,
             QuickInfoSupporter, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 9152793366076258048L;

  /** the owner. */
  protected Rat m_Owner;
  
  /** whether the reception was stopped. */
  protected boolean m_Stopped;

  /** the logging prefix. */
  protected String m_LoggingPrefix;

  /** whether reception is currently underway. */
  protected boolean m_ReceptionRunning;

  /** whether reception was interrupted. */
  protected boolean m_ReceptionInterrupted;

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
   * Sets the actor the receiver belongs to.
   * 
   * @param value	the owner
   */
  public void setOwner(Rat value) {
    m_Owner = value;
    updatePrefix();
  }

  /**
   * Returns the actor the receiver belongs to.
   * 
   * @return		the owner
   */
  public Rat getOwner() {
    return m_Owner;
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
   * Returns the additional information.
   *
   * @return		the additional information, null or 0-length string for no information
   */
  public String getAdditionalInformation() {
    return "Receiver generates: " + Utils.classToString(generates());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * <br><br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  public abstract Class generates();
  
  /**
   * Checks whether any output can be collected.
   * 
   * @return		true if output available
   */
  public abstract boolean hasPendingOutput();
  
  /**
   * Returns the received data.
   * 
   * @return		the data
   */
  public abstract Object output();

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
   * Hook method for performing checks before receiving data.
   * <br><br>
   * Default implementation returns null.
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
    while ((count < msec) && canReceive()) {
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
   * Initializes the reception.
   */
  public void initReception() {
    m_Stopped              = false;
    m_ReceptionInterrupted = false;
    m_ReceptionRunning     = false;
    getOptionManager().updateVariableValues(true);
  }

  /**
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  protected abstract String doReceive();

  /**
   * Interrupts the reception (eg when pausing).
   */
  public void interruptReception() {
    m_ReceptionInterrupted = true;
  }

  /**
   * Returns whether the reception was interrupted.
   *
   * @return		true if interrupted
   */
  public boolean getReceptionInterrupted() {
    return m_ReceptionInterrupted;
  }

  /**
   * Returns whether the reception is currently running.
   *
   * @return		true if running
   */
  public boolean isReceptionRunning() {
    return m_ReceptionRunning;
  }

  /**
   * Whether reception is possible.
   *
   * @return		true if possible
   * @see		#m_Stopped
   * @see		#m_ReceptionInterrupted
   * @see		#m_ReceptionRunning
   */
  protected boolean canReceive() {
    return !isStopped() && !getReceptionInterrupted() && !isReceptionRunning();
  }

  /**
   * Initiates the reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  public String receive() {
    String	result;
    
    m_Stopped              = false;
    m_ReceptionInterrupted = false;
    m_ReceptionRunning     = true;

    try {
      result = check();
      if (result == null)
        result = doReceive();
      return result;
    }
    finally {
      m_ReceptionRunning = false;
    }
  }
  
  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractRatInput shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractRatInput shallowCopy(boolean expand) {
    return (AbstractRatInput) OptionUtils.shallowCopy(this, expand);
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
   * Returns whether the receiver has been stopped.
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
  }
}
