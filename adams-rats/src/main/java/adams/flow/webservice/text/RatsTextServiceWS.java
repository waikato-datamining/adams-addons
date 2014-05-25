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
 * RatsTextServiceWS.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.webservice.text;

import javax.xml.ws.Endpoint;

import nz.ac.waikato.adams.webservice.rats.text.RatsTextService;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;

import adams.core.Utils;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.standalone.rats.RatInput;
import adams.flow.standalone.rats.RatInputUser;
import adams.flow.webservice.AbstractWebServiceProvider;

/**
 * Webservice for RATS Spectrum.
 * 
 * @author Fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 2085 $
 */
public class RatsTextServiceWS
  extends AbstractWebServiceProvider
  implements RatInputUser {

  /** for serilaization */
  private static final long serialVersionUID = -6865165378146103361L;

  /** end point for the web service */
  protected transient EndpointImpl m_Endpoint;
  
  /** the webservice implementation to use. */
  protected RatsTextService m_Implementation;
  
  /** the associated rat input. */
  protected RatInput m_RatInput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Provides a KNIR web service with the following services available:\n"
	+ "- get spectrum\n"
	+ "- put spectrum\n"
	+ "- transform spectrum\n"
	+ "";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	"implementation", "implementation", 
	new SimpleRatsTextService());
  }

  /**
   * Returns the default URL for the service.
   * 
   * @return		the URL
   */
  @Override
  public String getDefaultURL() {
    return "http://localhost:9090/RatsTextServicePort";
  }
  
  /**
   * Sets the Rat input associated with this webservice.
   * 
   * @param value	the rat input
   */
  public void setRatInput(RatInput value) {
    m_RatInput = value;
  }
  
  /**
   * Returns the Rat input associated with this webservice.
   * 
   * @return		the rat input
   */
  public RatInput getRatInput() {
    return m_RatInput;
  }

  /**
   * Sets the webservice implementation to use.
   * 
   * @param value	the implementation
   */
  public void setImplementation(RatsTextService value) {
    m_Implementation = value;
    reset();
  }

  /**
   * Returns the webservice implementation to use.
   * 
   * @return 		the implementation
   */
  public RatsTextService getImplementation() {
    return m_Implementation;
  }

  /**
   * Description of this option.
   * 
   * @return 		the description for the GUI
   */
  public String implementationTipText() {
    return "The implementation of the webservice to use.";
  }

  /**
   * Performs the actual start of the service.
   * 
   * @throws Exception	if start fails
   */
  @Override
  protected void doStart() throws Exception {
    RatsTextService implementer;

    if (m_Implementation instanceof OptionHandler)
      implementer = (RatsTextService) OptionUtils.shallowCopy((OptionHandler) m_Implementation, false);
    else
      implementer = (RatsTextService) Utils.deepCopy(m_Implementation);
    if (implementer instanceof OwnedByRatsTextServiceWS)
      ((OwnedByRatsTextServiceWS) implementer).setOwner(this);
    m_Endpoint  = (EndpointImpl) Endpoint.publish(getURL(), implementer);

    javax.xml.ws.soap.SOAPBinding binding = (javax.xml.ws.soap.SOAPBinding) m_Endpoint.getBinding();
    binding.setMTOMEnabled(true);
    	
    // Adding logging for incoming and outgoing messages
    if (isLoggingEnabled()) {
      m_Endpoint.getServer().getEndpoint().getInInterceptors().add(new LoggingInInterceptor());
      m_Endpoint.getServer().getEndpoint().getOutInterceptors().add(new LoggingOutInterceptor());
    }
  }

  /**
   * Performs the actual stop of the service.
   * 
   * @throws Exception	if stopping fails
   */
  @Override
  protected void doStop() throws Exception {
    if (m_Endpoint != null) {
      m_Endpoint.getServer().stop();
      m_Endpoint = null;
    }
  }
}
