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
 * WebserviceUtils.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import adams.core.Utils;
import adams.core.logging.LoggingLevelHandler;
import adams.core.net.ProxyHelper;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.core.Actor;
import adams.flow.core.TLSUtils;
import adams.flow.standalone.KeyManager;
import adams.flow.standalone.SSLContext;
import adams.flow.standalone.TrustManager;
import adams.flow.webservice.interceptor.InterceptorWithActor;
import adams.flow.webservice.interceptor.incoming.AbstractInInterceptorGenerator;
import adams.flow.webservice.interceptor.outgoing.AbstractOutInterceptorGenerator;
import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;

import javax.xml.ws.BindingProvider;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;

/**
 * Utility class around webservices.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WebserviceUtils {

  /** mime type: application/octet-stream. */
  public final static String MIMETYPE_APPLICATION_OCTETSTREAM = "application/octet-stream";

  /** mime type: plain/text. */
  public final static String MIMETYPE_PLAIN_TEXT = "plain/text";
  
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
   * Automatically configures TLS if present in actor's context.
   * 
   * @param owner		the owning actor
   * @param servicePort		the service port to set the timeouts for
   * @param connection		the timeout for the connection in msec, 0 is infinite
   * @param receive		the timeout for receiving in msec, 0 is infinite
   * @param url			the URL of the webservice, null to use default
   * @param inInterceptor	the interceptor for incoming messages, null if not available
   * @param outInterceptor	the interceptor for outcoing messages, null if not available
   * @see			ProxyHelper
   */
  public static void configureClient(Actor owner, Object servicePort, int connection, int receive, String url, AbstractInInterceptorGenerator inInterceptor, AbstractOutInterceptorGenerator outInterceptor) {
    Client 				client;
    HTTPConduit				http;
    HTTPClientPolicy 			clPolicy;
    ProxyAuthorizationPolicy		proxyPolicy;
    BindingProvider 			bindingProvider;
    AbstractPhaseInterceptor<Message> 	in;
    AbstractPhaseInterceptor<Message> 	out;
    String				actualURL;
    
    client   = ClientProxy.getClient(servicePort);
    http     = (HTTPConduit) client.getConduit();
    clPolicy = new HTTPClientPolicy();
    clPolicy.setConnectionTimeout(connection);
    clPolicy.setReceiveTimeout(receive);
    clPolicy.setAllowChunking(false);
    clPolicy.setAutoRedirect(false);
    clPolicy.setConnection(ConnectionType.KEEP_ALIVE);

    // proxy
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
	throw new IllegalStateException(
	    "Proxy type not supported by CXF clients: " + ProxyHelper.getSingleton().getProxyType());
    }
    http.setClient(clPolicy);
    if (proxyPolicy != null)
      http.setProxyAuthorization(proxyPolicy);	

    // alternative url?
    if (url != null) {
      bindingProvider = (BindingProvider) servicePort;
      bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
    }
    
    // interceptors?
    in = null;
    if (inInterceptor != null)
      in = inInterceptor.generate();
    if (in instanceof InterceptorWithActor)
      ((InterceptorWithActor) in).setActor(owner);
    if (in != null)
      client.getInInterceptors().add(in);

    out = null;
    if (outInterceptor != null)
      out = outInterceptor.generate();
    if (out instanceof InterceptorWithActor)
      ((InterceptorWithActor) out).setActor(owner);
    if (out != null)
      client.getOutInterceptors().add(out);

    // configure TLS (if https:// and actors present in flow)
    actualURL = "" + ((BindingProvider) servicePort).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
    if (actualURL.toLowerCase().startsWith("https://")) {
      if (!TLSUtils.configureClientTLS(owner, http))
        throw new IllegalStateException(
          "Failed to configure SSL context for '" + actualURL + "' - missing actors ("
	    + Utils.classesToString(new Class[]{KeyManager.class, TrustManager.class, SSLContext.class}) + ")?");
    }
  }

  /**
   * Configures the interceptors/logging for the service endpoint (incoming and outgoing).
   * 
   * @param owner		the owning actor
   * @param endpoint		the endpoint to configure
   * @param inInterceptor	the interceptor for incoming messages
   * @param outInterceptor	the interceptor for outcoing messages
   */
  public static void configureServiceInterceptors(Actor owner, EndpointImpl endpoint, AbstractInInterceptorGenerator inInterceptor, AbstractOutInterceptorGenerator outInterceptor) {
    AbstractPhaseInterceptor<Message> 	in;
    AbstractPhaseInterceptor<Message> 	out;
    
    in  = inInterceptor.generate();
    out = outInterceptor.generate();
    
    // logging
    if (owner.isLoggingEnabled()) {
      if (in instanceof LoggingLevelHandler)
	((LoggingLevelHandler) in).setLoggingLevel(owner.getLoggingLevel());
      if (out instanceof LoggingLevelHandler)
	((LoggingLevelHandler) out).setLoggingLevel(owner.getLoggingLevel());
    }
    
    // actor aware?
    if (in instanceof InterceptorWithActor)
      ((InterceptorWithActor) in).setActor(owner);
    if (out instanceof InterceptorWithActor)
      ((InterceptorWithActor) out).setActor(owner);
      
    // add interceptors
    if (in != null)
      endpoint.getServer().getEndpoint().getInInterceptors().add(in);
    if (out != null)
      endpoint.getServer().getEndpoint().getOutInterceptors().add(out);
  }
  
  /**
   * Loads the WSDL from the given location.
   * 
   * @param location	the location of the WSDL
   * @return		the content of the WSDL, null if failed to load
   */
  public static String loadWsdl(URL location) {
    StringBuilder	result;
    BufferedReader	reader;
    String		line;
    
    result = new StringBuilder();
    
    reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(location.openStream()));
      while ((line = reader.readLine()) != null) {
	result.append(line);
	result.append("\n");
      }
    }
    catch (Exception e) {
      System.err.println("Failed to load WSDL from " + location + ": ");
      e.printStackTrace();
      result = null;
    }
    finally {
      if (reader != null) {
	try {
	  reader.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
    
    if (result != null)
      return result.toString();
    else
      return null;
  }
  
  /**
   * Turns the WSDL content into content to be displayed as HTML.
   * 
   * @param wsdl	the WSDL to convert
   * @return		the HTML code
   */
  public static String wsdlToHtml(String wsdl) {
    StringBuilder	result;
    int			i;
    char		c;
    String		conv;
    boolean		lineStart;

    if (wsdl == null)
      return "";
    
    result    = new StringBuilder();
    lineStart = true;
    for (i = 0; i < wsdl.length(); i++) {
      c = wsdl.charAt(i);
      // line handling/indentation
      switch (c) {
	case '\r':
	case '\n':
	  lineStart = true;
	  conv = "" + c;
	  break;
	case ' ':
	  if (lineStart)
	    conv = "&nbsp;";
	  else
	    conv = " ";
	  break;
	default:
	  lineStart = false;
	  conv = "" + c;
	  break;
      }
      
      result.append(conv);
    }

    return result.toString();
  }

  /**
   * Creates a copy of the WS implementation, either using a shallow copy
   * (if implementing {@link OptionHandler}) or {@link Utils#deepCopy(Object)}.
   *
   * @param implementation	the webservice implemntation to copy
   * @return			the copy, null if failed to copy
   */
  public static Object copyImplementation(Object implementation) {
    if (implementation instanceof OptionHandler)
      return OptionUtils.shallowCopy((OptionHandler) implementation, false);
    else
      return Utils.deepCopy(implementation);
  }
}
