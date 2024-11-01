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
 * DockerDirectoryMappingParsing.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.core.option.parsing;

import adams.core.base.DockerDirectoryMapping;
import adams.core.option.AbstractOption;

/**
 * For parsing DockerDirectoryMappingParsing objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DockerDirectoryMappingParsing
    extends AbstractParsing {

  /**
   * Returns the BaseObject as string.
   *
   * @param option	the current option
   * @param object	the BaseObject object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((DockerDirectoryMapping) object).getValue();
  }

  /**
   * Returns a BaseObject generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a BaseObject
   * @return		the generated BaseObject
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new DockerDirectoryMapping(str);
  }
}
