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
 * XuggleScreenRecorder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.video;

import adams.core.io.PlaceholderFile;
import com.github.fracpete.screencast4j.record.Recorder;

/**
 <!-- globalinfo-start -->
 * Configures the com.github.fracpete.screencast4j.record.screen.XuggleScreenRecorder screen recorder.
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
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;screen.mp4
 * </pre>
 * 
 * <pre>-frames-per-second &lt;int&gt; (property: framesPerSecond)
 * &nbsp;&nbsp;&nbsp;The frames per second to use for recording.
 * &nbsp;&nbsp;&nbsp;default: 25
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The X position on the screen (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The Y position on the screen (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the rectangle (-1 = remainder).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the rectangle (-1 = remainder).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-capture-mouse &lt;boolean&gt; (property: captureMouse)
 * &nbsp;&nbsp;&nbsp;If enabled, the mouse gets captured as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XuggleScreenRecorder
  extends AbstractScreenRecorder {

  private static final long serialVersionUID = 4647531406540415284L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures the " + com.github.fracpete.screencast4j.record.screen.XuggleScreenRecorder.class.getName() + " screen recorder.";
  }

  /**
   * Returns the default output file to use.
   *
   * @return		the default
   */
  @Override
  protected PlaceholderFile getDefaultOutput() {
    return new PlaceholderFile("${TMP}/screen.mp4");
  }

  /**
   * Returns a fully configured recorder instance.
   *
   * @return		the new instance
   */
  @Override
  protected Recorder doConfigure() {
    com.github.fracpete.screencast4j.record.screen.XuggleScreenRecorder	result;

    result = new com.github.fracpete.screencast4j.record.screen.XuggleScreenRecorder();
    result.setFramesPerSecond(m_FramesPerSecond);
    result.setX(m_X - 1);
    result.setY(m_Y - 1);
    result.setWidth(m_Width);
    result.setHeight(m_Height);
    result.setCaptureMouse(m_CaptureMouse);
    result.setOutput(m_Output.getAbsoluteFile());

    return result;
  }
}
