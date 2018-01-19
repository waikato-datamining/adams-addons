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
 * AbstractInInterceptorGenerator.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.rest.interceptor.incoming;

import adams.core.CleanUpHandler;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for generators for incoming message interceptors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractInInterceptorGenerator<T extends AbstractInInterceptor>
  extends AbstractOptionHandler
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8741445331354712393L;

  /** whether the generator is enabled. */
  protected boolean m_Enabled;

  /** the last interceptor generated. */
  protected transient T m_LastInterceptor;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "enabled", "enabled",
      true);
  }

  /**
   * Sets whether the generator is enabled, ie instantiating the interceptor.
   *
   * @param value	true if enabled
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether the generator is enabled, ie instantiating the interceptor.
   *
   * @return		true if enabled
   */
  public boolean getEnabled() {
    return m_Enabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enabledTipText() {
    return "Interceptor gets only instantiated if the generator is enabled.";
  }

  /**
   * Hook method for checks, throws an exception if check fails.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void check() {
  }
  
  /**
   * Generates the actual interceptor for incoming messages.
   * 
   * @return		the interceptor
   */
  protected abstract T doGenerate();
  
  /**
   * Generates the interceptor for incoming messages.
   * 
   * @return		the interceptor
   */
  public T generate() {
    T	result;

    if (!m_Enabled)
      return null;

    m_LastInterceptor = null;
    check();
    result = doGenerate();
    if (result != null)
      result.setLoggingLevel(getLoggingLevel());
    m_LastInterceptor = result;
    
    return result;
  }
  
  /**
   * Checks whether there is a last interceptor available.
   * 
   * @return		true if available
   */
  public boolean hasLastInterceptor() {
    return (m_LastInterceptor != null);
  }
  
  /**
   * Returns the last interceptor that was generated.
   * 
   * @return		the interceptor, null if not available
   */
  public T getLastInterceptor() {
    return m_LastInterceptor;
  }
  
  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_LastInterceptor = null;
  }
}
