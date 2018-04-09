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
 * Fingerprint.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.audiofeaturegenerator.wave;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.data.audio.WaveContainer;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.report.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a spectrogram from the audio data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Spectrogram
  extends AbstractWaveFeatureGenerator
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 1096079057750734103L;

  /** the FFT sample size (power of 2). */
  protected int m_FFTSampleSize;

  /** the overlap factor (1/factor). */
  protected int m_OverlapFactor;

  /** whether to return the normalized spectrogram. */
  protected boolean m_Normalized;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a spectrogram from the audio data.";
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

    m_OptionManager.add(
      "normalized", "normalized",
      false);
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
   * Sets whether to use normalized or absolute spectrogram.
   *
   * @param value	true if to use normalized spectrogram
   */
  public void setNormalized(boolean value) {
    m_Normalized = value;
    reset();
  }

  /**
   * Returns whether to use normalized or absolute spectrogram.
   *
   * @return		true if to use normalized spectrogram
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
    return "If enabled, the normalized spectrogram is returned rather than the absolute one.";
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
    result += QuickInfoHelper.toString(this, "normalized", (m_Normalized ? "normalized" : "absolute"), ", ");

    return result;
  }

  /**
   * Creates the header from a template container.
   *
   * @param cont	the container to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(WaveContainer cont) {
    HeaderDefinition	result;
    int			i;

    result = new HeaderDefinition();
    result.add("Frame", DataType.NUMERIC);
    result.add("Timestamp (seconds)", DataType.NUMERIC);
    // FFT halves the number of points and then magnitude of complex number is computed
    // hence "/ 4"
    for (i = 0; i < m_FFTSampleSize / 4; i++)
      result.add("SPG-" + (i+1), DataType.NUMERIC);

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param cont	the container to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(WaveContainer cont) {
    List<Object>[]				result;
    int						i;
    int						n;
    com.musicg.wave.extension.Spectrogram	sp;
    double[][]					data;

    sp     = new com.musicg.wave.extension.Spectrogram(cont.getAudio(), m_FFTSampleSize, m_OverlapFactor);
    result = new List[sp.getNumFrames()];
    if (m_Normalized)
      data = sp.getNormalizedSpectrogramData();
    else
      data = sp.getAbsoluteSpectrogramData();
    for (i = 0; i < sp.getNumFrames(); i++) {
      result[i] = new ArrayList<>();
      result[i].add((double) i);
      result[i].add((i * 2.0) / sp.getUnitFrequency());   // not sure why "*2" is necessary
      for (n = 0; n < data[i].length; n++)
        result[i].add(data[i][n]);
    }

    return result;
  }
}
