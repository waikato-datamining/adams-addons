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

package adams.flow.source.redisaction;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import redis.clients.jedis.Jedis;

/**
 * Ancestor for Redis source actions.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRedisAction
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

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
   * Returns the classes the action generates as output.
   *
   * @return		the classes
   */
  public abstract Class[] generates();

  /**
   * For checking the state before executing the action.
   *
   * @param connection	the Redis connection
   * @return		null if successful, otherwise error message
   */
  public String check(Jedis connection) {
    if (connection == null)
      return "No Redis connection provided!";
    return null;
  }

  /**
   * Performs the action.
   *
   * @param connection	the Redis connection
   * @param errors      for collecting errors
   * @return		the generated object
   */
  protected abstract Object doExecute(Jedis connection, MessageCollection errors);

  /**
   * Performs the action on the specified object.
   *
   * @param connection	the Redis connection
   * @param errors      for collecting errors
   * @return		the generated object
   */
  public Object execute(Jedis connection, MessageCollection errors) {
    Object	result;
    String      msg;

    result = null;

    msg = check(connection);
    if (msg == null)
      result = doExecute(connection, errors);
    else
      errors.add(msg);

    return result;
  }
}
