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
 * RenjinListVectorToArray.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.LenientModeSupporter;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.SEXP;

import java.lang.reflect.Array;

/**
 * Converts an R list into an array.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RenjinListVectorToArray
  extends AbstractConversion
  implements LenientModeSupporter {

  private static final long serialVersionUID = 2094304665550675734L;

  /** whether to be lenient with types. */
  protected boolean m_Lenient;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts an Renjin list into an array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "lenient", "lenient",
      true);
  }

  /**
   * Sets whether to be lenient with types.
   *
   * @param value	true if lenient
   */
  @Override
  public void setLenient(boolean value) {
    m_Lenient = value;
    reset();
  }

  /**
   * Returns whether to be lenient with types.
   *
   * @return		true if lenient
   */
  @Override
  public boolean getLenient() {
    return m_Lenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String lenientTipText() {
    return "If enabled, the types will be more lenient.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    if (m_Lenient)
      return SEXP.class;
    else
      return ListVector.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return SEXP[].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    Object	result;
    ListVector	input;
    int		i;

    input  = (ListVector) m_Input;
    result = Array.newInstance(Object.class, input.length());
    for (i = 0; i < input.length(); i++)
      Array.set(result, i, input.get(i));

    return result;
  }
}
