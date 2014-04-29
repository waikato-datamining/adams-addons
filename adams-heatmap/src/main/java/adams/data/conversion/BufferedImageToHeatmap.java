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
 * BufferedImageToHeatmap.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

import adams.data.heatmap.Heatmap;
import adams.data.image.AbstractImage;

/**
 <!-- globalinfo-start -->
 * Turns a class java.awt.image.BufferedImage into a heatmap.<br/>
 * Simply uses the RGB value as heatmap value.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BufferedImageToHeatmap
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -3874290458679824062L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Turns a " + BufferedImage.class + " into a heatmap.\n"
	+ "Simply uses the RGB value as heatmap value, but ignores the alpha value.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return AbstractImage.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Heatmap.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Heatmap		result;
    BufferedImage	image;
    int			i;
    int			n;
    Raster		raster;
    ColorModel		colorModel;
    Object		data;
    int			value;
    
    image  = ((AbstractImage) m_Input).toBufferedImage();
    result = new Heatmap(image.getHeight(), image.getWidth());

    raster     = image.getRaster();
    colorModel = image.getColorModel();
    for (n = 0; n < image.getHeight(); n++) {
      for (i = 0; i < image.getWidth(); i++) {
        data  = raster.getDataElements(i, n, null);
        value =   (colorModel.getRed(data)   << 16) 
                | (colorModel.getGreen(data) <<  8) 
                | (colorModel.getBlue(data)  <<  0);
	result.set(n, i, value);
      }
    }
    
    return result;
  }
}
