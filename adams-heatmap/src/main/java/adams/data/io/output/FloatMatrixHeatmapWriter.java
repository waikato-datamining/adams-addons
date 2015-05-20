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
 * FloatMatrixHeatmapWriter.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.io.FileUtils;
import adams.data.heatmap.Heatmap;
import adams.data.io.input.FloatMatrixHeatmapReader;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Writes heat map files (2-D array of single precision floating point numbers).
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
 * &nbsp;&nbsp;&nbsp;The directory to write the container to.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.tmp
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FloatMatrixHeatmapWriter
  extends AbstractHeatmapWriter {

  /** for serialization. */
  private static final long serialVersionUID = -7828811375813385465L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Writes heat map files (2-D array of single precision floating point numbers).";
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
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the file chooser
   */
  @Override
  public String getFormatDescription() {
    return new FloatMatrixHeatmapReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new FloatMatrixHeatmapReader().getFormatExtensions();
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
    DataOutputStream	stream;
    FileOutputStream	fos;
    Heatmap		map;
    int			x;
    int			y;
    float		fvalue;
    int			ivalue;
    byte[]		bytes;

    result = false;

    stream = null;
    fos    = null;
    try {
      fos    = new FileOutputStream(m_Output.getAbsoluteFile());
      stream = new DataOutputStream(fos);
      map    = data.get(0);
      bytes  = new byte[4];
      for (y = 0; y < map.getHeight(); y++) {
	for (x = 0; x < map.getWidth(); x++) {
	  fvalue   = (float) map.get(y, x);
	  ivalue   = Float.floatToIntBits(fvalue);
	  bytes[3] = (byte) (ivalue >>> 24);
	  bytes[2] = (byte) (ivalue >>> 16);
	  bytes[1] = (byte) (ivalue >>>  8);
	  bytes[0] = (byte) (ivalue);
	  stream.write(bytes);
	}
      }
      result = true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write heatmap to '" + m_Output + "':", e);
    }
    finally {
      FileUtils.closeQuietly(stream);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }
}
