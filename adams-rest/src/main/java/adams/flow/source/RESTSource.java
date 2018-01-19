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
 * RESTSource.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.flow.core.AbstractRESTClient;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.rest.RESTClient;
import adams.flow.rest.RESTClientProducer;
import adams.flow.rest.echo.EchoClientSource;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RESTSource
  extends AbstractRESTClient
  implements OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = 3217721167611538066L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Obtains data from a webservice and forwards it.";
  }

  /**
   * Returns the default client to use.
   *
   * @return		the client
   */
  @Override
  protected RESTClientProducer getDefaultClient() {
    return new EchoClientSource();
  }

  /**
   * Checks the client.
   *
   * @param value	the client to check
   * @return		null if accepted, otherwise error message
   */
  @Override
  protected String checkClient(RESTClient value) {
    if (!(value instanceof RESTClientProducer))
      return "Does not implement " + RESTClientProducer.class.getName() + "!";
    return null;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return ((RESTClientProducer) m_Client).generates();
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    return query();
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return ((RESTClientProducer) m_Client).hasResponseData();
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    return new Token(((RESTClientProducer) m_Client).getResponseData());
  }

  /**
   * Cleans up the actor.
   */
  @Override
  public void cleanUp() {
    m_Client.cleanUp();
    super.cleanUp();
  }
}
