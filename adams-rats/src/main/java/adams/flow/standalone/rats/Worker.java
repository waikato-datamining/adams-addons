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
 * Worker.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import adams.core.Stoppable;
import adams.core.Utils;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingLevelHandler;
import adams.core.logging.LoggingObject;

/**
 * Worker class used in a thread.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class Worker
  extends LoggingObject
  implements LoggingLevelHandler, Runnable, Stoppable {
  
  /** for serialization. */
  private static final long serialVersionUID = 143445804089303521L;

  /** whether the execution was stopped. */
  protected boolean m_Stopped;
  
  /** whether the worker is still running. */
  protected boolean m_Running;

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public synchronized void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
    m_Logger       = null;
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
    while (count < msec) {
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
   * Does the actual work.
   */
  protected abstract void doRun();

  /**
   * Starts the work.
   */
  @Override
  public void run() {
    m_Stopped = false;
    m_Running = true;

    if (isLoggingEnabled())
      getLogger().fine("Running...");
    
    try {
      doRun();
    }
    catch (Exception e) {
      Utils.handleException(this, "Exception occurred on run!", e);
    }
    
    if (isLoggingEnabled())
      getLogger().fine("Finished");
    
    m_Running = false;
  }
  
  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
    if (isLoggingEnabled())
      getLogger().fine("Stopped");
  }
  
  /**
   * Returns whether the worker is still running.
   * 
   * @return		true if still running
   */
  public boolean isRunning() {
    return m_Running;
  }
}