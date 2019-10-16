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
 * CallableTextTransformers.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.flow;

import adams.core.MessageCollection;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Processing transformer for arbitrary text.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CallableTextTransformer
  extends AbstractCallableTransformer {

  private static final long serialVersionUID = -5634751937754511917L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return globalInfoBase() + "\n"
      + "Can be used for processing arbitrary text.";
  }

  /**
   * Processes the measurement.
   *
   * @param input	the incoming measurement
   * @return		the response
   */
  @POST
  @Path("/process")
  public Response process(String input) {
    Response		result;
    Object		output;
    MessageCollection	errors;

    result = null;
    errors = new MessageCollection();
    output = doProcess(input, errors);

    if (!errors.isEmpty())
      result = Response.status(500, errors.toString()).build();
    else if (output == null)
      result = Response.status(500, "No output generated").build();

    if ((result == null) && (output != null))
      result = Response.ok("" + output, "*/*").build();

    return result;
  }
}
