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
 * SpreadSheetToHeatmap.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.heatmap.Heatmap;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Turns an all-numeric spreadsheet into a heatmap.<br/>
 * Missing values&#47;cells are tolerated and replaced with the specified value for missing values.
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
 * <pre>-missing-value &lt;double&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The value to use in case of missing values&#47;cells.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetToHeatmap
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 4705676366087704263L;

  /** the default value for missing values. */
  protected double m_MissingValue;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Turns an all-numeric spreadsheet into a heatmap.\n"
	+ "Missing values/cells are tolerated and replaced with the specified "
	+ "value for missing values.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "missing-value", "missingValue",
	    0.0);
  }

  /**
   * Sets the value to use for missing values/cells.
   *
   * @param value	the value
   */
  public void setMissingValue(double value) {
    m_MissingValue = value;
    reset();
  }

  /**
   * Returns the value to use for missing values/celss.
   *
   * @return		the value
   */
  public double getMissingValue() {
    return m_MissingValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingValueTipText() {
    return "The value to use in case of missing values/cells.";
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
    int			i;
    int			n;
    Row			header;
    Row			row;
    String		key;
    double		value;
    
    sheet = (SpreadSheet) m_Input;
    
    // check columns
    header = sheet.getHeaderRow();
    for (i = 0; i < sheet.getColumnCount(); i++) {
      if (!sheet.isNumeric(i))
	throw new IllegalArgumentException("Column #" + (i+1) + " (" + header.getCell(i).getContent() + ") is not numeric!");
    }

    result = new Heatmap(sheet.getRowCount(), sheet.getColumnCount());
    for (n = 0; n < sheet.getRowCount(); n++) {
      row = sheet.getRow(n);
      for (i = 0; i < sheet.getColumnCount(); i++) {
	key   = header.getCellKey(i);
	value = m_MissingValue;
	if (row.hasCell(key) && !row.getCell(key).isMissing())
	  value = row.getCell(key).toDouble();
	result.set(n, i, value);
      }
    }
    
    return result;
  }
}
