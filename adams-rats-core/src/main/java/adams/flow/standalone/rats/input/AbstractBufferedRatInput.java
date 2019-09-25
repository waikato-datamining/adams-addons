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
 * AbstractBufferedRatInput.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.input;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Ancestor for buffering rat inputs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBufferedRatInput
  extends AbstractRatInput
  implements BufferedRatInput {

  private static final long serialVersionUID = -2564424816778971430L;

  /** the maximum number of items to buffer. */
  protected int m_MaxBuffer;

  /** the queue to use for buffering. */
  protected BlockingQueue m_Buffer;

  /** the current item to return. */
  protected Object m_Current;

  /** the internal timeout interval for polling the queue in msec. */
  protected int m_PollTimeout;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-buffer", "maxBuffer",
      getDefaultMaxBuffer(), 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PollTimeout = 100;
  }

  /**
   * Hook method for performing checks at setup time.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_Buffer = new ArrayBlockingQueue(m_MaxBuffer);
      if (isLoggingEnabled())
	getLogger().info("Using buffer size: " + m_MaxBuffer);
    }

    return result;
  }

  /**
   * Returns the default maximum for the buffer.
   *
   * @return		the default
   */
  protected int getDefaultMaxBuffer() {
    return 65535;
  }

  /**
   * Sets the maximum number of items to buffer.
   *
   * @param value	the maximum number of items to buffer
   */
  public void setMaxBuffer(int value) {
    if (getOptionManager().isValid("maxBuffer", value)) {
      m_MaxBuffer = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of items to buffer.
   *
   * @return		the maximum number of items to buffer
   */
  public int getMaxBuffer() {
    return m_MaxBuffer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxBufferTipText() {
    return "The maximum number of items to buffer.";
  }

  /**
   * For buffering the data received from the webservice.
   *
   * @param value	the data received
   */
  public void bufferData(Object value) {
    m_Buffer.add(value);
  }

  /**
   * Waits for the next data object, polling the queue.
   *
   * @return		the data, null if none available (eg when stopped)
   */
  protected Object poll() {
    Object	result;

    result = null;

    while (canReceive() && (result == null)) {
      try {
	result = m_Buffer.poll(m_PollTimeout, TimeUnit.MILLISECONDS);
      }
      catch (Exception e) {
        if (isLoggingEnabled())
          getLogger().log(Level.INFO, "Exception while polling", e);
      }
    }

    return result;
  }

  /**
   * Checks whether any output can be collected.
   * Blocks till either stopped or data has arrived.
   *
   * @return		true if output available
   */
  public boolean hasPendingOutput() {
    m_Current = poll();
    return (m_Current != null) && !m_Stopped;
  }

  /**
   * Returns the received data.
   *
   * @return		the data, null if none available or stopped
   */
  public Object output() {
    Object	result;

    if (m_Current != null) {
      result    = m_Current;
      m_Current = null;
    }
    else {
      result = poll();
    }

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    if (m_Buffer != null) {
      synchronized (m_Buffer) {
	m_Buffer.notifyAll();
      }
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Buffer.clear();
    super.cleanUp();
  }
}
