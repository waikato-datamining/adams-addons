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
 * AbstractTrailReader.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.ClassLister;
import adams.data.trail.Trail;

/**
 * Ancestor of trail readers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTrailReader
  extends AbstractDataContainerReader<Trail> {

  private static final long serialVersionUID = 6359759819108867894L;

  /**
   * Returns a list with classnames of readers.
   *
   * @return		the reader classnames
   */
  public static String[] getReaders() {
    return ClassLister.getSingleton().getClassnames(AbstractTrailReader.class);
  }
}
