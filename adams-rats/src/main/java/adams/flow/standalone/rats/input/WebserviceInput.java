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
 * WebserviceInput.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.webservice.WebServiceClient;
import adams.flow.webservice.WebServiceClientConsumer;
import adams.flow.webservice.WebServiceClientProducer;

import com.example.customerservice.flow.CustomersByName;

/**
 <!-- globalinfo-start -->
 * Uses a webservice for retrieving data from a webservice. Needs to be wrapped in poller.
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: com.example.customerservice.flow.CustomersByName
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 2086 $
 */
public class WebserviceInput
extends AbstractRatInput {

  /** for serialization. */
  private static final long serialVersionUID = -3681678330127394451L;

  /** the webservice client to use. */
  protected WebServiceClient m_Client;

  /** the spectrum received via webservice. */
  protected Object m_Data;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses a webservice for retrieving data from a webservice. Needs to be wrapped in poller.";
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
  }

  /**
   * Returns the default client to use.
   * 
   * @return		the client
   */
  protected WebServiceClientConsumer getDefaultClient() {
    return new CustomersByName();
  }

  /**
   * Checks the client.
   * 
   * @param value	the client to check
   * @return		null if accepted, otherwise error message
   */
  protected String checkClient(WebServiceClient value) {
    if (!(value instanceof WebServiceClientProducer))
      return "Does not implement " + WebServiceClientProducer.class.getName() + "!";
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
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  @Override
  public Class generates() {
    if (m_Client != null)
      return ((WebServiceClientConsumer) m_Client).accepts()[0];
    else
      return Unknown.class;
  }

  /**
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    String	result;
    
    result = null;
    
    try {
      m_Client.setOwner(getOwner());
      m_Client.query();
    }
    catch (Exception e) {
      result = handleException("Failed to receive data from webservice!", e);
    }
    
    return result;
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
}
