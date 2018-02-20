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
 * Append.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.mongodbdocumentupdate;

import adams.data.conversion.ConversionFromString;
import adams.data.conversion.StringToString;

/**
 * Appends the document with the specified key-value pairs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Append
  extends AbstractAppend {

  private static final long serialVersionUID = 3771202579365692102L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Appends the document with the specified key-value pairs.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String keyValuePairsTipText() {
    return "The key-value pairs to add.";
  }

  /**
   * Returns the default conversion.
   *
   * @return		the default
   */
  @Override
  protected ConversionFromString getDefaultValueConversion() {
    return new StringToString();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return     tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String valueConversionTipText() {
    return "For converting the value string into the actual type.";
  }

  /**
   * Returns the actual value.
   *
   * @param value	the value to turn into the actual value
   * @return		the actual value
   */
  @Override
  protected Object getActualValue(String value) {
    return value;
  }
}
