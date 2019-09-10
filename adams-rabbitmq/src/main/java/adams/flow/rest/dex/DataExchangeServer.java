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
 * DataExchangeServer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.dex;

import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.rest.AbstractRESTProvider;
import adams.flow.rest.dex.authentication.AbstractAuthentication;
import adams.flow.rest.dex.authentication.NoAuthenticationRequired;
import adams.flow.rest.dex.backend.AbstractBackend;
import adams.flow.rest.dex.backend.InMemory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DataExchangeServer
  extends AbstractRESTProvider {

  private static final long serialVersionUID = 2978764775645037701L;

  /** the authentication scheme. */
  protected AbstractAuthentication m_Authentication;

  /** the backend in use. */
  protected AbstractBackend m_Backend;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Only offers: " + Utils.classToString(DataExchange.class) + "\n\n"
      + new DataExchange().getAdditionalInformation();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "authentication", "authentication",
      new NoAuthenticationRequired());

    m_OptionManager.add(
      "backend", "backend",
      new InMemory());
  }

  /**
   * Sets the authentication scheme.
   *
   * @param value	the scheme
   */
  public void setAuthentication(AbstractAuthentication value) {
    m_Authentication = value;
    reset();
  }

  /**
   * Returns the authentication scheme.
   *
   * @return		the scheme
   */
  public AbstractAuthentication getAuthentication() {
    return m_Authentication;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String authenticationTipText() {
    return "The scheme to use for authenticating clients.";
  }

  /**
   * Sets the backend scheme.
   *
   * @param value	the scheme
   */
  public void setBackend(AbstractBackend value) {
    m_Backend = value;
    reset();
  }

  /**
   * Returns the backend scheme.
   *
   * @return		the scheme
   */
  public AbstractBackend getBackend() {
    return m_Backend;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backendTipText() {
    return "The scheme to use for managing the uploaded data.";
  }

  /**
   * Returns the default URL for the service.
   *
   * @return		the URL
   */
  @Override
  public String getDefaultURL() {
    return "http://localhost:8080/";
  }

  /**
   * Performs the actual start of the service.
   *
   * @return 		the server instance
   * @throws Exception	if start fails
   */
  @Override
  protected Server doStart() throws Exception {
    JAXRSServerFactoryBean	factory;
    DataExchange 		exchange;

    factory = new JAXRSServerFactoryBean();
    configureInterceptors(factory);

    exchange = new DataExchange();
    exchange.setAuthentication((AbstractAuthentication) OptionUtils.shallowCopy(m_Authentication, true));
    exchange.setBackend((AbstractBackend) OptionUtils.shallowCopy(m_Backend, true));
    exchange.setLoggingLevel(getLoggingLevel());
    factory.setServiceBean(exchange);
    factory.setAddress(getURL());

    configureTLS(factory);

    return factory.create();
  }
}
