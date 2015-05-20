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
 * DistributedEnQueue.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.flow.control.StorageName;
import adams.flow.control.StorageQueueHandler;
import adams.flow.control.StorageUpdater;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Distributes the incoming data in the specified queues in internal storage, iterating through the different queues.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; [-storage-name ...] (property: storageNames)
 * &nbsp;&nbsp;&nbsp;The names of the queues in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DistributedEnQueue
  extends AbstractRatOutput
  implements StorageUpdater {

  /** for serialization. */
  private static final long serialVersionUID = -148085385347072239L;
  
  /** the name of the queue in the internal storage. */
  protected StorageName[] m_StorageNames;

  /** the index of the next queue to be used. */
  protected int m_NextQueue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Distributes the incoming data in the specified queues in internal storage, iterating through the different queues.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "storage-name", "storageNames",
	    new StorageName[0]);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_NextQueue = 0;
  }

  /**
   * Sets the names for the queues in the internal storage.
   *
   * @param value	the names
   */
  public void setStorageNames(StorageName[] value) {
    m_StorageNames = value;
    reset();
  }

  /**
   * Returns the names for the queues in the internal storage.
   *
   * @return		the names
   */
  public StorageName[] getStorageNames() {
    return m_StorageNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNamesTipText() {
    return "The names of the queues in the internal storage.";
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
    return QuickInfoHelper.toString(this, "storageNames", m_StorageNames, "storage: ");
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
   * Hook method for performing checks at setup time.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String setUp() {
    String      result;

    result = super.setUp();

    if (result == null) {
      if (m_StorageNames.length < 1)
        result = "At least one queue must be defined!";
    }

    return result;
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
    queue  = getQueue(m_StorageNames[m_NextQueue]);
    if (queue == null)
      result = "Queue not available: " + m_StorageNames[m_NextQueue];
    else
      queue.add(m_Input);

    // move to next queue
    m_NextQueue++;
    if (m_NextQueue >= m_StorageNames.length)
      m_NextQueue = 0;

    return result;
  }
}
