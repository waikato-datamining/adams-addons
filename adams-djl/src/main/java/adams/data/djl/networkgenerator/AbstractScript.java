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
 * AbstractScript.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.data.djl.networkgenerator;

import adams.flow.core.AdditionalOptions;
import adams.flow.core.AdditionalOptionsHandler;

/**
 * Ancestor for network generator scripts.
 * <br><br>
 * Scripts of scripting languages like Groovy need to be derived from this class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractScript
  extends AbstractNetworkGenerator
  implements AdditionalOptionsHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4002706547202245972L;

  /** for storing the additional options. */
  protected AdditionalOptions m_AdditionalOptions;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_AdditionalOptions = new AdditionalOptions();
  }

  /**
   * Sets the additional options.
   *
   * @param options	the options (name &lt;-&gt;value relation)
   */
  public void setAdditionalOptions(AdditionalOptions options) {
    m_AdditionalOptions = (AdditionalOptions) options.clone();
  }

  /**
   * Returns the value associated with the (additional) option.
   *
   * @return	the options (name &lt;-&gt;value relation)
   */
  public AdditionalOptions getAdditionalOptions() {
    return m_AdditionalOptions;
  }
}
