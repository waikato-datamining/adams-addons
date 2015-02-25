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
 * AbstractFilteredHeatmapCrop.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.filter.heatmapcrop;

import adams.data.filter.AbstractFilter;
import adams.data.filter.PassThrough;
import adams.data.heatmap.Heatmap;

/**
 * Ancestor for crop algorithms that filter the original heatmap first
 * before performing the actual crop.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFilteredHeatmapCrop
  extends AbstractHeatmapCrop {

  /** the filter to apply to the data first. */
  protected AbstractFilter m_Filter;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    getDefaultFilter());
  }

  /**
   * Returns the default pre-filter to use.
   *
   * @return		the default
   */
  protected AbstractFilter getDefaultFilter() {
    return new PassThrough();
  }

  /**
   * Sets the pre-filter.
   *
   * @param value 	the filter
   */
  public void setFilter(AbstractFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the current pre-filter.
   *
   * @return 		the filter
   */
  public AbstractFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "Pre-filters the heatmap.";
  }

  /**
   * Performs the actual cropping, using the pre-filtered heatmap to manipulate
   * the original heatmap.
   *
   * @param filtered	the pre-filtered heatmap
   * @param original	the original heatmap
   * @return		the final data
   */
  protected abstract Heatmap doCrop(Heatmap filtered, Heatmap original);

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected Heatmap doCrop(Heatmap data) {
    Heatmap			result;
    Heatmap			filtered;
    AbstractFilter<Heatmap>	filter;

    filter   = (AbstractFilter<Heatmap>) m_Filter.shallowCopy(true);
    filtered = filter.filter(data);
    filter.destroy();

    result = doCrop(filtered, data);

    return result;
  }
}
