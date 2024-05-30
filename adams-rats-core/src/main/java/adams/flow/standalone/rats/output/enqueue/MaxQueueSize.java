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
 * MaxQueueSize.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.output.enqueue;

import adams.core.Utils;
import adams.flow.control.StorageQueueHandler;

/**
 * Ensures that the queue doesn't exceed the specified size; waits till the queue can accept data again.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MaxQueueSize
  extends AbstractEnqueueGuard {

  private static final long serialVersionUID = 1179005015402094837L;

  /** the limit for the queue. */
  protected int m_Limit;

  /** the time in msec to wait between checks. */
  protected int m_Wait;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Ensures that the queue doesn't exceed the specified size; waits till the queue can accept data again.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "limit", "limit",
      -1, -1, null);

    m_OptionManager.add(
      "wait", "wait",
      100, 1, null);
  }

  /**
   * Sets the maximum allowed queue size.
   *
   * @param value	the limit, <1 for unlimited
   */
  public void setLimit(int value) {
    m_Limit = value;
    reset();
  }

  /**
   * Returns the maximum allowed queue size.
   *
   * @return		the limit <0 for unlimited
   */
  public int getLimit() {
    return m_Limit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String limitTipText() {
    return "The maximum allowed queue size.";
  }

  /**
   * Sets the time to wait between checks.
   *
   * @param value	the time (msec)
   */
  public void setWait(int value) {
    m_Wait = value;
    reset();
  }

  /**
   * Returns the time to wait between checks.
   *
   * @return		the time (msec)
   */
  public int getWait() {
    return m_Wait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitTipText() {
    return "The time to wait between checks (in msec).";
  }

  /**
   * Enqueues the object if possible.
   *
   * @param handler	the queue to use
   * @param input	the data to queue
   * @param retrievalDelay 	the retrieval delay to impose
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doEnqueue(StorageQueueHandler handler, Object input, long retrievalDelay) {
    String	result;

    if ((m_Limit < 1) || (handler.size() < m_Limit)) {
      handler.addDelayedBy(input, retrievalDelay);
      return null;
    }

    result = null;
    while (handler.size() >= m_Limit) {
      Utils.wait(this, this, m_Wait, 50);
      if (m_Stopped) {
	result = "Enqueuing stopped!";
	break;
      }
      if (handler.size() < m_Limit) {
	handler.addDelayedBy(input, retrievalDelay);
	break;
      }
    }

    return result;
  }
}
