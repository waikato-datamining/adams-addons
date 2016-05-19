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
 * DL4JInitialization.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.application;

import org.nd4j.linalg.factory.Nd4j;

/**
 * DL4J initializations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JInitialization
  extends AbstractInitialization {

  private static final long serialVersionUID = -3692113051244963217L;

  /**
   * The title of the initialization.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "DL4J";
  }

  /**
   * Performs the initialization.
   *
   * @param parent	the application this initialization is for
   * @return		true if successful
   */
  @Override
  public boolean initialize(AbstractApplicationFrame parent) {
    Nd4j.ENFORCE_NUMERICAL_STABILITY = true;
    return true;
  }
}
