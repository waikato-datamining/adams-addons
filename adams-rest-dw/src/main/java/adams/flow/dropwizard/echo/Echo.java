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
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.flow.dropwizard.echo;

import adams.flow.dropwizard.AbstractRESTPlugin;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

/**
 <!-- globalinfo-start -->
 * Simple echo of the input.<br>
 * <br>
 * Based on code from here:<br>
 * https:&#47;&#47;github.com&#47;zalacer&#47;projects-tn&#47;blob&#47;master&#47;JAX-RSdemo1&#47;src&#47;main&#47;java&#47;tn&#47;HelloWorld.java
 * <br><br>
 <!-- globalinfo-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@Path("/echo/{input}")  // @Path needs to be at class level!
@Produces("text/plain")
public class Echo
  extends AbstractRESTPlugin {

  private static final long serialVersionUID = -5218893638471880150L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Simple echo of the input.\n"
	+ "\n"
	+ "Based on code from here:\n"
	+ "https://github.com/zalacer/projects-tn/blob/master/JAX-RSdemo1/src/main/java/tn/HelloWorld.java";
  }

  @GET
  public String ping(@PathParam("input") String input) {
    getLogger().info("input: " + input);
    return input;
  }
}
