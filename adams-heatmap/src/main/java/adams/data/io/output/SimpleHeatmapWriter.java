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
 * SimpleHeatmapWriter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.CompressionSupporter;
import adams.data.heatmap.Heatmap;
import adams.data.heatmap.HeatmapValue;
import adams.data.io.input.SimpleHeatmapReader;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

/**
 <!-- globalinfo-start -->
 * Writer that stores heatmaps in a simple CSV-like format.
 * <p/>
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
 * <pre>-num-decimals &lt;int&gt; (property: numDecimals)
 * &nbsp;&nbsp;&nbsp;The number of decimals to output after the decimal point (at most).
 * &nbsp;&nbsp;&nbsp;default: 6
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 16
 * </pre>
 * 
 * <pre>-output-meta-data &lt;boolean&gt; (property: outputMetaData)
 * &nbsp;&nbsp;&nbsp;If set to true, the meta data gets stored in the file as well (as comment
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-use-compression &lt;boolean&gt; (property: useCompression)
 * &nbsp;&nbsp;&nbsp;If enabled, the heatmap is compressed using GZIP and appending '.gz' to 
 * &nbsp;&nbsp;&nbsp;the filename.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 2018 $
 */
public class SimpleHeatmapWriter
  extends AbstractHeatmapWriter 
  implements CompressionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 5290679698357490093L;

  /** the number of decimals to use. */
  protected int m_NumDecimals;

  /** whether to output the meta data as well. */
  protected boolean m_OutputMetaData;

  /** whether to use compression. */
  protected boolean m_UseCompression;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writer that stores heatmaps in a simple CSV-like format.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-decimals", "numDecimals",
	    6, 0, 16);

    m_OptionManager.add(
	    "output-meta-data", "outputMetaData",
	    true);

    m_OptionManager.add(
	    "use-compression", "useCompression",
	    false);
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the file chooser
   */
  @Override
  public String getFormatDescription() {
    return new SimpleHeatmapReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 		the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new SimpleHeatmapReader().getFormatExtensions();
  }

  /**
   * Sets the number of decimals to output after the decimal point.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    m_NumDecimals = value;
    reset();
  }

  /**
   * Returns the number of decimals to output after the decimal point.
   *
   * @return		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or
   *         for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals to output after the decimal point (at most).";
  }

  /**
   * Sets whether to output the meta data as well.
   *
   * @param value	if true then the meta data gets output as well
   */
  public void setOutputMetaData(boolean value) {
    m_OutputMetaData = value;
    reset();
  }

  /**
   * Returns whether to output the meta data as well.
   *
   * @return		true if the meta data is output as well
   */
  public boolean getOutputMetaData() {
    return m_OutputMetaData;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or
   *         for listing the options.
   */
  public String outputMetaDataTipText() {
    return "If set to true, the meta data gets stored in the file as well (as comment).";
  }

  /**
   * Sets whether to use compression.
   *
   * @param value	true if to use compression
   */
  public void setUseCompression(boolean value) {
    m_UseCompression = value;
    reset();
  }

  /**
   * Returns whether compression is in use.
   *
   * @return 		true if compression is used
   */
  public boolean getUseCompression() {
    return m_UseCompression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or
   *         for listing the options.
   */
  public String useCompressionTipText() {
    return "If enabled, the heatmap is compressed using GZIP and appending '.gz' to the filename.";
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
   * Writes its content with the given writer.
   *
   * @param data	the spectra to write
   * @param writer	the writer to use
   * @param report	whether to output the report as well
   * @return		true if successfully written
   */
  protected boolean write(List<Heatmap> data, BufferedWriter writer, boolean report) {
    boolean		result;
    Heatmap		map;
    String[]		lines;

    result = true;
    
    try {
      map = data.get(0);

      // report?
      if (map.hasReport() && report) {
	lines = map.getReport().toProperties().toComment().split("\n");
	Arrays.sort(lines);
	writer.write(Utils.flatten(lines, "\n"));
	writer.write("\n");
      }

      // header
      writer.write("Row,Column,Intensity");
      writer.write("\n");

      // points
      for (HeatmapValue value: map) {
	writer.write("" + value.getY());
	writer.write(",");
	writer.write("" + value.getX());
	writer.write(",");
	writer.write(Utils.doubleToString(value.getValue(), m_NumDecimals));
	writer.write("\n");
      }

      writer.flush();

      result = true;
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed to write heatmap to writer!", e);
    }

    return result;
  }

  /**
   * Writes its content to the given file.
   *
   * @param data	the spectra to write
   * @param filename	the file to write to
   * @param report	whether to output the report as well
   * @return		true if successfully written
   */
  protected boolean write(List<Heatmap> data, String filename, boolean report) {
    boolean		result;
    BufferedWriter	writer;

    filename = new PlaceholderFile(filename).getAbsolutePath();
    try {
      if (filename.endsWith(".gz"))
	writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(filename))));
      else
	writer = new BufferedWriter(new FileWriter(filename));
      result = write(data, writer, report);
      writer.close();
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed to write heatmap to '" + filename + "'!", e);
    }

    return result;
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(List<Heatmap> data) {
    boolean result;

    result = write(data, m_Output.getAbsolutePath(), m_OutputMetaData);
    if (!result)
      getLogger().severe("Error writing data to '" + m_Output.getAbsolutePath() + "'!");

    return result;
  }
}
