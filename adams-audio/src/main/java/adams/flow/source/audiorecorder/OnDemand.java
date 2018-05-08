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
 * OnDemand.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.audiorecorder;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.RunnableWithLogging;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;

/**
 * Records a WAV file from when the user starts recording to when the user ends it.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class OnDemand
  extends AbstractFileBasedAudioRecorder {

  private static final long serialVersionUID = 8545013784919517494L;

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
    return "Records a WAV file from when the user starts recording to when the user ends it.";
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
   * Returns whether flow context is actually required.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresFlowContext() {
    return true;
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
   * Hook method for performing checks before attempting to generate the setups.
   *
   * @return null if successful, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null) {
      if (getFlowContext().isHeadless())
        result = "Cannot operate in headless environment!";
    }

    return result;
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
    int			retVal;

    errors = new MessageCollection();
    format = getAudioFormat();
    info   = getDataLineInfo(format, errors);
    if (!errors.isEmpty())
      return errors.toString();

    // wait for user to start
    retVal = GUIHelper.showConfirmMessage(getFlowContext().getParentComponent(), "Start recording");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return "User canceled dialog!";

    m_Monitor = new RunnableWithLogging() {
      @Override
      protected void doRun() {
	m_Recording = getRecordingWorker(info, format);
	new Thread(m_Recording).start();
	while (!isStopped())
	  Utils.wait(this, 1000, 100);
	m_Recording = null;
	m_Monitor   = null;
      }
      @Override
      public void stopExecution() {
        if (m_Recording != null)
          m_Recording.stopExecution();
	super.stopExecution();
      }
    };
    new Thread(m_Monitor).start();

    // wait for user to stop
    do {
      retVal = GUIHelper.showConfirmMessage(getFlowContext().getParentComponent(), "Stop recording");
    }
    while (retVal != ApprovalDialog.APPROVE_OPTION);
    m_Monitor.stopExecution();

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

