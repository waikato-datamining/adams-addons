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
 * AbstractEnqueueGuard.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.output.enqueue;

import adams.core.StoppableWithFeedback;
import adams.core.option.AbstractOptionHandler;
import adams.flow.control.StorageQueueHandler;

/**
 * Ancestor for queue guards.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractEnqueueGuard
  extends AbstractOptionHandler
  implements StoppableWithFeedback {

  private static final long serialVersionUID = 5233064497339257568L;

  /** whether the scheme has been stopped. */
  protected boolean m_Stopped;

  /**
   * Enqueues the object if possible.
   *
   * @param handler	the queue to use
   * @param input	the data to queue
   * @return		null if successful, otherwise error message
   */
  protected abstract String doEnqueue(StorageQueueHandler handler, Object input);

  /**
   * Enqueues the object if possible.
   *
   * @param handler	the queue to use
   * @param input	the data to queue
   * @return		null if successful, otherwise error message
   */
  public String enqueue(StorageQueueHandler handler, Object input) {
    m_Stopped = false;
    return doEnqueue(handler, input);
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
  }
}
