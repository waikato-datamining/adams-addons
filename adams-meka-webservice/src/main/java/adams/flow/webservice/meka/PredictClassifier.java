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
 * PredictClassifier.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.webservice.meka;

import adams.flow.webservice.AbstractWebServiceClientTransformer;
import adams.flow.webservice.WebserviceUtils;
import nz.ac.waikato.adams.webservice.meka.Dataset;
import nz.ac.waikato.adams.webservice.meka.PredictClassifierResponseObject;
import nz.ac.waikato.adams.webservice.meka.MekaService;
import nz.ac.waikato.adams.webservice.meka.MekaServiceService;

import javax.xml.ws.BindingProvider;
import java.net.URL;

/**
 * client for using the predict web service .
 * 
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PredictClassifier 
extends AbstractWebServiceClientTransformer<nz.ac.waikato.adams.webservice.meka.PredictClassifier, Dataset> {

  /** for serialization*/
  private static final long serialVersionUID = -4596049331963785695L;

  /** predict input object */
  protected nz.ac.waikato.adams.webservice.meka.PredictClassifier m_Predict;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Makes a prediction using the meka web service.";
  }

  /**
   * Returns the classes that are accepted input.
   * 
   * @return		the classes that are accepted
   */
  @Override
  public Class[] accepts() {
    return new Class[] {nz.ac.waikato.adams.webservice.meka.PredictClassifier.class};
  }

  /**
   * Sets the data for the request, if any.
   * 
   * @param value	the request data
   */
  @Override
  public void setRequestData(nz.ac.waikato.adams.webservice.meka.PredictClassifier value) {
    m_Predict = value;

  }

  /**
   * Returns the classes that this client generates.
   * 
   * @return		the classes
   */
  @Override
  public Class[] generates() {
    return new Class[] {Dataset.class};
  }

  /**
   * Returns the WSDL location.
   * 
   * @return		the location
   */
  @Override
  public URL getWsdlLocation() {
    return getClass().getClassLoader().getResource("wsdl/meka/MekaService.wsdl");
  }

  /**
   * Performs the actual webservice query.
   * 
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  protected void doQuery() throws Exception {
    MekaServiceService mekaServiceService;
    MekaService mekaService;
    mekaServiceService = new MekaServiceService(getWsdlLocation());
    mekaService = mekaServiceService.getMekaServicePort();
    WebserviceUtils.configureClient(
	m_Owner,
	mekaService, 
	m_ConnectionTimeout, 
	m_ReceiveTimeout, 
	(getUseAlternativeURL() ? getAlternativeURL() : null),
	m_InInterceptor,
	m_OutInterceptor);
    //check against schema
    WebserviceUtils.enableSchemaValidation(((BindingProvider) mekaService));
    
    PredictClassifierResponseObject returned = mekaService.predictClassifier(m_Predict.getDataset(), m_Predict.getModelName());
    // failed to generate data?
    if (returned.getErrorMessage() != null)
      throw new IllegalStateException(returned.getErrorMessage());
    setResponseData(returned.getReturnDataset());

    m_Predict = null;
  }
}
