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
 * GenericServer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest;

import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.flow.core.FlowContextHandler;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Generic REST service provider, which allows you to assemble the REST plugins that should make up the service.<br>
 * Automatically sets the flow context of plugins, if they should implement the adams.flow.core.FlowContextHandler interface.
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
 * <pre>-plugin &lt;adams.flow.rest.RESTPlugin&gt; [-plugin ...] (property: plugins)
 * &nbsp;&nbsp;&nbsp;The plugins that make up the REST service.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GenericServer
  extends AbstractRESTProvider {

  private static final long serialVersionUID = 6759800194384027943L;

  /** the plugins that make up the server. */
  protected RESTPlugin[] m_Plugins;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generic REST service provider, which allows you to assemble the REST "
	+ "plugins that should make up the service.\n"
	+ "Automatically sets the flow context of plugins, if they should implement "
	+ "the " + Utils.classToString(FlowContextHandler.class) + " interface.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "plugin", "plugins",
      new RESTPlugin[0]);
  }

  /**
   * Sets the REST plugins to use.
   *
   * @param value	the plugins
   */
  public void setPlugins(RESTPlugin[] value) {
    m_Plugins = value;
    reset();
  }

  /**
   * Returns the REST plugins in use.
   *
   * @return		the plugins
   */
  public RESTPlugin[] getPlugins() {
    return m_Plugins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String pluginsTipText() {
    return "The plugins that make up the REST service.";
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
    JAXRSServerFactoryBean 	factory;
    RESTPlugin[] 		plugins;
    int				i;

    factory = new JAXRSServerFactoryBean();
    configureInterceptors(factory);

    plugins = (RESTPlugin[]) ObjectCopyHelper.copyObject(m_Plugins);
    for (i = 0; i < plugins.length; i++) {
      if (plugins[i] instanceof FlowContextHandler)
	((FlowContextHandler) plugins[i]).setFlowContext(getFlowContext());
    }
    factory.setServiceBeans(Arrays.asList(plugins));
    factory.setAddress(getURL());

    return factory.create();
  }
}
