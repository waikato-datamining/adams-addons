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
 * TrustStoreHelper.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import adams.core.Properties;
import adams.core.base.BasePassword;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.env.TrustStoreDefinition;

/**
 * Helper class for the global trust store.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TrustStoreHelper {

  /** the props file. */
  public final static String FILENAME = "TrustStore.props";

  /** the trust store file. */
  public final static String TRUST_STORE_FILE = "TrustStoreFile";

  /** the trust store password. */
  public final static String TRUST_STORE_PASSWORD = "TrustStorePassword";

  /** the singleton. */
  protected static TrustStoreHelper m_Singleton;

  /** the properties. */
  protected Properties m_Properties;

  /** whether the settings got modified. */
  protected boolean m_Modified;

  /**
   * Initializes the helper.
   */
  private TrustStoreHelper() {
    super();
    reload();
  }

  /**
   * Whether the settings got modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Returns the trust store file.
   *
   * @return		the file name, directory if no file defined
   */
  public String getTrustStoreFileName() {
    String	result;

    result = m_Properties.getProperty(TRUST_STORE_FILE, "");
    if (result.trim().isEmpty())
      result = ".";

    return result;
  }

  /**
   * Returns the trust store file.
   *
   * @return		the file name, directory if no file defined
   */
  public PlaceholderFile getTrustStoreFile() {
    return new PlaceholderFile(getTrustStoreFileName());
  }

  /**
   * Updates the trust store file.
   *
   * @param value	the file
   */
  public void setTrustStoreFile(String value) {
    if (value.trim().isEmpty())
      value = ".";
    m_Modified = true;
    m_Properties.setProperty(TRUST_STORE_FILE, value);
  }

  /**
   * Returns the trust store password.
   *
   * @return		the password
   */
  public BasePassword getTrustStorePassword() {
    return m_Properties.getPassword(TRUST_STORE_PASSWORD, new BasePassword(""));
  }

  /**
   * Updates the trust store password.
   *
   * @param value	the password
   */
  public void setTrustStorePassword(BasePassword value) {
    m_Modified = true;
    m_Properties.setPassword(TRUST_STORE_PASSWORD, value);
  }

  /**
   * Reloads the properties file. Discards any unsaved settings.
   */
  public synchronized void reload() {
    m_Modified = false;

    try {
      m_Properties = Environment.getInstance().read(TrustStoreDefinition.KEY);
    }
    catch (Exception e) {
      m_Properties = new Properties();
    }
  }

  /**
   * Saves the settings in the user's home directory.
   *
   * @return		true if successfully saved
   */
  public synchronized boolean save() {
    boolean	result;

    result = Environment.getInstance().write(TrustStoreDefinition.KEY, m_Properties);
    if (result)
      m_Modified = false;

    return result;
  }

  /**
   * Sets the system-wide properties of the trust store (when not pointing to dir).
   */
  public void apply() {
    PlaceholderFile	file;

    file = getTrustStoreFile();
    if (file.exists() && !file.isDirectory()) {
      System.setProperty("javax.net.ssl.trustStore", getTrustStoreFile().getAbsolutePath());
      System.setProperty("javax.net.ssl.trustStorePassword", getTrustStorePassword().getValue());
    }
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public synchronized static TrustStoreHelper getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new TrustStoreHelper();

    return m_Singleton;
  }
}
