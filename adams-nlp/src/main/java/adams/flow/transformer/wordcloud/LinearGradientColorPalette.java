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
 * LinearGradientColorPalette.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wordcloud;

import adams.core.MessageCollection;
import adams.core.base.BaseColor;
import com.kennycason.kumo.palette.ColorPalette;

import java.awt.Color;

/**
 * Generates a linear gradient between the two colors or three colors if the second steps is >0.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LinearGradientColorPalette
  extends AbstractColorPalette {

  private static final long serialVersionUID = -918453046276035417L;

  /** the first color. */
  protected BaseColor m_First;

  /** the second color. */
  protected BaseColor m_Second;

  /** the third color. */
  protected BaseColor m_Third;

  /** the number of steps to use between first/second color. */
  protected int m_FirstSteps;

  /** the number of steps to use between second/third color. */
  protected int m_SecondSteps;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a linear gradient between the two colors or three colors if the second steps is >0.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "first", "first",
      new BaseColor(Color.RED));

    m_OptionManager.add(
      "second", "second",
      new BaseColor(Color.BLUE));

    m_OptionManager.add(
      "third", "third",
      new BaseColor(Color.GREEN));

    m_OptionManager.add(
      "first-steps", "firstSteps",
      30, 1, null);

    m_OptionManager.add(
      "second-steps", "secondSteps",
      0, 0, null);
  }

  /**
   * Sets the first color.
   *
   * @param value	the color
   */
  public void setFirst(BaseColor value) {
    m_First = value;
    reset();
  }

  /**
   * Returns the first color.
   *
   * @return		the color
   */
  public BaseColor getFirst() {
    return m_First;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstTipText() {
    return "The first color.";
  }

  /**
   * Sets the second color.
   *
   * @param value	the color
   */
  public void setSecond(BaseColor value) {
    m_Second = value;
    reset();
  }

  /**
   * Returns the second color.
   *
   * @return		the color
   */
  public BaseColor getSecond() {
    return m_Second;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String secondTipText() {
    return "The second color.";
  }

  /**
   * Sets the third color.
   *
   * @param value	the color
   */
  public void setThird(BaseColor value) {
    m_Third = value;
    reset();
  }

  /**
   * Returns the third color.
   *
   * @return		the color
   */
  public BaseColor getThird() {
    return m_Third;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thirdTipText() {
    return "The third color.";
  }

  /**
   * Sets the number of steps between first/second color.
   *
   * @param value	the steps
   */
  public void setFirstSteps(int value) {
    if (getOptionManager().isValid("firstSteps", value)) {
      m_FirstSteps = value;
      reset();
    }
  }

  /**
   * Returns the number of steps between first/second color.
   *
   * @return		the steps
   */
  public int getFirstSteps() {
    return m_FirstSteps;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstStepsTipText() {
    return "The number of steps to use between first and second color.";
  }

  /**
   * Sets the number of steps between second/third color.
   *
   * @param value	the steps
   */
  public void setSecondSteps(int value) {
    if (getOptionManager().isValid("secondSteps", value)) {
      m_SecondSteps = value;
      reset();
    }
  }

  /**
   * Returns the number of steps between second/third color.
   *
   * @return		the steps
   */
  public int getSecondSteps() {
    return m_SecondSteps;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String secondStepsTipText() {
    return "The number of steps to use between second and third color, ignored if 0.";
  }

  /**
   * Generates the color palette.
   *
   * @param errors	for collecting errors
   * @return		the color palette, null if none generated
   */
  @Override
  public ColorPalette generate(MessageCollection errors) {
    if (m_SecondSteps > 0)
      return new com.kennycason.kumo.palette.LinearGradientColorPalette(m_First.toColorValue(), m_Second.toColorValue(), m_Third.toColorValue(), m_FirstSteps, m_SecondSteps);
    else
      return new com.kennycason.kumo.palette.LinearGradientColorPalette(m_First.toColorValue(), m_Second.toColorValue(), m_FirstSteps);
  }
}
