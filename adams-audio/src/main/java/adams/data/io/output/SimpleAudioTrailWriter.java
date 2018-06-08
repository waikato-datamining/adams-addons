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
 * SimpleAudioTrailWriter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.Constants;
import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.data.DateFormatString;
import adams.data.audiotrail.AudioTrail;
import adams.data.spreadsheet.SpreadSheet;

import java.io.StringWriter;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Writes audio trails in the simple CSV-like format.<br>
 * The report data is prefixed with '# ' and the background is prefixed with '% '.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to write the container to.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.tmp
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleAudioTrailWriter
  extends AbstractAudioTrailWriter {

  private static final long serialVersionUID = -7138302129366743189L;

  /** the comment prefix. */
  public final static String COMMENT = Properties.COMMENT;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Writes audio trails in the simple CSV-like format.\n"
	+ "The report comes before the actual trail data.\n"
	+ "The report data is prefixed with '" + COMMENT + "'.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Simple trail format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"audiotrail"};
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
  protected boolean writeData(List<AudioTrail> data) {
    boolean			result;
    AudioTrail			trail;
    SpreadSheet 		sheet;
    StringWriter		swriter;
    Properties			props;
    CsvSpreadSheetWriter	writer;

    if (data.size() == 0)
      return false;

    trail   = data.get(0);
    swriter = new StringWriter();

    // report
    if (trail.hasReport()) {
      props = trail.getReport().toProperties();
      swriter.write(props.toComment());
    }

    // data
    sheet = trail.toSpreadSheet();
    writer = new CsvSpreadSheetWriter();
    writer.setTimeMsecFormat(new DateFormatString(Constants.TIME_FORMAT_MSECS));
    //writer.setTimeZone(TimeZone.getTimeZone("GMT"));
    writer.setMissingValue("");
    result = writer.write(sheet, swriter);

    if (result)
      result = FileUtils.writeToFile(m_Output.getAbsolutePath(), swriter.toString(), false);

    return result;
  }
}
