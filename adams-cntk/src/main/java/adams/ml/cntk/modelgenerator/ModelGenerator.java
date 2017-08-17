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
 * ModelGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelgenerator;

import adams.flow.core.FlowContextHandler;

import java.util.List;

/**
 * Interface for classes that generate CNTK model scripts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ModelGenerator
  extends FlowContextHandler {

  /**
   * Generates models.
   *
   * @param numInput	the number of input nodes
   * @param numOutput	the number of output nodes
   * @return		the models
   */
  public List<String> generate(int numInput, int numOutput);
}
