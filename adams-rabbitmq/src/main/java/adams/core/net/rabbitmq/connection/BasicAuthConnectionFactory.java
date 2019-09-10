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
 * BasicAuthConnectionFactory.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.connection;

import adams.core.MessageCollection;
import adams.core.PasswordPrompter;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BasePassword;
import adams.core.io.ConsoleHelper;
import adams.flow.control.Flow;
import adams.flow.core.StopHelper;
import adams.flow.core.StopMode;
import adams.gui.dialog.PasswordDialog;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;

/**
 * Performs user/password authentication.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BasicAuthConnectionFactory
  extends AbstractConnectionFactory
  implements PasswordPrompter {

  private static final long serialVersionUID = 1730696755155054710L;

  /** the host. */
  protected String m_Host;

  /** the port. */
  protected int m_Port;

  /** the virtual host. */
  protected String m_VirtualHost;

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

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs user/password authentication.";
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
      AMQP.PROTOCOL.PORT, 1, 65535);

    m_OptionManager.add(
      "virtual-host", "virtualHost",
      "/");

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
    result += QuickInfoHelper.toString(this, "virtualHost", m_VirtualHost, "");

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
   * Sets the virtual host to use.
   *
   * @param value	the virtual host
   */
  public void setVirtualHost(String value) {
    m_VirtualHost = value;
    reset();
  }

  /**
   * Returns the virtual host to use.
   *
   * @return		the virtual host
   */
  public String getVirtualHost() {
    return m_VirtualHost;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String virtualHostTipText() {
    return "The virtual host to use on the RabbitMQ server.";
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
  public void setStopMode(StopMode value) {
    m_StopMode = value;
    reset();
  }

  /**
   * Returns the stop mode.
   *
   * @return		the mode
   */
  public StopMode getStopMode() {
    return m_StopMode;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
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
    PasswordDialog dlg;

    dlg = new PasswordDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    dlg.setLocationRelativeTo(m_FlowContext.getParentComponent());
    ((Flow) m_FlowContext.getRoot()).registerWindow(dlg, dlg.getTitle());
    dlg.setVisible(true);
    ((Flow) m_FlowContext.getRoot()).deregisterWindow(dlg);
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
    password = ConsoleHelper.enterPassword("Please enter password (" + m_FlowContext.getName() + "):");
    if (password != null) {
      result           = true;
      m_ActualPassword = password;
    }

    return result;
  }

  /**
   * Returns whether a flow context is required.
   *
   * @return		true if required
   */
  protected boolean requiresFlowContext() {
    return m_PromptForPassword;
  }

  /**
   * Hook method for performing checks.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null) {
      if (m_User.isEmpty())
        result = "No user provided! For anonymous/guest connection, use " + Utils.classToString(GuestConnectionFactory.class) + " instead.";
    }

    return result;
  }

  /**
   * Generates the connection factory object.
   *
   * @param errors	for collecting errors
   * @return		the factory, null in case of error
   */
  @Override
  protected ConnectionFactory doGenerate(MessageCollection errors) {
    ConnectionFactory 	result;
    String		msg;

    result = new ConnectionFactory();
    result.setHost(m_Host);
    result.setPort(m_Port);
    result.setVirtualHost(m_VirtualHost);
    result.setUsername(m_User);

    m_ActualPassword = m_Password;

    msg = null;
    if (m_PromptForPassword && (m_Password.getValue().length() == 0)) {
      if (!m_FlowContext.isHeadless()) {
        if (!doInteract()) {
          if (m_StopFlowIfCanceled) {
            if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
              StopHelper.stop(m_FlowContext, m_StopMode, "Flow canceled: " + m_FlowContext.getFullName());
            else
              StopHelper.stop(m_FlowContext, m_StopMode, m_CustomStopMessage);
            msg = m_FlowContext.getStopMessage();
          }
        }
      }
      else if (supportsHeadlessInteraction()) {
        if (!doInteractHeadless()) {
          if (m_StopFlowIfCanceled) {
            if ((m_CustomStopMessage == null) || (m_CustomStopMessage.trim().length() == 0))
              StopHelper.stop(m_FlowContext, m_StopMode, "Flow canceled: " + m_FlowContext.getFullName());
            else
              StopHelper.stop(m_FlowContext, m_StopMode, m_CustomStopMessage);
            msg = m_FlowContext.getStopMessage();
          }
        }
      }
    }

    if (msg == null)
      result.setPassword(m_ActualPassword.getValue());
    else
      errors.add(msg);

    return result;
  }
}
