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
 * FixedDuration.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.audiorecorder;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.RunnableWithLogging;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;

/**
 * Records a WAV file of fixed duration.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FixedDuration
  extends AbstractFileBasedAudioRecorder {

  private static final long serialVersionUID = 8545013784919517494L;

  /** the duration to record. */
  protected int m_Duration;

  /** the recording worker thread. */
  protected RunnableWithLogging m_Recording;

  /** the monitor thread. */
  protected RunnableWithLogging m_Monitor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Records a WAV file of fixed duration. The WAV file name is used as output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "duration", "duration",
      1, 1, null);
  }

  /**
   * Returns the default output file.
   *
   * @return		the default
   */
  @Override
  protected PlaceholderFile getDefaultOutputFile() {
    return new PlaceholderFile("${TMP}/out.wav");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The WAV file to record the audio data to.";
  }

  /**
   * Sets the duration of the recording.
   *
   * @param value	the duration in sec
   */
  public void setDuration(int value) {
    if (getOptionManager().isValid("duration", value)) {
      m_Duration = value;
      reset();
    }
  }

  /**
   * Returns the duration of the recording.
   *
   * @return		the duration in sec
   */
  public int getDuration() {
    return m_Duration;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String durationTipText() {
    return "The duration to record in seconds.";
  }

  /**
   * Returns whether flow context is actually required.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresFlowContext() {
    return false;
  }

  /**
   * Returns worker runnable for recording audio.
   *
   * @param info 	the line info
   * @param format	the format to record in
   * @return		the runnable
   */
  protected RunnableWithLogging getRecordingWorker(final DataLine.Info info, final AudioFormat format) {
    return getRecordingWorker(info, format, Type.WAVE);
  }

  /**
   * Records the audio.
   *
   * @param output	the file to record to
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRecordTo(PlaceholderFile output) {
    AudioFormat		format;
    DataLine.Info	info;
    MessageCollection	errors;

    errors = new MessageCollection();
    format = getAudioFormat();
    info   = getDataLineInfo(format, errors);
    if (!errors.isEmpty())
      return errors.toString();

    m_Monitor = new RunnableWithLogging() {
      @Override
      protected void doRun() {
	long start = 0;
	long current;
	while (!isStopped()) {
	  if (m_Recording == null) {
	    m_Recording = getRecordingWorker(info, format);
	    start = System.currentTimeMillis();
	    new Thread(m_Recording).start();
	  }
	  current = System.currentTimeMillis();
	  if ((current - start) / 1000 >= m_Duration) {
	    if (!isStopped()) {
	      m_Recording.stopExecution();
	      m_Recording = null;
	      break;
	    }
	  }
	}
	m_Monitor = null;
      }
      @Override
      public void stopExecution() {
        if (m_Recording != null)
          m_Recording.stopExecution();
	super.stopExecution();
      }
    };
    new Thread(m_Monitor).start();

    while (!isStopped() && (m_Monitor != null))
      Utils.wait(this, 1000, 100);

    return null;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    if (m_Monitor != null)
      m_Monitor.stopExecution();
    super.stopExecution();
  }
}

