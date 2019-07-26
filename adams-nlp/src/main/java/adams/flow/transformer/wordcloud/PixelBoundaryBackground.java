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
 * PixelBoundaryBackground.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wordcloud;

import adams.core.MessageCollection;
import adams.core.io.PlaceholderFile;
import com.kennycason.kumo.bg.Background;

/**
 * Generates a background mode based on the transparent pixel-boundaries of the specified image.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PixelBoundaryBackground
  extends AbstractBackground {

  private static final long serialVersionUID = 2848272343570036328L;

  /** the background image. */
  protected PlaceholderFile m_Background;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a background mode based on the transparent pixel-boundaries of the specified image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "background", "background",
      new PlaceholderFile());
  }

  /**
   * Sets the background image to use.
   *
   * @param value	the image
   */
  public void setBackground(PlaceholderFile value) {
    m_Background = value;
    reset();
  }

  /**
   * Returns the background image to use.
   *
   * @return		the image
   */
  public PlaceholderFile getBackground() {
    return m_Background;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundTipText() {
    return "The background image to use.";
  }

  /**
   * Generates the background.
   *
   * @param errors 	for collecting errors
   * @return		the background, null if none generated
   */
  @Override
  public Background generate(MessageCollection errors) {
    if (!m_Background.exists()) {
      errors.add("Background image does not exist: " + m_Background);
      return null;
    }
    if (m_Background.isDirectory()) {
      errors.add("Background image points to a directory: " + m_Background);
      return null;
    }
    try {
      return new com.kennycason.kumo.bg.PixelBoundryBackground(m_Background.getAbsoluteFile());
    }
    catch (Exception e) {
      errors.add("Failed to construct the pixel boundary background!", e);
      return null;
    }
  }
}
