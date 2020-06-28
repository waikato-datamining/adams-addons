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
 * Copyright (C) 2018-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import javazoom.jl.player.Player;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import java.io.FileInputStream;

/**
 <!-- globalinfo-start -->
 * Plays back an audio file (MP3&#47;WAV).
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AudioPlayback
  extends AbstractSink
  implements LineListener {

  private static final long serialVersionUID = -1056104741820669736L;

  /** whether the playback has finished. */
  protected boolean m_PlaybackFinished;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Plays back an audio file (MP3/WAV).";
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
    FileInputStream 	fis;
    Player 		player;

    result             = null;
    file               = new PlaceholderFile(m_InputToken.getPayload(String.class));
    clip               = null;
    m_PlaybackFinished = false;

    // mp3
    if (file.getName().toLowerCase().endsWith(".mp3")) {
      fis = null;
      try {
	fis    = new FileInputStream(file.getAbsoluteFile());
	player = new Player(fis);
	player.play();
        while (!isStopped() && !m_PlaybackFinished && !player.isComplete())
          Utils.wait(this, 1000, 100);
      }
      catch (Exception e) {
        result = handleException("Failed to playback file: " + file, e);
      }
      finally {
	FileUtils.closeQuietly(fis);
      }
    }
    // wav, etc
    else {
      try {
        ais = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
        format = ais.getFormat();
        info = new DataLine.Info(Clip.class, format);
        clip = (Clip) AudioSystem.getLine(info);
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
