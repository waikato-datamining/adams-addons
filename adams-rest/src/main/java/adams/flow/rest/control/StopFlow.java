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
 * StopFlow.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.control;

import adams.core.MessageCollection;
import adams.flow.core.Actor;
import adams.flow.rest.AbstractRegisteredFlowRESTPlugin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Allows to stop a (registered) flow via its ID.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StopFlow
  extends AbstractRegisteredFlowRESTPlugin {

  private static final long serialVersionUID = -3247606641885793684L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to stop a (registered) flow via its ID.";
  }

  /**
   * Stops the flow with the specified ID.
   * You can use -1 as shortcut for the only running flow.
   *
   * @param id		the flow ID, use -1 for only one running
   * @return		"OK" or error message
   */
  @GET
  @Path("/flow/control/stop/{id}")
  public String restart(@PathParam("id") int id) {
    String		result;
    MessageCollection	errors;
    Actor		flow;

    result = "OK";
    errors = new MessageCollection();
    flow   = getFlow(id, false, errors);
    if (!errors.isEmpty())
      return errors.toString();

    if (flow != null)
      flow.stopExecution();

    return result;
  }
}
