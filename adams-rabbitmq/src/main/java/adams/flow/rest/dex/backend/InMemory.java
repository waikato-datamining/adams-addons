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
 * InMemory.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.dex.backend;

import adams.core.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simply stores the uploaded data in memory, with no persistence.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InMemory
  extends AbstractBackend {

  private static final long serialVersionUID = 918572755476028354L;

  /**
   * Wraps the actual data item with the expiry timestamp.
   */
  public static class DataContainer
    implements Serializable {

    private static final long serialVersionUID = 1379234889822596730L;

    /** the data item. */
    protected byte[] m_Data;

    /** the expiry timestamp. */
    protected Date m_Expiry;

    /**
     * Initializes the container.
     *
     * @param data	the data to wrap
     * @param expiry	the expiry timestamp
     */
    public DataContainer(byte[] data, Date expiry) {
      m_Data   = data;
      m_Expiry = expiry;
    }

    /**
     * Returns the stored data.
     *
     * @return		the data
     */
    public byte[] getData() {
      return m_Data;
    }

    /**
     * Returns the expiry timestamp.
     *
     * @return		the expiry
     */
    public Date getExpiry() {
      return m_Expiry;
    }

    /**
     * Returns whether the data item has expired and need to be removed.
     *
     * @return		true if expired
     */
    public boolean hasExpired() {
      return (System.currentTimeMillis() >= m_Expiry.getTime());
    }
  }

  /** the in-memory storage. */
  protected Map<String,DataContainer> m_Storage;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply stores the uploaded data in memory, with no persistence.";
  }

  /**
   * Initializes the backend.
   *
   * @return		null if sucessfully initialized, otherwise error message
   */
  @Override
  protected String doInitBackend() {
    m_Storage = new HashMap<>();
    return null;
  }

  /**
   * Removes all items.
   */
  protected void doClear() {
    m_Storage.clear();
  }

  /**
   * Purges any expired items.
   */
  @Override
  protected void doPurge() {
    List<String> 	expired;

    if (m_Storage.size() == 0)
      return;

    expired = new ArrayList<>();
    for (String key: m_Storage.keySet()) {
      if (m_Storage.get(key).hasExpired())
        expired.add(key);
    }

    if (expired.size() > 0) {
      for (String key : expired)
	m_Storage.remove(key);
      if (isLoggingEnabled())
	getLogger().info("Purged: " + Utils.flatten(expired, ", "));
    }
  }

  /**
   * Checks whether the item is present.
   *
   * @param token	the token to check
   * @return		true if available
   */
  @Override
  protected boolean hasItem(String token) {
    return m_Storage.containsKey(token);
  }

  /**
   * Gets the item, if present.
   *
   * @param token	the token to get
   * @return		the item, null if not available
   */
  @Override
  protected byte[] getItem(String token) {
    DataContainer	cont;

    cont = m_Storage.get(token);
    if (cont != null)
      return cont.getData();
    else
      return null;
  }

  /**
   * Adds the item, returns the generated token.
   *
   * @param data	the data to add
   * @return		the token, null if failed to add
   */
  protected String addItem(byte[] data) {
    String  		result;
    DataContainer	cont;

    result = nextToken();
    cont   = new DataContainer(data, new Date(System.currentTimeMillis() + m_TimeToLive * 1000));
    m_Storage.put(result, cont);
    if (isLoggingEnabled())
      getLogger().info("Data added: token=" + result + ", expiry=" + cont.getExpiry());

    return result;
  }

  /**
   * Removes the data associated with the token.
   *
   * @param token	the token to remove the data for
   * @return		true if removed
   */
  protected boolean removeItem(String token) {
    DataContainer	removed;

    removed = m_Storage.remove(token);
    if (isLoggingEnabled())
      getLogger().info("Data removed: " + token);

    return (removed != null);
  }
}
