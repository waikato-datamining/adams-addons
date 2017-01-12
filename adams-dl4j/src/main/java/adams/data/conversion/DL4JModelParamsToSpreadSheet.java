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
 * DL4JModelParamsToSpreadSheet.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import org.deeplearning4j.nn.api.Model;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 <!-- globalinfo-start -->
 * Extracts the parameter matrix of a DL4J model and turns it into a spreadsheet.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JModelParamsToSpreadSheet
  extends AbstractConversion {

  private static final long serialVersionUID = -8817702395174849629L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Extracts the parameter matrix of a DL4J model and turns it into a spreadsheet.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Model.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    SpreadSheet 	result;
    Model		model;
    INDArray		params;
    Row			row;
    int			i;
    int			n;
    int			rows;
    int			cols;
    INDArray		prow;

    model  = (Model) m_Input;
    params = model.params();
    rows   = params.rows();
    cols   = params.columns();
    result = new DefaultSpreadSheet();

    // header
    row = result.getHeaderRow();
    for (i = 0; i < cols; i++)
      row.addCell("" + i).setContentAsString("Col-" + (i+1));

    // data
    for (n = 0; n < rows; n++) {
      prow = params.getRow(n);
      row  = result.addRow();
      for (i = 0; i < cols; i++)
	row.addCell(i).setContent(prow.getDouble(i));
    }

    return result;
  }
}
