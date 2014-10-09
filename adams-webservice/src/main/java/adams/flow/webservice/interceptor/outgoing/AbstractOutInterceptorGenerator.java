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
 * AbstractOutInterceptorGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor.outgoing;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for generators for outgoing message interceptors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOutInterceptorGenerator<T extends AbstractOutInterceptor>
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8741445331354712393L;

  /**
   * Hook method for checks, throws an exception if check fails.
   * <p/>
   * Default implementation does nothing.
   */
  protected void check() {
  }
  
  /**
   * Generates the actual interceptor for outgoing messages.
   * 
   * @return		the interceptor
   */
  protected abstract T doGenerate();
  
  /**
   * Generates the interceptor for outgoing messages.
   * 
   * @return		the interceptor
   */
  public T generate() {
    check();
    return doGenerate();
  }
}
