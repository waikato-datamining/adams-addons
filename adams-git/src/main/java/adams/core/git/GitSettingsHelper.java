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
 * GitSettingsHelper.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.git;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.env.Environment;
import adams.env.GitDefinition;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;

import java.io.File;

/**
 * Helper class for the default git.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GitSettingsHelper {

  /** the props file. */
  public final static String FILENAME = "Git.props";

  /** the ssh key dir. */
  public final static String SSH_DIR = "SshDir";

  /** the known_hosts file. */
  public final static String KNOWN_HOSTS = "KnownHosts";

  /** the user. */
  public final static String USER = "User";

  /** the email. */
  public final static String EMAIL = "Email";

  /** the logging level. */
  public final static String LOGGING_LEVEL = "LoggingLevel";

  /** the flow editor support. */
  public final static String FLOW_EDITOR_SUPPORT = "FlowEditorSupport";

  /** the singleton. */
  protected static GitSettingsHelper m_Singleton;

  /** the properties. */
  protected Properties m_Properties;

  /** whether the settings got modified. */
  protected boolean m_Modified;

  /**
   * Initializes the helper.
   */
  private GitSettingsHelper() {
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
   * Returns the default ssh dir.
   *
   * @return		the default dir
   */
  public String getDefaultSshDir() {
    return new File(FS.DETECTED.userHome(), File.separator + ".ssh").getAbsolutePath();
  }

  /**
   * Returns the ssh key dir.
   *
   * @return		the dir, by default the user's home dir + .ssh
   */
  public String getSshDir() {
    String	result;

    result = m_Properties.getPath(SSH_DIR, getDefaultSshDir());
    if (result.trim().isEmpty())
      result = getDefaultSshDir();

    return result;
  }

  /**
   * Returns the ssh dir file.
   *
   * @return		the directory
   */
  public PlaceholderFile getSshDirFile() {
    return new PlaceholderFile(getSshDir());
  }

  /**
   * Updates the ssh key file.
   *
   * @param value	the file
   */
  public void setSshDir(String value) {
    if (value.trim().isEmpty())
      value = getDefaultSshDir();
    m_Modified = true;
    m_Properties.setProperty(SSH_DIR, value);
  }

  /**
   * Returns the default known_hosts file.
   *
   * @return		the default file
   */
  public String getDefaultKnownHosts() {
    return getDefaultSshDir() + File.separator + "known_hosts";
  }

  /**
   * Returns the known_hosts file.
   *
   * @return		the file, by default the user's home dir + .ssh/known_hosts
   */
  public String getKnownHosts() {
    String	result;

    result = m_Properties.getProperty(KNOWN_HOSTS, getDefaultKnownHosts());
    if (result.trim().isEmpty())
      result = getDefaultKnownHosts();

    return result;
  }

  /**
   * Returns the known_hosts file.
   *
   * @return		the file
   */
  public PlaceholderFile getKnownHostsFile() {
    return new PlaceholderFile(getKnownHosts());
  }

  /**
   * Updates the known_hosts file.
   *
   * @param value	the file
   */
  public void setKnownHosts(String value) {
    if (value.trim().isEmpty())
      value = getDefaultKnownHosts();
    m_Modified = true;
    m_Properties.setProperty(KNOWN_HOSTS, value);
  }

  /**
   * Returns the global user, if possible.
   *
   * @return		the global user, empty string if not available
   */
  public String getGlobalUser() {
    try {
      return SystemReader.getInstance().getUserConfig().getString("user", null, "name");
    }
    catch (Exception e) {
      // ignored
      return "";
    }
  }

  /**
   * Returns the user for commits.
   *
   * @return		the user
   */
  public String getUser() {
    String	result;

    result = m_Properties.getProperty(USER, "");
    if (result.isEmpty())
      result = getGlobalUser();

    return result;
  }

  /**
   * Updates the user for commits.
   *
   * @param value	the user
   */
  public void setUser(String value) {
    m_Modified = true;
    m_Properties.setProperty(USER, value);
  }

  /**
   * Returns the global email, if possible.
   *
   * @return		the global email, empty string if not available
   */
  public String getGlobalEmail() {
    try {
      return SystemReader.getInstance().getUserConfig().getString("user", null, "email");
    }
    catch (Exception e) {
      // ignored
      return "";
    }
  }

  /**
   * Returns the email for commits.
   *
   * @return		the email
   */
  public String getEmail() {
    String	result;

    result = m_Properties.getProperty(EMAIL, "");
    if (result.isEmpty())
      result = getGlobalEmail();

    return result;
  }

  /**
   * Updates the email for commits.
   *
   * @param value	the email
   */
  public void setEmail(String value) {
    m_Modified = true;
    m_Properties.setProperty(EMAIL, value);
  }

  /**
   * Returns the email for commits.
   *
   * @return		the email
   */
  public LoggingLevel getLoggingLevel() {
    try {
      return LoggingLevel.valueOf(m_Properties.getProperty(LOGGING_LEVEL, "INFO"));
    }
    catch (Exception e) {
      return LoggingLevel.INFO;
    }
  }

  /**
   * Reloads the properties file. Discards any unsaved settings.
   */
  public synchronized void reload() {
    m_Modified  = false;

    try {
      m_Properties = Environment.getInstance().read(GitDefinition.KEY);
    }
    catch (Exception e) {
      m_Properties = new Properties();
    }
  }

  /**
   * Returns whether to enable git support in the flow editor.
   *
   * @return		true if to enable support
   */
  public boolean getFlowEditorSupport() {
    return m_Properties.getBoolean(FLOW_EDITOR_SUPPORT, false);
  }

  /**
   * Updates whether to enable git support in the flow editor.
   *
   * @param value	true if to enable support
   */
  public void setFlowEditorSupport(boolean value) {
    m_Modified = true;
    m_Properties.setBoolean(FLOW_EDITOR_SUPPORT, value);
  }

  /**
   * Returns the current properties.
   *
   * @return		the properties
   */
  public synchronized Properties getProperties() {
    return m_Properties;
  }

  /**
   * Saves the settings in the user's home directory.
   *
   * @return		true if successfully saved
   */
  public synchronized boolean save() {
    boolean	result;

    result = Environment.getInstance().write(GitDefinition.KEY, m_Properties);
    if (result)
      m_Modified = false;

    return result;
  }

  /**
   * Returns the singleton.
   *
   * @return		the singleton
   */
  public synchronized static GitSettingsHelper getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new GitSettingsHelper();

    return m_Singleton;
  }
}
