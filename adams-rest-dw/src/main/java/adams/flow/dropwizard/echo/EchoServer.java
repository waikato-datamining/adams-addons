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
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.dropwizard.echo;

import adams.core.Utils;
import adams.flow.dropwizard.AbstractRESTProvider;
import com.codahale.metrics.health.HealthCheck;
import com.github.fracpete.javautils.struct.Struct2;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

/**
 <!-- globalinfo-start -->
 * Only offers: adams.flow.dropwizard.echo.Echo<br>
 * <br>
 * REST Class<br>
 * - Path: &#64;jakarta.ws.rs.Path(value="&#47;echo&#47;{input}")<br>
 * - Produces: &#64;jakarta.ws.rs.Produces(value={"text&#47;plain"})<br>
 * <br>
 * REST Method 'ping'<br>
 * - Parameter #1: &#64;jakarta.ws.rs.PathParam(value="input")<br>
 * - Method(s): GET<br>
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EchoServer
  extends AbstractRESTProvider {

  private static final long serialVersionUID = 2978764775645037701L;

  public static class EchoApplication
    extends Application<Configuration> {

    /** the owner. */
    protected EchoServer m_Owner;

    /** the environment. */
    protected Environment m_Environment;

    public EchoApplication(EchoServer owner) {
      m_Owner = owner;
    }

    @Override
    public String getName() {
      return "echo";
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
      // nothing to do yet
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
      environment.jersey().register(new Echo());
      m_Environment = environment;

      // add a dummy health check
      environment.healthChecks().register("dummy", new HealthCheck() {
	@Override
	protected Result check() throws Exception {
	  return Result.healthy();
	}
      });
    }

    public Environment getEnvironment() {
      return m_Environment;
    }
  }

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
   * Performs the actual start of the service.
   *
   * @return 		the tuple of application and environment
   * @throws Exception	if start fails
   */
  @Override
  protected Struct2<Application, Environment> doStart() throws Exception {
    EchoApplication	application;

    application = new EchoApplication(this);
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
