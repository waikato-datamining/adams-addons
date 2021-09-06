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

package adams.flow.sink.redisaction;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import redis.clients.jedis.Jedis;

/**
 * Ancestor for Redis sink actions.
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
   * Returns the classes the action accepts as input.
   *
   * @return		the classes
   */
  public abstract Class[] accepts();

  /**
   * For checking the state before executing the action.
   *
   * @param connection	the Redis connection
   * @param o		the object to process
   * @return		null if successful, otherwise error message
   */
  public String check(Jedis connection, Object o) {
    if (connection == null)
      return "No Redis connection provided!";
    if (o == null)
      return "No object provided!";
    return null;
  }

  /**
   * Performs the action on the specified object.
   *
   * @param connection	the Redis connection
   * @param o		the object to process
   * @return		null if successful, otherwise error message
   */
  protected abstract String doExecute(Jedis connection, Object o);

  /**
   * Performs the action on the specified object.
   *
   * @param connection	the Redis connection
   * @param o		the object to process
   * @return		null if successful, otherwise error message
   */
  public String execute(Jedis connection, Object o) {
    String	result;

    result = check(connection, o);
    if (result == null)
      result = doExecute(connection, o);

    return result;
  }
}
