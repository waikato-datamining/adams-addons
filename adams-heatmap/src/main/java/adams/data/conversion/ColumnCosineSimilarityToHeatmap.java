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
 * ColumnCosineSimilarityToHeatmap.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.heatmap.Heatmap;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetHelper;
import adams.data.statistics.StatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Computes the cosine similarities between the specified range of columns and outputs a heatmap with the calculated values.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-columns &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The columns to include in the calculations.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ColumnCosineSimilarityToHeatmap
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4705676366087704263L;

  /** the range of columns to compute the correlation for. */
  protected SpreadSheetColumnRange m_Columns;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Computes the cosine similarities between the specified range "
          + "of columns and outputs a heatmap with the calculated values.";
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
   * Sets the columns to include in the calculations.
   *
   * @param value	the columns
   */
  public void setColumns(SpreadSheetColumnRange value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the columns to include in the calculations.
   *
   * @return		the columns
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
    return "The columns to include in the calculations.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Heatmap.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Heatmap		result;
    SpreadSheet		sheet;
    int[] 		cols;
    List<double[]> 	values;
    int 		i;
    int 		n;
    double		cc;

    sheet = (SpreadSheet) m_Input;
    m_Columns.setData(sheet);
    cols = m_Columns.getIntIndices();
    if (cols.length == 0)
      throw new IllegalStateException("Failed to determine any columns using: " + m_Columns.getRange());

    // get values of numeric columns
    values = new ArrayList<>();
    for (i = 0; i < cols.length; i++) {
      if (sheet.isNumeric(cols[i]))
        values.add(SpreadSheetHelper.getNumericColumn(sheet, cols[i]));
    }

    // compute heatmap
    result = new Heatmap(values.size(), values.size());
    for (i = 0; i < cols.length; i++) {
      for (n = i; n < cols.length; n++) {
	cc = StatUtils.cosineSimilarity(values.get(i), values.get(n));
	result.set(i, n, cc);
      }
    }

    return result;
  }
}
