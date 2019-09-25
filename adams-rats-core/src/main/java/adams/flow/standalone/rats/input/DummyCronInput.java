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
 * DummyCronInput.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * To be used in conjunction with a 'Cron' rat input.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DummyCronInput
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
    return "To be used in conjunction with a 'Cron' rat input.";
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
   * <br><br>
   * Does nothing.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    return null;
  }
}
