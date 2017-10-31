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
 * DefaultSpreadSheetRowApplier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelapplier;

import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;

/**
 * Applies the model to the cells of a spreadsheet row.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultSpreadSheetRowApplier
  extends AbstractNumericArrayApplier<Row> {

  private static final long serialVersionUID = 6354440278825130565L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Applies the model to a row of a spreadsheet.\n"
	+ m_ModelLoader.automaticOrderInfo();
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
    TFloatList 		values;
    int			i;
    Cell		cell;

    values = new TFloatArrayList();
    for (i = 0; i < input.getOwner().getColumnCount(); i++) {
      cell = input.getCell(i);
      if (cell.isNumeric() && !cell.isMissing())
	values.add(cell.toDouble().floatValue());
      else
        values.add(Float.NaN);
    }

    return applyModel(values.toArray());
  }
}
