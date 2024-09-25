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
 * AbstractRESTClientSink.java
 * Copyright (C) 2018-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.rest;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;

import java.net.URI;
import java.util.logging.Level;

/**
 * Ancestor for REST webservice clients.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <I> the type of input data to handle
 */
public abstract class AbstractRESTClientSink<I>
  extends AbstractOptionHandler
  implements RESTClientConsumer<I>, QuickInfoSupporter, AlternativeUrlSupporter {

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

  /** the last error that was generated. */
  protected String m_LastError;

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
        new URI(value).toURL();
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
   * Checks whether there was an error with the last call.
   *
   * @return		true if there was an error
   * @see		#getLastError()
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }

  /**
   * Returns the last error that occurred.
   *
   * @return		the last error, null if none occurred
   */
  public String getLastError() {
    return m_LastError;
  }

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
    m_LastError = null;
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
   * Cleans up data structures, frees up memory.
   * <br>
   * Default implementation does nothing.
   */
  @Override
  public void cleanUp() {
  }
}
