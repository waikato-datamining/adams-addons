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
 * DefaultWekaInstanceApplier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelapplier;

import adams.data.weka.WekaAttributeRange;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import weka.core.Instance;

/**
 * Applies the model to the specified (numeric, non-missing) values of a Weka Instance.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultWekaInstanceApplier
  extends AbstractNumericArrayApplier<Instance> {

  private static final long serialVersionUID = 6354440278825130565L;

  /** the attributes to use as input. */
  protected WekaAttributeRange m_Attributes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the model to the specified (numeric, non-missing) attributes of a row.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "attributes", "attributes",
      new WekaAttributeRange(WekaAttributeRange.ALL));
  }

  /**
   * Sets the attributes to use as input.
   *
   * @param value	the range
   */
  public void setAttributes(WekaAttributeRange value) {
    m_Attributes = value;
    reset();
  }

  /**
   * Returns the attributes to use as input.
   *
   * @return  		the range
   */
  public WekaAttributeRange getAttributes() {
    return m_Attributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributesTipText() {
    return "The attributes in the row to use as input (only numeric, non-missing cells are used).";
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
    TDoubleList 	values;
    int[]		indices;

    values = new TDoubleArrayList();
    m_Attributes.setData(input.dataset());
    indices = m_Attributes.getIntIndices();
    for (int index: indices) {
      if (input.attribute(index).isNumeric() && !input.isMissing(index) && (input.classIndex() != index))
	values.add(input.value(index));
    }

    return applyModel(values.toArray());
  }
}
