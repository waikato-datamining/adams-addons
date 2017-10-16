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
 * DefaultWekaInstanceApplier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelapplier;

import weka.core.Instance;

/**
 * Applies the model to a Weka Instance.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultWekaInstanceApplier
  extends AbstractNumericArrayApplier<Instance> {

  private static final long serialVersionUID = 6354440278825130565L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the model to a Weka Instance.";
  }

  /**
   * Returns the class that the applier accepts.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Instance.class;
  }

  /**
   * Performs the actual application of the model.
   *
   * @param input	the input
   * @return		the score
   */
  @Override
  protected float[] doApplyModel(Instance input) {
    return applyModel(input.toDoubleArray());
  }
}
