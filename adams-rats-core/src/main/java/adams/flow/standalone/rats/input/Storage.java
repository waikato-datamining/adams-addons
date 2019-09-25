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
 * Storage.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.core.QuickInfoHelper;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Outputs the specified storage item, if possible
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
 * &nbsp;&nbsp;&nbsp;The name of the item in internal storage.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Storage
  extends AbstractRatInput
  implements StorageUser {

  /** for serialization. */
  private static final long serialVersionUID = 6942772195383207110L;

  /** the name of the item in the internal storage. */
  protected StorageName m_StorageName;

  /** the item obtained from the queue. */
  protected Object m_Output;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Outputs the specified storage item, if possible.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Output = null;
  }

  /**
   * Sets the name for the item in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the item in the internal storage.
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
    return "The name of the item in internal storage.";
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
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
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    adams.flow.control.Storage	storage;

    m_Output = null;
    storage  = getOwner().getStorageHandler().getStorage();
    if (storage.has(m_StorageName))
      m_Output = storage.get(m_StorageName);

    return null;
  }

  /**
   * Checks whether any output can be collected.
   * 
   * @return		true if output available
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Output != null);
  }

  /**
   * Returns the received data.
   * 
   * @return		the data
   */
  @Override
  public Object output() {
    Object	result;
    
    result   = m_Output;
    m_Output = null;
    
    return result;
  }
}
