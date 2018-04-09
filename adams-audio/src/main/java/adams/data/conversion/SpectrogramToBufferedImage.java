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
 * SpectrogramToBufferedImage.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.data.image.BufferedImageContainer;
import adams.gui.visualization.core.AbstractColorGradientGenerator;
import adams.gui.visualization.core.BiColorGenerator;
import com.musicg.wave.extension.Spectrogram;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Generates an image from the incoming spectrogram.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpectrogramToBufferedImage
  extends AbstractConversion {

  private static final long serialVersionUID = 6569357563362936237L;

  /** the generator to use. */
  protected AbstractColorGradientGenerator m_Generator;

  /** the gradient colors. */
  protected transient Color[] m_GradientColors;

  /** the lookup table. */
  protected transient TIntIntMap m_ColorLookup;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an image from the incoming spectrogram.";
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
    m_ColorLookup    = null;
  }

  /**
   * Sets the color generator.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractColorGradientGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the color generator.
   *
   * @return		the generator
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
    return Spectrogram.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return BufferedImageContainer.class;
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
   * Generates the color lookup.
   *
   * @return		the colors
   */
  protected TIntIntMap getColorLookup() {
    Color[]	colors;
    int		i;

    if (m_ColorLookup == null) {
      m_ColorLookup = new TIntIntHashMap();
      colors        = getGradientColors();
      for (i = 0; i < colors.length; i++)
        m_ColorLookup.put(i, colors[i].getRGB());
    }

    return m_ColorLookup;
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
    BufferedImage		img;
    Spectrogram			sp;
    double[][]			data;
    TIntIntMap			colors;
    int				i;
    int				n;
    int				colorIndex;

    sp     = (Spectrogram) m_Input;
    data   = sp.getNormalizedSpectrogramData();
    result = new BufferedImageContainer();
    img    = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_RGB);
    colors = getColorLookup();
    for (i = 0; i < data.length; i++) {
      for (n = 0; n < data[i].length; n++) {
        colorIndex = (int) (data[i][n] * (colors.size() - 1));
        img.setRGB(i, img.getHeight() - n - 1, colors.get(colorIndex));
      }
    }

    result.setImage(img);

    return result;
  }
}
