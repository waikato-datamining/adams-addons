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
 * RESTTransformer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.flow.core.AbstractRESTClient;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.rest.RESTClient;
import adams.flow.rest.RESTClientConsumer;
import adams.flow.rest.RESTClientProducer;
import adams.flow.rest.echo.EchoClientTransformer;

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
public class RESTTransformer
  extends AbstractRESTClient
  implements InputConsumer, OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = 3217721167611538066L;

  /** the input token. */
  protected Token m_InputToken;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sends data to a webservice and forwards the response.";
  }

  /**
   * Returns the default client to use.
   *
   * @return		the client
   */
  @Override
  protected RESTClientConsumer getDefaultClient() {
    return new EchoClientTransformer();
  }

  /**
   * Checks the client.
   *
   * @param value	the client to check
   * @return		null if accepted, otherwise error message
   */
  @Override
  protected String checkClient(RESTClient value) {
    if (!(value instanceof RESTClientConsumer))
      return "Does not implement " + RESTClientConsumer.class.getName() + "!";
    if (!(value instanceof RESTClientProducer))
      return "Does not implement " + RESTClientProducer.class.getName() + "!";
    return null;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return ((RESTClientConsumer) m_Client).accepts();
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  @Override
  public void input(Token token) {
    m_InputToken = token;
    ((RESTClientConsumer) m_Client).setRequestData(token.getPayload());
  }

  /**
   * Returns whether an input token is currently present.
   *
   * @return		true if input token present
   */
  public boolean hasInput() {
    return (m_InputToken != null);
  }

  /**
   * Returns the current input token, if any.
   *
   * @return		the input token, null if none present
   */
  public Token currentInput() {
    return m_InputToken;
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
    m_InputToken = null;
    return new Token(((RESTClientProducer) m_Client).getResponseData());
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    m_InputToken = null;
    super.wrapUp();
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
