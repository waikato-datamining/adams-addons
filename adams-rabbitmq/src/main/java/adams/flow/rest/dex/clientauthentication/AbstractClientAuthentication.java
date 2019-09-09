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

package adams.flow.rest.dex.clientauthentication;

import adams.core.MessageCollection;
import adams.core.base.BaseKeyValuePair;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for client authentication schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractClientAuthentication
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 3157981057620546957L;

  /**
   * Hook method for checks.
   *
   * @return		null if check passed, otherwise error message
   */
  protected String check() {
    return null;
  }

  /**
   * Generates the authentication parameters for sending to the server.
   *
   * @param errors  	for collecting errors
   * @return		the generated key-value pairs, null in case of an error
   */
  protected abstract BaseKeyValuePair[] doGenerate(MessageCollection errors);

  /**
   * Generates the authentication parameters for sending to the server.
   *
   * @param errors  	for collecting errors
   * @return		the generated key-value pairs, null in case of an error
   */
  public BaseKeyValuePair[] generate(MessageCollection errors) {
    BaseKeyValuePair[]	result;
    String 		msg;

    result = null;
    msg    = check();
    if (msg == null)
      result = doGenerate(errors);

    if (errors.isEmpty())
      return result;
    else
      return null;
  }
}
