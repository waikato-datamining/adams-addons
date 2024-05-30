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
 * PassThrough.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.output.enqueue;

import adams.flow.control.StorageQueueHandler;

/**
 * Just enqueues the data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractEnqueueGuard {

  private static final long serialVersionUID = 1179005015402094837L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Just enqueues the data.";
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
    handler.addDelayedBy(input, retrievalDelay);
    return null;
  }
}
