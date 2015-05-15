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
 * SimpleHeatmapReader.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

/**
 <!-- globalinfo-start -->
 * Reads heatmaps in the internal heatmap CSV format.
 * <p/>
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 2152 $
 */
public class SimpleHeatmapReader
  extends AbstractHeatmapReader {

  /** for serialization. */
  private static final long serialVersionUID = -2903357410192470809L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads heatmaps in the internal heatmap CSV format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Simple heatmap CSV format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"hm", "hm.gz"};
  }

  /**
   * Determines the dimensions of the heatmap and stores the Width and
   * Height information in the report.
   *
   * @param content	the content to parse
   * @param meta	the report to store the dimensions in
   */
  protected void determineDimensions(List<String> content, Report meta) {
    String[]		parts;
    int			height;
    int			width;
    int			row;
    int			col;

    height = 0;
    width  = 0;

    for (String line: content) {
      if (line.trim().length() == 0)
	continue;
      parts = line.split(",");
      if (parts.length == 3) {
	row    = Integer.parseInt(parts[0]);
	col    = Integer.parseInt(parts[1]);
	height = Math.max(height, row + 1);
	width  = Math.max(width,  col + 1);
      }
    }

    meta.setValue(new Field("Height", DataType.NUMERIC), height);
    meta.setValue(new Field("Width",  DataType.NUMERIC), width);
  }

  /**
   * Reads its content from the given reader.
   *
   * @param reader	the reader to use
   * @return		true if successfully read
   */
  protected boolean read(BufferedReader reader) {
    boolean		result;
    Heatmap 		map;
    Report 		meta;
    String		line;
    List<String>	content;
    List<String>	report;
    String[]		parts;

    result = true;

    try {
      // read from file
      content = new ArrayList<String>();
      while ((line = reader.readLine()) != null)
	content.add(line);

      // report?
      report = new ArrayList<String>();
      while ((content.size() > 0) && content.get(0).startsWith(Properties.COMMENT)) {
	report.add(content.get(0));
	content.remove(0);
      }
      meta = null;
      if (report.size() > 0)
	meta = Report.parseProperties(Properties.fromComment(Utils.flatten(report, "\n")));
      if (meta == null)
	meta = new Report();
      if (!meta.hasValue("Height") || !meta.hasValue("Width"))
	determineDimensions(content, meta);
      map = new Heatmap(meta.getDoubleValue("Height").intValue(), meta.getDoubleValue("Width").intValue());
      if (meta.hasValue("ID"))
	map.setID(meta.getStringValue("ID"));
      map.setReport(meta);
      m_ReadData.add(map);

      // header - ignored
      if (content.size() > 0)
	content.remove(0);

      // data points
      while ((content.size() > 0) && result) {
	line = content.get(0).trim();
	content.remove(0);
	if (line.length() == 0)
	  continue;
	parts = line.split(",");
	if (parts.length == 3)
	  map.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Double.parseDouble(parts[2]));
      }
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed to read heatmap data!", e);
    }

    return result;
  }

  /**
   * Reads its content from the given file.
   *
   * @param filename	the file to read from
   * @return		true if successfully read
   */
  protected boolean read(String filename) {
    boolean		result;
    BufferedReader	reader;
    FileInputStream     fis;
    FileReader		fr;

    filename = new PlaceholderFile(filename).getAbsolutePath();
    fis      = null;
    fr       = null;
    reader   = null;
    try {
      if (filename.endsWith(".gz")) {
	fis    = new FileInputStream(filename);
	reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(fis)));
      }
      else {
	fr     = new FileReader(filename);
	reader = new BufferedReader(fr);
      }
      result = read(reader);
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed to read spectral data from '" + filename + "'!", e);
    }
    finally {
      FileUtils.closeQuietly(reader);
      FileUtils.closeQuietly(fr);
      FileUtils.closeQuietly(fis);
    }

    return result;
  }

  /**
   * Performs the actual reading.
   */
  @Override
  protected void readData() {
    read(m_Input.getAbsolutePath());
  }
}
