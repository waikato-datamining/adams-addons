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
 * HeatmapToBufferedImage.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import adams.data.heatmap.Heatmap;
import adams.data.image.AbstractImage;
import adams.data.image.BufferedImageContainer;
import adams.gui.visualization.core.AbstractColorGradientGenerator;
import adams.gui.visualization.core.BiColorGenerator;

/**
 <!-- globalinfo-start -->
 * Turns a heatmap into a BufferedImage.
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
 * <pre>-generator &lt;adams.gui.visualization.heatmap.AbstractColorGradientGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for creating the gradient colors.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.heatmap.BiColorGenerator
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapToBufferedImage
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 2535421741524997185L;

  /** the generator to use. */
  protected AbstractColorGradientGenerator m_Generator;

  /** the gradient colors. */
  protected Color[] m_GradientColors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Turns a heatmap into a BufferedImage.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    new BiColorGenerator());
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();

    m_GradientColors = null;
  }

  /**
   * Sets the number of gradient colors to use.
   *
   * @param value	the number of colors
   */
  public void setGenerator(AbstractColorGradientGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the number of gradient colors to use.
   *
   * @return		the number of colors
   */
  public AbstractColorGradientGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use for creating the gradient colors.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Heatmap.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return AbstractImage.class;
  }

  /**
   * Generates the gradient colors.
   *
   * @return		the colors
   */
  protected Color[] getGradientColors() {
    if (m_GradientColors == null)
      m_GradientColors = m_Generator.generate();

    return m_GradientColors;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    BufferedImageContainer	result;
    BufferedImage		image;
    Color[]			colors;
    Heatmap			map;
    double			min;
    double			max;
    double			range;
    int				x;
    int				y;
    Graphics2D			g;
    Color			color;

    map    = (Heatmap) m_Input;
    colors = getGradientColors();
    min    = Double.MAX_VALUE;
    max    = Double.MIN_VALUE;
    for (y = 0; y < map.getHeight(); y++) {
      for (x = 0; x < map.getWidth(); x++) {
	if (map.get(y, x) > 0.0)
	  min = Math.min(map.get(y, x), min);   // we don't want zeroes
	max = Math.max(map.get(y, x), max);
      }
    }
    range = max - min;

    image = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_RGB);
    g      = image.createGraphics();
    for (y = 0; y < map.getHeight(); y++) {
      for (x = 0; x < map.getWidth(); x++) {
	if (map.get(y, x) == 0.0)
	  color = colors[0];
	else
	  color = colors[(int) (((map.get(y, x) - min) / range) * (colors.length - 2)) + 1];
	g.setColor(color);
	g.drawLine(x, y, x, y);
      }
    }

    result = new BufferedImageContainer();
    result.setImage(image);
    
    return result;
  }
}
