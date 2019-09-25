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
 * AbstractLogObjectGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.log;

import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.standalone.Rat;

/**
 * Ancestor for log generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLogObjectGenerator<T>
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 8710057745812807791L;

  /**
   * Handles the given error message.
   *
   * @param rat		the rat that captured this error
   * @param source	the source of the error
   * @param type	the type of error
   * @param msg		the error message to log
   * @return		the generated log container
   */
  public abstract T generate(Rat rat, Actor source, String type, String msg);
}
