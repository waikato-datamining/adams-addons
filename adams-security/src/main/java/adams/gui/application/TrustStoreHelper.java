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
 * TrustStoreHelper.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

/**
 * Initializes the global trust store settings.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TrustStoreHelper
  extends AbstractInitialization {

  /** for serialization. */
  private static final long serialVersionUID = -380898549266760244L;

  /**
   * The title of the initialization.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Trust store";
  }
  
  /**
   * Performs the initialization.
   * 
   * 
   * @param parent	the application this initialization is for, can be null
   * @return		true if successful
   */
  @Override
  protected boolean doInitialize(final AbstractApplicationFrame parent) {
    adams.core.management.TrustStoreHelper.getSingleton().apply();
    return true;
  }
}
