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
 * DisplayClassifier.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.meka;

import adams.flow.webservice.AbstractWebServiceClientSource;
import adams.flow.webservice.WebserviceUtils;
import nz.ac.waikato.adams.webservice.meka.DisplayClassifierResponseObject;
import nz.ac.waikato.adams.webservice.meka.MekaService;
import nz.ac.waikato.adams.webservice.meka.MekaServiceService;

import javax.xml.ws.BindingProvider;
import java.net.URL;

/**
 * Displays the string representation of a built classifier model.
 * 
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DisplayClassifier 
extends AbstractWebServiceClientSource<String> {

  /** for serialization*/
  private static final long serialVersionUID = 1297440704076575307L;

  /** name of model to display */
  protected String m_Classifier;

  /** the service instance. */
  protected transient MekaServiceService m_Service;

  /** the port instance. */
  protected transient MekaService m_Port;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "displays a string representing a classifier"; 
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	"classifier", "classifier", "");
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
   * set the name of the classifier to display.
   * 
   * @param s		name of classifier
   */
  public void setClassifier(String s) {
    m_Classifier = s;
    reset();
  }
  
  /**
   * get the name of the classifier to display.
   * 
   * @return		name of classifier
   */
  public String getClassifier() {
    return m_Classifier;
  }
  
  /**
   * Description of this option.
   * 
   * @return		description of the classifier option
   */
  public String classifierTipText() {
    return "name of the classifier to display";
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
    return getClass().getClassLoader().getResource("wsdl/meka/MekaService.wsdl");
  }

  /**
   * Performs the actual webservice query.
   * 
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  protected void doQuery() throws Exception {
    if (m_Service == null) {
      m_Service = new MekaServiceService(getWsdlLocation());
      m_Port = m_Service.getMekaServicePort();
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
    DisplayClassifierResponseObject returned = m_Port.displayClassifier(m_Classifier);
    // failed to generate data?
    if (returned.getErrorMessage() != null)
      throw new IllegalStateException(returned.getErrorMessage());
    setResponseData(returned.getDisplayString());

    m_Classifier = null;
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
