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
 * SpreadSheetHeatmapReader.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.data.heatmap.Heatmap;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.columnfinder.ByContentType;
import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Turns spreadsheets into heatmaps.
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
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The file to read and turn into a container.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-create-dummy-report (property: createDummyReport)
 * &nbsp;&nbsp;&nbsp;If true, then a dummy report is created if none present.
 * </pre>
 * 
 * <pre>-reader &lt;adams.data.io.input.AbstractSpreadSheetReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for reading the spreadsheet data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.CsvSpreadSheetReader
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetHeatmapReader
  extends AbstractHeatmapReader {

  /** for serialization. */
  private static final long serialVersionUID = -2903357410192470809L;

  /** the spreadsheet reader to use. */
  protected SpreadSheetReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Turns spreadsheets into heatmaps.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reader", "reader",
	    new CsvSpreadSheetReader());
  }

  /**
   * Sets the spreadsheet reader to use.
   *
   * @param value	the reader
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the spreadsheet reader in use.
   *
   * @return		the reader
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for reading the spreadsheet data.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Heatmap in spreadsheet-format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"*"};
  }

  /**
   * Performs the actual reading.
   */
  @Override
  protected void readData() {
    Heatmap		map;
    SpreadSheet		sheet;
    ByContentType	finder;
    int[]		numeric;
    int			i;
    int			n;
    Cell		cell;
    double		value;

    sheet = m_Reader.read(m_Input);
    finder = new ByContentType();
    finder.setContentTypes(new ContentType[]{ContentType.LONG, ContentType.DOUBLE});
    numeric = finder.findColumns(sheet);
    if (isLoggingEnabled())
      getLogger().info("columns: " + Utils.arrayToString(numeric));
    if (numeric.length == 0) {
      getLogger().severe("No numeric columns found!");
      return;
    }

    // assemble heatmap data
    map = new Heatmap(sheet.getRowCount(), numeric.length);
    if (isLoggingEnabled())
      getLogger().info("map: rows=" + map.getHeight() + ", cols=" + map.getWidth());

    for (n = 0; n < sheet.getRowCount(); n++) {
      for (i = 0; i < numeric.length; i++) {
	cell  = sheet.getCell(n, numeric[i]);
	value = 0.0;
	if ((cell != null) && cell.isNumeric())
	  value = cell.toDouble();
	map.set(n, i, value);
      }
    }
    
    m_ReadData.add(map);
  }
}
