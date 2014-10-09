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
 * BaseLogging.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor.outgoing;

import java.io.OutputStream;
import java.io.Writer;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import adams.flow.webservice.interceptor.InterceptorHelper;

/**
 * Interceptor for outgoing messages.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseLogging
  extends AbstractOutInterceptor {

  /**
   * Initializes the interceptor.
   */
  public BaseLogging() {
    super(Phase.SEND);
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
    if (!isLoggingEnabled())
      return;
    
    final OutputStream os = message.getContent(OutputStream.class);
    final Writer iowriter = message.getContent(Writer.class);
    if ((os == null) && (iowriter == null))
      return;

    // Write the output while caching it for the log message
    boolean hasLogged = message.containsKey(InterceptorHelper.OUTGOING_LOG_SETUP);
    if (!hasLogged) {
      message.put(InterceptorHelper.OUTGOING_LOG_SETUP, Boolean.TRUE);
      if (os != null) {
	final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
	message.setContent(OutputStream.class, newOut);
	newOut.registerCallback(new OutgoingLoggingCallback(getLogger(), message, os));
      }
    }
  }
}
