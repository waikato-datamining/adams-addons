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
 * RenjinDoubleArrayVectorToSpreadSheet.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.LenientModeSupporter;
import adams.core.RObjectHelper;
import adams.core.Utils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import org.renjin.sexp.DoubleArrayVector;
import org.renjin.sexp.SEXP;

/**
 * Turns a double array vector matrix into a spreadsheet.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RenjinDoubleArrayVectorToSpreadSheet
  extends AbstractConversion
  implements LenientModeSupporter {

  private static final long serialVersionUID = 9071543768077107751L;

  /** whether to be lenient with types. */
  protected boolean m_Lenient;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a Renjin double array vector matrix into a spreadsheet.";
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
      return DoubleArrayVector.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet		result;
    Row			row;
    DoubleArrayVector	input;
    int[]  		dims;
    int			cols;
    int			rows;
    int			i;
    int			x;
    int			y;
    String[]		colNames;
    String[]		rowNames;

    input    = (DoubleArrayVector) m_Input;
    dims     = RObjectHelper.getDimensions(input);
    if (dims.length == 1) {
      cols     = dims[0];
      rows     = 1;
      colNames = RObjectHelper.getDimensionNames(input, 0);
      rowNames = null;
    }
    else if (dims.length == 2) {
      rows = dims[0];
      cols = dims[1];
      rowNames = RObjectHelper.getDimensionNames(input, 0);
      colNames = RObjectHelper.getDimensionNames(input, 1);
    }
    else {
      throw new IllegalStateException("Expected 1 or 2 dimensions, but got: " + Utils.arrayToString(dims));
    }

    result = new DefaultSpreadSheet();

    // header
    row = result.getHeaderRow();
    if (rowNames != null)
      row.addCell("rid").setContentAsString("Row-ID");
    for (i = 0; i < cols; i++) {
      if (colNames == null)
	row.addCell("" + i).setContentAsString("Col-" + (i + 1));
      else
	row.addCell("" + i).setContentAsString(colNames[i]);
    }
    for (i = 0; i < rows; i++)
      result.addRow();

    // data
    for (i = 0; i < input.length(); i++) {
      y = i % rows;
      x = i / rows;
      if (rowNames != null) {
        result.getCell(y, 0).setContentAsString(rowNames[y]);
        x++;
      }
      result.getCell(y, x).setContent(input.getElementAsDouble(i));
    }

    return result;
  }
}
