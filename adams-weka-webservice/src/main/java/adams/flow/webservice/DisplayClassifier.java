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
 * DisplayClassifier.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import java.net.URL;

import javax.xml.ws.BindingProvider;

import nz.ac.waikato.adams.webservice.weka.DisplayClassifierResponseObject;
import nz.ac.waikato.adams.webservice.weka.WekaService;
import nz.ac.waikato.adams.webservice.weka.WekaServiceService;

/**
 * Displays the string representation of a built classifier model.
 * 
 * @author msf8
 * @version $Revision$
 */
public class DisplayClassifier 
extends AbstractWebServiceClientSource<String>{

  /** for serialization*/
  private static final long serialVersionUID = 1297440704076575307L;
  
  /**string returned to display */
  protected String m_ReturnedString;
  
  /** name of model to display */
  protected String m_Classifier;
  
  /** response object */
  protected DisplayClassifierResponseObject m_Returned;

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
   * Checks whether there is any response data to be collected.
   * 
   * @return		true if data can be collected
   * @see		#getResponseData()
   */
  @Override
  public boolean hasResponseData() {
    return m_ReturnedString != null;
  }

  /**
   * Returns the response data, if any.
   * 
   * @return		the response data
   */
  @Override
  public String getResponseData() {
    String toReturn = m_ReturnedString;
    m_ReturnedString = null;
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
    WebserviceUtils.configureClient(wekaService, m_ConnectionTimeout, m_ReceiveTimeout);
    //check against schema
    WebserviceUtils.enableSchemaValidation(((BindingProvider) wekaService));
    m_Returned = wekaService.displayClassifier(m_Classifier);
    // failed to generate data?
    if (m_Returned.getErrorMessage() != null)
      throw new IllegalStateException(m_Returned.getErrorMessage());
    m_ReturnedString = m_Returned.getDisplayString();

    m_Classifier = null;
  }
}
