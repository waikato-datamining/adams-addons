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
 * SimpleDockerConnection.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.core.base.BasePassword;
import adams.core.base.DockerDirectoryMapping;
import adams.core.io.PlaceholderFile;
import adams.core.management.CommandResult;
import adams.core.management.OS;
import adams.docker.SimpleDockerHelper;
import adams.docker.simpledocker.PullType;

/**
 <!-- globalinfo-start -->
 * Wraps around the local docker binary and can (optionally) log in to the registry (and log out once the flow finishes).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SimpleDockerConnection
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-registry &lt;java.lang.String&gt; (property: registry)
 * &nbsp;&nbsp;&nbsp;The custom registry to use, uses docker hub if left empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The registry user to use.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-password &lt;adams.core.base.BasePassword&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The registry password to use.
 * &nbsp;&nbsp;&nbsp;default: {}
 * </pre>
 *
 * <pre>-binary &lt;adams.core.io.PlaceholderFile&gt; (property: binary)
 * &nbsp;&nbsp;&nbsp;The docker binary to use, uses default if pointing to dir.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-dir-mapping &lt;adams.core.base.DockerDirectoryMapping&gt; [-dir-mapping ...] (property: dirMappings)
 * &nbsp;&nbsp;&nbsp;The directory mappings to make available to the actual docker commands.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-login &lt;boolean&gt; (property: login)
 * &nbsp;&nbsp;&nbsp;Whether to log in to the registry when the flow starts.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-logout &lt;boolean&gt; (property: logout)
 * &nbsp;&nbsp;&nbsp;Whether to log out to the registry when the flow wraps up.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleDockerConnection
  extends AbstractStandalone {

  private static final long serialVersionUID = -7794355274296751461L;

  /** the docker registry to use. */
  protected String m_Registry;

  /** the registry user. */
  protected String m_User;

  /** the registry password. */
  protected BasePassword m_Password;

  /** the alternative docker binary to use. */
  protected PlaceholderFile m_Binary;

  /** the directory mappings to use. */
  protected DockerDirectoryMapping[] m_DirMappings;

  /** the expanded directory mappings in use. */
  protected transient DockerDirectoryMapping[] m_ExpandedDirMappings;

  /** whether to login into the registry when starting the flow. */
  protected boolean m_Login;

  /** whether to logout from the registry when stopping the flow. */
  protected boolean m_Logout;

  /** how to pull. */
  protected PullType m_PullType;

  /** the actual docker binary to use. */
  protected String m_ActualBinary;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Wraps around the local docker binary and can (optionally) log in to the registry "
      + "(and log out once the flow finishes).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "registry", "registry",
      "");

    m_OptionManager.add(
      "user", "user",
      "");

    m_OptionManager.add(
      "password", "password",
      new BasePassword());

    m_OptionManager.add(
      "binary", "binary",
      new PlaceholderFile());

    m_OptionManager.add(
      "dir-mapping", "dirMappings",
      new DockerDirectoryMapping[0]);

    m_OptionManager.add(
      "login", "login",
      false);

    m_OptionManager.add(
      "logout", "logout",
      false);

    m_OptionManager.add(
      "pull-type", "pullType",
      PullType.DEFAULT);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ExpandedDirMappings = null;
  }

  /**
   * Sets the custom registry to use.
   *
   * @param value	the registry, empty string for default
   */
  public void setRegistry(String value) {
    m_Registry = value;
    reset();
  }

  /**
   * Returns the custom registry in use.
   *
   * @return		the registry, empty string for default
   */
  public String getRegistry() {
    return m_Registry;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String registryTipText() {
    return "The custom registry to use, uses docker hub if left empty.";
  }

  /**
   * Sets the registry user to use.
   *
   * @param value	the user
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the registry user in use.
   *
   * @return		the user
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The registry user to use.";
  }

  /**
   * Sets the registry password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the registry password in use.
   *
   * @return		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The registry password to use.";
  }

  /**
   * Sets the docker binary to use.
   *
   * @param value	the binary, uses default if pointing to dir
   */
  public void setBinary(PlaceholderFile value) {
    m_Binary = value;
    reset();
  }

  /**
   * Returns the docker binary in use.
   *
   * @return		the binary, uses default if pointing to dir
   */
  public PlaceholderFile getBinary() {
    return m_Binary;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binaryTipText() {
    return "The docker binary to use, uses default if pointing to dir.";
  }

  /**
   * Sets the directory mappings to make available to the actual docker commands.
   *
   * @param value	the mappings
   */
  public void setDirMappings(DockerDirectoryMapping[] value) {
    m_DirMappings = value;
    reset();
  }

  /**
   * Returns the directory mappings to make available to the actual docker commands.
   *
   * @return		the mappings
   */
  public DockerDirectoryMapping[] getDirMappings() {
    return m_DirMappings;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dirMappingsTipText() {
    return "The directory mappings to make available to the actual docker commands.";
  }

  /**
   * Returns the directory mappings to make available to the actual docker commands, with any
   * variables (local and container) and placeholders (local only) expanded.
   *
   * @return		the mappings
   */
  public DockerDirectoryMapping[] getExpandedDirMappings() {
    DockerDirectoryMapping[] 	result;
    int				i;
    Variables			vars;
    Placeholders		phs;

    if (m_ExpandedDirMappings == null) {
      result = new DockerDirectoryMapping[m_DirMappings.length];
      if (m_DirMappings.length > 0) {
	vars = getVariables();
	phs = Placeholders.getSingleton();
	for (i = 0; i < m_DirMappings.length; i++) {
	  result[i] = new DockerDirectoryMapping(
	    vars.expand(phs.expand(m_DirMappings[i].localDir())),
	    vars.expand(m_DirMappings[i].containerDir())
	  );
	}
      }
      m_ExpandedDirMappings = result;
    }

    return m_ExpandedDirMappings;
  }

  /**
   * Sets whether to log in to the registry when the flow starts.
   *
   * @param value	true if to login
   */
  public void setLogin(boolean value) {
    m_Login = value;
    reset();
  }

  /**
   * Returns whether to log in to the registry when the flow starts.
   *
   * @return		true if to login
   */
  public boolean getLogin() {
    return m_Login;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loginTipText() {
    return "Whether to log in to the registry when the flow starts.";
  }

  /**
   * Sets whether to log out to the registry when the flow wraps up.
   *
   * @param value	true if to logout
   */
  public void setLogout(boolean value) {
    m_Logout = value;
    reset();
  }

  /**
   * Returns whether to log out to the registry when the flow wraps up.
   *
   * @return		true if to logout
   */
  public boolean getLogout() {
    return m_Logout;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logoutTipText() {
    return "Whether to log out to the registry when the flow wraps up.";
  }

  /**
   * Sets how to pull images.
   *
   * @param value	the type
   */
  public void setPullType(PullType value) {
    m_PullType = value;
    reset();
  }

  /**
   * Returns how to pull images.
   *
   * @return		the type
   */
  public PullType getPullType() {
    return m_PullType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pullTypeTipText() {
    return "Determines how to pull images.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "registry", (m_Registry.isEmpty() ? "-default-" : m_Registry), "registry: ");
    if (!m_User.isEmpty() || getOptionManager().hasVariableForProperty("user"))
      result += QuickInfoHelper.toString(this, "user", m_User, ", user: ");
    if (!m_Password.isEmpty() || getOptionManager().hasVariableForProperty("password"))
      result += QuickInfoHelper.toString(this, "password", m_Password, ", pw: ");
    result += QuickInfoHelper.toString(this, "login", m_Login, "login", ", ");
    result += QuickInfoHelper.toString(this, "logout", m_Logout, "logout", ", ");
    if (getOptionManager().hasVariableForProperty("dirMappings") || m_DirMappings.length > 0)
      result += QuickInfoHelper.toString(this, "dirMappings", m_DirMappings, ", mappings: ");
    result += QuickInfoHelper.toString(this, "pullType", m_PullType, ", pull: ");

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_ActualBinary = null;
      if (!m_Binary.isDirectory()) {
	if (!m_Binary.getAbsoluteFile().exists())
	  result = "Docker binary does not exist: " + m_Binary.getAbsolutePath();
	else
	  m_ActualBinary = m_Binary.getAbsolutePath();
      }
      else {
	if (OS.isWindows())
	  m_ActualBinary = "docker.exe";
	else
	  m_ActualBinary = "docker";
      }
    }

    return result;
  }

  /**
   * Returns the actual docker binary in use.
   *
   * @return		the binary, null if not determined
   */
  public String getAcualBinary() {
    return m_ActualBinary;
  }

  /**
   * Determines the actual pull type to perform.
   *
   * @param override	the (potentical( override from another class
   * @return		the actual type
   */
  public PullType getActualPullType(PullType override) {
    PullType	result;

    result = m_PullType;
    if (override != PullType.DEFAULT)
      result = override;

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    CommandResult 	res;

    result = null;
    if (m_ActualBinary == null)
      result = "No docker binary available!";

    if ((result == null) && m_Login) {
      res = SimpleDockerHelper.login(getAcualBinary(), m_Registry, m_User, m_Password.getValue());
      if (res.exitCode != 0) {
	result = "Login failed!\n"
	  + "exit code: " + res.exitCode
	  + (res.stdout != null ? "\nstdout:\n" + res.stdout : "")
	  + (res.stderr != null ? "\nstderr:\n" + res.stderr : "");
      }
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    CommandResult	res;

    if ((m_ActualBinary != null) && m_Logout) {
      res = SimpleDockerHelper.logout(getAcualBinary(), m_Registry);
      if (res.exitCode != 0) {
	getLogger().warning("Logout failed!\n"
	  + "exit code: " + res.exitCode
	  + (res.stdout != null ? "\nstdout:\n" + res.stdout : "")
	  + (res.stderr != null ? "\nstderr:\n" + res.stderr : ""));
      }
    }

    super.wrapUp();
  }
}
