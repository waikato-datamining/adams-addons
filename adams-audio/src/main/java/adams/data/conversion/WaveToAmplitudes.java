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
 * WaveToAmplitudes.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.audio.WaveContainer;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Extracts the amplitudes from a Wave object.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WaveToAmplitudes
  extends AbstractConversion {

  private static final long serialVersionUID = -4549516743908263051L;

  /** whether to return normalized amplitudes. */
  protected boolean m_Normalized;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Extracts the amplitudes from a Wave object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "normalized", "normalized",
      false);
  }

  /**
   * Sets whether to extract normalized or absolute amplitudes.
   *
   * @param value	true for normalized
   */
  public void setNormalized(boolean value) {
    m_Normalized = value;
    reset();
  }

  /**
   * Returns whether to extract normalized or absolute amplitudes.
   *
   * @return		true for normalized
   */
  public boolean getNormalized() {
    return m_Normalized;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String normalizedTipText() {
    return "If enabled, normalized amplitudes are returned rather than absolute ones.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "normalized", (m_Normalized ? "normalized" : "absolute"));
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return WaveContainer.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return double[].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    double[]		result;
    WaveContainer	cont;
    TDoubleList		list;

    cont = (WaveContainer) m_Input;
    if (m_Normalized) {
      result = cont.getAudio().getNormalizedAmplitudes();
    }
    else {
      list = new TDoubleArrayList();
      for (short s: cont.getAudio().getSampleAmplitudes())
        list.add(s);
      result = list.toArray();
    }

    return result;
  }
}
