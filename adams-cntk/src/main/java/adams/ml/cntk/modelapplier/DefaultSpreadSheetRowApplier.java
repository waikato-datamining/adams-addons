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
 * DefaultSpreadSheetRowApplier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelapplier;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;

/**
 * Applies the model to the specified (numeric) cells of a row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultSpreadSheetRowApplier
  extends AbstractNumericArrayApplier<Row> {

  private static final long serialVersionUID = 6354440278825130565L;

  /** the columns to use as input. */
  protected SpreadSheetColumnRange m_Columns;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the model to the specified (numeric, non-missing) columns of a row.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "columns", "columns",
      new SpreadSheetColumnRange(SpreadSheetColumnRange.ALL));
  }

  /**
   * Sets the columns to use as input.
   *
   * @param value	the range
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the columns to use as input.
   *
   * @return  		the range
   */
  public SpreadSheetColumnRange getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The columns in the row to use as input (only numeric, non-missing cells are used).";
  }

  /**
   * Returns the class that the applier accepts.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual application of the model.
   *
   * @param input	the input
   * @return		the score
   */
  @Override
  protected float[] doApplyModel(Row input) {
    TFloatList values;
    int[]		indices;
    Cell		cell;

    values = new TFloatArrayList();
    m_Columns.setData(input.getOwner());
    indices = m_Columns.getIntIndices();
    for (int index: indices) {
      cell = input.getCell(index);
      if (cell.isNumeric() && !cell.isMissing())
	values.add(cell.toDouble().floatValue());
    }

    return applyModel(values.toArray());
  }
}
