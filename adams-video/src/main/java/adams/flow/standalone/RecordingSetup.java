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
 * RecordingSetup.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.data.video.AbstractRecorder;
import adams.data.video.XuggleScreenRecorder;
import com.github.fracpete.screencast4j.record.Recorder;

import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Defines a recording setup for sound and video.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: RecordingSetup
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-recorder &lt;adams.data.video.AbstractRecorder&gt; (property: recorder)
 * &nbsp;&nbsp;&nbsp;The recorder setup to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.video.XuggleScreenRecorder
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RecordingSetup
  extends AbstractStandalone {

  private static final long serialVersionUID = -7820311755817280084L;

  /** the key for storing the recorder in the backup. */
  public final static String BACKUP_RECORDER = "recorder";

  /** the recording setup to use. */
  protected AbstractRecorder m_Recorder;

  /** the configured recorder. */
  protected transient Recorder m_ActualRecorder;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines a recording setup for sound and video.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "recorder", "recorder",
      new XuggleScreenRecorder());
  }

  /**
   * Sets the recorder setup to use.
   *
   * @param value	the recorder
   */
  public void setRecorder(AbstractRecorder value) {
    m_Recorder = value;
    reset();
  }

  /**
   * Returns the recorder setup to use.
   *
   * @return		the recorder
   */
  public AbstractRecorder getRecorder() {
    return m_Recorder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String recorderTipText() {
    return "The recorder setup to use.";
  }

  /**
   * Removes entries from the backup.
   *
   * @see		#reset()
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_RECORDER);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_ActualRecorder != null)
      result.put(BACKUP_RECORDER, m_ActualRecorder);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_RECORDER)) {
      m_ActualRecorder = (Recorder) state.get(BACKUP_RECORDER);
      state.remove(BACKUP_RECORDER);
    }

    super.restoreState(state);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    try {
      m_ActualRecorder = m_Recorder.configure();
    }
    catch (Exception e) {
      result = handleException("Failed to initialize recording setup:", e);
    }

    return result;
  }

  /**
   * Starts the recording.
   */
  public void startRecording() {
    if (m_ActualRecorder != null)
      m_ActualRecorder.start();
  }

  /**
   * Stops the recording.
   */
  public void stopRecording() {
    if (m_ActualRecorder != null) {
      m_ActualRecorder.stop();
      m_ActualRecorder.cleanUp();
      m_ActualRecorder = null;
    }
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    stopRecording();
    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    stopRecording();
    super.wrapUp();
  }
}
