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
 * WebserviceOutput.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.flow.core.Unknown;
import adams.flow.standalone.rats.output.webservice.AbstractWebserviceResponseDataPostProcessor;
import adams.flow.standalone.rats.output.webservice.NullPostProcessor;
import adams.flow.webservice.WebServiceClient;
import adams.flow.webservice.WebServiceClientConsumer;
import adams.flow.webservice.WebServiceClientProducer;
import com.example.customerservice.flow.UpdateCustomer;

/**
 <!-- globalinfo-start -->
 * Allows to send data to webservices using the supplied client.<br>
 * If the webservice client implements adams.flow.webservice.WebServiceClientProducer then the supplied post-processor can be used to inspect the response from the webservice, e.g., for inspecting any error messages.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-client &lt;adams.flow.webservice.WebServiceClient&gt; (property: client)
 * &nbsp;&nbsp;&nbsp;The webservice client to use.
 * &nbsp;&nbsp;&nbsp;default: com.example.customerservice.flow.UpdateCustomer -out-interceptor adams.flow.webservice.interceptor.outgoing.NullGenerator
 * </pre>
 * 
 * <pre>-response-post-processor &lt;adams.flow.standalone.rats.output.webservice.AbstractWebserviceResponseDataPostProcessor&gt; (property: responsePostProcessor)
 * &nbsp;&nbsp;&nbsp;The post-processor to use for the webservice response (if the client implements 
 * &nbsp;&nbsp;&nbsp;adams.flow.webservice.WebServiceClientProducer).
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.output.webservice.NullPostProcessor
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WebserviceOutput
  extends AbstractRatOutput {

  /** for serialization. */
  private static final long serialVersionUID = -3752727785209685369L;

  /** the webservice client to use. */
  protected WebServiceClient m_Client;

  /** the post-processor for the response (if applicable). */
  protected AbstractWebserviceResponseDataPostProcessor m_ResponsePostProcessor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Allows to send data to webservices using the supplied client.\n"
	+ "If the webservice client implements " + WebServiceClientProducer.class.getName() + " "
	+ "then the supplied post-processor can be used to inspect the response "
	+ "from the webservice, e.g., for inspecting any error messages.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "client", "client",
      getDefaultClient());

    m_OptionManager.add(
      "response-post-processor", "responsePostProcessor",
      new NullPostProcessor());
  }

  /**
   * Returns the default client to use.
   *
   * @return		the client
   */
  protected WebServiceClient getDefaultClient() {
    return new UpdateCustomer();
  }

  /**
   * Checks the client.
   *
   * @param value	the client to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkClient(WebServiceClient value) {
    if (!(value instanceof WebServiceClientConsumer))
      return "Does not implement " + WebServiceClientConsumer.class.getName() + "!";
    return null;
  }

  /**
   * Sets the webservice client to use.
   *
   * @param value	the webservice client to use
   */
  public void setClient(WebServiceClient value) {
    String	msg;

    msg = checkClient(value);
    if (msg == null) {
      m_Client = value;
      m_Client.setOwner(getOwner());
      reset();
    }
    else {
      getLogger().severe("Failed to set client: " + msg);
    }
  }

  /**
   * Returns the webservice client in use.
   *
   * @return		the webservice client in use
   */
  public WebServiceClient getClient() {
    return m_Client;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String clientTipText() {
    return "The webservice client to use.";
  }

  /**
   * Sets the post-processor for the webservice response, if the client
   * implements {@link WebServiceClientProducer}.
   *
   * @param value	the post-processor
   */
  public void setResponsePostProcessor(AbstractWebserviceResponseDataPostProcessor value) {
    m_ResponsePostProcessor = value;
    reset();
  }

  /**
   * Returns the post-processor for the webservice response, if the client
   * implements {@link WebServiceClientProducer}.
   *
   * @return		the post-processor
   */
  public AbstractWebserviceResponseDataPostProcessor getResponsePostProcessor() {
    return m_ResponsePostProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String responsePostProcessorTipText() {
    return "The post-processor to use for the webservice response (if the client implements " + WebServiceClientProducer.class.getName() + ").";
  }

  /**
   * Returns the type of data that gets accepted.
   *
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    if (m_Client != null)
      return ((WebServiceClientConsumer) m_Client).accepts();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Performs the actual transmission.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String	result;
    Object	response;

    result = null;

    try {
      m_Client.setOwner(getOwner());
      ((WebServiceClientConsumer) m_Client).setRequestData(m_Input);
      m_Client.query();
    }
    catch (Exception e) {
      result = handleException("Failed to send data to webservice!", e);
    }

    if (result == null) {
      if (m_Client instanceof WebServiceClientProducer) {
	try {
	  response = ((WebServiceClientProducer) m_Client).getResponseData();
	  m_ResponsePostProcessor.setFlowContext(getOwner());
	  m_ResponsePostProcessor.postProcess(response);
	}
	catch (Exception e) {
	  result = handleException("Failed to post-process response data from webservice!", e);
	}
      }
    }

    return result;
  }
}
