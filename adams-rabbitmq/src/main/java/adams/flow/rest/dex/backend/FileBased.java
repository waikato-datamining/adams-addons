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
 * FileBased.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.dex.backend;

import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.io.lister.LocalDirectoryLister;
import adams.core.logging.CustomLoggingLevelObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Stores the data on disk in the specified directory ("<token>-<expiry>.ser").
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileBased
  extends AbstractBackend {

  private static final long serialVersionUID = 918572755476028354L;

  /**
   * Wraps the actual data item with the expiry timestamp.
   */
  public static class DataContainer
    extends CustomLoggingLevelObject {

    private static final long serialVersionUID = 1379234889822596730L;

    /** the file containing the data. */
    protected File m_File;

    /** the expiry timestamp. */
    protected Date m_Expiry;

    /**
     * Initializes the container.
     *
     * @param file	the data to wrap
     * @param expiry	the expiry timestamp
     */
    public DataContainer(File file, Date expiry) {
      m_File   = file;
      m_Expiry = expiry;
    }

    /**
     * Initializes the container.
     *
     * @param file	the data to wrap
     * @param expiry	the expiry timestamp
     */
    public DataContainer(byte[] data, File file, Date expiry) {
      this(file, expiry);
      try {
        SerializationHelper.write(file.getAbsolutePath(), data);
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Failed to write data to: " + file, e);
      }
    }

    /**
     * Returns the file with the data.
     *
     * @return		the file
     */
    public File getFile() {
      return m_File;
    }

    /**
     * Loads the data from disk.
     *
     * @return		the data, null if failed to load
     */
    public byte[] getData() {
      if (!m_File.exists() || m_File.isDirectory())
        return null;
      try {
	return (byte[]) SerializationHelper.read(m_File.getAbsolutePath());
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Failed to read data from: " + m_File, e);
        return null;
      }
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

  /** the directory to store the data in. */
  protected PlaceholderDirectory m_DataDir;

  /** the available data items. */
  protected Map<String,DataContainer> m_Storage;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Stores the data on disk in the specified directory (\"<token>-<expiry>.ser\").";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "data-dir", "dataDir",
      new PlaceholderDirectory());
  }

  /**
   * Sets the directory to store the data in.
   *
   * @param value	the directory
   */
  public void setDataDir(PlaceholderDirectory value) {
    m_DataDir = value;
    reset();
  }

  /**
   * Returns the directory to store the data in.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getDataDir() {
    return m_DataDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataDirTipText() {
    return "The directory to store the data in.";
  }

  /**
   * Initializes the backend.
   *
   * @return		null if sucessfully initialized, otherwise error message
   */
  @Override
  protected String doInitBackend() {
    LocalDirectoryLister	lister;
    String[]			files;
    PlaceholderFile 		phfile;
    String			token;
    String			expiry;
    DataContainer		cont;

    m_Storage = new HashMap<>();
    lister    = new LocalDirectoryLister();
    lister.setListFiles(true);
    lister.setListDirs(false);
    lister.setRecursive(false);
    lister.setWatchDir(m_DataDir.getAbsolutePath());
    lister.setRegExp(new BaseRegExp(".*-[0-9]+\\.ser"));
    files = lister.list();
    if (isLoggingEnabled())
      getLogger().info("Loading " + files.length + " files from " + m_DataDir);
    for (String file: files) {
      phfile = new PlaceholderFile(file);
      token  = phfile.getName().replaceAll("-.*", "");
      expiry = phfile.getName().replaceAll(".*-", "").replace(".ser", "");
      if (Utils.isLong(expiry)) {
        cont = new DataContainer(phfile, new Date(Long.parseLong(expiry)));
        m_Storage.put(token, cont);
      }
      else {
        getLogger().warning("File does not conform to format: <token>-<expiry>.ser");
      }
    }

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
    DataContainer	cont;

    if (m_Storage.size() == 0)
      return;

    expired = new ArrayList<>();
    for (String key: m_Storage.keySet()) {
      if (m_Storage.get(key).hasExpired())
        expired.add(key);
    }

    if (expired.size() > 0) {
      for (String key : expired) {
        cont = m_Storage.get(key);
        if (!cont.getFile().delete())
          getLogger().warning("Failed to purge file: " + cont.getFile());
	m_Storage.remove(key);
      }
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
    PlaceholderFile	file;
    long		expiry;

    result = nextToken();
    expiry = System.currentTimeMillis() + m_TimeToLive * 1000;
    file   = new PlaceholderFile(m_DataDir.getAbsolutePath() + File.separator + result + "-" + expiry + ".ser");
    cont   = new DataContainer(data, file, new Date(expiry));
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
    if (removed != null) {
      if (removed.getFile().delete())
        getLogger().info("Removed: " + removed.getFile());
      else
        getLogger().warning("Failed to remove: " + removed.getFile());
    }
    if (isLoggingEnabled())
      getLogger().info("Data removed: " + token);

    return (removed != null);
  }
}
