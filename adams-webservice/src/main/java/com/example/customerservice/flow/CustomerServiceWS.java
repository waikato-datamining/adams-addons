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
 * CustomerServiceWS.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package com.example.customerservice.flow;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.flow.webservice.AbstractWebServiceProvider;
import com.example.customerservice.CustomerService;
import com.example.customerservice.server.CustomerServiceImpl;
import org.apache.cxf.jaxws.EndpointImpl;

import javax.xml.ws.Endpoint;

/**
 * Simple webservice that returns example customer data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    author = "Apache CXF",
    license = License.APACHE2,
    note = "Code from 'WSDL first' example",
    url = "http://cxf.apache.org/docs/sample-projects.html"
)
public class CustomerServiceWS
  extends AbstractWebServiceProvider {

  /** for serialization. */
  private static final long serialVersionUID = 6120554466219373496L;

  /** the endpoint. */
  protected EndpointImpl m_Endpoint;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Provides a customer service webservice.\n"
	+ "Enable logging to see inbound/outgoing messages.";
  }

  /**
   * Returns the default URL for the service.
   * 
   * @return		the URL
   */
  @Override
  public String getDefaultURL() {
    return "http://localhost:9090/CustomerServicePort";
  }

  /**
   * Performs the actual start of the service.
   * 
   * @throws Exception	if start fails
   */
  @Override
  protected void doStart() throws Exception {
    CustomerService 	implementor;
    
    implementor = new CustomerServiceImpl(this);
    m_Endpoint = (EndpointImpl) Endpoint.create(implementor);
    configureTLS(m_Endpoint, getURL());
    m_Endpoint.publish(getURL());

    configureInterceptors(m_Endpoint);
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
