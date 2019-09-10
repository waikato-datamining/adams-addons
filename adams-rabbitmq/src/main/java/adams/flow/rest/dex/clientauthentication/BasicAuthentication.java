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
 * NoAuthentication.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.dex.clientauthentication;

import adams.core.ClassCrossReference;
import adams.core.MessageCollection;
import adams.core.PasswordPrompter;
import adams.core.base.BaseKeyValuePair;
import adams.core.base.BasePassword;
import adams.flow.control.Flow;
import adams.gui.dialog.PasswordDialog;

import java.awt.Dialog.ModalityType;

/**
 * Generates simple user/password authentication.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BasicAuthentication
  extends AbstractClientAuthentication
  implements ClassCrossReference, PasswordPrompter {

  private static final long serialVersionUID = -8658731460295213717L;

  /** the user. */
  protected String m_User;

  /** the password. */
  protected BasePassword m_Password;

  /** the actual password to use. */
  protected BasePassword m_ActualPassword;

  /** whether to prompt the user for a password if none provided. */
  protected boolean m_PromptForPassword;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates simple user/password authentication.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "user", "user",
      "");

    m_OptionManager.add(
      "password", "password",
      new BasePassword());

    m_OptionManager.add(
      "prompt-for-password", "promptForPassword",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ActualPassword = null;
  }

  /**
   * Sets the user.
   *
   * @param value	the user
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the user.
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
    return "The name of the user to connect as.";
  }

  /**
   * Sets the password.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the password.
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
    return "The password for the user.";
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
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{adams.flow.rest.dex.authentication.BasicAuthentication.class};
  }

  /**
   * Whether the scheme actually requires a flow context.
   *
   * @return		true if required
   */
  @Override
  protected boolean requiresFlowContext() {
    return m_PromptForPassword;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  protected boolean doInteract() {
    boolean		result;
    PasswordDialog 	dlg;

    dlg = new PasswordDialog(null, ModalityType.DOCUMENT_MODAL);
    dlg.setLocationRelativeTo(getFlowContext().getParentComponent());
    ((Flow) getFlowContext().getRoot()).registerWindow(dlg, dlg.getTitle());
    dlg.setVisible(true);
    ((Flow) getFlowContext().getRoot()).deregisterWindow(dlg);
    result = (dlg.getOption() == PasswordDialog.APPROVE_OPTION);

    if (result)
      m_ActualPassword = dlg.getPassword();

    return result;
  }

  /**
   * Generates the authentication parameters for sending to the server.
   *
   * @param errors  	for collecting errors
   * @return		the generated key-value pairs, null in case of an error
   */
  @Override
  protected BaseKeyValuePair[] doGenerate(MessageCollection errors) {
    if (m_ActualPassword == null) {
      if (m_PromptForPassword) {
	if (!doInteract()) {
	  errors.add("User canceled password dialog!");
	  return null;
	}
      }
      else {
        m_ActualPassword = m_Password;
      }
      if (isLoggingEnabled())
        getLogger().info("user/password: " + m_User + "/" + m_ActualPassword);
    }

    return new BaseKeyValuePair[]{
      new BaseKeyValuePair(adams.flow.rest.dex.authentication.BasicAuthentication.KEY_USER, m_User),
      new BaseKeyValuePair(adams.flow.rest.dex.authentication.BasicAuthentication.KEY_PASSWORD, m_ActualPassword.getValue()),
    };
  }
}
