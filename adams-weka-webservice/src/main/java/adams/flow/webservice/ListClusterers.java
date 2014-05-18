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
 * ListClusterers.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.ws.BindingProvider;

import nz.ac.waikato.adams.webservice.weka.WekaService;
import nz.ac.waikato.adams.webservice.weka.WekaServiceService;

/**
 * Lists all currently available clusterer models.
 * 
 * @author msf8
 * @version $Revision$
 */
public class ListClusterers 
extends AbstractWebServiceClientSource<ArrayList<String>>{

  /** for serialization*/
  private static final long serialVersionUID = -5574821589508118074L;

  /** list of clusterers returned */
  protected ArrayList<String> m_Returned;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "displays a list of all the clusterers currently stored";
  }

  /**
   * Returns the classes that this client generates.
   * 
   * @return		the classes
   */
  @Override
  public Class[] generates() {
    return new Class[] {ArrayList.class};
  }

  /**
   * Checks whether there is any response data to be collected.
   * 
   * @return		true if data can be collected
   * @see		#getResponseData()
   */
  @Override
  public boolean hasResponseData() {
    return m_Returned != null;
  }

  /**
   * Returns the response data, if any.
   * 
   * @return		the response data
   */
  @Override
  public ArrayList<String> getResponseData() {
    ArrayList<String >toReturn = m_Returned;
    m_Returned = null;
    return toReturn;
  }

  /**
   * Returns the WSDL location.
   * 
   * @return		the location
   */
  @Override
  protected URL getWsdlLocation() {
    return getClass().getClassLoader().getResource("wsdl/weka/WekaService.wsdl");
  }

  /**
   * Performs the actual webservice query.
   * 
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  protected void doQuery() throws Exception {
    WekaServiceService wekaServiceService;
    WekaService wekaService;
    wekaServiceService = new WekaServiceService(getWsdlLocation());
    wekaService = wekaServiceService.getWekaServicePort();
    WebserviceUtils.configureClient(wekaService, m_ConnectionTimeout, m_ReceiveTimeout, getUseAlternativeURL() ? getAlternativeURL() : null);
    //check against schema
    WebserviceUtils.enableSchemaValidation(((BindingProvider) wekaService));
    m_Returned = (ArrayList<String>)wekaService.listClusterers();
  }
}
