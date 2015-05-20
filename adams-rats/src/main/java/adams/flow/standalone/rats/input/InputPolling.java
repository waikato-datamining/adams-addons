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
 * InputPolling.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;


/**
 <!-- globalinfo-start -->
 * Turns the base-input into one that performs polling every x msec.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-input &lt;adams.flow.standalone.rats.RatInput&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The receiver to wrap.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.DummyInput
 * </pre>
 * 
 * <pre>-wait-poll &lt;int&gt; (property: waitPoll)
 * &nbsp;&nbsp;&nbsp;The number of milli-seconds to wait before polling again.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InputPolling
  extends AbstractMetaRatInput
  implements PollingRatInput {

  /** for serialization. */
  private static final long serialVersionUID = -6499152819821767430L;
  
  /** the waiting period in msec before polling again. */
  protected int m_WaitPoll;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the base-input into one that performs polling every x msec.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "wait-poll", "waitPoll",
	    1000, 0, null);
  }

  /**
   * Sets the number of milli-seconds to wait before polling.
   *
   * @param value	the number of milli-seconds
   */
  public void setWaitPoll(int value) {
    if (value >= 0) {
      m_WaitPoll = value;
      reset();
    }
    else {
      getLogger().warning("Number of milli-seconds to wait must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the number of milli-seconds to wait before polling again.
   *
   * @return		the number of milli-seconds
   */
  public int getWaitPoll() {
    return m_WaitPoll;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitPollTipText() {
    return "The number of milli-seconds to wait before polling again.";
  }

  /**
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  @Override
  public Class generates() {
    return m_Input.generates();
  }
}
