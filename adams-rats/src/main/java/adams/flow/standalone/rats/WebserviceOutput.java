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
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import adams.flow.core.Unknown;
import adams.flow.webservice.WebServiceClient;
import adams.flow.webservice.WebServiceClientConsumer;

import com.example.customerservice.flow.UpdateCustomer;

/**
 <!-- globalinfo-start -->
 * Allows to send data to webservices using the supplied client.
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
 * &nbsp;&nbsp;&nbsp;default: com.example.customerservice.flow.UpdateCustomer
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

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to send data to webservices using the supplied client.";
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
    
    result = null;
    
    try {
      m_Client.setOwner(getOwner());
      ((WebServiceClientConsumer) m_Client).setRequestData(m_Input);
      m_Client.query();
    }
    catch (Exception e) {
      result = handleException("Failed to send data to webservice!", e);
    }
    
    return result;
  }
}
