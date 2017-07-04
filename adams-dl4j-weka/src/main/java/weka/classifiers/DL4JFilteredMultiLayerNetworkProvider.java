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
 * DL4JFilteredMultiLayerNetworkProvider.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import weka.filters.Filter;

/**
 * Interface for classes that filter their data with a Weka filter before
 * presenting it to the model.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface DL4JFilteredMultiLayerNetworkProvider
  extends DL4JMultiLayerNetworkProvider {

  /**
   * Returns the filter that is used to filter the data before presenting it
   * to the network.
   *
   * @return		the filter in use
   */
  public Filter getPreFilter();
}
