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
 * Subscribe.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.core.PublishSubscribeHandler;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.event.PublicationEvent;
import adams.event.PublicationListener;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Subscribes to the specified publish&#47;subscribe handler and forwards data that is being published.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-max-buffer &lt;int&gt; (property: maxBuffer)
 * &nbsp;&nbsp;&nbsp;The maximum number of items to buffer.
 * &nbsp;&nbsp;&nbsp;default: 65535
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the queue in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: queue
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Subscribe
  extends AbstractBufferedRatInput
  implements StorageUpdater, PublicationListener {

  /** for serialization. */
  private static final long serialVersionUID = 6942772195383207110L;

  /** the name of the queue in the internal storage. */
  protected StorageName m_StorageName;

  /** the pub/sub handler. */
  protected transient PublishSubscribeHandler m_Handler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Subscribes to the specified publish/subscribe handler and forwards "
          + "data that is being published.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName("pubsub"));
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
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  @Override
  public Class generates() {
    return Unknown.class;
  }

  /**
   * Gets called when data is being published.
   *
   * @param e		the data event
   */
  public void dataPublished(PublicationEvent e) {
    if (!isStopped()) {
      if (isLoggingEnabled())
        getLogger().info("Data published by " + e.getDataSource() + ": " + e.getPublishedData());
      bufferData(e.getPublishedData());
    }
  }

  /**
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    String		result;

    result = null;
    if (!getOwner().getStorageHandler().getStorage().has(m_StorageName)) {
      result = "Storage item not found: " + m_StorageName;
    }
    else if (!(getOwner().getStorageHandler().getStorage().get(m_StorageName) instanceof PublishSubscribeHandler)) {
      result = "Storage item '" + m_StorageName + "' is not a " + Utils.classToString(PublishSubscribeHandler.class) + "!";
    }
    else {
      m_Handler = (PublishSubscribeHandler) getOwner().getStorageHandler().getStorage().get(m_StorageName);
      m_Handler.addSubscriber(this);
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_Handler != null) {
      m_Handler.removeSubscriber(this);
      m_Handler = null;
    }

    super.cleanUp();
  }
}
