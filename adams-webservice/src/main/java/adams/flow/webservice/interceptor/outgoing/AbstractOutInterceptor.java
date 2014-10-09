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
 * AbstractOutInterceptor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor.outgoing;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;

import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingLevelHandler;
import adams.core.logging.LoggingSupporter;

/**
 * Interceptor for outgoing messages.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOutInterceptor
  extends AbstractPhaseInterceptor<Message>
  implements LoggingSupporter, LoggingLevelHandler {

  /** the logging level. */
  protected LoggingLevel m_LoggingLevel;

  /** the logger in use. */
  protected transient Logger m_Logger;

  /**
   * Initializes the interceptor.
   * 
   * @param phase	the phase to use
   */
  protected AbstractOutInterceptor(String phase) {
    super(phase);
    initializeLogging();
    initialize();
  }
  
  /**
   * Initializes the members.
   * <p/>
   * Default implementation does nothing.
   */
  protected void initialize() {
  }
  
  /**
   * Pre-configures the logging.
   */
  protected void initializeLogging() {
    m_LoggingLevel = LoggingLevel.WARNING;
  }
  
  /**
   * Initializes the logger.
   * <p/>
   * Default implementation uses the class name.
   */
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(getClass());
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }
  
  /**
   * Returns the logger in use.
   * 
   * @return		the logger
   */
  public synchronized Logger getLogger() {
    if (m_Logger == null)
      configureLogger();
    return m_Logger;
  }

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
   * Returns the logging level.
   *
   * @return 		the level
   */
  public LoggingLevel getLoggingLevel() {
    return m_LoggingLevel;
  }
  
  /**
   * Returns whether logging is enabled.
   * 
   * @return		true if at least {@link Level#CONFIG}
   */
  public boolean isLoggingEnabled() {
    return LoggingHelper.isAtLeast(m_LoggingLevel.getLevel(), Level.CONFIG);
  }
  
  /**
   * Returns whether info logging is enabled.
   * 
   * @return		true if at least {@link Level#INFO}
   */
  public boolean isInfoLoggingEnabled() {
    return LoggingHelper.isAtLeast(m_LoggingLevel.getLevel(), Level.INFO);
  }
}
