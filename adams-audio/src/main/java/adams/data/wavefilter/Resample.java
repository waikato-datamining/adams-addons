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
 * Resample.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.data.wavefilter;

import adams.core.QuickInfoHelper;
import adams.data.audio.WaveContainer;
import com.musicg.dsp.Resampler;
import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;

/**
 <!-- globalinfo-start -->
 * Adjusts the data to use the target sample rate.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-target-sample-rate &lt;int&gt; (property: targetSampleRate)
 * &nbsp;&nbsp;&nbsp;The new sample rate to use.
 * &nbsp;&nbsp;&nbsp;default: 44100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Resample
  extends AbstractWaveFilter {

  /** for serialization. */
  private static final long serialVersionUID = 2319957467336388607L;

  /** the target sample rate. */
  protected int m_TargetSampleRate;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adjusts the data to use the target sample rate.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "target-sample-rate", "targetSampleRate",
      44100, 1, null);
  }

  /**
   * Sets the target sample rate to use.
   *
   * @param value	the sample rate
   */
  public void setTargetSampleRate(int value) {
    m_TargetSampleRate = value;
    reset();
  }

  /**
   * Returns the target sample to use.
   *
   * @return		the sample rate
   */
  public int getTargetSampleRate() {
    return m_TargetSampleRate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String targetSampleRateTipText() {
    return "The new sample rate to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "targetSampleRate", m_TargetSampleRate, "sample rate: ");
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected WaveContainer processData(WaveContainer data) {
    WaveContainer 	result;
    WaveHeader 		headerOld;
    WaveHeader 		headerNew;
    byte[] 		bytesOld;
    byte[] 		bytesNew;
    Wave		waveNew;
    Resampler		resampler;

    headerOld = data.getAudio().getWaveHeader();
    bytesOld  = data.getAudio().getBytes();
    resampler = new Resampler();
    bytesNew  = resampler.reSample(bytesOld, headerOld.getBitsPerSample(), headerOld.getSampleRate(), m_TargetSampleRate);
    headerNew = headerOld.clone();
    headerNew.setSampleRate(m_TargetSampleRate);
    waveNew   = new Wave(headerNew, bytesNew);
    result    = (WaveContainer) data.getHeader();
    result.setAudio(waveNew);

    return result;
  }
}
