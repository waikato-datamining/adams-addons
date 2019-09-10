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
import adams.flow.rest.dex.authentication.NoAuthenticationRequired;

/**
 * Generates no authentication parameters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NoAuthentication
  extends AbstractClientAuthentication
  implements ClassCrossReference {

  private static final long serialVersionUID = -8658731460295213717L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates no authentication parameters.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{NoAuthenticationRequired.class};
  }

  /**
   * Whether the scheme actually requires a flow context.
   *
   * @return		true if required
   */
  protected boolean requiresFlowContext() {
    return false;
  }

  /**
   * Generates the authentication parameters for sending to the server.
   *
   * @param errors  	for collecting errors
   * @return		the generated key-value pairs, null in case of an error
   */
  @Override
  protected BaseKeyValuePair[] doGenerate(MessageCollection errors) {
    return new BaseKeyValuePair[0];
  }
}
