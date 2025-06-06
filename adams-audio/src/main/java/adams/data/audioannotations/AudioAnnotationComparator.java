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
 * AudioAnnotationComparator.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.audioannotations;

import adams.data.container.DataPointComparator;

/**
 * Comparator for annotation objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AudioAnnotationComparator
  extends DataPointComparator<AudioAnnotation> {

  private static final long serialVersionUID = -8169185817314762674L;

  /**
   * Compares its two arguments for order. Returns a negative integer, zero,
   * or a positive integer as the first argument is less than, equal to, or
   * greater than the second.
   *
   * @param o1		the first object
   * @param o2		the second object
   * @return		a negative integer, zero, or a positive integer as
   * 			the first argument is less than, equal to, or greater
   * 			than the second.
   */
  @Override
  public int compare(AudioAnnotation o1, AudioAnnotation o2) {
    int		result;

    result = o1.getTimestamp().compareTo(o2.getTimestamp());

    return result;
  }
}
