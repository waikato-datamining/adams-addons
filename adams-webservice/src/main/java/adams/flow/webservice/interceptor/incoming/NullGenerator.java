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
 * NullGenerator.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor.incoming;


/**
 * Does not generate an interceptor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NullGenerator
  extends AbstractInInterceptorGenerator<AbstractInInterceptor> {

  /** for serialization. */
  private static final long serialVersionUID = -8109018608359183466L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Does not generate an interceptor.";
  }

  /**
   * Generates the actual interceptor for incoming messages.
   * 
   * @return		always null
   */
  @Override
  protected AbstractInInterceptor doGenerate() {
    return null;
  }
}
