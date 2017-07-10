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

/**
 * DefaultNumericArrayApplier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelapplier;

import adams.core.Utils;
import adams.data.statistics.StatUtils;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;

/**
 * Applies the model to the specified (numeric) cells of a row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultNumericArrayApplier
  extends AbstractNumericArrayApplier<Object> {

  private static final long serialVersionUID = 6354440278825130565L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the model to the specified numeric array of byte, int, long, float or double values (primitives or objects).";
  }

  /**
   * Returns the class that the applier accepts.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Object.class;
  }

  /**
   * Performs the actual application of the model.
   *
   * @param input	the input
   * @return		the score
   */
  @Override
  protected float[] doApplyModel(Object input) {
    TFloatList 		values;
    Number[]		numArray;
    int			i;

    if (input instanceof byte[])
      numArray = StatUtils.toNumberArray((byte[]) input);
    else if (input instanceof Byte[])
      numArray = (Number[]) input;
    else if (input instanceof int[])
      numArray = StatUtils.toNumberArray((int[]) input);
    else if (input instanceof Integer[])
      numArray = (Number[]) input;
    else if (input instanceof long[])
      numArray = StatUtils.toNumberArray((long[]) input);
    else if (input instanceof Long[])
      numArray = (Number[]) input;
    else if (input instanceof float[])
      numArray = StatUtils.toNumberArray((float[]) input);
    else if (input instanceof Float[])
      numArray = (Number[]) input;
    else if (input instanceof double[])
      numArray = StatUtils.toNumberArray((double[]) input);
    else if (input instanceof Double[])
      numArray = (Number[]) input;
    else
      throw new IllegalStateException("Unhandled input class: " + Utils.classToString(input.getClass()));

    values = new TFloatArrayList();
    for (i = 0; i < numArray.length; i++)
      values.add(numArray[i].floatValue());

    return applyModel(values.toArray());
  }
}
