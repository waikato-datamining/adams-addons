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
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import java.net.URL;

import adams.core.option.AbstractOptionHandler;
import adams.flow.core.AbstractActor;

/**
 * Ancestor for webservice clients.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <O> the type of output data to handle
 */
public abstract class AbstractWebServiceClientSource<O>
  extends AbstractOptionHandler
  implements WebServiceClientProducer<O> {

  /** for serialization. */
  private static final long serialVersionUID = 3420305488797791952L;

  /** the owner. */
  protected AbstractActor m_Owner;
  
  /** the connection timeout. */
  protected int m_ConnectionTimeout;
  
  /** the receive timeout. */
  protected int m_ReceiveTimeout;

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
   * Sets the actor that executes this webservice.
   * 
   * @param value	the owner
   */
  public void setOwner(AbstractActor value) {
    m_Owner = value;
  }
  
  /**
   * Returns the owning actor.
   * 
   * @return		the owner
   */
  public AbstractActor getOwner() {
    return m_Owner;
  }

  /**
   * Returns the WSDL location.
   * 
   * @return		the location
   */
  protected abstract URL getWsdlLocation();
  
  /**
   * Hook method before querying the webservice.
   * <p/>
   * Default implementation ensures that an owner is set.
   * 
   * @throws Exception	if it fails for some reason
   */
  protected void preQuery() throws Exception {
    if (m_Owner == null)
      throw new IllegalStateException("No owning actor set!");
  }
  
  /**
   * Performs the actual webservice query.
   * 
   * @throws Exception	if accessing webservice fails for some reason
   */
  protected abstract void doQuery() throws Exception;
  
  /**
   * Hook method after querying the webservice.
   * <p/>
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
  }
}
