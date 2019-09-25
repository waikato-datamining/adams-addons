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
 * Publish.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.core.PublishSubscribeHandler;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Publishes the incoming data using the specified publish&#47;subscribe handler in storage.
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
 * &nbsp;&nbsp;&nbsp;The name of the pub&#47;sub data structure in the internal storage.
 * &nbsp;&nbsp;&nbsp;default: pubsub
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Publish
  extends AbstractRatOutput
  implements StorageUpdater {

  /** for serialization. */
  private static final long serialVersionUID = -148085385347072239L;
  
  /** the name of the queue in the internal storage. */
  protected StorageName m_StorageName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Publishes the incoming data using the specified publish/subscribe handler in storage.";
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
   * Sets the name for the pub/sub data structure in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the pub/sub data structure in the internal storage.
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
    return "The name of the pub/sub data structure in the internal storage.";
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
    String			result;
    Storage 			storage;
    PublishSubscribeHandler 	handler;

    result  = null;
    handler = null;
    storage = getOwner().getStorageHandler().getStorage();
    if (!storage.has(m_StorageName))
      result = "Storage item does not exist: " + m_StorageName;
    else if (!(storage.get(m_StorageName) instanceof PublishSubscribeHandler))
      result = "Storage item '" + m_StorageName + "' is not a " + Utils.classToString(PublishSubscribeHandler.class) + "!";
    else
      handler = (PublishSubscribeHandler) storage.get(m_StorageName);

    if (handler != null)
      handler.publish(this, m_Input);

    return result;
  }
}
