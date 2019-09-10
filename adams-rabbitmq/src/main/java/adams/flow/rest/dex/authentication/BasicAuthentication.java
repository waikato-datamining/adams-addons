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
 * BasicAuthentication.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.dex.authentication;

import adams.core.Utils;
import adams.core.base.BasePassword;
import adams.core.base.BaseString;

import java.util.HashMap;
import java.util.Map;

/**
 * Matches the user/password in the parameters against the provided ones.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BasicAuthentication
  extends AbstractAuthentication {

  private static final long serialVersionUID = -7676121366240447918L;

  /** the key for the user. */
  public final static String KEY_USER = "user";

  /** the key for the password. */
  public final static String KEY_PASSWORD = "password";

  /** the users. */
  protected BaseString[] m_Users;

  /** the passwords. */
  protected BasePassword[] m_Passwords;

  /** for hashing the users. */
  protected transient Map<String,String> m_Lookup;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Matches the user (key: " + KEY_USER + ") and password (key: " + KEY_PASSWORD + ") "
      + "obtained from the parameters against the provided users/passwords.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "user", "users",
      new BaseString[0]);

    m_OptionManager.add(
      "password", "passwords",
      new BasePassword[0]);
  }

  /**
   * Sets the users.
   *
   * @param value	the users
   */
  public void setUsers(BaseString[] value) {
    m_Users     = value;
    m_Passwords = (BasePassword[]) Utils.adjustArray(m_Passwords, m_Users.length, new BasePassword());
    reset();
  }

  /**
   * Returns the users.
   *
   * @return		the users
   */
  public BaseString[] getUsers() {
    return m_Users;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String usersTipText() {
    return "The names of the users that can connect.";
  }

  /**
   * Sets the passwords.
   *
   * @param value	the passwords
   */
  public void setPasswords(BasePassword[] value) {
    m_Passwords = value;
    m_Users     = (BaseString[]) Utils.adjustArray(m_Users, m_Passwords.length, new BaseString());
    reset();
  }

  /**
   * Returns the passwords.
   *
   * @return		the passwords
   */
  public BasePassword[] getPasswords() {
    return m_Passwords;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String passwordsTipText() {
    return "The passwords for the users.";
  }

  /**
   * Performs the authentication.
   *
   * @param params	the parameters to use use for authentication
   * @return		null if successfully authenticated, otherwise error message
   */
  @Override
  protected String doAuthenticate(Map<String, String> params) {
    int		i;

    // initialize user/pw lookup
    if (m_Lookup == null) {
      m_Lookup = new HashMap<>();
      for (i = 0; i < m_Users.length; i++)
        m_Lookup.put(m_Users[i].getValue(), m_Passwords[i].getValue());
      if (isLoggingEnabled())
        getLogger().info("users/passwords: " + m_Lookup);
    }

    if (!params.containsKey(KEY_USER))
      return "User parameter missing: " + KEY_USER;
    if (!params.containsKey(KEY_PASSWORD))
      return "Password parameter missing: " + KEY_PASSWORD;
    if (m_Lookup.containsKey(params.get(KEY_USER)) && m_Lookup.get(params.get(KEY_USER)).equals(params.get(KEY_PASSWORD)))
      return null;
    else
      return "Invalid user/password!";
  }
}
