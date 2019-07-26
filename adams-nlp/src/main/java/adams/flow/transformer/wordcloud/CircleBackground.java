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
 * CircleBackground.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wordcloud;

import adams.core.MessageCollection;
import com.kennycason.kumo.bg.Background;

/**
 * Generates a circular background.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CircleBackground
  extends AbstractBackground {

  private static final long serialVersionUID = 2848272343570036328L;

  /** the radius. */
  protected int m_Radius;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a circular background.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "radius", "radius",
      300, 1, null);
  }

  /**
   * Sets the radius for the background.
   *
   * @param value	the radius
   */
  public void setRadius(int value) {
    if (getOptionManager().isValid("radius", value)) {
      m_Radius = value;
      reset();
    }
  }

  /**
   * Returns the radius for the background.
   *
   * @return		the radius
   */
  public int getRadius() {
    return m_Radius;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String radiusTipText() {
    return "The radius for the background.";
  }

  /**
   * Generates the background.
   *
   * @param errors 	for collecting errors
   * @return		the background, null if none generated
   */
  @Override
  public Background generate(MessageCollection errors) {
    return new com.kennycason.kumo.bg.CircleBackground(m_Radius);
  }
}
