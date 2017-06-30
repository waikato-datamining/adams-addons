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
 * MaxTime.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.trainstopcriterion;

import adams.flow.container.DL4JModelContainer;

/**
 <!-- globalinfo-start -->
 * Defines a maximum number of seconds for training.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-max-seconds &lt;int&gt; (property: maxSeconds)
 * &nbsp;&nbsp;&nbsp;The maximum number of seconds to train.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MaxTime
  extends AbstractTrainStopCriterion {

  private static final long serialVersionUID = 6975594226423139162L;

  /** the maximum number of seconds. */
  protected int m_MaxSeconds;

  /** the start time. */
  protected long m_Start;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines a maximum number of seconds for training.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-seconds", "maxSeconds",
      1000, 1, null);
  }

  /**
   * Sets the number of seconds.
   *
   * @param value	the seconds
   */
  public void setMaxSeconds(int value) {
    m_MaxSeconds = value;
    reset();
  }

  /**
   * Returns the number of seconds.
   *
   * @return  		the seconds
   */
  public int getMaxSeconds() {
    return m_MaxSeconds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxSecondsTipText() {
    return "The maximum number of seconds to train.";
  }

  /**
   * For initializing/reseting the scheme.
   */
  public void start() {
    super.start();
    m_Start = System.currentTimeMillis();
  }

  /**
   * Returns whether a flow context is required or optional.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresFlowContext() {
    return false;
  }

  /**
   * Performs the actual checking for stopping the training.
   *
   * @param cont	the container to use for stopping
   * @return		true if to stop training
   */
  @Override
  protected boolean doCheckStopping(DL4JModelContainer cont) {
    return (m_Start + m_MaxSeconds*1000 >= System.currentTimeMillis());
  }
}
