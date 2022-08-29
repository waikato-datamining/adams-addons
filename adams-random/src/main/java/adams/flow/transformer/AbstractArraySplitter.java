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
 * AbstractArraySplitter.java
 * Copyright (C) 2014-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.DeepCopyOperator;
import adams.core.QuickInfoHelper;
import adams.core.classmanager.ClassManager;
import adams.data.SplitResultType;
import adams.data.random.JavaRandomInt;
import adams.data.random.RandomIntegerRangeGenerator;
import adams.flow.core.Unknown;
import gnu.trove.list.array.TIntArrayList;

import java.lang.reflect.Array;

/**
 * Ancestor for array splitters.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractArraySplitter
    extends AbstractTransformer
    implements DeepCopyOperator {

  /** for serialization. */
  private static final long serialVersionUID = 8536100625511019961L;

  /** the random number generator. */
  protected RandomIntegerRangeGenerator m_Generator;

  /** the type of data to return. */
  protected SplitResultType m_SplitResult;

  /** whether to perform a deep copy of the elements. */
  protected boolean m_DeepCopy;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "generator", "generator",
        new JavaRandomInt());

    m_OptionManager.add(
        "split-result", "splitResult",
        SplitResultType.SPLIT);

    m_OptionManager.add(
        "deep-copy", "deepCopy",
        true);
  }

  /**
   * Sets the random number generator.
   *
   * @param value	the generator
   */
  public void setGenerator(RandomIntegerRangeGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the random number generator.
   *
   * @return		the generator
   */
  public RandomIntegerRangeGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The random number generator to use for selecting the elements.";
  }

  /**
   * Sets what type of the split to return, e.g., sample or inverse of sample.
   *
   * @param value	the type
   */
  public void setSplitResult(SplitResultType value) {
    m_SplitResult = value;
    reset();
  }

  /**
   * Returns what type of the split to return, e.g., sample or inverse of sample.
   *
   * @return		the type
   */
  public SplitResultType getSplitResult() {
    return m_SplitResult;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String splitResultTipText() {
    return "The type of data to return: e.g., the sample, the inverse of the sample or both (split and inverse).";
  }

  /**
   * Sets whether to perform a deep copy of each array element before transferring it into the target array.
   *
   * @param value	true if to copy
   */
  @Override
  public void setDeepCopy(boolean value) {
    m_DeepCopy = value;
    reset();
  }

  /**
   * Returns whether to perform a deep copy of each array element before transferring it into the target array.
   *
   * @return		true if to copy
   */
  @Override
  public boolean getDeepCopy() {
    return m_DeepCopy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String deepCopyTipText() {
    return "If enabled, a deep copy of each array element is performed before transferring it into the target array.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "generator", m_Generator, "generator: ");
    result += QuickInfoHelper.toString(this, "splitResult", m_SplitResult, ", result: ");
    result += QuickInfoHelper.toString(this, "deepCopy", m_DeepCopy, "deep copy", ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the classes
   */
  public Class[] accepts() {
    return new Class[]{Unknown[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the classes
   */
  public Class[] generates() {
    return new Class[]{Unknown[].class};
  }

  /**
   * Creates a new array from the old and the given indices.
   *
   * @param arrayOld	the old array
   * @param indices	the indices to grab from the old array
   * @param log		the info for the logger
   * @return		the new array
   */
  protected Object newArray(Object arrayOld, TIntArrayList indices, String log) {
    Object	result;
    int		i;

    if (isLoggingEnabled())
      getLogger().info("Indices (" + log + "): " + indices);

    result = Array.newInstance(arrayOld.getClass().getComponentType(), indices.size());
    for (i = 0; i < indices.size(); i++) {
      if (m_DeepCopy)
        Array.set(result, i, ClassManager.getSingleton().deepCopy(Array.get(arrayOld, indices.get(i))));
      else
        Array.set(result, i, Array.get(arrayOld, indices.get(i)));
    }

    return result;
  }
}
