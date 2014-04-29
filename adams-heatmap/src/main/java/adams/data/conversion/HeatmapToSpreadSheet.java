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
 * HeatmapToSpreadSheet.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.Collections;
import java.util.List;

import adams.data.heatmap.Heatmap;
import adams.data.report.AbstractField;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Turns a heatmap into a spreadsheet object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapToSpreadSheet
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4500736133110802963L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Turns a heatmap into a spreadsheet object.\n"
      + "The attached report gets stored as comments.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Heatmap.class;
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
    SpreadSheet		result;
    Heatmap		map;
    int			x;
    int			y;
    Row			row;
    List<AbstractField>	fields;

    result = new SpreadSheet();
    map    = (Heatmap) m_Input;

    // comments
    fields = map.getReport().getFields();
    Collections.sort(fields);
    for (AbstractField field: fields)
      result.addComment(field.getName() + ": " + map.getReport().getValue(field));

    // header
    row = result.getHeaderRow();
    for (x = 0; x < map.getWidth(); x++)
      row.addCell("x" + x).setContent("x" + x);

    // data
    for (y = 0; y < map.getHeight(); y++) {
      row = result.addRow("y" + y);
      for (x = 0; x < map.getWidth(); x++)
	row.addCell("x" + x).setContent(map.get(y, x));
    }

    return result;
  }
}
