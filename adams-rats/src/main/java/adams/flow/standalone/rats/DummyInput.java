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

/**
 * DummyInput.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DummyInput
  extends AbstractRatInput {

  /** for serialization. */
  private static final long serialVersionUID = -4640490350537786881L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy input receiver, does nothing.";
  }

  /**
   * Returns the type of data this scheme generates.
   * 
   * @return		the class
   */
  @Override
  public Class generates() {
    return Unknown.class;
  }

  /**
   * Checks whether any output can be collected.
   * 
   * @return		true if output available
   */
  @Override
  public boolean hasPendingOutput() {
    return false;
  }

  /**
   * Returns the received data.
   * 
   * @return		the data
   */
  @Override
  public Object output() {
    return null;
  }

  /**
   * Performs the actual reception of data.
   * <p/>
   * Does nothing.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    return null;
  }
}
