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
 * RObjectHelper.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.core;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.Null;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.Vector;

/**
 * Helper class for common R object operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RObjectHelper {

  /**
   * Returns the dimensions of the object.
   *
   * @param obj		the object to get the dimensions for
   * @return		the dimensions
   */
  public static int[] getDimensions(SEXP obj) {
    TIntList	result;
    Vector	dims;
    int		i;

    result = new TIntArrayList();
    dims   = obj.getAttributes().getDim();
    if (dims == Null.INSTANCE) {
      result.add(obj.length());
    }
    else {
      for (i = 0; i < dims.length(); i++)
	result.add(dims.getElementAsInt(i));
    }

    return result.toArray();
  }

  /**
   * Returns the dimension names for the specified dimension.
   *
   * @param obj		the object to get the dimension names for
   * @param dim		the dimension index (0-based)
   * @return		the names, null if failed to obtain
   */
  public static String[] getDimensionNames(SEXP obj, int dim) {
    String[]	result;
    Vector	dims;
    int		i;
    ListVector	list;

    result = null;
    dims = obj.getAttributes().getDimNames();
    if (dims instanceof ListVector) {
      list   = (ListVector) dims;
      if (dim < list.length())
	result = vectorToStringArray((Vector) list.get(dim));
    }

    return result;
  }

  /**
   * Turns the vector into a string array.
   *
   * @param v		the vector to convert
   * @return		the string array
   */
  public static String[] vectorToStringArray(Vector v) {
    String[] 	result;
    int 	i;

    result = new String[v.length()];
    for (i = 0; i < v.length(); i++)
      result[i] = v.getElementAsString(i);

    return result;
  }
}
