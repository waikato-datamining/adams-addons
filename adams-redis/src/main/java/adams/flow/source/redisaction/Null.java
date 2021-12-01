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
 * Null.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.redisaction;

import adams.core.MessageCollection;
import adams.flow.core.Unknown;
import adams.flow.standalone.RedisConnection;

/**
 * Dummy, generates nothing.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Null
  extends AbstractRedisAction {

  private static final long serialVersionUID = -2641452562344340857L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, generates nothing.";
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
   * @param errors     for collecting errors
   * @return the generated object
   */
  @Override
  protected Object doExecute(RedisConnection connection, MessageCollection errors) {
    return null;
  }
}
