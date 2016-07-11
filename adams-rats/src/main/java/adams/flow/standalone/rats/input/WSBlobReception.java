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
 * WSBlobReception.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.data.blob.BlobContainer;
import adams.flow.webservice.WebServiceProvider;
import adams.flow.webservice.blob.RatsBlobServiceWS;

/**
 <!-- globalinfo-start -->
 * Uses a webservice for retrieving text. Internally polls whether data has arrived.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-web-service &lt;adams.flow.webservice.WebServiceProvider&gt; (property: webService)
 * &nbsp;&nbsp;&nbsp;The webservice provider to use.
 * &nbsp;&nbsp;&nbsp;default: knir.flow.webservice.RatsServiceWS -implementation knir.flow.webservice.RatsService
 * </pre>
 * 
 * <pre>-wait-poll &lt;int&gt; (property: waitPoll)
 * &nbsp;&nbsp;&nbsp;The number of milli-seconds to wait before polling again whether data has 
 * &nbsp;&nbsp;&nbsp;arrived.
 * &nbsp;&nbsp;&nbsp;default: 50
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 2083 $
 */
public class WSBlobReception
  extends AbstractBufferedRatInput {

  /** for serialization. */
  private static final long serialVersionUID = -3681678330127394451L;
  
  /** the webservice to run. */
  protected WebServiceProvider m_WebService;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses a webservice for retrieving text. Internally polls whether data has arrived.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "web-service", "webService",
	    getDefaultWebService());
  }

  /**
   * Returns the default webservice provider to use.
   *
   * @return		the default provider
   */
  protected WebServiceProvider getDefaultWebService() {
    return new RatsBlobServiceWS();
  }

  /**
   * Sets the webservice provider to use.
   *
   * @param value	the provider
   */
  public void setWebService(WebServiceProvider value) {
    m_WebService = value;
    m_WebService.setOwner(getOwner());
    if (m_WebService instanceof RatInputUser)
      ((RatInputUser) m_WebService).setRatInput(this);
    reset();
  }

  /**
   * Returns the webservice provider in use.
   *
   * @return		the provider
   */
  public WebServiceProvider getWebService() {
    return m_WebService;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String webServiceTipText() {
    return "The webservice provider to use.";
  }

  /**
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  @Override
  public Class generates() {
    return BlobContainer.class;
  }

  /**
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    String	result;
    
    result = null;
    
    if (!m_WebService.isRunning()) {
      m_WebService.setOwner(getOwner());
      if (m_WebService instanceof RatInputUser)
	((RatInputUser) m_WebService).setRatInput(this);
      result = m_WebService.start();
    }

    return result;
  }
  
  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_WebService.stop();
    super.stopExecution();
  }
}
