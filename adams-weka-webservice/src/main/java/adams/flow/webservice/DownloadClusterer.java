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
 * DownloadClusterer.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.webservice;

import java.net.URL;

import javax.xml.ws.BindingProvider;

import nz.ac.waikato.adams.webservice.weka.DownloadClustererResponseObject;
import nz.ac.waikato.adams.webservice.weka.WekaService;
import nz.ac.waikato.adams.webservice.weka.WekaServiceService;
import weka.clusterers.Clusterer;
import adams.core.SerializationHelper;

/**
 * Client for download a cluster model.
 * 
 * @author FracPete (fracpete at waikato ac dot nz)
 * @version $Revision$
 */
public class DownloadClusterer 
extends AbstractWebServiceClientTransformer<nz.ac.waikato.adams.webservice.weka.DownloadClusterer, Clusterer>{

  /** for serialization*/
  private static final long serialVersionUID = -4596049331963785695L;

  /** download input object */
  protected nz.ac.waikato.adams.webservice.weka.DownloadClusterer m_Download;

  /** classifier returned */
  protected Clusterer m_ReturnedClusterer;
  
  /** response object */
  protected DownloadClustererResponseObject m_Returned;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Downloads a previously generated cluster model.";
  }

  /**
   * Returns the classes that are accepted input.
   * 
   * @return		the classes that are accepted
   */
  @Override
  public Class[] accepts() {
    return new Class[]{nz.ac.waikato.adams.webservice.weka.DownloadClusterer.class};
  }

  /**
   * Sets the data for the request, if any.
   * 
   * @param value	the request data
   */
  @Override
  public void setRequestData(nz.ac.waikato.adams.webservice.weka.DownloadClusterer value) {
    m_Download = value;

  }

  /**
   * Returns the classes that this client generates.
   * 
   * @return		the classes
   */
  @Override
  public Class[] generates() {
    return new Class[]{Clusterer.class};
  }

  /**
   * Checks whether there is any response data to be collected.
   * 
   * @return		true if data can be collected
   * @see		#getResponseData()
   */
  @Override
  public boolean hasResponseData() {
    return m_ReturnedClusterer != null;
  }

  /**
   * Returns the response data, if any.
   * 
   * @return		the response data
   */
  @Override
  public Clusterer getResponseData() {
    Clusterer result = m_ReturnedClusterer;
    m_ReturnedClusterer = null;
    return result;
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
    WekaServiceService wekaServiceService;
    WekaService wekaService;
    wekaServiceService = new WekaServiceService(getWsdlLocation());
    wekaService = wekaServiceService.getWekaServicePort();
    WebserviceUtils.configureClient(wekaService, m_ConnectionTimeout, m_ReceiveTimeout, getUseAlternativeURL() ? getAlternativeURL() : null);
    //check against schema
    WebserviceUtils.enableSchemaValidation(((BindingProvider) wekaService));
    
    m_Returned = wekaService.downloadClusterer(m_Download.getModelName()); 
    // failed to download model?
    if (m_Returned.getErrorMessage() != null)
      throw new IllegalStateException(m_Returned.getErrorMessage());
    m_ReturnedClusterer = (Clusterer) SerializationHelper.read(m_Returned.getModelData().getInputStream());

    m_Download = null;
  }
}
