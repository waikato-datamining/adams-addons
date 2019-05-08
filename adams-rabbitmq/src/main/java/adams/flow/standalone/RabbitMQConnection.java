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
 * RabbitMQConnection.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.base.BasePassword;
import adams.core.io.ConsoleHelper;
import adams.core.net.rabbitmq.RabbitMQHelper;
import adams.flow.control.Flow;
import adams.flow.core.OptionalPasswordPrompt;
import adams.flow.core.StopHelper;
import adams.flow.core.StopMode;
import adams.gui.dialog.PasswordDialog;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Defines a connection to a RabbitMQ broker.
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
 * &nbsp;&nbsp;&nbsp;default: RabbitMQConnection
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
 * <pre>-host &lt;java.lang.String&gt; (property: host)
 * &nbsp;&nbsp;&nbsp;The host (name&#47;IP address) to connect to.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The port to connect to.
 * &nbsp;&nbsp;&nbsp;default: 5672
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 65535
 * </pre>
 *
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The database user to connect with.
 * </pre>
 *
 * <pre>-password &lt;adams.core.base.BasePassword&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The password of the database user.
 * </pre>
 *
 * <pre>-prompt-for-password &lt;boolean&gt; (property: promptForPassword)
 * &nbsp;&nbsp;&nbsp;If enabled and authentication is required, the user gets prompted for enter
 * &nbsp;&nbsp;&nbsp;a password if none has been provided in the setup.
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
public class RabbitMQConnection
  extends AbstractStandalone
  implements OptionalPasswordPrompt {

  /** for serialization. */
  private static final long serialVersionUID = -1726172998200420556L;

  /** the SSH host. */
  protected String m_Host;

  /** the SSH port. */
  protected int m_Port;

  /** database username. */
  protected String m_User;

  /** database password. */
  protected BasePassword m_Password;

  /** the actual SMTP password to use. */
  protected BasePassword m_ActualPassword;

  /** whether to prompt the user for a password if none provided. */
  protected boolean m_PromptForPassword;

  /** whether to stop the flow if canceled. */
  protected boolean m_StopFlowIfCanceled;

  /** the custom stop message to use if flow gets stopped due to cancelation. */
  protected String m_CustomStopMessage;

  /** how to perform the stop. */
  protected StopMode m_StopMode;

  /** the connection. */
  protected transient com.rabbitmq.client.Connection m_Connection;

  /** the auto-created queues that need to get deleted again. */
  protected List<String> m_AutoCreatedQueues;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines a connection to a RabbitMQ broker.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "host", "host",
      "");

    m_OptionManager.add(
      "port", "port",
      AMQP.PROTOCOL.PORT, 1, 65535);

    m_OptionManager.add(
      "user", "user",
      "", false);

    m_OptionManager.add(
      "password", "password",
      new BasePassword(), false);

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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_AutoCreatedQueues = new ArrayList<>();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "user", (m_User.isEmpty() ? "guest" : m_User));
    result += QuickInfoHelper.toString(this, "host", (m_Host.length() == 0 ? "??" : m_Host), "@");
    result += QuickInfoHelper.toString(this, "port", m_Port, ":");

    if (QuickInfoHelper.hasVariable(this, "promptForPassword") || m_PromptForPassword) {
      result += ", prompt for password";
      result += QuickInfoHelper.toString(this, "stopFlowIfCanceled", m_StopFlowIfCanceled, "stop flow", ", ");
    }

    return result;
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
   * Returns the host to connect to.
   *
   * @return		the host name/ip
   */
  public String getHost() {
    return m_Host;
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
   * Returns the port to connect to.
   *
   * @return 		the port
   */
  public int getPort() {
    return m_Port;
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
   * Sets the database user.
   *
   * @param value	the user
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the database user.
   *
   * @return 		the user
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The database user to connect with.";
  }

  /**
   * Sets the database password.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the database password.
   *
   * @return 		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The password of the database user.";
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
      "If enabled and authentication is required, the user gets prompted "
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
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  public boolean doInteract() {
    boolean		result;
    PasswordDialog	dlg;

    dlg = new PasswordDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
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
  public boolean supportsHeadlessInteraction() {
    return true;
  }

  /**
   * Performs the interaction with the user in a headless environment.
   *
   * @return		true if successfully interacted
   */
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
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    com.rabbitmq.client.Connection	conn;

    result = null;

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

    if (result == null) {
      conn = getConnection();
      if (!conn.isOpen())
        result = "Failed to connect to broker (" + getHost() + ":" + getPort() + ")";
    }

    return result;
  }

  /**
   * Returns the database connection in use. Reconnects the database, to make
   * sure that the database connection is the correct one.
   *
   * @return		the connection object
   */
  protected com.rabbitmq.client.Connection retrieveConnection() {
    ConnectionFactory 	factory;

    factory = new ConnectionFactory();
    factory.setHost(m_Host);
    factory.setPort(m_Port);
    if (!m_User.isEmpty()) {
      factory.setUsername(m_User);
      factory.setPassword(m_ActualPassword.getValue());
    }
    try {
      return factory.newConnection();
    }
    catch (Exception e) {
      handleException("Failed to connect to broker " + m_Host + ":" + m_Port, e);
      return null;
    }
  }

  /**
   * Returns the database connection in use. Reconnects the database, to make
   * sure that the database connection is the correct one.
   *
   * @return		the connection object
   */
  public com.rabbitmq.client.Connection getConnection() {
    if (m_Connection == null)
      m_Connection = retrieveConnection();
    return m_Connection;
  }

  /**
   * Creates a new channel and returns it.
   *
   * @return		the channel, null if failed to create or no connection available
   */
  public Channel createChannel() {
    return RabbitMQHelper.createChannel(this, getConnection());
  }

  /**
   * Adds the queue to the list of queues that were automatically created
   * and need deleting when the flow wraps up.
   *
   * @param queue	the queue to add
   */
  public void addAutoCreatedQueue(String queue) {
    m_AutoCreatedQueues.add(queue);
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    RabbitMQHelper.closeQuietly(m_Connection, m_AutoCreatedQueues);
    m_Connection = null;

    super.wrapUp();
  }
}
