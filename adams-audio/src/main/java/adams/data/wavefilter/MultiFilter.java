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
 * MultiFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.data.wavefilter;

import adams.data.audio.WaveContainer;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiFilter
  extends AbstractWaveFilter {

  /** for serialization. */
  private static final long serialVersionUID = 2319957467336388607L;

  /** the filters. */
  protected AbstractWaveFilter[] m_Filters;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Applies the filters sequentially, with the output data of one filter "
          + "being the input data for the next.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filters",
      new AbstractWaveFilter[0]);
  }

  /**
   * Sets the filters to use.
   *
   * @param value	the filters
   */
  public void setFilters(AbstractWaveFilter[] value) {
    m_Filters = value;
    reset();
  }

  /**
   * Returns the filters in use.
   *
   * @return		the filters
   */
  public AbstractWaveFilter[] getFilters() {
    return m_Filters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filtersTipText() {
    return "The filters to apply sequentially.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected WaveContainer processData(WaveContainer data) {
    WaveContainer 	result;
    int			i;

    result = data;

    for (i = 0; i < m_Filters.length; i++) {
      if (isLoggingEnabled())
        getLogger().info("Applying filter #" + (i+1) + "...");
      result = m_Filters[i].filter(result);
    }

    return result;
  }
}
