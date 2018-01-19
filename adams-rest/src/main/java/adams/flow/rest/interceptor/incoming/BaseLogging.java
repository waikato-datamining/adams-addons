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
 * BaseLogging.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.rest.interceptor.incoming;

import adams.flow.rest.interceptor.InterceptorHelper;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

/**
 * Interceptor for incoming messages.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BaseLogging
  extends AbstractInInterceptor {

  /**
   * Initializes the interceptor.
   */
  public BaseLogging() {
    super(Phase.RECEIVE);
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
    LoggingMessage buffer = InterceptorHelper.writeIncomingMessage(message);
    if (buffer != null)
      getLogger().log(getLoggingLevel().getLevel(), buffer.toString());
  }
}
