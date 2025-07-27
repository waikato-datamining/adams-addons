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
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.dropwizard;

import adams.core.AdditionalInformationHandler;
import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.flow.core.FlowContextHandler;
import adams.flow.core.FlowContextUtils;
import adams.flow.dropwizard.healthcheck.Dummy;
import com.codahale.metrics.health.HealthCheck;
import com.github.fracpete.javautils.struct.Struct2;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-config-file &lt;adams.core.io.PlaceholderFile&gt; (property: configFile)
 * &nbsp;&nbsp;&nbsp;The YAML config file to use for the application, see here for details on
 * &nbsp;&nbsp;&nbsp;the format: https:&#47;&#47;www.dropwizard.io&#47;en&#47;stable&#47;manual&#47;configuration.html
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-plugin &lt;adams.flow.dropwizard.RESTPlugin&gt; [-plugin ...] (property: plugins)
 * &nbsp;&nbsp;&nbsp;The plugins that make up the REST service.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-health-check &lt;com.codahale.metrics.health.HealthCheck&gt; [-health-check ...] (property: healthChecks)
 * &nbsp;&nbsp;&nbsp;The health checks to register.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.dropwizard.healthcheck.Dummy
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

  public static class GenericApplication
    extends Application<Configuration> {

    /** the owner. */
    protected GenericServer m_Owner;

    /** the environment. */
    protected Environment m_Environment;

    public GenericApplication(GenericServer owner) {
      m_Owner = owner;
    }

    @Override
    public String getName() {
      return "generic";
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
      // nothing to do yet
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
      RESTPlugin[]	plugins;

      plugins = ObjectCopyHelper.copyObject(m_Owner.getPlugins());
      m_Owner.configurePlugins(plugins);

      for (RESTPlugin plugin: plugins) {
	if (m_Owner.isLoggingEnabled())
	  m_Owner.getLogger().info("Registering plugin: " + Utils.classToString(plugin));
	environment.jersey().register(plugin);
      }
      m_Environment = environment;

      // add a dummy health check
      if (m_Owner.getHealthChecks().length == 0) {
	if (m_Owner.isLoggingEnabled())
	  m_Owner.getLogger().info("Registering dummy health check");
	environment.healthChecks().register("dummy", new HealthCheck() {
	  @Override
	  protected Result check() throws Exception {
	    return Result.healthy();
	  }
	});
      }
      else {
	for (HealthCheck check: m_Owner.getHealthChecks()) {
	  if (m_Owner.isLoggingEnabled())
	    m_Owner.getLogger().info("Registering health check: " + Utils.classToString(check));
	  environment.healthChecks().register(check.getClass().getName(), check);
	}
      }
    }

    public Environment getEnvironment() {
      return m_Environment;
    }
  }

  /** the plugins that make up the server. */
  protected RESTPlugin[] m_Plugins;

  /** the healthchecks. */
  protected HealthCheck[] m_HealthChecks;

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
      "health-check", "healthChecks",
      getDefaultHealthChecks());
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
   * Returns the default health checks to use.
   *
   * @return		the default
   */
  protected HealthCheck[] getDefaultHealthChecks() {
    return new HealthCheck[]{new Dummy()};
  }

  /**
   * Sets the health checks to use.
   *
   * @param value	the checks
   */
  public void setHealthChecks(HealthCheck[] value) {
    m_HealthChecks = value;
    reset();
  }

  /**
   * Returns the health checks in use.
   *
   * @return		the checks
   */
  public HealthCheck[] getHealthChecks() {
    return m_HealthChecks;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String healthChecksTipText() {
    return "The health checks to register.";
  }

  /**
   * For configuring the plugins, e.g., setting the flow context.
   *
   * @param plugins	the plugins to configure
   */
  public void configurePlugins(RESTPlugin[] plugins) {
    int		i;

    for (i = 0; i < plugins.length; i++)
      FlowContextUtils.update(plugins[i], getFlowContext());
  }

  /**
   * Performs the actual start of the service.
   *
   * @return 		the tuple of application and environment
   * @throws Exception	if start fails
   */
  protected Struct2<Application, Environment> doStart() throws Exception {
    GenericApplication 		application;

    application = new GenericApplication(this);
    if (m_ConfigFile.isDirectory()) {
      application.run("server");
    }
    else {
      if (isLoggingEnabled())
	getLogger().info("Using config file: " + m_ConfigFile);
      application.run("server", m_ConfigFile.getAbsolutePath());
    }

    return new Struct2<>(application, application.getEnvironment());
  }
}
