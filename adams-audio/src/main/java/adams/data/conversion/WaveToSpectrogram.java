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
 * WaveToSpectrogram.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.audio.WaveContainer;
import com.musicg.wave.extension.Spectrogram;

/**
 * Generates a spectrogram from the incoming Wave container.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WaveToSpectrogram
  extends AbstractConversion {

  private static final long serialVersionUID = -4549516743908263051L;

  /** the FFT sample size (power of 2). */
  protected int m_FFTSampleSize;

  /** the overlap factor (1/factor). */
  protected int m_OverlapFactor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a spectrogram from the incoming Wave container.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "fft-sample-size", "FFTSampleSize",
      1024, 2, null);

    m_OptionManager.add(
      "overlap-factor", "overlapFactor",
      0, 0, null);
  }

  /**
   * Sets the FFT sample size (power of 2).
   *
   * @param value	the sample size
   */
  public void setFFTSampleSize(int value) {
    if (getOptionManager().isValid("FFTSampleSize", value) && (Integer.bitCount(value) == 1)) {
      m_FFTSampleSize = value;
      reset();
    }
  }

  /**
   * Returns the FFT samepl size (power of 2).
   *
   * @return		the sample size
   */
  public int getFFTSampleSize() {
    return m_FFTSampleSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String FFTSampleSizeTipText() {
    return "The sample size for the fast fourier transformation; must be a power of 2.";
  }

  /**
   * Sets the overlap factor (1/factor).
   *
   * @param value	the factor
   */
  public void setOverlapFactor(int value) {
    if (getOptionManager().isValid("overlapFactor", value)) {
      m_OverlapFactor = value;
      reset();
    }
  }

  /**
   * Returns the overlap factor (1/factor).
   *
   * @return		the factor
   */
  public int getOverlapFactor() {
    return m_OverlapFactor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlapFactorTipText() {
    return "The overlap factor (1/factor), eg 4 = 1/4 = 25%; 0 = no overlap.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "FFTSampleSize", m_FFTSampleSize, "sample size: ");
    result += QuickInfoHelper.toString(this, "overlapFactor", (m_OverlapFactor == 0 ? "-none-" : "1/" + m_OverlapFactor), ", overlap factor: ");

    return result;
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
    return Spectrogram.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    return new com.musicg.wave.extension.Spectrogram(
      ((WaveContainer) m_Input).getAudio(), m_FFTSampleSize, m_OverlapFactor);
  }
}
