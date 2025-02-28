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
 * AbstractJepScriptlet.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.scripting;

import adams.core.logging.CustomLoggingLevelObject;
import adams.core.logging.LoggingHelper;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;

/**
 * Ancestor for Jep/Python scripts.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractJepScriptlet
  extends CustomLoggingLevelObject
  implements FlowContextHandler {

  private static final long serialVersionUID = 7919172085779747176L;

  /** the owning thread (with the interpreter). */
  protected JepScriptingEngineThread m_Owner;

  /** the ID of the scriptlet. */
  protected String m_ID;

  /** the last error. */
  protected String m_LastError;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** whether the scriplet has been executed. */
  protected boolean m_Finished;

  /**
   * Initializes the scriptlet.
   *
   * @param id		the ID of the script
   */
  public AbstractJepScriptlet(String id) {
    m_Owner           = null;
    m_ID              = id;
    m_FlowContext     = null;
    m_Finished        = false;
  }

  /**
   * Initializes the logger.
   */
  @Override
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(m_ID);
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }

  /**
   * Sets the owning thread.
   *
   * @param value	the owner
   */
  public void setOwner(JepScriptingEngineThread value) {
    m_Owner = value;
  }

  /**
   * Returns the owning thread.
   *
   * @return		the owner
   */
  public JepScriptingEngineThread getOwner() {
    return m_Owner;
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns the ID of the script.
   *
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Whether the scriptlet has finished execution.
   *
   * @return		true when finished
   */
  public boolean hasFinished() {
    return m_Finished;
  }

  /**
   * Checks whether an error is present.
   *
   * @return		true if error present
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }

  /**
   * Returns any error that was encountered.
   *
   * @return		the error, null if none encountered
   */
  public String getLastError() {
    return m_LastError;
  }

  /**
   * Executes the script.
   *
   * @return		null if successful, otherwise error message
   */
  public abstract String execute();

  /**
   * Sets the error message and that the script has finished to true.
   *
   * @param msg		the error message to set
   */
  public void fail(String msg) {
    m_LastError = msg;
    m_Finished  = true;
  }
}
