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
 * RESTSink.java
 * Copyright (C) 2018-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import adams.flow.core.AbstractRESTClient;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.flow.rest.RESTClient;
import adams.flow.rest.RESTClientConsumer;
import adams.flow.rest.echo.EchoClientSink;

/**
 <!-- globalinfo-start -->
 * Sends data to a REST webservice.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: RESTSink
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
 * <pre>-client &lt;adams.flow.rest.RESTClient&gt; (property: client)
 * &nbsp;&nbsp;&nbsp;The webservice client to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.rest.echo.EchoClientSink -out-interceptor adams.flow.rest.interceptor.outgoing.NullGenerator
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RESTSink
  extends AbstractRESTClient
  implements InputConsumer {

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
    return "Sends data to a REST webservice.";
  }

  /**
   * Returns the default client to use.
   *
   * @return		the client
   */
  @Override
  protected RESTClientConsumer getDefaultClient() {
    return new EchoClientSink();
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result       = query();
    m_InputToken = null;

    return result;
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
