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
 * AbstractSoundRecorder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.video;

/**
 * Ancestor for sound recorders.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSoundRecorder
  extends AbstractRecorder {

  private static final long serialVersionUID = -4437891144873073171L;

  /** the frequency to use. */
  protected float m_Frequency;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "frequency", "frequency",
      44100.0f, 1.0f, null);
  }

  /**
   * Sets the frequency.
   *
   * @param value	the frequency
   */
  public void setFrequency(float value) {
    if (getOptionManager().isValid("frequency", value)) {
      m_Frequency = value;
      reset();
    }
  }

  /**
   * Returns the frequency.
   *
   * @return		the frequency
   */
  public float getFrequency() {
    return m_Frequency;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String frequencyTipText() {
    return "The frequency to use.";
  }
}