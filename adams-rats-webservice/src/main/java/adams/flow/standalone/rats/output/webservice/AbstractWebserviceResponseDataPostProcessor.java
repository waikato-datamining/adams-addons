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
 * AbstractWebserviceResponseDataPostProcessor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.output.webservice;

import adams.core.ErrorProvider;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;

/**
 * Ancestor for schemes that post-process the response data received from
 * the server end of the webservice. E.g., for handling potential error
 * messages in the response rather than just quietly ignoring them.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of response data to post-process
 */
public abstract class AbstractWebserviceResponseDataPostProcessor<T>
  extends AbstractOptionHandler
  implements FlowContextHandler, ErrorProvider {

  private static final long serialVersionUID = -6157013941356537849L;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the last error that was generated. */
  protected String m_LastError;

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
   * Checks whether there was an error with the last call.
   *
   * @return		true if there was an error
   * @see		#getLastError()
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }

  /**
   * Returns the last error that occurred.
   *
   * @return		the last error, null if none occurred
   */
  public String getLastError() {
    return m_LastError;
  }

  /**
   * For post-processing the response data.
   *
   * @param response	the data to post-process
   */
  protected abstract void doPostProcess(T response);

  /**
   * For post-processing the response data.
   *
   * @param response	the data to post-process
   */
  public void postProcess(T response) {
    m_LastError = null;
    doPostProcess(response);
  }
}
