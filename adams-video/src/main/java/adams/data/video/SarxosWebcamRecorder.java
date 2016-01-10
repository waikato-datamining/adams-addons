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
 * SarxosWebcamRecorder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.video;

import adams.core.io.PlaceholderFile;
import com.github.fracpete.screencast4j.record.Recorder;

import java.awt.Dimension;

/**
 <!-- globalinfo-start -->
 * Configures the com.github.fracpete.screencast4j.record.webcam.SarxosWebcamRecorder webcam recorder.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to store the recorded output in.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;webcam.mp4
 * </pre>
 * 
 * <pre>-frames-per-second &lt;int&gt; (property: framesPerSecond)
 * &nbsp;&nbsp;&nbsp;The frames per second to use for recording.
 * &nbsp;&nbsp;&nbsp;default: 25
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-id &lt;java.lang.String&gt; (property: ID)
 * &nbsp;&nbsp;&nbsp;The ID of the webcam to use (use empty string for the default one).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the webcam.
 * &nbsp;&nbsp;&nbsp;default: 320
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the webcam.
 * &nbsp;&nbsp;&nbsp;default: 240
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SarxosWebcamRecorder
  extends AbstractWebcamRecorder {

  private static final long serialVersionUID = -8595993588074135465L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures the " + com.github.fracpete.screencast4j.record.webcam.SarxosWebcamRecorder.class.getName() + " webcam recorder.";
  }

  /**
   * Returns the default output file to use.
   *
   * @return		the default
   */
  @Override
  protected PlaceholderFile getDefaultOutput() {
    return new PlaceholderFile("${TMP}/webcam.mp4");
  }

  /**
   * Returns a fully configured recorder instance.
   *
   * @return		the new instance
   */
  @Override
  protected Recorder doConfigure() {
    com.github.fracpete.screencast4j.record.webcam.SarxosWebcamRecorder	result;

    result = new com.github.fracpete.screencast4j.record.webcam.SarxosWebcamRecorder();
    result.setFramesPerSecond(m_FramesPerSecond);
    result.setWebcamID(m_ID);
    result.setSize(new Dimension(m_Width, m_Height));
    result.setOutput(m_Output.getAbsoluteFile());

    return result;
  }
}
