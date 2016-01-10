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
 * SampledSoundRecorder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.video;

import com.github.fracpete.screencast4j.record.Recorder;

/**
 <!-- globalinfo-start -->
 * Configures the com.github.fracpete.screencast4j.record.sound.SampledSoundRecorder sound recorder.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-frequency &lt;float&gt; (property: frequency)
 * &nbsp;&nbsp;&nbsp;The frequency to use.
 * &nbsp;&nbsp;&nbsp;default: 44100.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SampledSoundRecorder
  extends AbstractSoundRecorder {

  private static final long serialVersionUID = 7104230038440319177L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures the " + com.github.fracpete.screencast4j.record.sound.SampledSoundRecorder.class.getName() + " sound recorder.";
  }

  /**
   * Returns a fully configured recorder instance.
   *
   * @return		the new instance
   */
  @Override
  protected Recorder doConfigure() {
    com.github.fracpete.screencast4j.record.sound.SampledSoundRecorder	result;

    result = new com.github.fracpete.screencast4j.record.sound.SampledSoundRecorder();
    result.setFrequency(m_Frequency);

    return result;
  }
}
