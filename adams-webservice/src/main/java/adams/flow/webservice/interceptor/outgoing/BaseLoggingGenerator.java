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
 * BaseLoggingGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor.outgoing;

/**
 * Generator for {@link BaseLogging}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseLoggingGenerator
  extends AbstractOutInterceptorGenerator<BaseLogging> {

  /** for serialization. */
  private static final long serialVersionUID = -8109018608359183466L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a " + BaseLogging.class.getName() + " instance.\n"
	+ "Simply logs the messages using its logger instance.";
  }

  /**
   * Generates the actual interceptor for incoming messages.
   * 
   * @return		the interceptor
   */
  @Override
  protected BaseLogging doGenerate() {
    return new BaseLogging();
  }
}
