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
import adams.core.base.BaseKeyValuePair;
import adams.core.base.BasePassword;

/**
 * Generates simple user/password authentication.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BasicAuthentication
  extends AbstractClientAuthentication
  implements ClassCrossReference {

  private static final long serialVersionUID = -8658731460295213717L;

  /** the user. */
  protected String m_User;

  /** the password. */
  protected BasePassword m_Password;

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
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{adams.flow.rest.dex.authentication.BasicAuthentication.class};
  }

  /**
   * Generates the authentication parameters for sending to the server.
   *
   * @param errors  	for collecting errors
   * @return		the generated key-value pairs, null in case of an error
   */
  @Override
  protected BaseKeyValuePair[] doGenerate(MessageCollection errors) {
    return new BaseKeyValuePair[]{
      new BaseKeyValuePair(adams.flow.rest.dex.authentication.BasicAuthentication.KEY_USER, m_User),
      new BaseKeyValuePair(adams.flow.rest.dex.authentication.BasicAuthentication.KEY_PASSWORD, m_Password.getValue()),
    };
  }
}
