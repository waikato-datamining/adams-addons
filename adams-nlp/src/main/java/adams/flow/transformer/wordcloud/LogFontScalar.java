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
 * LogFontScalar.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wordcloud;

import adams.core.MessageCollection;
import com.kennycason.kumo.font.scale.FontScalar;

/**
 * Uses log scaling between the specified min and max.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LogFontScalar
  extends AbstractFontScalar {

  private static final long serialVersionUID = 8093648982408852841L;

  /** the minimum font size. */
  protected int m_Min;

  /** the maximum font size. */
  protected int m_Max;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses log scaling between the specified min and max.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min", "min",
      10, 1, null);

    m_OptionManager.add(
      "max", "max",
      40, 1, null);
  }

  /**
   * Sets the minimum font size.
   *
   * @param value	the minimum
   */
  public void setMin(int value) {
    m_Min = value;
    reset();
  }

  /**
   * Returns the minimum font size.
   *
   * @return		the minimum
   */
  public int getMin() {
    return m_Min;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minTipText() {
    return "The minimum font size.";
  }

  /**
   * Sets the maximum font size.
   *
   * @param value	the maximum
   */
  public void setMax(int value) {
    m_Max = value;
    reset();
  }

  /**
   * Returns the maximum font size.
   *
   * @return		the maximum
   */
  public int getMax() {
    return m_Max;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxTipText() {
    return "The maximum font size.";
  }

  /**
   * Generates the font scalar.
   *
   * @param errors 	for collecting errors
   * @return		the font scalar, null if none generated
   */
  @Override
  public FontScalar generate(MessageCollection errors) {
    return new com.kennycason.kumo.font.scale.LogFontScalar(m_Min, m_Max);
  }
}
