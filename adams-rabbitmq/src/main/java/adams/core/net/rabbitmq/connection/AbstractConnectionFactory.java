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
 * AbstractConnectionFactory.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.connection;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Ancestor for RabbitMQ connection factory objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractConnectionFactory
  extends AbstractOptionHandler
  implements QuickInfoSupporter, FlowContextHandler {

  private static final long serialVersionUID = 7200572980372377702L;

  /** the flow context. */
  protected Actor m_FlowContext;

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
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns whether a flow context is required.
   *
   * @return		true if required
   */
  protected abstract boolean requiresFlowContext();

  /**
   * Hook method for performing checks.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    if (requiresFlowContext() && (m_FlowContext == null))
      return "No flow context set!";
    return null;
  }

  /**
   * Generates the connection factory object.
   *
   * @param errors	for collecting errors
   * @return		the factory, null in case of error
   */
  protected abstract ConnectionFactory doGenerate(MessageCollection errors);

  /**
   * Generates the connection factory object.
   *
   * @param errors	for collecting errors
   * @return		the factory, null in case of error
   */
  public ConnectionFactory generate(MessageCollection errors) {
    ConnectionFactory	result;
    String		msg;

    msg = check();
    if (check() != null) {
      errors.add(msg);
      return null;
    }

    result = doGenerate(errors);
    if (!errors.isEmpty())
      result = null;

    return result;
  }
}
