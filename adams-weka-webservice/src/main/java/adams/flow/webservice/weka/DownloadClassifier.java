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
 * DownloadClassifier.java
 * Copyright (C) 2014-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.webservice.weka;

import adams.core.SerializationHelper;
import adams.flow.webservice.AbstractWebServiceClientTransformer;
import adams.flow.webservice.WebserviceUtils;
import nz.ac.waikato.adams.webservice.weka.DownloadClassifierResponseObject;
import nz.ac.waikato.adams.webservice.weka.WekaService;
import nz.ac.waikato.adams.webservice.weka.WekaServiceService;
import weka.classifiers.Classifier;

import javax.xml.ws.BindingProvider;
import java.net.URL;

/**
 * Client for download a classifier model.
 * 
 * @author FracPete (fracpete at waikato ac dot nz)
 */
public class DownloadClassifier 
extends AbstractWebServiceClientTransformer<nz.ac.waikato.adams.webservice.weka.DownloadClassifier, Classifier> {

  /** for serialization*/
  private static final long serialVersionUID = -4596049331963785695L;

  /** download input object */
  protected nz.ac.waikato.adams.webservice.weka.DownloadClassifier m_Download;

  /** response object */
  protected DownloadClassifierResponseObject m_Returned;

  /** the service instance. */
  protected transient WekaServiceService m_Service;

  /** the port instance. */
  protected transient WekaService m_Port;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Downloads a previously generated classifier model.";
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
   * Returns the classes that are accepted input.
   * 
   * @return		the classes that are accepted
   */
  @Override
  public Class[] accepts() {
    return new Class[]{nz.ac.waikato.adams.webservice.weka.DownloadClassifier.class};
  }

  /**
   * Sets the data for the request, if any.
   * 
   * @param value	the request data
   */
  @Override
  public void setRequestData(nz.ac.waikato.adams.webservice.weka.DownloadClassifier value) {
    m_Download = value;

  }

  /**
   * Returns the classes that this client generates.
   * 
   * @return		the classes
   */
  @Override
  public Class[] generates() {
    return new Class[]{Classifier.class};
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
        m_OutInterceptor);
      //check against schema
      WebserviceUtils.enableSchemaValidation(((BindingProvider) m_Port));
    }
    
    m_Returned = m_Port.downloadClassifier(m_Download.getModelName());
    // failed to download model?
    if (m_Returned.getErrorMessage() != null)
      throw new IllegalStateException(m_Returned.getErrorMessage());
    setResponseData((Classifier) SerializationHelper.read(m_Returned.getModelData().getInputStream()));

    m_Download = null;
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
