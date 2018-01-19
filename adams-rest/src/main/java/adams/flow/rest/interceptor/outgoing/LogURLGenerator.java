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
 * LogURLGenerator.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.rest.interceptor.outgoing;

/**
 * Generator for {@link LogURL}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class LogURLGenerator
  extends AbstractOutInterceptorGenerator<LogURL> {

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
	"Generates a " + LogURL.class.getName() + " instance.\n"
	+ "Simply logs the endpoint URL, ie the URL that gets contacted.\n"
	+ "The URL is available through the " + LogURL.class.getName() + "#getLastURL() method.\n"
	+ "If logging is enabled (INFO+), the URL gets output via the logger as well.";
  }

  /**
   * Generates the actual interceptor for incoming messages.
   * 
   * @return		the interceptor
   */
  @Override
  protected LogURL doGenerate() {
    return new LogURL();
  }
}
