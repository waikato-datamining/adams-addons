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
 * ModelConfigurator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.model;

import org.deeplearning4j.nn.api.Model;

import java.io.Serializable;

/**
 * Interface for classes that can configure a {@link Model}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ModelConfigurator
  extends Serializable {

  /**
   * Configures a model and returns it.
   *
   * @param numInput	the number of input nodes
   * @param numOutput	the number of output nodes
   * @return		the model
   */
  public Model configureModel(int numInput, int numOutput);
}
