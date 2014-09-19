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
 * LogFileOutInterceptor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.message.Message;

/**
 * Interceptor for outgoing messages, writing the data to a log file.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogFileOutInterceptor
  extends AbstractOutInterceptor {

  /** the file to write to. */
  protected File m_LogFile;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_LogFile = new File(".");
  }
  
  /**
   * Sets the log file to write to.
   * 
   * @param value	the file to write to
   */
  public void setLogFile(File value) {
    m_LogFile = value;
  }
  
  /**
   * Returns the log file to write to.
   * 
   * @return		the file to write to
   */
  public File getLogFile() {
    return m_LogFile;
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
    if (m_LogFile.isDirectory())
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
	newOut.registerCallback(new FileBasedCallback(m_LogFile, message, os));
      }
    }
  }
}
