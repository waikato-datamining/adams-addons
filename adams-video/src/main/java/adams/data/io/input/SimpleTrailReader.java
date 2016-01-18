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
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Constants;
import adams.core.Properties;
import adams.core.Range;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.GzipUtils;
import adams.data.DateFormatString;
import adams.data.image.BufferedImageHelper;
import adams.data.image.IntArrayMatrixView;
import adams.data.io.output.SimpleTrailWriter;
import adams.data.report.Report;
import adams.data.spreadsheet.DenseFloatDataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.trail.Step;
import adams.data.trail.Trail;
import gnu.trove.list.array.TByteArrayList;

import java.awt.image.BufferedImage;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads trails in simple CSV-like format.<br>
 * See adams.data.io.output.SimpleTrailWriter for more details on format.
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
    return
      "Reads trails in simple CSV-like format.\n"
	+ "See " + SimpleTrailWriter.class.getName() + " for more details on format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new SimpleTrailWriter().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new SimpleTrailWriter().getFormatExtensions();
  }

  /**
   * Performs the actual reading.
   */
  @Override
  protected void readData() {
    CsvSpreadSheetReader	reader;
    List<String>		lines;
    List<String>		comments;
    List<String>		background;
    List<String>		data;
    SpreadSheet 		sheet;
    Trail			trail;
    Report			report;
    Step			step;
    boolean			header;
    StringReader		sreader;
    int				x;
    int				y;
    int				width;
    int				height;
    IntArrayMatrixView		matrix;
    byte[]			pixels;
    TByteArrayList		compressed;
    byte[]			uncompressed;
    int				offset;
    int				pixel;
    boolean			hasX;
    boolean			hasY;
    int				i;
    String			col;
    HashMap<Integer,String> metaCols;

    lines      = FileUtils.loadFromFile(m_Input.getAbsoluteFile());
    comments   = new ArrayList<>();
    background = new ArrayList<>();
    data       = new ArrayList<>();
    header     = true;
    for (String line: lines) {
      if (header) {
	if (line.startsWith(SimpleTrailWriter.COMMENT)) {
	  comments.add(line);
	}
	else if (line.startsWith(SimpleTrailWriter.BACKGROUND)) {
	  background.add(line.substring(SimpleTrailWriter.BACKGROUND.length()));
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
    reader.setMissingValue("");
    reader.setDateTimeMsecColumns(new Range(Range.FIRST));
    reader.setDateTimeMsecLenient(true);
    reader.setDateTimeMsecFormat(new DateFormatString(Constants.TIMESTAMP_FORMAT_MSECS));
    sheet = reader.read(sreader);
    if (sheet == null) {
      getLogger().severe("Failed to read file from: " + m_Input);
      return;
    }
    hasX     = false;
    hasY     = false;
    metaCols = new HashMap<>();
    for (i = 0; i < sheet.getColumnNames().size(); i++) {
      col = sheet.getColumnNames().get(i);
      if (col.equals("X"))
	hasX = true;
      if (col.equals("Y"))
	hasY = true;
      if (col.startsWith(Trail.PREFIX_META))
	metaCols.put(i, col.substring(Trail.PREFIX_META.length()));
    }
    report = Report.parseProperties(Properties.fromComment(Utils.flatten(comments, "\n")));
    trail  = new Trail();
    for (Row row: sheet.rows()) {
      step = new Step(
	row.getCell(0).toTimeMsec(),
	hasX ? row.getCell(1).toDouble().floatValue() : 0f,
	hasY ? row.getCell(2).toDouble().floatValue() : 0f);
      // meta-data?
      for (int n: metaCols.keySet()) {
	if (row.hasCell(n) && !row.getCell(n).isMissing())
	  step.addMetaData(metaCols.get(n), row.getCell(n).getNative());
      }
      trail.add(step);
    }
    trail.setReport(report);

    if (background.size() > 0) {
      width  = Integer.parseInt(background.remove(0).trim());
      height = Integer.parseInt(background.remove(0).trim());
      compressed = new TByteArrayList();
      for (y = 0; y < background.size(); y++) {
	pixels = Utils.fromHexArray(background.get(y));
	compressed.add(pixels);
      }
      if (isLoggingEnabled())
	getLogger().info("compressed background bytes: " + compressed.size());
      uncompressed = GzipUtils.decompress(compressed.toArray(), 1024);
      if (uncompressed != null) {
	if (isLoggingEnabled())
	  getLogger().info("uncompressed background bytes: " + uncompressed.length);
	matrix = new IntArrayMatrixView(width, height);
	for (y = 0; y < height; y++) {
	  for (x = 0; x < width; x++) {
	    offset = y * width * 4 + x * 4;
	    pixel = BufferedImageHelper.combine(
	      uncompressed[offset    ],
	      uncompressed[offset + 1],
	      uncompressed[offset + 2],
	      uncompressed[offset + 3]);
	    matrix.set(x, y, pixel);
	  }
	}
	trail.setBackground(matrix.toBufferedImage(BufferedImage.TYPE_INT_ARGB));
      }
    }
    m_ReadData.add(trail);
  }
}
