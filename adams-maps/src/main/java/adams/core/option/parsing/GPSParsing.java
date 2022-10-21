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
 * GPSParsing.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.option.parsing;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.data.gps.AbstractGPS;

import java.lang.reflect.Constructor;

/**
 * For parsing GPS options.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GPSParsing
    extends AbstractParsing {

  /**
   * Turns the string into an instance of the specified GPS class.
   *
   * @param cls		the GPS class to instantiate
   * @param str		the string to parse
   * @return		the instantiated GPS object
   */
  public static AbstractGPS valueOf(Class cls, String str) {
    AbstractGPS	result;
    Constructor constr;

    try {
      constr = cls.getConstructor(new Class[]{String.class});
      result = (AbstractGPS) constr.newInstance(new Object[]{str});
    }
    catch (Exception e) {
      try {
	result = (AbstractGPS) cls.getDeclaredConstructor().newInstance();
      }
      catch (Exception ex) {
	System.err.println("Failed to instantiate " + cls.getName() + " as " + AbstractGPS.class.getName() + "!");
	ex.printStackTrace();
	result = null;
      }
    }

    return result;
  }

  /**
   * Returns the GPS coordinates as string.
   *
   * @param option	the current option
   * @param object	the GPS coordinates object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return object.toString();
  }

  /**
   * Returns GPS coordinates generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to GPS coordinates
   * @return		the generated GPS coordinates
   */
  public static Object valueOf(AbstractOption option, String str) {
    return valueOf(((AbstractArgumentOption) option).getBaseClass(), str);
  }
}
