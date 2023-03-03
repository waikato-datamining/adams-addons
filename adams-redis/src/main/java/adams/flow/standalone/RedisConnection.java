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
 * RedisConnection.java
 * Copyright (C) 2021-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.MessageCollection;
import adams.core.PasswordPrompter;
import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.io.ConsoleHelper;
import adams.flow.control.Flow;
import adams.flow.core.OptionalPasswordPrompt;
import adams.flow.core.StopHelper;
import adams.flow.core.StopMode;
import adams.gui.dialog.PasswordDialog;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SslVerifyMode;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Defines a connection to a Redis server.
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: RedisConnection
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-host &lt;java.lang.String&gt; (property: host)
 * &nbsp;&nbsp;&nbsp;The host (name&#47;IP address) to connect to.
 * &nbsp;&nbsp;&nbsp;default: localhost
 * </pre>
 *
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The port to connect to.
 * &nbsp;&nbsp;&nbsp;default: 6379
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 65535
 * </pre>
 *
 * <pre>-database &lt;int&gt; (property: database)
 * &nbsp;&nbsp;&nbsp;The database to use (usually 0).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 65535
 * </pre>
 *
 * <pre>-use-ssl &lt;boolean&gt; (property: useSSL)
 * &nbsp;&nbsp;&nbsp;If enabled, SSL is used for the connection.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-start-tls &lt;boolean&gt; (property: startTLS)
 * &nbsp;&nbsp;&nbsp;If enabled, StartTLS is used with SSL connections.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-ssl-verify-mode &lt;NONE|CA|FULL&gt; (property: SSLVerifyMode)
 * &nbsp;&nbsp;&nbsp;How to verify SSL peers.
 * &nbsp;&nbsp;&nbsp;default: NONE
 * </pre>
 *
 * <pre>-password &lt;adams.core.base.BasePassword&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The password to use for connecting.
 * </pre>
 *
 * <pre>-prompt-for-password &lt;boolean&gt; (property: promptForPassword)
 * &nbsp;&nbsp;&nbsp;If enabled, the user gets prompted for enter a password if none has been
 * &nbsp;&nbsp;&nbsp;provided in the setup.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-stop-mode &lt;GLOBAL|STOP_RESTRICTOR&gt; (property: stopMode)
 * &nbsp;&nbsp;&nbsp;The stop mode to use.
 * &nbsp;&nbsp;&nbsp;default: GLOBAL
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RedisConnection
  extends AbstractStandalone
  implements OptionalPasswordPrompt, PasswordPrompter {

  /** for serialization. */
  private static final long serialVersionUID = -1726172998200420556L;

  public final static int DEFAULT_PORT = 6379;

  /** the host. */
  protected String m_Host;

  /** the port. */
  protected int m_Port;

  /** the database. */
  protected int m_Database;

  /** whether to use SSL. */
  protected boolean m_UseSSL;

  /** whether to start TLS. */
  protected boolean m_StartTLS;

  /** how to verify peers. */
  protected SslVerifyMode m_SSLVerifyMode;

  /** the password to use. */
  protected BasePassword m_Password;

  /** the actual password to use. */
  protected BasePassword m_ActualPassword;

  /** whether to prompt the user for a password if none provided. */
  protected boolean m_PromptForPassword;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** how to perform the stop. */
  protected StopMode m_StopMode;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /** the client object. */
  protected transient RedisClient m_Client;

  /** the connection object. */
  protected transient Map<Class, StatefulRedisConnection> m_Connections;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines a connection to a Redis server.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "host", "host",
      "localhost");

    m_OptionManager.add(
      "port", "port",
      DEFAULT_PORT, 1, 65535);

    m_OptionManager.add(
      "database", "database",
      0, 0, 65535);

    m_OptionManager.add(
      "use-ssl", "useSSL",
      false);

    m_OptionManager.add(
      "start-tls", "startTLS",
      false);

    m_OptionManager.add(
      "ssl-verify-mode", "SSLVerifyMode",
      SslVerifyMode.NONE);

    m_OptionManager.add(
      "password", "password",
      new BasePassword(""), false);

    m_OptionManager.add(
      "prompt-for-password", "promptForPassword",
      false);

    m_OptionManager.add(
      "stop-if-canceled", "stopFlowIfCanceled",
      false);

    m_OptionManager.add(
      "custom-stop-message", "customStopMessage",
      "");

    m_OptionManager.add(
      "stop-mode", "stopMode",
      StopMode.GLOBAL);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String  result;
    List<String> options;

    result = QuickInfoHelper.toString(this, "host", m_Host);
    result += QuickInfoHelper.toString(this, "port", m_Port, ":");
    result += QuickInfoHelper.toString(this, "database", m_Database, "/");

    options = new ArrayList<>();
    if (QuickInfoHelper.hasVariable(this, "promptForPassword") || m_PromptForPassword) {
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "promptForPassword", m_PromptForPassword, "prompt for password"));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stop flow"));
    }
    if (QuickInfoHelper.hasVariable(this, "useSSL") || m_UseSSL) {
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useSSL", m_UseSSL, "SSL"));
      QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "startTLS", m_StartTLS, "TLS"));
      result += QuickInfoHelper.toString(this, "SSLVerifyMode", m_SSLVerifyMode, ", ssl-verify-mode: ");
    }
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Returns the host to connect to.
   *
   * @return		the host name/ip
   */
  public String getHost() {
    return m_Host;
  }

  /**
   * Sets the host to connect to.
   *
   * @param value	the host name/ip
   */
  public void setHost(String value) {
    m_Host = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hostTipText() {
    return "The host (name/IP address) to connect to.";
  }

  /**
   * Returns the port to connect to.
   *
   * @return 		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Sets the port to connect to.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if (getOptionManager().isValid("port", value)) {
      m_Port = value;
      reset();
    }
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String portTipText() {
    return "The port to connect to.";
  }

  /**
   * Returns the database to use.
   *
   * @return		the database ID
   */
  public int getDatabase() {
    return m_Database;
  }

  /**
   * Sets the database to use.
   *
   * @param value	the database ID
   */
  public void setDatabase(int value) {
    m_Database = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String databaseTipText() {
    return "The database to use (usually 0).";
  }

  /**
   * Returns whether to use SSL for the connection.
   *
   * @return		true if to use
   */
  public boolean getUseSSL() {
    return m_UseSSL;
  }

  /**
   * Sets whether to use SSL for the connection.
   *
   * @param value	true if to use
   */
  public void setUseSSL(boolean value) {
    m_UseSSL = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useSSLTipText() {
    return "If enabled, SSL is used for the connection.";
  }

  /**
   * Returns whether to use StartTLS with SSL connections.
   *
   * @return		true if to use
   */
  public boolean getStartTLS() {
    return m_StartTLS;
  }

  /**
   * Sets whether to use StartTLS with SSL connections.
   *
   * @param value	true if to use
   */
  public void setStartTLS(boolean value) {
    m_StartTLS = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTLSTipText() {
    return "If enabled, StartTLS is used with SSL connections.";
  }

  /**
   * Returns how to verify SSL peers.
   *
   * @return		how to verify
   */
  public SslVerifyMode getSSLVerifyMode() {
    return m_SSLVerifyMode;
  }

  /**
   * Sets how to verify SSL peers.
   *
   * @param value	how to verify
   */
  public void setSSLVerifyMode(SslVerifyMode value) {
    m_SSLVerifyMode = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String SSLVerifyModeTipText() {
    return "How to verify SSL peers.";
  }

  /**
   * Sets the password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the password to use.
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
    return "The password to use for connecting.";
  }

  /**
   * Sets whether to prompt for a password if none currently provided.
   *
   * @param value	true if to prompt for a password
   */
  public void setPromptForPassword(boolean value) {
    m_PromptForPassword = value;
    reset();
  }

  /**
   * Returns whether to prompt for a password if none currently provided.
   *
   * @return		true if to prompt for a password
   */
  public boolean getPromptForPassword() {
    return m_PromptForPassword;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String promptForPasswordTipText() {
    return
      "If enabled, the user gets prompted "
	+ "for enter a password if none has been provided in the setup.";
  }

  /**
   * Sets whether to stop the flow if dialog canceled.
   *
   * @param value	if true flow gets stopped if dialog canceled
   */
  public void setStopFlowIfCanceled(boolean value) {
    m_StopFlowIfCanceled = value;
    reset();
  }

  /**
   * Returns whether to stop the flow if dialog canceled.
   *
   * @return 		true if the flow gets stopped if dialog canceled
   */
  public boolean getStopFlowIfCanceled() {
    return m_StopFlowIfCanceled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String stopFlowIfCanceledTipText() {
    return "If enabled, the flow gets stopped in case the user cancels the dialog.";
  }

  /**
   * Sets the custom message to use when stopping the flow.
   *
   * @param value	the stop message
   */
  public void setCustomStopMessage(String value) {
    m_CustomStopMessage = value;
    reset();
  }

  /**
   * Returns the custom message to use when stopping the flow.
   *
   * @return		the stop message
   */
  public String getCustomStopMessage() {
    return m_CustomStopMessage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String customStopMessageTipText() {
    return
      "The custom stop message to use in case a user cancelation stops the "
	+ "flow (default is the full name of the actor)";
  }

  /**
   * Sets the stop mode.
   *
   * @param value	the mode
   */
  @Override
  public void setStopMode(StopMode value) {
    m_StopMode = value;
    reset();
  }

  /**
   * Returns the stop mode.
   *
   * @return		the mode
   */
  @Override
  public StopMode getStopMode() {
    return m_StopMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String stopModeTipText() {
    return "The stop mode to use.";
  }

  /**
   * Creates a new connection.
   *
   * @param errors    for collecting errors
   * @return          the connection, null if failed to create
   */
  public StatefulRedisConnection<String, String> newConnection(MessageCollection errors) {
    try {
      return m_Client.connect();
    }
    catch (Exception e) {
      errors.add("Failed to create new connection!", e);
      return null;
    }
  }

  /**
   * Returns the client in use.
   *
   * @return		the client object
   */
  public RedisClient getClient() {
    return m_Client;
  }

  /**
   * Returns the connection in use.
   *
   * @param codec	the codec to use
   * @return		the connection object
   */
  public StatefulRedisConnection getConnection(Class codec) {
    if (m_Connections.containsKey(codec))
      return m_Connections.get(codec);
    if (codec == ByteArrayCodec.class) {
      m_Connections.put(codec, m_Client.connect(new ByteArrayCodec()));
      return m_Connections.get(codec);
    }
    if (codec == StringCodec.class) {
      m_Connections.put(codec, m_Client.connect(StringCodec.UTF8));
      return m_Connections.get(codec);
    }
    throw new IllegalStateException("Unhandled codec!");
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    boolean		result;
    PasswordDialog dlg;

    dlg = new PasswordDialog((Dialog) null, Dialog.ModalityType.DOCUMENT_MODAL);
    dlg.setLocationRelativeTo(getParentComponent());
    ((Flow) getRoot()).registerWindow(dlg, dlg.getTitle());
    dlg.setVisible(true);
    ((Flow) getRoot()).deregisterWindow(dlg);
    result = (dlg.getOption() == PasswordDialog.APPROVE_OPTION);

    if (result)
      m_ActualPassword = dlg.getPassword();

    return result;
  }

  /**
   * Returns whether headless interaction is supported.
   *
   * @return		true if interaction in headless environment is possible
   */
  @Override
  public boolean supportsHeadlessInteraction() {
    return true;
  }

  /**
   * Performs the interaction with the user in a headless environment.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteractHeadless() {
    boolean		result;
    BasePassword	password;

    result   = false;
    password = ConsoleHelper.enterPassword("Please enter password (" + getName() + "):");
    if (password != null) {
      result           = true;
      m_ActualPassword = password;
    }

    return result;
  }

  /**
   * Performs the connection.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String connect() {
    String		result;
    RedisURI.Builder 	builder;

    result = null;
    try {
      builder = RedisURI.Builder.redis(m_Host, m_Port).withDatabase(m_Database);
      if (!m_ActualPassword.isEmpty())
	builder.withPassword(m_ActualPassword.getValue().toCharArray());
      if (m_UseSSL) {
	builder.withSsl(true);
	if (m_StartTLS)
	  builder.withStartTls(true);
	builder.withVerifyPeer(m_SSLVerifyMode);
      }
      m_Client = RedisClient.create(builder.build());
    }
    catch (Exception e) {
      result = handleException("Failed to create Redis client: " + m_Host + ":" + m_Port + "/" + m_Database, e);
    }

    m_Connections = new HashMap<>();

    return result;
  }

  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    if (m_Client == null) {
      if (isLoggingEnabled())
	getLogger().info("Starting new session");

      m_ActualPassword = m_Password;

      if (m_PromptForPassword && (m_Password.getValue().length() == 0)) {
	if (!isHeadless()) {
	  if (!doInteract()) {
	    if (m_StopFlowIfCanceled) {
	      if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
		StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
	      else
		StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
	      result = getStopMessage();
	    }
	  }
	}
	else if (supportsHeadlessInteraction()) {
	  if (!doInteractHeadless()) {
	    if (m_StopFlowIfCanceled) {
	      if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
		StopHelper.stop(this, m_StopMode, "Flow canceled: " + getFullName());
	      else
		StopHelper.stop(this, m_StopMode, m_CustomStopMessage);
	      result = getStopMessage();
	    }
	  }
	}
      }

      if (result == null)
	result = connect();
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("Re-using current session");
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_Connections != null) {
      for (Class codec: m_Connections.keySet())
	m_Connections.get(codec).close();
      m_Connections = null;
    }
    if (m_Client != null) {
      m_Client.shutdown();
      m_Client = null;
    }

    super.wrapUp();
  }
}
