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
 * AbstractWaveFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.wavefilter;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.audio.WaveContainer;

/**
 * Ancestor for filters that operate on Wave containers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractWaveFilter
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = -1015958609880455479L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * The default implementation only checks whether there is any data set.
   *
   * @param data	the data to filter
   */
  protected void checkData(WaveContainer data) {
    if (data == null)
      throw new IllegalStateException("No input data provided!");
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected abstract WaveContainer processData(WaveContainer data);

  /**
   * Returns the filtered data.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  public WaveContainer filter(WaveContainer data) {
    checkData(data);
    return processData(data);
  }
}
