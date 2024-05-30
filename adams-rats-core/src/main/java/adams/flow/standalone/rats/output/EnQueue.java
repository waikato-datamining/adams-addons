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
 * EnQueue.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;
import adams.flow.control.StorageUpdater;
import adams.flow.core.Unknown;
import adams.flow.standalone.rats.output.enqueue.AbstractEnqueueGuard;
import adams.flow.standalone.rats.output.enqueue.PassThrough;

/**
 <!-- globalinfo-start -->
 * Enqueues the incoming data in the specified queue in internal storage.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the queue in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: queue
 * </pre>
 * 
 * <pre>-guard &lt;adams.flow.standalone.rats.output.enqueue.AbstractEnqueueGuard&gt; (property: guard)
 * &nbsp;&nbsp;&nbsp;The guard for enqueuing the data.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.output.enqueue.PassThrough
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class EnQueue
  extends AbstractRatOutput
  implements StorageUpdater {

  /** for serialization. */
  private static final long serialVersionUID = -148085385347072239L;
  
  /** the name of the queue in the internal storage. */
  protected StorageName m_StorageName;

  /** the guard for enqueuing the data. */
  protected AbstractEnqueueGuard m_Guard;

  /** the retrieval delay in msecs. */
  protected long m_RetrievalDelay;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Enqueues the incoming data in the specified queue in internal storage.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName("queue"));

    m_OptionManager.add(
      "guard", "guard",
      new PassThrough());

    m_OptionManager.add(
      "retrieval-delay", "retrievalDelay",
      0L, 0L, null);
  }

  /**
   * Sets the name for the queue in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the queue in the internal storage.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the queue in the internal storage.";
  }

  /**
   * Sets the guard for enqueuing the data.
   *
   * @param value	the guard
   */
  public void setGuard(AbstractEnqueueGuard value) {
    m_Guard = value;
    reset();
  }

  /**
   * Returns the guard for enqueuing the data.
   *
   * @return		the guard
   */
  public AbstractEnqueueGuard getGuard() {
    return m_Guard;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String guardTipText() {
    return "The guard for enqueuing the data.";
  }

  /**
   * Returns whether storage items are being updated.
   * 
   * @return		true if storage items are updated
   */
  public boolean isUpdatingStorage() {
    return true;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
  }

  /**
   * Sets the delay to enforce on the retrieval of objects from the queue.
   *
   * @param value	the delay
   */
  public void setRetrievalDelay(long value) {
    m_RetrievalDelay = value;
    reset();
  }

  /**
   * Returns the delay to enforce on the retrieval of objects from the queue.
   *
   * @return		the delay
   */
  public long getRetrievalDelay() {
    return m_RetrievalDelay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String retrievalDelayTipText() {
    return "The delay to enforce for the retrieval of objects from the queue.";
  }

  /**
   * Returns the type of data that gets accepted.
   * 
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Performs the actual transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String		result;
    StorageQueueHandler	queue;
    
    result = null;
    queue  = getQueue(m_StorageName);
    if (queue == null)
      result = "Queue not available: " + m_StorageName;
    else
      m_Guard.enqueue(queue, m_Input, m_RetrievalDelay);

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Guard.stopExecution();
    super.stopExecution();
  }
}
