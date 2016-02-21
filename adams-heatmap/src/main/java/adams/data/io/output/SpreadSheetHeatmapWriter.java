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
 * SpreadSheetHeatmapWriter.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.data.heatmap.Heatmap;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Writes heat map files using a spreadsheet format.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to write the container to.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.tmp
 * </pre>
 * 
 * <pre>-writer &lt;adams.data.io.output.AbstractSpreadSheetWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The spreadsheet writer to use for writing the heatmap data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.CsvSpreadSheetWriter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetHeatmapWriter
  extends AbstractHeatmapWriter {

  /** for serialization. */
  private static final long serialVersionUID = -7828811375813385465L;

  /** the spreadsheet writer to use. */
  protected SpreadSheetWriter m_Writer;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Writes heat map files using a spreadsheet format.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_OutputIsFile = true;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "writer", "writer",
	    new CsvSpreadSheetWriter());
  }

  /**
   * Sets the spreadsheet writer to use.
   *
   * @param value	the writer
   */
  public void setWriter(SpreadSheetWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the spreadsheet writer in use.
   *
   * @return		the writer
   */
  public SpreadSheetWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The spreadsheet writer to use for writing the heatmap data.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Heatmap in spreadsheet-format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"*"};
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(List<Heatmap> data) {
    boolean		result;
    Heatmap		map;
    int			x;
    int			y;
    SpreadSheet		sheet;
    Row			row;

    result = false;

    map   = data.get(0);
    sheet = new DefaultSpreadSheet();
    
    // header
    row = sheet.getHeaderRow();
    for (x = 0; x < map.getWidth(); x++)
      row.addCell("" + x).setContent("x" + (x+1));
    
    // data
    for (y = 0; y < map.getHeight(); y++) {
      row = sheet.addRow();
      for (x = 0; x < map.getWidth(); x++)
	row.addCell(x).setContent(map.get(y, x));
    }
    
    // write
    result = m_Writer.write(sheet, m_Output);

    return result;
  }
}
