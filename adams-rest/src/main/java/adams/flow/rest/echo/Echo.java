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

import adams.flow.rest.AbstractRESTPlugin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

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
  @Path("/echo/{input}")
  @Produces("text/plain")
  public String ping(@PathParam("input") String input) {
    getLogger().info("input: " + input);
    return input;
  }
}
