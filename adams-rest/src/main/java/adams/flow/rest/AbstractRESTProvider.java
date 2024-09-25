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
 * AbstractRESTProvider.java
 * Copyright (C) 2018-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.rest;

import adams.core.Utils;
import adams.core.base.BaseURL;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.KeyManagerFactoryProvider;
import adams.flow.core.SSLContextProvider;
import adams.flow.core.TLSUtils;
import adams.flow.core.TrustManagerFactoryProvider;
import adams.flow.rest.interceptor.incoming.AbstractInInterceptorGenerator;
import adams.flow.rest.interceptor.outgoing.AbstractOutInterceptorGenerator;
import adams.flow.standalone.RESTServer;
import org.apache.cxf.configuration.jsse.TLSServerParameters;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;

import java.net.URI;
import java.util.logging.Level;

/**
 * Ancestor for servers providing webservices.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRESTProvider
  extends AbstractOptionHandler
  implements RESTProvider {

  /** for serialization. */
  private static final long serialVersionUID = 5989094825183495544L;

  /** the owning actor. */
  protected Actor m_Owner;

  /** the URL of the webservice. */
  protected String m_URL;

  /** the interceptor generator for incoming messages. */
  protected AbstractInInterceptorGenerator m_InInterceptor;

  /** the interceptor generator for outgoing messages. */
  protected AbstractOutInterceptorGenerator m_OutInterceptor;

  /** whether the webservice is running. */
  protected boolean m_Running;

  /** the server. */
  protected Server m_Server;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "url", "URL",
      getDefaultURL());

    m_OptionManager.add(
      "in-interceptor", "inInterceptor",
      getDefaultInInterceptor());

    m_OptionManager.add(
      "out-interceptor", "outInterceptor",
      getDefaultOutInterceptor());
  }

  /**
   * Sets the actor that executes this webservice.
   *
   * @param value	the owner
   */
  @Override
  public void setFlowContext(Actor value) {
    m_Owner = value;
  }

  /**
   * Returns the owning actor.
   *
   * @return		the owner
   */
  @Override
  public Actor getFlowContext() {
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
      new URI(value).toURL();
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
   * Returns the default interceptor for incoming messages.
   *
   * @return		the interceptor
   */
  protected AbstractInInterceptorGenerator getDefaultInInterceptor() {
    return new adams.flow.rest.interceptor.incoming.NullGenerator();
  }

  /**
   * Sets the interceptor for incoming messages.
   *
   * @param value	the interceptor
   */
  public void setInInterceptor(AbstractInInterceptorGenerator value) {
    m_InInterceptor = value;
    reset();
  }

  /**
   * Returns the interceptor for incoming messages.
   *
   * @return		the interceptor
   */
  public AbstractInInterceptorGenerator getInInterceptor() {
    return m_InInterceptor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inInterceptorTipText() {
    return "The interceptor to use for incoming messages.";
  }

  /**
   * Returns the default interceptor for outgoing messages.
   *
   * @return		the interceptor
   */
  protected AbstractOutInterceptorGenerator getDefaultOutInterceptor() {
    return new adams.flow.rest.interceptor.outgoing.NullGenerator();
  }

  /**
   * Sets the interceptor for outgoing messages.
   *
   * @param value	the interceptor
   */
  public void setOutInterceptor(AbstractOutInterceptorGenerator value) {
    m_OutInterceptor = value;
    reset();
  }

  /**
   * Returns the interceptor for outgoing messages.
   *
   * @return		the interceptor
   */
  public AbstractOutInterceptorGenerator getOutInterceptor() {
    return m_OutInterceptor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outInterceptorTipText() {
    return "The interceptor to use for outgoing messages.";
  }

  /**
   * Performs some initial checks before starting the service.
   * <br><br>
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
    if (m_Owner instanceof RESTServer)
      ((RESTServer) m_Owner).log(msg, id);
  }

  /**
   * Configures the interceptors/logging for the factory (incoming and outgoing).
   *
   * @param factory	the factory to configure
   * @see		#m_InInterceptor
   * @see		#m_OutInterceptor
   */
  protected void configureInterceptors(JAXRSServerFactoryBean factory) {
    RESTUtils.configureFactoryInterceptors(m_Owner, factory, m_InInterceptor, m_OutInterceptor);
  }

  /**
   * Configures TLS support (if using https:// and actors present in flow).
   *
   * @param factory	the factory to configure
   * @throws IllegalStateException	if https used but failed to configure TLS params
   */
  protected void configureTLS(JAXRSServerFactoryBean factory) throws Exception {
    TLSServerParameters 		tlsparams;
    JettyHTTPServerEngineFactory 	jettyFactory;
    BaseURL 				baseURL;

    if (factory.getAddress().startsWith("https://")) {
      tlsparams = TLSUtils.configureServerTLS(m_Owner);
      if (tlsparams != null) {
	jettyFactory = factory.getBus().getExtension(JettyHTTPServerEngineFactory.class);
	baseURL = new BaseURL(factory.getAddress());
	jettyFactory.setTLSServerParametersForPort(baseURL.urlValue().getPort(), tlsparams);
      }
      else {
	throw new IllegalStateException(
	  "Failed to configure SSL context for '" + factory.getAddress() + "' - missing actors ("
	    + Utils.classesToString(new Class[]{KeyManagerFactoryProvider.class, TrustManagerFactoryProvider.class, SSLContextProvider.class}) + ")?");
      }
    }
  }

  /**
   * Performs the actual start of the service.
   *
   * @return 		the server instance
   * @throws Exception	if start fails
   */
  protected abstract Server doStart() throws Exception;

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
      m_Server = doStart();
      result    = null;
      m_Running = true;
    }
    catch (Exception e) {
      msg = "Failed to start service: ";
      getLogger().log(Level.SEVERE, msg, e);
      result = msg + LoggingHelper.throwableToString(e);
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
  protected void doStop() throws Exception {
    if (m_Server != null)
      m_Server.stop();
  }

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
      m_Server = null;
      result = null;
    }
    catch (Exception e) {
      msg = "Failed to stop service: ";
      getLogger().log(Level.SEVERE, msg, e);
      result = msg + LoggingHelper.throwableToString(e);
    }

    m_Running = false;

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_InInterceptor.cleanUp();
    m_OutInterceptor.cleanUp();
  }
}
