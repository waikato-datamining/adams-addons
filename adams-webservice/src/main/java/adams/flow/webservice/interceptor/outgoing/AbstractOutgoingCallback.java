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
 * AbstractOutgoingCallback.java
 * Copyright (C) Apache Foundation
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor.outgoing;

import java.io.OutputStream;

import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.flow.webservice.interceptor.InterceptorHelper;

/**
 * Ancestor for callback classes for outgoing messages.
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @author Apache CXF
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "Apache CXF",
    license = License.APACHE2,
    note = "org.apache.cxf.interceptor.LoggingOutInterceptor#setupBuffer and org.apache.cxf.interceptor.AbstractLoggingInterceptor#writePayload and org.apache.cxf.interceptor.LoggingInInterceptor#logging"
)
public abstract class AbstractOutgoingCallback
  implements CachedOutputStreamCallback {

  /** the message this callback is for. */
  protected final Message m_Message;
  
  /** the output stream to use. */
  protected final OutputStream m_OrigStream;

  /**
   * Initializes the callback.
   * 
   * @param msg		the message
   * @param os		the output stream
   */
  public AbstractOutgoingCallback(final Message msg, final OutputStream os) {
    m_Message    = msg;
    m_OrigStream = os;
  }

  /**
   * Not used.
   */
  public void onFlush(CachedOutputStream cos) {  
  }

  /**
   * Finishes up writing the data to a buffer and calling {@link #write(LoggingMessage)}
   * with the buffer.
   * 
   * @param cos		the output stream
   * @see		#write(LoggingMessage)
   */
  public void onClose(CachedOutputStream cos) {
    LoggingMessage buffer = InterceptorHelper.setupOutgoingBuffer(m_Message);

    String ct = (String)m_Message.get(Message.CONTENT_TYPE);

    if (cos.getTempFile() == null) {
      buffer.getMessage().append("Outbound Message:\n");
    } 
    else {
      buffer.getMessage().append("Outbound Message (saved to tmp file):\n");
      buffer.getMessage().append("Filename: " + cos.getTempFile().getAbsolutePath() + "\n");
    }
    try {
      String encoding = (String)m_Message.get(Message.ENCODING);
      InterceptorHelper.writeOutgoingPayload(buffer.getPayload(), m_Message, cos, encoding, ct); 
    } 
    catch (Exception ex) {
      System.err.println("Failed to write payload!");
      ex.printStackTrace();
    }

    try {
      //empty out the cache
      cos.lockOutputStream();
      cos.resetOut(null, false);
    } 
    catch (Exception ex) {
      //ignore
    }
    m_Message.setContent(OutputStream.class, m_OrigStream);
    
    write(buffer);
  }
  
  /**
   * Callback specific handling of the generated buffer.
   * 
   * @param buffer	the buffer with the collected data
   */
  protected abstract void write(LoggingMessage buffer);
}