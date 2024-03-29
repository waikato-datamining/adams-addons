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
 * CustomersByNameCallableTransformer.java
 * Copyright (C) 2012-2019 University of Waikato, Hamilton, New Zealand
 */
package com.example.customerservice.flow;

import adams.core.License;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.flow.webservice.AbstractWebServiceClientTransformerWithCallableTransformer;
import adams.flow.webservice.WebserviceUtils;
import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.CustomerServiceService;

import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.util.List;

/**
 * Simple client for querying customer names and post-processing the names
 * with a callable transformer.
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
public class CustomersByNameCallableTransformer
  extends AbstractWebServiceClientTransformerWithCallableTransformer<String,String> {

  /** for serialization. */
  private static final long serialVersionUID = -5099626472532203256L;
  
  /** the name of the customers to look up. */
  protected String m_CustomerName;
  
  /** the provided customer name. */
  protected String m_ProvidedCustomerName;

  /** the service instance. */
  protected transient CustomerServiceService m_Service;

  /** the port instance. */
  protected transient CustomerService m_Port;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns customer names.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "customer-name", "customerName",
	    "Smith");
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Service = null;
    m_Port    = null;
  }

  /**
   * Sets the customer name to look up.
   * 
   * @param value	the name
   */
  public void setCustomerName(String value) {
    m_CustomerName = value;
    reset();
  }
  
  /**
   * Returns the customer name to look up.
   * 
   * @return		the name
   */
  public String getCustomerName() {
    return m_CustomerName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customerNameTipText() {
    return "The customer name to look up.";
  }

  /**
   * Returns the classes that are accepted input.
   * 
   * @return		the classes that are accepted, null if no input
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Sets the data for the request, if any.
   * 
   * @param value	the request data
   */
  @Override
  public void setRequestData(String value) {
    m_ProvidedCustomerName = value;
  }

  /**
   * Returns the WSDL location.
   * 
   * @return		the location
   */
  @Override
  public URL getWsdlLocation() {
    return getClass().getClassLoader().getResource("wsdl/customerservice/CustomerService.wsdl");
  }

  /**
   * Queries the webservice.
   * 
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  protected void doQuery() throws Exception {
    String			name;
    String			customer;
    
    if (m_ProvidedCustomerName == null)
      name = m_CustomerName;
    else
      name = m_ProvidedCustomerName;
    
    if (m_Service == null) {
      m_Service = new CustomerServiceService(getWsdlLocation());
      m_Port    = m_Service.getCustomerServicePort();
      WebserviceUtils.configureClient(
        m_Owner,
        m_Port,
        m_ConnectionTimeout,
        m_ReceiveTimeout,
        (getUseAlternativeURL() ? getAlternativeURL() : null),
        m_InInterceptor,
        m_OutInterceptor);
      WebserviceUtils.enableSchemaValidation(((BindingProvider) m_Port));
    }
    List<Customer> customers = m_Port.getCustomersByName(name);
    customer = customers.get(0).getCustomerId() + ": " + customers.get(0).getName() + ", " + Utils.flatten(customers.get(0).getAddress(), " ");

    try {
      setResponseData(applyTransformer(customer));
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to apply callable transformer", e);
    }
    m_ProvidedCustomerName = null;
  }

  /**
   * Returns the classes that this processor generates.
   * 
   * @return		the classes, null if no output
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Cleans up the client.
   */
  @Override
  public void cleanUp() {
    m_Service = null;
    m_Port    = null;

    super.cleanUp();
  }
}
