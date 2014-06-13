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
 * AbstractWebServiceProvider.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import java.net.URL;
import java.util.logging.Level;

import org.apache.cxf.jaxws.EndpointImpl;

import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.AbstractActor;
import adams.flow.standalone.WSServer;

/**
 * Ancestor for servers providing webservices.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractWebServiceProvider
  extends AbstractOptionHandler
  implements WebServiceProvider {

  /** for serialization. */
  private static final long serialVersionUID = 5989094825183495544L;

  /** the owning actor. */
  protected AbstractActor m_Owner;

  /** the URL of the webservice. */
  protected String m_URL;

  /** whether the webservice is running. */
  protected boolean m_Running;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "url", "URL",
	    getDefaultURL());
  }
  
  /**
   * Sets the actor that executes this webservice.
   * 
   * @param value	the owner
   */
  @Override
  public void setOwner(AbstractActor value) {
    m_Owner = value;
  }
  
  /**
   * Returns the owning actor.
   * 
   * @return		the owner
   */
  @Override
  public AbstractActor getOwner() {
    return m_Owner;
  }
  
  /**
   * Returns the default URL for the service.
   * 
   * @return		the URL
   */
  public abstract String getDefaultURL();
  
  /**
   * Sets the URL to use.
   * 
   * @param value	the URL to use
   */
  public void setURL(String value) {
    try {
      new URL(value);
      m_URL = value;
      reset();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Invalid URL: " + value, e);
    }
  }
  
  /**
   * Returns the URL used for the service.
   * 
   * @return		the URL
   */
  public String getURL() {
    return m_URL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String URLTipText() {
    return "The URL of the service.";
  }
  
  /**
   * Performs some initial checks before starting the service.
   * <p/>
   * Default implementation does nothing.
   */
  protected void check() {
  }
  
  /**
   * Logs an error message if a valid global log actor has been set up.
   * 
   * @param msg		the message to log
   * @param id		an optional ID of the data token that failed in the web service
   */
  public void log(String msg, String id) {
    if (m_Owner instanceof WSServer)
      ((WSServer) m_Owner).log(msg, id);
  }

  /**
   * Configures the logging for the endpoint (incoming and outgoing).
   * 
   * @param endpoint	the endpoint to configure
   */
  protected void configureLogging(EndpointImpl endpoint) {
    BaseLoggingInInterceptor 	logIn;
    BaseLoggingOutInterceptor 	logOut;

    if (isLoggingEnabled()) {
      logIn = new BaseLoggingInInterceptor();
      logIn.setLoggingLevel(getLoggingLevel());
      logOut = new BaseLoggingOutInterceptor();
      logOut.setLoggingLevel(getLoggingLevel());
      endpoint.getServer().getEndpoint().getInInterceptors().add(logIn);
      endpoint.getServer().getEndpoint().getOutInterceptors().add(logOut);
    }
  }
  
  /**
   * Performs the actual start of the service.
   * 
   * @throws Exception	if start fails
   */
  protected abstract void doStart() throws Exception;
  
  /**
   * Starts the service.
   * 
   * @return 		null if successful, otherwise error message
   */
  public String start() {
    String	result;
    String	msg;
    
    try {
      check();
      doStart();
      result    = null;
      m_Running = true;
    }
    catch (Exception e) {
      msg = "Failed to start service: ";
      getLogger().log(Level.SEVERE, msg, e);
      result = msg + Utils.throwableToString(e);
    }
    
    return result;
  }
  
  /**
   * Returns whether the service is running.
   * 
   * @return		true if running
   */
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Performs the actual stop of the service.
   * 
   * @throws Exception	if stopping fails
   */
  protected abstract void doStop() throws Exception;
  
  /**
   * Stops the service.
   * 
   * @return		null if successful, otherwise error message
   */
  public String stop() {
    String	result;
    String	msg;
    
    try {
      doStop();
      result = null;
    }
    catch (Exception e) {
      msg = "Failed to stop service: ";
      getLogger().log(Level.SEVERE, msg, e);
      result = msg + Utils.throwableToString(e);
    }
    
    m_Running = false;
    
    return result;
  }
}
