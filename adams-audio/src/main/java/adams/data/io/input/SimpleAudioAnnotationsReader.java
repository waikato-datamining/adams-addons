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
 * SimpleAudioAnnotationsReader.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Constants;
import adams.core.Properties;
import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.data.DateFormatString;
import adams.data.audioannotations.AudioAnnotation;
import adams.data.audioannotations.AudioAnnotations;
import adams.data.io.output.SimpleAudioAnnotationsWriter;
import adams.data.report.Report;
import adams.data.spreadsheet.DenseFloatDataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads audio annotations in simple CSV-like format.<br>
 * See adams.data.io.output.SimpleAudioTrailWriter for more details on format.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The file to read and turn into a container.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-create-dummy-report &lt;boolean&gt; (property: createDummyReport)
 * &nbsp;&nbsp;&nbsp;If true, then a dummy report is created if none present.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleAudioAnnotationsReader
  extends AbstractAudioAnnotationsReader {

  private static final long serialVersionUID = 1681189490537858223L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Reads audio annotations in simple CSV-like format.\n"
	+ "See " + SimpleAudioAnnotationsWriter.class.getName() + " for more details on format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new SimpleAudioAnnotationsWriter().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new SimpleAudioAnnotationsWriter().getFormatExtensions();
  }

  /**
   * Performs the actual reading.
   */
  @Override
  protected void readData() {
    CsvSpreadSheetReader	reader;
    List<String>		lines;
    List<String>		comments;
    List<String>		data;
    SpreadSheet 		sheet;
    AudioAnnotations trail;
    Report			report;
    AudioAnnotation step;
    boolean			header;
    StringReader		sreader;
    int				i;
    String			col;
    HashMap<Integer,String> 	metaCols;

    lines      = FileUtils.loadFromFile(m_Input.getAbsoluteFile());
    comments   = new ArrayList<>();
    data       = new ArrayList<>();
    header     = true;
    for (String line: lines) {
      if (header) {
	if (line.startsWith(SimpleAudioAnnotationsWriter.COMMENT)) {
	  comments.add(line);
	}
	else {
	  header = false;
	  data.add(line);
	}
      }
      else {
	data.add(line);
      }
    }

    sreader = new StringReader(Utils.flatten(data, "\n"));
    reader  = new CsvSpreadSheetReader();
    reader.setDataRowType(new DenseFloatDataRow());
    reader.setComment("#");
    reader.setMissingValue(new BaseRegExp(""));
    reader.setTimeMsecColumns(new Range(Range.FIRST));
    reader.setTimeMsecLenient(true);
    reader.setTimeMsecFormat(new DateFormatString(Constants.TIME_FORMAT_MSECS));
    //reader.setTimeZone(TimeZone.getTimeZone("GMT"));
    sheet = reader.read(sreader);
    if (sheet == null) {
      getLogger().severe("Failed to read file from: " + m_Input);
      return;
    }
    metaCols = new HashMap<>();
    for (i = 0; i < sheet.getColumnNames().size(); i++) {
      col = sheet.getColumnNames().get(i);
      if (col.startsWith(AudioAnnotations.PREFIX_META))
	metaCols.put(i, col.substring(AudioAnnotations.PREFIX_META.length()));
    }
    report = Report.parseProperties(Properties.fromComment(Utils.flatten(comments, "\n")));
    trail  = new AudioAnnotations();
    for (Row row: sheet.rows()) {
      step = new AudioAnnotation(
	row.getCell(0).toAnyDateType());
      // meta-data?
      for (int n: metaCols.keySet()) {
	if (row.hasCell(n) && !row.getCell(n).isMissing())
	  step.addMetaData(metaCols.get(n), row.getCell(n).getNative());
      }
      trail.add(step);
    }
    trail.setReport(report);
    m_ReadData.add(trail);
  }
}
