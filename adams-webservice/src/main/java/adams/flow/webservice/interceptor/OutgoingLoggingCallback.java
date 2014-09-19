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
 * OutgoingLoggingCallback.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor;

import java.io.OutputStream;
import java.util.logging.Logger;

import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.message.Message;

/**
 * Callback class for outgoing logging.
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OutgoingLoggingCallback
  extends AbstractOutgoingCallback {

  /** the logger to use for outputting the collected data. */
  protected Logger m_Logger;

  /**
   * Initializes the callback.
   * 
   * @param logger	the logger to use for outputting the data
   * @param msg		the message to process
   * @param os		the output stream
   */
  public OutgoingLoggingCallback(final Logger logger, final Message msg, final OutputStream os) {
    super(msg, os);
    m_Logger = logger;
  }

  /**
   * Outputs the buffer using its logger.
   * 
   * @param buffer	the buffer with the collected data
   */
  @Override
  protected void write(LoggingMessage buffer) {
    m_Logger.info(buffer.toString());
  }
}