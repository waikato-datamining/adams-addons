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
 * RESTUtils.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.rest;

import adams.core.Utils;
import adams.core.logging.LoggingLevelHandler;
import adams.core.net.ProxyHelper;
import adams.flow.core.Actor;
import adams.flow.rest.interceptor.InterceptorWithActor;
import adams.flow.rest.interceptor.incoming.AbstractInInterceptorGenerator;
import adams.flow.rest.interceptor.outgoing.AbstractOutInterceptorGenerator;
import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.xml.ws.BindingProvider;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class around REST webservices.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RESTUtils {

  /** mime type: application/octet-stream. */
  public final static String MIMETYPE_APPLICATION_OCTETSTREAM = "application/octet-stream";

  /** mime type: plain/text. */
  public final static String MIMETYPE_PLAIN_TEXT = "plain/text";

  /**
   * Sets the timeouts for connection and receiving. Also configures the
   * proxy settings in case there is a system-wide proxy configured.
   * 
   * @param owner		the owning actor
   * @param servicePort		the service port to set the timeouts for
   * @param connection		the timeout for the connection in msec, 0 is infinite
   * @param receive		the timeout for receiving in msec, 0 is infinite
   * @param url			the URL of the webservice, null to use default
   * @see			ProxyHelper
   */
  public static void configureClient(Actor owner, Object servicePort, int connection, int receive, String url) {
    Client 				client;
    HTTPConduit				http;
    HTTPClientPolicy 			clPolicy;
    ProxyAuthorizationPolicy		proxyPolicy;
    BindingProvider 			bindingProvider;

    client   = ClientBuilder.newClient();
    http     = WebClient.getConfig(client).getHttpConduit();
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
  }

  /**
   * Configures the interceptors/logging for the service endpoint (incoming and outgoing).
   * 
   * @param owner		the owning actor
   * @param factory		the server factory to update
   * @param inInterceptor	the interceptor for incoming messages
   * @param outInterceptor	the interceptor for outcoing messages
   */
  public static void configureFactoryInterceptors(Actor owner, JAXRSServerFactoryBean factory, AbstractInInterceptorGenerator inInterceptor, AbstractOutInterceptorGenerator outInterceptor) {
    AbstractPhaseInterceptor<Message> 	in;
    AbstractPhaseInterceptor<Message> 	out;
    
    in  = inInterceptor.generate();
    out = outInterceptor.generate();
    
    // logging
    if (owner.isLoggingEnabled()) {
      if ((in != null) && (in instanceof LoggingLevelHandler))
	((LoggingLevelHandler) in).setLoggingLevel(owner.getLoggingLevel());
      if ((out != null) && (out instanceof LoggingLevelHandler))
	((LoggingLevelHandler) out).setLoggingLevel(owner.getLoggingLevel());
    }
    
    // actor aware?
    if ((in != null) && (in instanceof InterceptorWithActor))
      ((InterceptorWithActor) in).setActor(owner);
    if ((out != null) && (out instanceof InterceptorWithActor))
      ((InterceptorWithActor) out).setActor(owner);
      
    // add interceptors
    if (in != null)
      factory.getInInterceptors().add(in);
    if (out != null)
      factory.getOutInterceptors().add(out);
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
   * Returns the additional for the class, if any.
   *
   * @param cls		the class to inspect
   * @return		the generated information, null if none available
   */
  protected static String getAdditionalInformation(Class cls) {
    StringBuilder	result;
    Annotation 		annotation;

    result = new StringBuilder();

    if (cls.isAnnotationPresent(Path.class)) {
      annotation = cls.getAnnotation(Path.class);
      result.append("- Path: " + annotation + "\n");
    }
    if (cls.isAnnotationPresent(Consumes.class)) {
      annotation = cls.getAnnotation(Consumes.class);
      result.append("- Consumes: " + annotation + "\n");
    }
    if (cls.isAnnotationPresent(Produces.class)) {
      annotation = cls.getAnnotation(Produces.class);
      result.append("- Produces: " + annotation + "\n");
    }

    if (result.length() == 0)
      return null;
    else
      return result.toString();
  }

  /**
   * Returns the additional for the method, if any.
   *
   * @param method	the method to inspect
   * @return		the generated information, null if none available
   */
  protected static String getAdditionalInformation(Method method) {
    StringBuilder	result;
    Annotation 		annotation;
    List<String>	methods;
    int			index;

    result = new StringBuilder();

    if (method.isAnnotationPresent(Path.class)) {
      annotation = method.getAnnotation(Path.class);
      result.append("- Path: " + annotation + "\n");
    }
    if (method.isAnnotationPresent(Consumes.class)) {
      annotation = method.getAnnotation(Consumes.class);
      result.append("- Consumes: " + annotation + "\n");
    }
    if (method.isAnnotationPresent(Produces.class)) {
      annotation = method.getAnnotation(Produces.class);
      result.append("- Produces: " + annotation + "\n");
    }
    index = 0;
    for (Annotation[] annotations: method.getParameterAnnotations()) {
      index++;
      if (annotations.length > 0)
        result.append("- Parameter #" + index + ": " + Utils.flatten(annotations, ", ") + "\n");
    }
    methods = new ArrayList<>();
    if (method.isAnnotationPresent(GET.class))
      methods.add("GET");
    if (method.isAnnotationPresent(POST.class))
      methods.add("POST");
    if (methods.size() > 0)
      result.append("- Method(s): " + Utils.flatten(methods, ", ") + "\n");

    if (result.length() == 0)
      return null;
    else
      return result.toString();
  }

  /**
   * Generates information about the plugin, to be used for the information
   * return by {@link adams.core.AdditionalInformationHandler}.
   *
   * @param plugin	the plugin to generate the information for
   * @return		the information, null if none available
   */
  public static String getAdditionalInformation(RESTPlugin plugin) {
    StringBuilder	result;
    String		info;

    result = new StringBuilder();

    // class
    info = getAdditionalInformation(plugin.getClass());
    if (info != null)
      result.append("REST Class\n").append(info);

    // methods
    for (Method method: plugin.getClass().getDeclaredMethods()) {
      info = getAdditionalInformation(method);
      if (info != null) {
        if (result.length() > 0)
          result.append("\n");
        result.append("REST Method '" + method.getName() + "'\n");
        result.append(info);
      }
    }

    if (result.length() == 0)
      return null;
    else
      return result.toString();
  }
}
