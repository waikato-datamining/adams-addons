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
 * SimpleImageHeatmapReader.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.data.heatmap.Heatmap;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.IntArrayMatrixView;
import adams.data.report.Report;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads an image with the specified reader and turns the grayscale version of the image into a heatmap.
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
 * <pre>-reader &lt;adams.data.io.input.AbstractImageReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The image reader to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.JAIImageReader
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleImageHeatmapReader
  extends AbstractHeatmapReader {

  /** for serialization. */
  private static final long serialVersionUID = -2903357410192470809L;

  /** the image reader to use. */
  protected AbstractImageReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Reads an image with the specified reader and turns the grayscale "
	  + "version of the image into a heatmap.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"reader", "reader",
	new JAIImageReader());
  }

  /**
   * Sets the reader to use.
   *
   * @param value 	the reader
   */
  public void setReader(AbstractImageReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader to use.
   *
   * @return 		the reader
   */
  public AbstractImageReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The image reader to use.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Simple image heatmap reader";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    if (m_Reader == null)
      return new String[]{"*"};
    else
      return m_Reader.getFormatExtensions();
  }

  /**
   * Performs the actual reading.
   */
  @Override
  protected void readData() {
    Heatmap map;
    Report report;
    AbstractImageContainer	cont;
    BufferedImage 		image;
    IntArrayMatrixView		matrix;
    int				x;
    int				y;
    int[]			rgba;

    try {
      // read image
      cont   = m_Reader.read(m_Input);
      image  = BufferedImageHelper.convert(cont.toBufferedImage(), BufferedImage.TYPE_BYTE_GRAY);
      matrix = BufferedImageHelper.getPixelMatrix(image);

      // assemble meta-data
      report = Heatmap.createEmptyReport();
      report.setStringValue(Heatmap.FIELD_FILENAME, m_Input.getAbsolutePath());

      // read heatmap data
      map = new Heatmap(matrix.getHeight(), matrix.getWidth());
      map.setReport(report);
      map.setID(m_Input.getName());
      for (y = 0; y < matrix.getHeight(); y++) {
	for (x = 0; x < matrix.getWidth(); x++) {
	  rgba = matrix.getRGBA(x, y);
	  map.set(y, x, rgba[0]);
	}
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read heatmap from '" + m_Input + "'!", e);
      map = null;
    }

    if (map != null)
      m_ReadData.add(map);
  }
}
