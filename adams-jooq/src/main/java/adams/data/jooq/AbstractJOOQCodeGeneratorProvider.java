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
 * AbstractJOOQCodeGeneratorProvider.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.jooq;

import org.jooq.util.Generator;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for providers that create instances of jOOQ code generators.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8697 $
 */
public abstract class AbstractJOOQCodeGeneratorProvider
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5411867936600439311L;

  /**
   * Performs some checks.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void check() {
  }
  
  /**
   * Returns the actual code generator.
   * 
   * @return		the code generator
   */
  protected abstract Generator doGenerate();

  /**
   * Returns the code generator to use.
   */
  public Generator generate() {
    check();
    return doGenerate();
  }
}
