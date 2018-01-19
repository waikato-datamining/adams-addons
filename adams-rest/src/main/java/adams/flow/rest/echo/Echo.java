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
 * Echo.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.echo;

import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingObject;
import adams.flow.rest.RESTPlugin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Simple echo of the input.
 * <br>
 * Based on code from <a href="https://github.com/zalacer/projects-tn/blob/master/JAX-RSdemo1/src/main/java/tn/HelloWorld.java">here</a>.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@Path("/")
public class Echo
  extends LoggingObject
  implements RESTPlugin {

  private static final long serialVersionUID = -5218893638471880150L;

  /**
   * Pre-configures the logging.
   */
  @Override
  protected void initializeLogging() {
    super.initializeLogging();
    m_LoggingLevel = LoggingLevel.INFO;
  }

  @GET
  @Path("/echo/{input}")
  @Produces("text/plain")
  public String ping(@PathParam("input") String input) {
    getLogger().info("input: " + input);
    return input;
  }
}
