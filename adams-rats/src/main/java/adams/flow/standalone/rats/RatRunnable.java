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
 * RatRunnable.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import adams.core.Utils;
import adams.flow.core.RunnableWithLogging;
import adams.flow.core.Token;
import adams.flow.standalone.Rat;

/**
 * Runnable class for Rat used in a thread.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RatRunnable
  extends RunnableWithLogging {
  
  /** for serialization. */
  private static final long serialVersionUID = 143445804089303521L;
  
  /** the owning Rat. */
  protected Rat m_Owner;
  
  /** whether we have any actors to apply to the data. */
  protected boolean m_HasActors;
  
  /**
   * Initializes the runnable.
   * 
   * @param owner	the owning actor
   */
  public RatRunnable(Rat owner) {
    super();
    
    m_Owner     = owner;
    m_HasActors = (owner.getActorHandler().active() > 0);
  }
  
  /**
   * Returns the owning actor.
   * 
   * @return		the owner
   */
  public Rat getOwner() {
    return m_Owner;
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
      catch (Throwable t) {
	// ignored
      }
    }
  }
  
  /**
   * Transmits the data.
   * 
   * @param data 	the data to transmit, ignored if null
   * @return		null if successful, otherwise error message
   */
  protected String transmit(Object data) {
    String	result;
    
    result = null;

    if (data != null) {
      while (!m_Owner.getTransmitter().canInput() && !m_Stopped)
	doWait(100);

      if (!m_Stopped) {
	if (isLoggingEnabled())
	  getLogger().finer("Inputting to " + m_Owner.getTransmitter().getFullName());
	m_Owner.getTransmitter().input(data);
	
	if (isLoggingEnabled())
	  getLogger().info("Transmitting to " + m_Owner.getTransmitter().getFullName());
	result = m_Owner.getTransmitter().transmit();
	
	if (result != null)
	  getLogger().warning("Failed to transmit to " + m_Owner.getTransmitter().getFullName() + ": " + result);
	else if (isLoggingEnabled())
	  getLogger().info("Transmitted to " + m_Owner.getTransmitter().getFullName());
      }
    }
    
    return result;
  }

  /**
   * Performs the actual execution.
   */
  @Override
  protected void doRun() {
    String	result;
    Object	data;
    Token	token;
    
    while (!m_Stopped) {
      data = null;
      if (isLoggingEnabled())
	getLogger().info("Receiving from " + m_Owner.getReceiver().getFullName());
      if (m_Owner.getReceiver().isStopped())
	break;
      
      try {
	result = m_Owner.getReceiver().receive();
      }
      catch (Throwable t) {
	result = Utils.throwableToString(t);
      }
      
      if (result != null) {
	getOwner().log("Failed to receive from " + m_Owner.getReceiver().getFullName() + ": " + result, "receive");
      }
      else {
	if (isLoggingEnabled())
	  getLogger().info("Received from " + m_Owner.getReceiver().getFullName());
	if (isLoggingEnabled())
	  getLogger().fine("Pending output from " + m_Owner.getReceiver().getFullName() + ": " + m_Owner.getReceiver().hasPendingOutput());

	try {
	  while (m_Owner.getReceiver().hasPendingOutput() && !m_Stopped) {
	    data = m_Owner.getReceiver().output();
	    if (isLoggingEnabled())
	      getLogger().finer("Data: " + data);

	    if (m_Stopped)
	      break;

	    // actors?
	    if (m_HasActors) {
	      if (data != null) {
		m_Owner.getActorHandler().input(new Token(data));
		result = m_Owner.getActorHandler().execute();
		if (result == null) {
		  while (m_Owner.getActorHandler().hasPendingOutput() && !m_Stopped) {
		    token  = m_Owner.getActorHandler().output();
		    result = transmit(token.getPayload());
		    if (result != null)
		      break;
		  }
		}
	      }
	    }
	    else {
	      result = transmit(data);
	    }
	  }
	}
	catch (Throwable t) {
	  result = Utils.throwableToString(t);
	}

	// log error
	if (result != null) {
	  if (m_HasActors)
	    getOwner().log("Actors failed to transform/transmit data: " + result, "transform/transmit");
	  else
	    getOwner().log("Failed to transmit data: " + result, "transmit");
	}
      }
      
      // wait before next poll?
      if (!m_Stopped) {
	if (m_Owner.getReceiver() instanceof PollingRatInput) {
	  doWait(((PollingRatInput) m_Owner.getReceiver()).getWaitPoll());
	}
      }
    }
  }
}