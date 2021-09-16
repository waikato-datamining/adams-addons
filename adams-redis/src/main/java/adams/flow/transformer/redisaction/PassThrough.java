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
 * PassThrough.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.redisaction;

import adams.core.MessageCollection;
import adams.flow.core.Unknown;
import redis.clients.jedis.Jedis;

/**
 * Just passes through the object, no Redis interaction.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractRedisAction {

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Just passes through the object, no Redis interaction.";
  }

  /**
   * Returns the classes the action accepts as input.
   *
   * @return the classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the classes the action generates as output.
   *
   * @return the classes
   */
  @Override
  public Class generates() {
    return Unknown.class;
  }

  /**
   * Performs the action.
   *
   * @param connection the Redis connection
   * @param o          the object to process
   * @param errors     for collecting errors
   * @return the generated object
   */
  @Override
  protected Object doExecute(Jedis connection, Object o, MessageCollection errors) {
    return o;
  }
}
