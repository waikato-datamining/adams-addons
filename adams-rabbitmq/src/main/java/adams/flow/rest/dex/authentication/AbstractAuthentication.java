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
 * AbstractAuthentication.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.dex.authentication;

import adams.core.option.AbstractOptionHandler;

import java.util.Map;

/**
 * Ancestor for authentication schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAuthentication
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 3157981057620546957L;

  /**
   * Hook method for checks.
   *
   * @param params	the parameters to check
   * @return		null if check passed, otherwise error message
   */
  protected String check(Map<String,String> params) {
    return null;
  }

  /**
   * Performs the authentication.
   *
   * @param params	the parameters to use use for authentication
   * @return		null if successfully authenticated, otherwise error message
   */
  protected abstract String doAuthenticate(Map<String,String> params);

  /**
   * Performs the authentication.
   *
   * @param params	the parameters to use use for authentication
   * @return		null if successfully authenticated, otherwise error message
   */
  public String authenticate(Map<String,String> params) {
    String	result;

    result = check(params);
    if (result == null)
      result = doAuthenticate(params);

    return result;
  }
}
