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
 * AbstractRedisAction.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.redisaction;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.StoppableWithFeedback;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;
import redis.clients.jedis.Jedis;

/**
 * Ancestor for Redis sink actions.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRedisAction
  extends AbstractOptionHandler
  implements QuickInfoSupporter, StoppableWithFeedback, FlowContextHandler {

  /** whether the action has been stopped. */
  protected boolean m_Stopped;

  /** the flow context. */
  protected transient Actor m_FlowContext;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_Stopped = false;
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
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br><br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the classes the action accepts as input.
   *
   * @return		the classes
   */
  public abstract Class[] accepts();

  /**
   * Returns the classes the action generates as output.
   *
   * @return		the classes
   */
  public abstract Class generates();

  /**
   * For checking the state before executing the action.
   *
   * @param connection	the Redis connection
   * @param o		the object to process
   * @return		null if successful, otherwise error message
   */
  public String check(Jedis connection, Object o) {
    if (m_FlowContext == null)
      return "No flow context set!";
    if (connection == null)
      return "No Redis connection provided!";
    if (o == null)
      return "No object provided!";
    return null;
  }

  /**
   * Performs the action.
   *
   * @param connection	the Redis connection
   * @param o		the object to process
   * @param errors      for collecting errors
   * @return		the generated object
   */
  protected abstract Object doExecute(Jedis connection, Object o, MessageCollection errors);

  /**
   * Performs the action on the specified object.
   *
   * @param connection	the Redis connection
   * @param o		the object to process
   * @param errors      for collecting errors
   * @return		the generated object
   */
  public Object execute(Jedis connection, Object o, MessageCollection errors) {
    Object	result;
    String      msg;

    result    = null;
    m_Stopped = false;

    msg = check(connection, o);
    if (msg == null)
      result = doExecute(connection, o, errors);
    else
      errors.add(msg);

    return result;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }
}
