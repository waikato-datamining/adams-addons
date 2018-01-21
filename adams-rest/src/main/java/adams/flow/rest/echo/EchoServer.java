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
 * EchoServer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.echo;

import adams.core.Utils;
import adams.flow.rest.AbstractRESTProvider;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

/**
 * Simple REST-based echo server.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EchoServer
  extends AbstractRESTProvider {

  private static final long serialVersionUID = 2978764775645037701L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Only offers: " + Utils.classToString(Echo.class);
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
    Echo			echo;

    factory = new JAXRSServerFactoryBean();
    configureInterceptors(factory);

    echo = new Echo();
    echo.setLoggingLevel(getLoggingLevel());
    factory.setServiceBean(echo);
    factory.setAddress(getURL());

    return factory.create();
  }
}
