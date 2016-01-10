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
 * MultiRecorder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.video;

import com.github.fracpete.screencast4j.record.Recorder;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiRecorder
  extends AbstractRecorder {

  private static final long serialVersionUID = 7104230038440319177L;

  /** the recorders to use. */
  protected AbstractRecorder[] m_Recorders;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures the " + com.github.fracpete.screencast4j.record.MultiRecorder.class.getName() + " recorder.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "recorder", "recorders",
      new AbstractRecorder[0]);
  }

  /**
   * Sets the recorders to use.
   *
   * @param value	the recorders
   */
  public void setRecorders(AbstractRecorder[] value) {
    m_Recorders = value;
    reset();
  }

  /**
   * Returns the recorders to use.
   *
   * @return		the recorders
   */
  public AbstractRecorder[] getFramesPerSecond() {
    return m_Recorders;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String recordersTipText() {
    return "The recorders to use.";
  }

  /**
   * Returns a fully configured recorder instance.
   *
   * @return		the new instance
   */
  @Override
  protected Recorder doConfigure() {
    com.github.fracpete.screencast4j.record.MultiRecorder	result;
    List<Recorder>						recs;
    int								i;

    result = new com.github.fracpete.screencast4j.record.MultiRecorder();
    recs   = new ArrayList<>();
    for (i = 0; i < m_Recorders.length; i++) {
      try {
	recs.add(m_Recorders[i].configure());
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to configure recorder #" + (i+1) + "!", e);
      }
    }
    result.setRecorders(recs.toArray(new Recorder[recs.size()]));

    return result;
  }
}
