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
 * FloatMatrixHeatmapReader.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.data.heatmap.Heatmap;
import adams.data.report.Report;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads heat map files (2-D array of single precision floating point numbers).
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
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the heatmap.
 * &nbsp;&nbsp;&nbsp;default: 240
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the heatmap.
 * &nbsp;&nbsp;&nbsp;default: 320
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FloatMatrixHeatmapReader
  extends AbstractHeatmapReader {

  /** for serialization. */
  private static final long serialVersionUID = -2903357410192470809L;

  /** the height of the heatmap. */
  protected int m_Height;

  /** the width of the heatmap. */
  protected int m_Width;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Reads heat map files (2-D array of single precision floating point numbers).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "height", "height",
	    getDefaultHeight(), 1, null);

    m_OptionManager.add(
	    "width", "width",
	    getDefaultWidth(), 1, null);
  }

  /**
   * Returns the default height of the heatmap.
   *
   * @return		the default height
   */
  protected int getDefaultHeight() {
    return 240;
  }

  /**
   * Sets the height of the heatmap.
   *
   * @param value	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the height of the heatmap.
   *
   * @return		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the heatmap.";
  }

  /**
   * Returns the default width of the heatmap.
   *
   * @return		the default width
   */
  protected int getDefaultWidth() {
    return 320;
  }

  /**
   * Sets the width of the heatmap.
   *
   * @param value	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the width of the heatmap.
   *
   * @return		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the heatmap.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Float matrix Heatmap";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"dat"};
  }

  /**
   * Performs the actual reading.
   */
  @Override
  protected void readData() {
    Heatmap		map;
    DataInputStream	stream;
    int			x;
    int			y;
    int			ivalue;
    float		fvalue;
    byte[]		bytes;
    int			read;
    Report		report;

    stream = null;
    try {
      // assemble meta-data
      report = Heatmap.createEmptyReport();

      // read heatmap data
      map = new Heatmap(m_Height, m_Width);
      map.setReport(report);
      stream = new DataInputStream(new FileInputStream(m_Input.getAbsoluteFile()));
      x      = 0;
      y      = 0;
      bytes  = new byte[4];
      do {
	read = stream.read(bytes);
	if (read == 4) {
	  ivalue = ByteBuffer.wrap(new byte[]{bytes[3], bytes[2], bytes[1], bytes[0]}).getInt();
	  fvalue = Float.intBitsToFloat(ivalue);
	  map.set(y, x, fvalue);
	  x++;
	  if (x == m_Width) {
	    x = 0;
	    y++;
	    if (y == m_Height)
	      break;
	  }
	}
      }
      while (read == 4);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read heatmap from '" + m_Input + "'!", e);
      map = null;
    }
    finally {
      if (stream != null) {
	try {
	  stream.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    if (map != null)
      m_ReadData.add(map);
  }
}
