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
 * Enqueue.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.rest.interceptor.outgoing;

import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.core.NullToken;
import adams.flow.core.QueueHelper;
import adams.flow.rest.interceptor.InterceptorHelper;
import adams.flow.rest.interceptor.InterceptorWithActor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import java.io.OutputStream;
import java.io.Writer;

/**
 * Enqueues a token in the specified queue whenever an outgoing message 
 * is intercepted.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Enqueue
  extends AbstractOutInterceptor
  implements InterceptorWithActor {

  /** the queue to enqueue the token in. */
  protected StorageName m_StorageName;
  
  /** whether to enqueue the message or just a {@link NullToken}. */
  protected boolean m_EnqueueMessage;
  
  /** the actor to use for getting access to queues. */
  protected Actor m_Actor;

  /**
   * Initializes the interceptor.
   */
  public Enqueue() {
    super(Phase.SEND);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_StorageName    = new StorageName("queue");
    m_EnqueueMessage = false;
    m_Actor          = null;
  }
  
  /**
   * Sets the queue to use.
   * 
   * @param value	the queue
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
  }
  
  /**
   * Returns the queue in use.
   * 
   * @return		the queue
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Sets whether to enqueue the whole message or just a {@link NullToken}.
   * 
   * @param value	true if to enqueue whole message
   */
  public void setEnqueueMessage(boolean value) {
    m_EnqueueMessage = value;
  }
  
  /**
   * Returns whether to enqueue the whole message or just a {@link NullToken}.
   * 
   * @return		true if to enqueue whole message
   */
  public boolean getEnqueueMessage() {
    return m_EnqueueMessage;
  }
  
  /**
   * Sets the actor to use.
   * 
   * @param value	the actor to use
   */
  @Override
  public void setActor(Actor value) {
    m_Actor = value;
  }

  /**
   * Returns the actor in use.
   * 
   * @return		the actor in use
   */
  @Override
  public Actor getActor() {
    return m_Actor;
  }
  
  /**
   * Intercepts a message. 
   * Interceptors should NOT invoke handleMessage or handleFault
   * on the next interceptor - the interceptor chain will
   * take care of this.
   * 
   * @param message
   */
  @Override
  public void handleMessage(Message message) throws Fault {
    final OutputStream 	os;
    final Writer 	iowriter;
    boolean 		hasLogged;
    
    if (m_Actor == null)
      return;

    // Write the output while caching it for the log message
    hasLogged = message.containsKey(InterceptorHelper.OUTGOING_LOG_SETUP);
    if (hasLogged)
      return;
    
    if (m_EnqueueMessage) {
      os       = message.getContent(OutputStream.class);
      iowriter = message.getContent(Writer.class);
      if ((os == null) && (iowriter == null))
        return;

      message.put(InterceptorHelper.OUTGOING_LOG_SETUP, Boolean.TRUE);
      if (os != null) {
	final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
	message.setContent(OutputStream.class, newOut);
	newOut.registerCallback(new AbstractOutgoingCallback(message, os) {
	  @Override
	  protected void write(LoggingMessage buffer) {
	    if (isLoggingEnabled())
	      getLogger().info(m_StorageName + ": " + buffer);
	    QueueHelper.enqueue(m_Actor, m_StorageName, "" + buffer);
	  }
	});
      }
    }
    else {
      if (isLoggingEnabled())
	getLogger().info(m_StorageName + ": null token");
      QueueHelper.enqueue(m_Actor, m_StorageName, new NullToken());
    }
  }
}
