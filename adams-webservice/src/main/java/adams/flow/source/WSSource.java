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

/**
 * WSSource.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.flow.core.AbstractWSClient;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.webservice.WebServiceClient;
import adams.flow.webservice.WebServiceClientConsumer;
import adams.flow.webservice.WebServiceClientProducer;

import com.example.customerservice.flow.CustomersByName;

/**
 <!-- globalinfo-start -->
 * Obtains data from a webservice and forwards it.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WSSource
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-client &lt;adams.flow.webservice.WebServiceClient&gt; (property: client)
 * &nbsp;&nbsp;&nbsp;The webservice client to use.
 * &nbsp;&nbsp;&nbsp;default: com.example.customerservice.flow.CustomersByName
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WSSource
  extends AbstractWSClient
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
	protected WebServiceClientConsumer getDefaultClient() {
    return new CustomersByName();
  }
  
  /**
   * Checks the client.
   * 
   * @param value	the client to check
   * @return		null if accepted, otherwise error message
   */
  @Override
	protected String checkClient(WebServiceClient value) {
    if (!(value instanceof WebServiceClientProducer))
      return "Does not implement " + WebServiceClientProducer.class.getName() + "!";
    return null;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return ((WebServiceClientProducer) m_Client).generates();
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
    return ((WebServiceClientProducer) m_Client).hasResponseData();
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    return new Token(((WebServiceClientProducer) m_Client).getResponseData());
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
