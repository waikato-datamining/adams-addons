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
 * CustomersByNameGlobalTransformer.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package com.example.customerservice.flow;

import java.net.URL;
import java.util.List;

import adams.core.License;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.flow.webservice.AbstractWebServiceClientTransformerWithCallableTransformer;
import adams.flow.webservice.WebserviceUtils;

import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.CustomerServiceService;

/**
 * Simple client for querying customer names and post-processing the names
 * with a global transformer.
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
public class CustomersByNameGlobalTransformer
  extends AbstractWebServiceClientTransformerWithCallableTransformer<String,String> {

  /** for serialization. */
  private static final long serialVersionUID = -5099626472532203256L;
  
  /** the name of the customers to look up. */
  protected String m_CustomerName;
  
  /** the provided customer name. */
  protected String m_ProvidedCustomerName;
  
  /** the list of customers that were obtained from webservice. */
  protected List<Customer> m_Customers;

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
  protected URL getWsdlLocation() {
    return getClass().getClassLoader().getResource("wsdl/customerservice/CustomerService.wsdl");
  }

  /**
   * Queries the webservice.
   * 
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  protected void doQuery() throws Exception {
    CustomerServiceService 	customerServiceService;
    CustomerService 		customerService;
    String			name;
    
    if (m_ProvidedCustomerName == null)
      name = m_CustomerName;
    else
      name = m_ProvidedCustomerName;
    
    m_Customers            = null;
    customerServiceService = new CustomerServiceService(getWsdlLocation());
    customerService        = customerServiceService.getCustomerServicePort();
    WebserviceUtils.configureClient(customerService, m_ConnectionTimeout, m_ReceiveTimeout, getAlternativeURL());
    m_Customers            = customerService.getCustomersByName(name);
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
   * Checks whether there is any response data to be collected.
   * 
   * @return		true if data can be collected
   * @see		#getResponseData()
   */
  public boolean hasResponseData() {
    return (m_Customers != null) && (m_Customers.size() > 0);
  }

  /**
   * Returns the response data, if any.
   * 
   * @return		the response data
   */
  @Override
  public String getResponseData() {
    String 	result;
    
    result = m_Customers.get(0).getCustomerId() + ": " + m_Customers.get(0).getName() + ", " + Utils.flatten(m_Customers.get(0).getAddress(), " ");
    m_Customers.remove(0);
    
    try {
      result = applyTransformer(result);
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to apply global transformer", e);
    }
    
    return result;
  }
}
