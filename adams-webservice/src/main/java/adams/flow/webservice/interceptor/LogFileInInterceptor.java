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
 * LogFileInInterceptor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor;

import java.io.File;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import adams.core.io.FileUtils;

/**
 * Interceptor for incoming messages, writing them to a file.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogFileInInterceptor
  extends AbstractInInterceptor {

  /** the file to write to. */
  protected File m_LogFile;

  /**
   * Initializes the interceptor.
   */
  public LogFileInInterceptor() {
    super(Phase.RECEIVE);
  }

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
    LoggingMessage buffer = InterceptorHelper.writeIncomingMessage(message);
    if (buffer != null) {
      FileUtils.writeToFile(m_LogFile.getAbsolutePath(), buffer.toString(), true);
    }
  }
}
