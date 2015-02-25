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
 * AbstractHeatmapThreshold.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.filter.heatmapthreshold;

import adams.core.option.AbstractOptionHandler;
import adams.data.heatmap.Heatmap;

/**
 * Ancestor for algorithsm that determine a threshold from a heatmap.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHeatmapThreshold
  extends AbstractOptionHandler {

  /**
   * Checks whether the heatmap can be processed, throws an {@link java.lang.IllegalArgumentException}
   * if not.
   * <p/>
   * Default implementation only checks whether a heatmap is present.
   *
   * @param map		the heatmap to check.
   */
  protected void check(Heatmap map) {
    if (map == null)
      throw new IllegalStateException("No heatmap provided!");
  }

  /**
   * Performs the actual calculation of the threshold.
   *
   * @param map		the map to base the calculation on
   * @return		the threshold
   */
  protected abstract double doCalcThreshold(Heatmap map);

  /**
   * Calculates the threshold for the given heatmap.
   *
   * @param map		the map to base the calculation on
   * @return		the threshold
   */
  public double calcThreshold(Heatmap map) {
    check(map);
    return doCalcThreshold(map);
  }
}
