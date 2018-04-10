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
 * ConvertAudio.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.ffmpeg;

import adams.core.QuickInfoHelper;

/**
 * Converts audio files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ConvertAudio
  extends AbstractFFmpegPluginWithOptions {

  private static final long serialVersionUID = -385408477332933453L;

  /** the output encoder to use. */
  protected String m_Encoder;

  /** the bitrate to use. */
  protected int m_BitRate;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts audio files.\n"
      + "You can use the following commandline to check the available encoders:\n"
      + "ffmpeg -encoders";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "encoder", "encoder",
      "pcm_s16le");

    m_OptionManager.add(
      "bit-rate", "bitRate",
      44100, 1, null);
  }

  /**
   * Sets the encoder to use.
   *
   * @param value	the encoder
   */
  public void setEncoder(String value) {
    m_Encoder = value;
    reset();
  }

  /**
   * Returns the encoder to use.
   *
   * @return		the encoder
   */
  public String getEncoder() {
    return m_Encoder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encoderTipText() {
    return "The encoder to use for the output audio file.";
  }

  /**
   * Sets the bit rate to use.
   *
   * @param value	the bit rate
   */
  public void setBitRate(int value) {
    m_BitRate = value;
    reset();
  }

  /**
   * Returns the bit rate to use.
   *
   * @return		the bit rate
   */
  public int getBitRate() {
    return m_BitRate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bitRateTipText() {
    return "The bit rate to use for the output file.";
  }

  /**
   * Returns a quick info about the plugin, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "encoder", m_Encoder, ", encoder: ");
    result += QuickInfoHelper.toString(this, "bitRate", m_BitRate, ", bit-rate: ");

    return result;
  }

  /**
   * Assembles the actual input command-line options, not including the
   * additional options or the input file.
   *
   * @return		the command-line
   */
  @Override
  protected String assembleActualInputOptions() {
    return "";
  }

  /**
   * Assembles the actual output command-line options, not including the
   * additional options.
   *
   * @return		the command-line
   */
  @Override
  protected String assembleActualOutputOptions() {
    return "-acodec " + m_Encoder + " -ar " + m_BitRate;
  }
}
