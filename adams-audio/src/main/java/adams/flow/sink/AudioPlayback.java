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
 * AudioPlayback.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

/**
 <!-- globalinfo-start -->
 * Plays back an audio file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: AudioPlayback
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-start &lt;double&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;The starting position for the playback in seconds.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AudioPlayback
  extends AbstractSink
  implements LineListener {

  private static final long serialVersionUID = -1056104741820669736L;

  /** the starting position in seconds. */
  protected double m_Start;

  /** whether the playback has finished. */
  protected boolean m_PlaybackFinished;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Plays back an audio file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "start", "start",
      0.0, 0.0, null);
  }

  /**
   * Sets the starting position in seconds.
   *
   * @param value	the start
   */
  public void setStart(double value) {
    if (getOptionManager().isValid("start", value)) {
      m_Start = value;
      reset();
    }
  }

  /**
   * Returns the starting position in seconds.
   *
   * @return		the start
   */
  public double getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "The starting position for the playback in seconds.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "start", m_Start, "start: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile	file;
    AudioInputStream 	ais;
    AudioFormat 	format;
    DataLine.Info 	info;
    Clip 		clip;

    result             = null;
    file               = new PlaceholderFile(m_InputToken.getPayload(String.class));
    clip               = null;
    m_PlaybackFinished = false;
    try {
      ais    = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
      format = ais.getFormat();
      info   = new DataLine.Info(Clip.class, format);
      clip   = (Clip) AudioSystem.getLine(info);
      clip.addLineListener(this);
      clip.open(ais);
      clip.start();
      while (!isStopped() && !m_PlaybackFinished)
	Utils.wait(this, 1000, 100);
    }
    catch (Exception e) {
      result = handleException("Failed to playback file: " + file, e);
    }
    finally {
      if (clip != null) {
	clip.removeLineListener(this);
	clip.close();
      }
    }

    return result;
  }

  /**
   * Informs the listener that a line's state has changed. The listener can
   * then invoke {@code LineEvent} methods to obtain information about the
   * event.
   *
   * @param  event a line event that describes the change
   */
  public void update(LineEvent event) {
    if (isLoggingEnabled())
      getLogger().info("Line event: " + event.getType());
    if (event.getType() == Type.STOP)
      m_PlaybackFinished = true;
  }
}
