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
 * DisplayCluster.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.weka;

import adams.flow.webservice.AbstractWebServiceClientSource;
import adams.flow.webservice.WebserviceUtils;
import nz.ac.waikato.adams.webservice.weka.DisplayClustererResponseObject;
import nz.ac.waikato.adams.webservice.weka.WekaService;
import nz.ac.waikato.adams.webservice.weka.WekaServiceService;

import javax.xml.ws.BindingProvider;
import java.net.URL;

/**
 * Displays the string representation of a clusterer model.
 * 
 * @author msf8
 */
public class DisplayCluster 
extends AbstractWebServiceClientSource<String> {

  /** for serialization */
  private static final long serialVersionUID = 8229995796562261847L;

  /** clusterer to display */
  protected String m_Clusterer;

  /** the service instance. */
  protected transient WekaServiceService m_Service;

  /** the port instance. */
  protected transient WekaService m_Port;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public String globalInfo() {
    return "displays a string representing a clusterer";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	"clusterer", "clusterer", "");
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
   * set the name of the clusterer.
   * 
   * @param s	name of clusterer to display
   */
  public void setClusterer(String s) {
    m_Clusterer = s;
    reset();
  }
  
  /**
   * get the name of the clusterer.
   * 
   * @return	name of the clusterer to display
   */
  public String getClusterer() {
    return m_Clusterer;
  }
  
  /**
   * description of this option.
   * 
   * @return	Description of the clusterer name option
   */
  public String clusterTipText() {
    return "name of clusterer to display";
  }
  
  /**
   * Returns the classes that this client generates.
   * 
   * @return		the classes
   */
  @Override
  public Class[] generates() {
    return new Class[] {String.class};
  }

  /**
   * Returns the WSDL location.
   * 
   * @return		the location
   */
  @Override
  public URL getWsdlLocation() {
    return getClass().getClassLoader().getResource("wsdl/weka/WekaService.wsdl");

  }

  /**
   * Performs the actual webservice query.
   * 
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  protected void doQuery() throws Exception {
    if (m_Service == null) {
      m_Service = new WekaServiceService(getWsdlLocation());
      m_Port = m_Service.getWekaServicePort();
      WebserviceUtils.configureClient(
        m_Owner,
        m_Port,
        m_ConnectionTimeout,
        m_ReceiveTimeout,
        (getUseAlternativeURL() ? getAlternativeURL() : null),
        m_InInterceptor,
        null);
      //check against schema
      WebserviceUtils.enableSchemaValidation(((BindingProvider) m_Port));
    }
    DisplayClustererResponseObject returned = m_Port.displayClusterer(m_Clusterer);
    // failed to generate data?
    if (returned.getErrorMessage() != null)
      throw new IllegalStateException(returned.getErrorMessage());
    setResponseData(returned.getDisplayString());

    m_Clusterer = null;
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
