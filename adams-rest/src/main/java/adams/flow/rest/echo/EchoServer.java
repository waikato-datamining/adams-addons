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
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.echo;

import adams.core.Utils;
import adams.flow.rest.AbstractRESTProvider;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

/**
 <!-- globalinfo-start -->
 * Only offers: adams.flow.rest.echo.Echo<br>
 * <br>
 * REST Method 'ping'<br>
 * - Path: &#64;javax.ws.rs.Path(value=&#47;echo&#47;{input})<br>
 * - Produces: &#64;javax.ws.rs.Produces(value=[text&#47;plain])<br>
 * - Method(s): GET<br>
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-url &lt;java.lang.String&gt; (property: URL)
 * &nbsp;&nbsp;&nbsp;The URL of the service.
 * &nbsp;&nbsp;&nbsp;default: http:&#47;&#47;localhost:8080&#47;
 * </pre>
 *
 * <pre>-in-interceptor &lt;adams.flow.rest.interceptor.incoming.AbstractInInterceptorGenerator&gt; (property: inInterceptor)
 * &nbsp;&nbsp;&nbsp;The interceptor to use for incoming messages.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.rest.interceptor.incoming.NullGenerator
 * </pre>
 *
 * <pre>-out-interceptor &lt;adams.flow.rest.interceptor.outgoing.AbstractOutInterceptorGenerator&gt; (property: outInterceptor)
 * &nbsp;&nbsp;&nbsp;The interceptor to use for outgoing messages.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.rest.interceptor.outgoing.NullGenerator
 * </pre>
 *
 <!-- options-end -->
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
    return "Only offers: " + Utils.classToString(Echo.class) + "\n\n"
      + new Echo().getAdditionalInformation();
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

    configureTLS(factory);

    return factory.create();
  }
}
