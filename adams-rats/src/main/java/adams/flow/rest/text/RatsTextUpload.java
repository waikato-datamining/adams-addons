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
 * RatsTextUpload.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.text;

import adams.flow.rest.AbstractRESTPlugin;
import adams.flow.standalone.rats.input.BufferedRatInput;
import adams.flow.standalone.rats.input.RatInput;
import adams.flow.standalone.rats.input.RatInputUser;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 <!-- globalinfo-start -->
 * Simple plugin for submitting text in plain-text format to RATS input.
 * <br><br>
 <!-- globalinfo-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RatsTextUpload
  extends AbstractRESTPlugin
  implements RatInputUser {

  private static final long serialVersionUID = 17885556434586202L;

  /** the rat input. */
  protected RatInput m_RatInput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simple plugin for submitting text in plain-text format to RATS input.";
  }

  /**
   * Sets the rat input to use.
   *
   * @param value	the rat input
   */
  @Override
  public void setRatInput(RatInput value) {
    m_RatInput = value;
  }

  /**
   * Returns the rat input in use.
   *
   * @return		the rat input, null if none set
   */
  @Override
  public RatInput getRatInput() {
    return m_RatInput;
  }

  @POST
  @Path("/upload")
  public void upload(@FormParam("content") String content) {
    if (m_RatInput == null) {
      getLogger().warning("No RAT input set!");
      return;
    }
    if (getRatInput() instanceof BufferedRatInput)
      ((BufferedRatInput) getRatInput()).bufferData(content);
  }
}
