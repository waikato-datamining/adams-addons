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
 * AbstractAudioRecorder.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.audiorecorder;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.StoppableWithFeedback;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * Ancestor for classes that record audio.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data that the recorder generates
 */
public abstract class AbstractAudioRecorder<T>
  extends AbstractOptionHandler
  implements QuickInfoSupporter, FlowContextHandler, StoppableWithFeedback {

  private static final long serialVersionUID = -5670995444597510384L;

  /** the sample rate. */
  protected float m_SampleRate;

  /** the sample size in bits. */
  protected int m_Bits;

  /** the number of channels. */
  protected int m_Channels;

  /** whether signed or unsigned. */
  protected boolean m_Signed;

  /** whether big endian or little endian. */
  protected boolean m_BigEndian;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** whether recording has been stopped. */
  protected boolean m_Stopped;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "sample-rate", "sampleRate",
      44100.0f, 1f, null);

    m_OptionManager.add(
      "bits", "bits",
      16, 1, null);

    m_OptionManager.add(
      "channels", "channels",
      1, 1, null);

    m_OptionManager.add(
      "signed", "signed",
      true);

    m_OptionManager.add(
      "big-endian", "bigEndian",
      true);
  }

  /**
   * Sets the flow context.
   *
   * @param value the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns whether flow context is actually required.
   *
   * @return true if required
   */
  public abstract boolean requiresFlowContext();

  /**
   * Hook method for performing checks before attempting to generate the setups.
   *
   * @return null if successful, otherwise error message
   */
  protected String check() {
    if (requiresFlowContext()) {
      if (m_FlowContext == null)
	return "No flow context set!";
    }
    return null;
  }

  /**
   * Sets the sample rate to use (hertz).
   *
   * @param value the sample rate
   */
  public void setSampleRate(float value) {
    if (getOptionManager().isValid("sampleRate", value)) {
      m_SampleRate = value;
      reset();
    }
  }

  /**
   * Returns the sample rate to use (hertz).
   *
   * @return the reader
   */
  public float getSampleRate() {
    return m_SampleRate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String sampleRateTipText() {
    return "The sample rate (hertz) to use when recording.";
  }

  /**
   * Sets the sample size in bits.
   *
   * @param value the number of bits
   */
  public void setBits(int value) {
    if (getOptionManager().isValid("bits", value)) {
      m_Bits = value;
      reset();
    }
  }

  /**
   * Returns the sample size in bits.
   *
   * @return the number of bits
   */
  public int getBits() {
    return m_Bits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String bitsTipText() {
    return "The sample size in bits.";
  }

  /**
   * Sets the number of channels to record.
   *
   * @param value the channels
   */
  public void setChannels(int value) {
    if (getOptionManager().isValid("channels", value)) {
      m_Channels = value;
      reset();
    }
  }

  /**
   * Returns the number of channels to record.
   *
   * @return the channels
   */
  public int getChannels() {
    return m_Channels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String channelsTipText() {
    return "The number of channels to record.";
  }

  /**
   * Sets whether to use signed data.
   *
   * @param value true if signed data
   */
  public void setSigned(boolean value) {
    m_Signed = value;
    reset();
  }

  /**
   * Returns whether to use signed data.
   *
   * @return true if signed data
   */
  public boolean getSigned() {
    return m_Signed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String signedTipText() {
    return "Whether to record signed/unsigned data.";
  }

  /**
   * Sets whether to use big endian.
   *
   * @param value true if big endian
   */
  public void setBigEndian(boolean value) {
    m_BigEndian = value;
    reset();
  }

  /**
   * Returns whether to use big endian.
   *
   * @return true if big endian
   */
  public boolean getBigEndian() {
    return m_BigEndian;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String bigEndianTipText() {
    return "Whether to use big or little endian.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String result;

    result = QuickInfoHelper.toString(this, "sampleRate", m_SampleRate, "rate: ");
    result += QuickInfoHelper.toString(this, "bits", m_Bits, ", bits: ");
    result += QuickInfoHelper.toString(this, "channels", m_Channels, ", channels: ");

    return result;
  }

  /**
   * Returns the type of data that it outputs.
   *
   * @return the data type
   */
  public abstract Class generates();

  /**
   * Returns the audio format based on the options.
   *
   * @return the audio format
   */
  protected AudioFormat getAudioFormat() {
    return new AudioFormat(m_SampleRate, m_Bits, m_Channels, m_Signed, m_BigEndian);
  }

  /**
   * Returns the data line info. Checks whether the format is supported.
   *
   * @param format	the format to get the info for
   * @param errors	for storing error messages, like line is not supported
   * @return		the info object
   */
  protected DataLine.Info getDataLineInfo(AudioFormat format, MessageCollection errors) {
    DataLine.Info	result;

    result = new DataLine.Info(TargetDataLine.class, format);
    if (!AudioSystem.isLineSupported(result))
      errors.add("Line is not supported!");

    return result;
  }

  /**
   * Records the audio.
   *
   * @return		the generated data
   */
  protected abstract T doRecord();

  /**
   * Records the audio.
   *
   * @return		the generated data
   */
  public T record() {
    String	msg;

    m_Stopped = false;
    msg       = check();
    if (msg != null)
      throw new IllegalStateException(msg);

    return doRecord();
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }
}
