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
 * SimpleTrailReader.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Constants;
import adams.core.Properties;
import adams.core.Range;
import adams.data.DateFormatString;
import adams.data.report.Report;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.trail.Step;
import adams.data.trail.Trail;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleTrailReader
  extends AbstractTrailReader {

  private static final long serialVersionUID = 1681189490537858223L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads trails in simple CSV-like format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Simple trail format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"trail"};
  }

  /**
   * Performs the actual reading.
   */
  @Override
  protected void readData() {
    CsvSpreadSheetReader	reader;
    SpreadSheet 		sheet;
    Trail			trail;
    Report			report;
    StringBuilder		comments;
    Step			step;

    reader = new CsvSpreadSheetReader();
    reader.setComment("#");
    reader.setMissingValue("");
    reader.setDateTimeMsecColumns(new Range(Range.FIRST));
    reader.setDateTimeMsecLenient(true);
    reader.setDateTimeMsecFormat(new DateFormatString(Constants.TIMESTAMP_FORMAT_MSECS));
    sheet = reader.read(m_Input.getAbsolutePath());
    if (sheet == null) {
      getLogger().severe("Failed to read file from: " + m_Input);
      return;
    }
    comments = new StringBuilder();
    for (String line: sheet.getComments())
      comments.append(Properties.COMMENT + line + "\n");
    report = Report.parseProperties(Properties.fromComment(comments.toString()));
    trail = new Trail();
    for (Row row: sheet.rows()) {
      step = new Step(
	row.getCell(0).toAnyDateType(),
	row.getCell(1).toDouble().floatValue(),
	row.getCell(2).toDouble().floatValue());
      trail.add(step);
    }
    trail.setReport(report);
    m_ReadData.add(trail);
  }
}
