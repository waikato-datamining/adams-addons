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
 * AbstractDataSetIteratorConfigurator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.datasetiterator;

import adams.core.option.AbstractOptionHandler;
import org.deeplearning4j.datasets.iterator.DataSetIterator;

/**
 * Ancestor for dataset configurators configurators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataSetIteratorConfigurator
  extends AbstractOptionHandler
  implements DataSetIteratorConfigurator {

  private static final long serialVersionUID = -5049221729823530346L;

  /**
   * Hook method before configuring the iterator.
   * <br>
   * Default implementation does nothing.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    return null;
  }

  /**
   * Configures the actual {@link DataSetIterator} and returns it.
   *
   * @return		the iterator
   */
  protected abstract DataSetIterator doConfigureDataSetIterator();

  /**
   * Configures the {@link DataSetIterator} and returns it.
   *
   * @return		the iterator
   */
  public DataSetIterator configureDataSetIterator() {
    check();
    return doConfigureDataSetIterator();
  }
}
