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
 * AbstractWebServiceClientSource.java
 * Copyright (C) 2012-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.event.WebServiceClientProducerResponseDataEvent;
import adams.event.WebServiceClientProducerResponseDataListener;
import adams.flow.core.Actor;
import adams.flow.webservice.interceptor.incoming.AbstractInInterceptorGenerator;

import java.net.URL;
import java.util.HashSet;
import java.util.logging.Level;

/**
 * Ancestor for webservice clients.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <O> the type of output data to handle
 */
public abstract class AbstractWebServiceClientSource<O>
  extends AbstractOptionHandler
  implements WebServiceClientProducer<O>, QuickInfoSupporter, WsdlUrlProvider, AlternativeUrlSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3420305488797791952L;

  /** the owner. */
  protected Actor m_Owner;

  /** the connection timeout. */
  protected int m_ConnectionTimeout;

  /** the receive timeout. */
  protected int m_ReceiveTimeout;

  /** whether to use an alternative URL. */
  protected boolean m_UseAlternativeURL;

  /** the URL of the webservice. */
  protected String m_AlternativeURL;

  /** the interceptor generator for incoming messages. */
  protected AbstractInInterceptorGenerator m_InInterceptor;

  /** the response data. */
  protected transient O m_ResponseData;

  /** the listeners for reponse data. */
  protected HashSet<WebServiceClientProducerResponseDataListener> m_ResponseDataListeners;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "connection-timeout", "connectionTimeout",
      300000, 0, null);

    m_OptionManager.add(
      "receive-timeout", "receiveTimeout",
      300000, 0, null);

    m_OptionManager.add(
      "use-alternative-url", "useAlternativeURL",
      false);

    m_OptionManager.add(
      "alternative-url", "alternativeURL",
      getDefaultAlternativeURL());

    m_OptionManager.add(
      "in-interceptor", "inInterceptor",
      getDefaultInInterceptor());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ResponseDataListeners = new HashSet<>();
  }

  /**
   * Sets the timeout for connection.
   *
   * @param value	the timeout in msec, 0 is infinite
   */
  public void setConnectionTimeout(int value) {
    m_ConnectionTimeout = value;
    reset();
  }

  /**
   * Returns the timeout for the connection.
   *
   * @return		the timeout in msec, 0 is infinite
   */
  public int getConnectionTimeout() {
    return m_ConnectionTimeout;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String connectionTimeoutTipText() {
    return "The connection timeout in msec, 0 is infinite.";
  }

  /**
   * Sets the timeout for receiving.
   *
   * @param value	the timeout in msec, 0 is infinite
   */
  public void setReceiveTimeout(int value) {
    m_ReceiveTimeout = value;
    reset();
  }

  /**
   * Returns the timeout for receiving.
   *
   * @return		the timeout in msec, 0 is infinite
   */
  public int getReceiveTimeout() {
    return m_ReceiveTimeout;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String receiveTimeoutTipText() {
    return "The timeout for receiving in msec, 0 is infinite.";
  }

  /**
   * Sets whether to use the alternative URL.
   *
   * @param value	whether to use the alternative URL
   */
  public void setUseAlternativeURL(boolean value) {
    m_UseAlternativeURL = value;
    reset();
  }

  /**
   * Returns whether to use the alternative URL used for the service.
   *
   * @return		true if to use alternative URL
   */
  public boolean getUseAlternativeURL() {
    return m_UseAlternativeURL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAlternativeURLTipText() {
    return "If enabled, the specified alternative URL is used.";
  }

  /**
   * Returns the default URL for the service.
   *
   * @return		the URL
   */
  public String getDefaultAlternativeURL() {
    return "http://localhost:8080/";
  }

  /**
   * Sets the alternative URL to use.
   *
   * @param value	the URL to use
   */
  public void setAlternativeURL(String value) {
    if ((value != null) && !value.isEmpty()) {
      try {
	new URL(value);
	m_AlternativeURL = value;
	reset();
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Invalid URL: " + value, e);
      }
    }
  }

  /**
   * Returns the alternative URL used for the service.
   *
   * @return		the URL
   */
  public String getAlternativeURL() {
    return m_AlternativeURL;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alternativeURLTipText() {
    return "The URL of the service.";
  }

  /**
   * Returns the default interceptor for incoming messages.
   *
   * @return		the interceptor
   */
  protected AbstractInInterceptorGenerator getDefaultInInterceptor() {
    return new adams.flow.webservice.interceptor.incoming.NullGenerator();
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
   * Returns the additional information.
   *
   * @return		the additional information, null or 0-length string for no information
   */
  public String getAdditionalInformation() {
    StringBuilder	result;
    String		wsdl;

    result = new StringBuilder("WSDL: " + getWsdlLocation());
    wsdl   = WebserviceUtils.loadWsdl(getWsdlLocation());
    if (wsdl != null) {
      result.append("\n\n");
      result.append(WebserviceUtils.wsdlToHtml(wsdl));
    }

    return result.toString();
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result = null;

    if (m_UseAlternativeURL || QuickInfoHelper.hasVariable(this, "useAlternativeURL"))
      result = QuickInfoHelper.toString(this, "alternativeURL", m_AlternativeURL);

    return result;
  }

  /**
   * Sets the actor that executes this webservice.
   *
   * @param value	the owner
   */
  public void setOwner(Actor value) {
    m_Owner = value;
  }

  /**
   * Returns the owning actor.
   *
   * @return		the owner
   */
  public Actor getOwner() {
    return m_Owner;
  }

  /**
   * Adds the listener for response data being received.
   *
   * @param l		the listener to add
   */
  public void addResponseDataListener(WebServiceClientProducerResponseDataListener l) {
    m_ResponseDataListeners.add(l);
  }

  /**
   * Removes the listener for response data being received.
   *
   * @param l		the listener to remove
   */
  public void removeResponseDataListener(WebServiceClientProducerResponseDataListener l) {
    m_ResponseDataListeners.remove(l);
  }

  /**
   * Notifies all listeners that response data has arrived.
   */
  protected synchronized void notifyResponseDataListeners() {
    WebServiceClientProducerResponseDataEvent	e;

    e = new WebServiceClientProducerResponseDataEvent(this);
    for (WebServiceClientProducerResponseDataListener l: m_ResponseDataListeners)
      l.webServiceReponseDataReceived(e);
  }

  /**
   * Checks whether there is any response data to be collected.
   *
   * @return		true if data can be collected
   * @see		#getResponseData()
   */
  public boolean hasResponseData() {
    return (m_ResponseData != null);
  }

  /**
   * Sets the response data.
   *
   * @param value	the response data
   */
  public void setResponseData(O value) {
    m_ResponseData = value;
    if (m_ResponseData != null)
      notifyResponseDataListeners();
  }

  /**
   * Returns the response data, if any.
   *
   * @return		the response data
   */
  public O getResponseData() {
    O		result;

    result         = m_ResponseData;
    m_ResponseData = null;

    return result;
  }

  /**
   * Returns the WSDL location.
   *
   * @return		the location
   */
  public abstract URL getWsdlLocation();

  /**
   * Hook method before querying the webservice.
   * <br><br>
   * Default implementation ensures that an owner is set.
   *
   * @throws Exception	if it fails for some reason
   */
  protected void preQuery() throws Exception {
    if (m_Owner == null)
      throw new IllegalStateException("No owning actor set!");
    m_ResponseData = null;
  }

  /**
   * Performs the actual webservice query.
   *
   * @throws Exception	if accessing webservice fails for some reason
   */
  protected abstract void doQuery() throws Exception;

  /**
   * Hook method after querying the webservice.
   * <br><br>
   * Default implementation does nothing.
   *
   * @throws Exception	if it fails for some reason
   */
  protected void postQuery() throws Exception {
  }

  /**
   * Queries the webservice.
   *
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  public void query() throws Exception {
    preQuery();
    doQuery();
    postQuery();
  }

  /**
   * Cleans up the client.
   */
  @Override
  public void cleanUp() {
    m_InInterceptor.cleanUp();
  }
}
