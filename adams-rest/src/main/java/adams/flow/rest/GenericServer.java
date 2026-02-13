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
 * Copyright (C) 2018-2026 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest;

import adams.core.AdditionalInformationHandler;
import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.flow.core.FlowContextHandler;
import adams.flow.core.FlowContextUtils;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Generic REST service provider, which allows you to assemble the REST plugins that should make up the service.<br>
 * Automatically sets the flow context of plugins, if they should implement the adams.flow.core.FlowContextHandler interface.<br>
 * Optionally, OpenAPI documentation can be made available at &lt;URL&gt;&#47;api-docs&#47;?url=&#47;openapi.json
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
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
 * <pre>-enable-openapi &lt;boolean&gt; (property: enableOpenAPI)
 * &nbsp;&nbsp;&nbsp;Whether to enable OpenAPI documentation at &lt;URL&gt;&#47;api-docs&#47;?url=&#47;openapi.json
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GenericServer
  extends AbstractRESTProvider
  implements AdditionalInformationHandler {

  private static final long serialVersionUID = 6759800194384027943L;

  /** the plugins that make up the server. */
  protected RESTPlugin[] m_Plugins;

  /** whether to enable open api documentation. */
  protected boolean m_EnableOpenAPI;

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
	+ "the " + Utils.classToString(FlowContextHandler.class) + " interface.\n"
	+ "Optionally, OpenAPI documentation can be made available at <URL>/api-docs/?url=/openapi.json";
  }

  /**
   * Returns the additional information.
   *
   * @return		the additional information, null or 0-length string for no information
   */
  public String getAdditionalInformation() {
    StringBuilder 	result;

    if (m_Plugins.length == 0)
      return null;

    result = new StringBuilder();
    for (RESTPlugin plugin: m_Plugins) {
      if (result.length() > 0)
	result.append("\n");
      result.append(RESTUtils.getAdditionalInformation(plugin));
    }

    return result.toString();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "plugin", "plugins",
      getDefaultPlugins());

    m_OptionManager.add(
      "enable-openapi", "enableOpenAPI",
      false);
  }

  /**
   * Returns the default plugins to use.
   *
   * @return		the default
   */
  protected RESTPlugin[] getDefaultPlugins() {
    return new RESTPlugin[0];
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
   * Sets whether to enable OpenAPI documentation.
   *
   * @param value	true if to enable
   */
  public void setEnableOpenAPI(boolean value) {
    m_EnableOpenAPI = value;
    reset();
  }

  /**
   * Returns whether to enable OpenAPI documentation.
   *
   * @return		true if to enable
   */
  public boolean getEnableOpenAPI() {
    return m_EnableOpenAPI;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enableOpenAPITipText() {
    return "Whether to enable OpenAPI documentation at <URL>/api-docs/?url=/openapi.json";
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
   * For configuring the plugins, e.g., setting the flow context.
   *
   * @param plugins	the plugins to configure
   */
  protected void configurePlugins(RESTPlugin[] plugins) {
    int		i;

    for (i = 0; i < plugins.length; i++)
      FlowContextUtils.update(plugins[i], getFlowContext());
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
    OpenApiFeature 		openApiFeature;
    Set<String> 		resourceClasses;

    factory = new JAXRSServerFactoryBean();
    configureInterceptors(factory);

    plugins = ObjectCopyHelper.copyObject(m_Plugins);
    configurePlugins(plugins);
    factory.setServiceBeans(Arrays.asList(plugins));
    factory.setAddress(getURL());

    configureTLS(factory);

    if (m_EnableOpenAPI) {
      openApiFeature = new OpenApiFeature();
      openApiFeature.setTitle("ADAMS");
      resourceClasses = new HashSet<>();
      for (RESTPlugin plugin : plugins)
	resourceClasses.add(plugin.getClass().getName());
      openApiFeature.setResourceClasses(resourceClasses);
      factory.getFeatures().add(openApiFeature);
    }

    return factory.create();
  }
}
