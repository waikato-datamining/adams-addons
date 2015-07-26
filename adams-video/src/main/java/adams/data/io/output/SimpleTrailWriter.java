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
 * SimpleTrailWriter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.data.io.input.SimpleTrailReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.trail.Trail;

import java.util.List;

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
public class SimpleTrailWriter
  extends AbstractTrailWriter {

  private static final long serialVersionUID = -7138302129366743189L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes trails in the simple CSV-like format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the file chooser
   */
  @Override
  public String getFormatDescription() {
    return new SimpleTrailReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new SimpleTrailReader().getFormatExtensions();
  }

  /**
   * Returns whether writing of multiple containers is supported.
   *
   * @return 		true if multiple containers are supported
   */
  @Override
  public boolean canWriteMultiple() {
    return false;
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(List<Trail> data) {
    SpreadSheet 		sheet;
    String[]			lines;
    CsvSpreadSheetWriter	writer;

    if (data.size() == 0)
      return false;

    sheet = data.get(0).toSpreadSheet();
    lines = data.get(0).getReport().toProperties().toString().split("\n");
    for (String line: lines)
      sheet.addComment(line);

    writer = new CsvSpreadSheetWriter();
    writer.setMissingValue("");
    writer.setOutputComments(true);
    writer.setComment("#");

    return writer.write(sheet, m_Output.getAbsolutePath());
  }
}
