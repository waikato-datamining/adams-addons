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
 * WekaServiceWS.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.webservice;

import nz.ac.waikato.adams.webservice.weka.WekaService;
import org.apache.cxf.jaxws.EndpointImpl;

import javax.xml.ws.Endpoint;

/**
 * Webservice for WEKA.
 * 
 * @author msf8
 * @author Fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaServiceWS
  extends AbstractWebServiceProvider {

  /** for serilaization */
  private static final long serialVersionUID = -6865165378146103361L;

  /** end point for the web service */
  protected transient EndpointImpl m_Endpoint;
  
  /** the webservice implementation to use. */
  protected WekaService m_Implementation;
  
  /** the number of classifiers to store in cache. */
  protected int m_ClassifierCacheSize;
  
  /** the number of clusterers to store in cache. */
  protected int m_ClustererCacheSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Provides a weka web service with the following services available:\n"
	+ "- training (classifiers/clusterers)\n"
	+ "- cross-validation (clusterers)\n"
	+ "- testing (classifiers)\n"
	+ "- making predictions (classifiers/clusterers)\n"
	+ "- list classifiers (classifiers/clusterers)\n"
	+ "- transform data\n"
	+ "Enable logging to see inbound/outgoing messages.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	"implementation", "implementation", 
	new SimpleWekaService());
  
    m_OptionManager.add(
	"classifier-cache-size", "classifierCacheSize", 
	10, 1, null);
    
    m_OptionManager.add(
	"clusterer-cache-size", "clustererCacheSize", 
	10, 1, null);
  }

  /**
   * Returns the default URL for the service.
   * 
   * @return		the URL
   */
  @Override
  public String getDefaultURL() {
    return "http://localhost:9090/WekaServicePort";
  }

  /**
   * Sets the webservice implementation to use.
   * 
   * @param value	the implementation
   */
  public void setImplementation(WekaService value) {
    m_Implementation = value;
    reset();
  }

  /**
   * Returns the webservice implementation to use.
   * 
   * @return 		the implementation
   */
  public WekaService getImplementation() {
    return m_Implementation;
  }

  /**
   * Description of this option.
   * 
   * @return 		the description for the GUI
   */
  public String implementationTipText() {
    return "The implementation of the webservice to use.";
  }

  /**
   * Sets the number of classifiers to keep in memory.
   * 
   * @param value	the number of classifiers to keep
   */
  public void setClassifierCacheSize(int value) {
    if (value > 0) {
      m_ClassifierCacheSize = value;
      reset();
    }
    else {
      getLogger().severe("At least 1 classifier must be kept in memory, provided: " + value);
    }
  }

  /**
   * Get the number of folds used for the cross validation
   * 
   * @return NUmber of folds in the cross validation
   */
  public int getClassifierCacheSize() {
    return m_ClassifierCacheSize;
  }

  /**
   * Description of this option
   * 
   * @return Description of the folds option
   */
  public String classifierCacheSizeTipText() {
    return "The number of classifiers to keep in memory.";
  }

  /**
   * Sets the number of clusterers to keep in memory.
   * 
   * @param value	the number of clusterers to keep
   */
  public void setClustererCacheSize(int value) {
    if (value > 0) {
      m_ClustererCacheSize = value;
      reset();
    }
    else {
      getLogger().severe("At least 1 clusterer must be kept in memory, provided: " + value);
    }
  }

  /**
   * Get the number of folds used for the cross validation
   * 
   * @return NUmber of folds in the cross validation
   */
  public int getClustererCacheSize() {
    return m_ClustererCacheSize;
  }

  /**
   * Description of this option
   * 
   * @return Description of the folds option
   */
  public String clustererCacheSizeTipText() {
    return "The number of clusterers to keep in memory.";
  }

  /**
   * Performs the actual start of the service.
   * 
   * @throws Exception	if start fails
   */
  @Override
  protected void doStart() throws Exception {
    WekaService implementer;

    implementer = (WekaService) WebserviceUtils.copyImplementation(m_Implementation);
    if (implementer instanceof OwnedByWekaServiceWS)
      ((OwnedByWekaServiceWS) implementer).setOwner(this);
    m_Endpoint  = (EndpointImpl) Endpoint.publish(getURL(), implementer);

    javax.xml.ws.soap.SOAPBinding binding = (javax.xml.ws.soap.SOAPBinding) m_Endpoint.getBinding();
    binding.setMTOMEnabled(true);

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
