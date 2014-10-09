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
 * LogURL.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor.outgoing;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

/**
 * Interceptor for outgoing messages that simply logs the outgoing URL.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogURL
  extends AbstractOutInterceptor {

  /** the last url. */
  protected String m_LastURL;
  
  /**
   * Initializes the interceptor.
   */
  public LogURL() {
    super(Phase.SEND);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_LastURL = null;
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
    m_LastURL = "" + message.getContextualProperty(Message.ENDPOINT_ADDRESS);
    if (isLoggingEnabled())
      getLogger().info(m_LastURL);
  }
  
  /**
   * Returns the last URL encountered.
   * 
   * @return		the URL, null if none yet set
   */
  public String getLastURL() {
    return m_LastURL;
  }
}
