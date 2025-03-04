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
 * GroovyServer.java
 * Copyright (C) 2019-2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevelHandler;
import adams.core.scripting.Groovy;
import adams.flow.core.FlowContextHandler;
import adams.flow.core.FlowContextUtils;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * GenericREST service provider, which allows you to assemble the REST plugins that should make up the service from Groovy scripts.<br>
 * Scripts either need to implement adams.flow.rest.RESTPlugin interface or be derived from the adams.flow.rest.AbstractRESTPlugin superclass.<br>
 * Automatically sets the flow context of plugins, if they should implement the adams.flow.core.FlowContextHandler interface.<br>
 * If implementing the adams.core.logging.LoggingLevelHandler interface, they plugin receives the same logging level as this server.
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
 * <pre>-plugin &lt;adams.core.io.PlaceholderFile&gt; [-plugin ...] (property: plugins)
 * &nbsp;&nbsp;&nbsp;The plugins (ie the Groovy scripts) that make up the REST service.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GroovyServer
  extends AbstractRESTProvider{

  private static final long serialVersionUID = 6759800194384027943L;

  /** the scripts with the plugins that make up the server. */
  protected PlaceholderFile[] m_UnparametrizedScripts;

  /** the parametrized scripts. */
  protected GroovyPlugin[] m_ParametrizedScripts;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "GenericREST service provider, which allows you to assemble the REST "
	+ "plugins that should make up the service from Groovy scripts.\n"
	+ "\n"
	+ "Unparametrized scripts:\n"
        + "Scripts either need to implement " + Utils.classToString(RESTPlugin.class) + " interface "
        + "or be derived from the " + Utils.classToString(AbstractRESTPlugin.class) + " superclass.\n"
	+ "Automatically sets the flow context of plugins, if they should implement "
	+ "the " + Utils.classToString(FlowContextHandler.class) + " interface.\n"
        + "If implementing the " + Utils.classToString(LoggingLevelHandler.class) + " interface, "
	+ "they plugin receives the same logging level as this server.\n"
	+ "\n"
	+ "Parametrized scripts:\n";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "unparametrized-script", "unparametrizedScripts",
      new PlaceholderFile[0]);

    m_OptionManager.add(
      "parametrized-script", "parametrizedScripts",
      new GroovyPlugin[0]);
  }

  /**
   * Sets the non-parametrized REST Groovy scripts to use.
   *
   * @param value	the scripts
   */
  public void setUnparametrizedScripts(PlaceholderFile[] value) {
    m_UnparametrizedScripts = value;
    reset();
  }

  /**
   * Returns the non-parametrized REST Groovy scripts in use.
   *
   * @return		the scripts
   */
  public PlaceholderFile[] getUnparametrizedScripts() {
    return m_UnparametrizedScripts;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String unparametrizedScriptsTipText() {
    return "The (non-parametrized) Groovy scripts that make up the REST service.";
  }

  /**
   * Sets the parametrized REST Groovy scripts to use.
   *
   * @param value	the scripts
   */
  public void setParametrizedScripts(GroovyPlugin[] value) {
    m_ParametrizedScripts = value;
    reset();
  }

  /**
   * Returns the parametrized REST Groovy scripts in use.
   *
   * @return		the scripts
   */
  public GroovyPlugin[] getParametrizedScripts() {
    return m_ParametrizedScripts;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String parametrizedScriptsTipText() {
    return "The (parametrized) Groovy scripts that make up the REST service.";
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
   * Loads the plugin scripts.
   *
   * @return		the loaded plugins
   * @throws Exception	if loading fails
   */
  protected Object[] loadPlugins() throws Exception {
    List<Object> 	result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < m_UnparametrizedScripts.length; i++)
      result.add(Groovy.getSingleton().newInstance(m_UnparametrizedScripts[i], Object.class));
    for (i = 0; i < m_ParametrizedScripts.length; i++)
      result.add(m_ParametrizedScripts[i].loadPlugin(getFlowContext()));

    return result.toArray(new Object[0]);
  }

  /**
   * For configuring the plugins, e.g., setting the flow context and logging level.
   *
   * @param plugins	the plugins to configure
   */
  protected void configurePlugins(Object[] plugins) {
    int		i;

    for (i = 0; i < plugins.length; i++) {
      if (FlowContextUtils.isHandler(plugins[i]))
	FlowContextUtils.update(plugins[i], getFlowContext());
      if (plugins[i] instanceof LoggingLevelHandler)
	((LoggingLevelHandler) plugins[i]).setLoggingLevel(getLoggingLevel());
    }
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
    Object[] 			plugins;

    factory = new JAXRSServerFactoryBean();
    configureInterceptors(factory);

    plugins = loadPlugins();
    configurePlugins(plugins);
    factory.setServiceBeans(Arrays.asList(plugins));
    factory.setAddress(getURL());

    configureTLS(factory);

    return factory.create();
  }
}
