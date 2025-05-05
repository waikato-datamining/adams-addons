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
 * OutputDirGenerator.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.djl.outputdirgenerator;

import adams.core.io.PlaceholderDirectory;
import adams.flow.core.FlowContextHandler;

/**
 * Interface for output directory generator schemes.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface OutputDirGenerator
  extends FlowContextHandler {

  /**
   * Generates the output directory.
   *
   * @return		the directory
   */
  public PlaceholderDirectory generate();
}
