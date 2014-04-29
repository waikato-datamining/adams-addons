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
 * WebserviceUtils.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import java.net.Proxy;

import javax.xml.ws.BindingProvider;

import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;

import adams.core.Utils;
import adams.core.net.ProxyHelper;

/**
 * Utility class around webservices.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WebserviceUtils {

  /** mime type: application/octet-stream. */
  public final static String MIMETYPE_APPLICATION_OCTETSTREAM = "application/octet-stream";
  
  /**
   * Enables the schema validation.
   * 
   * @param provider	the webservice to enable schema validation for
   */
  public static void enableSchemaValidation(BindingProvider provider) {
    provider.getRequestContext().put("schema-validation-enabled", "true");
  }

  /**
   * Disables the schema validation.
   * 
   * @param provider	the webservice to disable schema validation for
   */
  public static void disableSchemaValidation(BindingProvider provider) {
    provider.getRequestContext().put("schema-validation-enabled", "false");
  }
  
  /**
   * Sets the timeouts for connection and receiving. Also configures the
   * proxy settings in case there is a system-wide proxy configured.
   * 
   * @param servicePort	the service port to set the timeouts for
   * @param connection	the timeout for the connection in msec, 0 is infinite
   * @param receive	the timeout for receiving in msec, 0 is infinite
   * @see		ProxyHelper
   */
  public static void configureClient(Object servicePort, int connection, int receive) {
    Client 			client;
    HTTPConduit			http;
    HTTPClientPolicy 		clPolicy;
    ProxyAuthorizationPolicy	proxyPolicy;
    
    client   = ClientProxy.getClient(servicePort);
    http     = (HTTPConduit) client.getConduit();
    clPolicy = new HTTPClientPolicy();
    clPolicy.setConnectionTimeout(connection);
    clPolicy.setReceiveTimeout(receive);
    clPolicy.setAllowChunking(false);
    clPolicy.setAutoRedirect(false);
    clPolicy.setConnection(ConnectionType.KEEP_ALIVE);
    proxyPolicy = null;
    
    switch (ProxyHelper.getSingleton().getProxyType()) {
      case DIRECT:
	// do nothing
	break;

      case HTTP:
	clPolicy.setProxyServerType(ProxyServerType.HTTP);
	clPolicy.setProxyServer(ProxyHelper.getSingleton().getHost(Proxy.Type.HTTP));
	clPolicy.setProxyServerPort(ProxyHelper.getSingleton().getPort(Proxy.Type.HTTP));
	clPolicy.setNonProxyHosts(Utils.flatten(ProxyHelper.getSingleton().getNoProxy(Proxy.Type.HTTP), "|"));
	if (ProxyHelper.getSingleton().getAuthentication(Proxy.Type.HTTP)) {
	  proxyPolicy = new ProxyAuthorizationPolicy();
	  proxyPolicy.setUserName(ProxyHelper.getSingleton().getUser(Proxy.Type.HTTP));
	  proxyPolicy.setPassword(ProxyHelper.getSingleton().getPassword(Proxy.Type.HTTP).getValue());
	  proxyPolicy.setAuthorizationType("Basic");
	}
	break;
      
      case SOCKS:
	clPolicy.setProxyServerType(ProxyServerType.SOCKS); 
	clPolicy.setProxyServer(ProxyHelper.getSingleton().getHost(Proxy.Type.SOCKS));
	clPolicy.setProxyServerPort(ProxyHelper.getSingleton().getPort(Proxy.Type.SOCKS));
	clPolicy.setNonProxyHosts(Utils.flatten(ProxyHelper.getSingleton().getNoProxy(Proxy.Type.SOCKS), "|"));
	if (ProxyHelper.getSingleton().getAuthentication(Proxy.Type.SOCKS)) {
	  proxyPolicy = new ProxyAuthorizationPolicy();
	  proxyPolicy.setUserName(ProxyHelper.getSingleton().getUser(Proxy.Type.SOCKS));
	  proxyPolicy.setPassword(ProxyHelper.getSingleton().getPassword(Proxy.Type.SOCKS).getValue());
	  proxyPolicy.setAuthorizationType("Basic");  // correct?
	}
	break;
      
      default:
	System.err.println("Proxy type not supported by CXF clients: " + ProxyHelper.getSingleton().getProxyType());
    }
    
    http.setClient(clPolicy);
    if (proxyPolicy != null)
      http.setProxyAuthorization(proxyPolicy);	
  }
}
