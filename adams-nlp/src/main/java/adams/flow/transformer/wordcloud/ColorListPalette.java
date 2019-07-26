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
 * ColorListPalette.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wordcloud;

import adams.core.MessageCollection;
import adams.core.base.BaseColor;
import com.github.fracpete.javautils.enumerate.Enumerated;
import com.kennycason.kumo.palette.ColorPalette;

import java.awt.Color;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Generates a color palette using the specified colors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ColorListPalette
  extends AbstractColorPalette {

  private static final long serialVersionUID = -6953764652690047084L;

  /** the colors to use. */
  protected BaseColor[] m_Colors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a color palette using the specified colors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color", "colors",
      new BaseColor[0]);
  }

  /**
   * Sets the colors to create the palette from.
   *
   * @param value	the colors
   */
  public void setColors(BaseColor[] value) {
    m_Colors = value;
    reset();
  }

  /**
   * Returns the colors to create the palette from.
   *
   * @return		the colors
   */
  public BaseColor[] getColors() {
    return m_Colors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorsTipText() {
    return "The colors to create the palette from.";
  }

  /**
   * Generates the color palette.
   *
   * @param errors	for collecting errors
   * @return		the color palette, null if none generated
   */
  @Override
  public ColorPalette generate(MessageCollection errors) {
    Color[]	colors;

    if (m_Colors.length == 0) {
      errors.add("No colors defined!");
      return null;
    }

    colors = new Color[m_Colors.length];
    for (Enumerated<BaseColor> color: enumerate(m_Colors))
      colors[color.index] = color.value.toColorValue();

    return new ColorPalette(colors);
  }
}
