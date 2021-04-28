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
 * AbstractMultiHeatmapOperation.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.multiheatmapoperation;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.heatmap.Heatmap;

/**
 * Abstract base class for operations that require multiple heatmaps.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <O> the generated output type
 */
public abstract class AbstractMultiHeatmapOperation<O>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 1185449853784824033L;

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
   * Returns the minimum number of heatmaps that are required for the operation.
   *
   * @return		the number of heatmaps that are required, <= 0 means no lower limit
   */
  public abstract int minNumSheetsRequired();

  /**
   * Returns the maximum number of heatmaps that are required for the operation.
   *
   * @return		the number of heatmaps that are required, <= 0 means no upper limit
   */
  public abstract int maxNumSheetsRequired();

  /**
   * The type of data that is generated.
   *
   * @return		the class
   */
  public abstract Class generates();

  /**
   * Checks the heatmaps.
   * <br><br>
   * Default implementation only ensures that heatmaps are present.
   *
   * @param maps	the heatmaps to check
   */
  protected void check(Heatmap[] maps) {
    if ((maps == null) || (maps.length == 0))
      throw new IllegalStateException("No heatmaps provided!");

    if (minNumSheetsRequired() > 0) {
      if (maps.length < minNumSheetsRequired())
	throw new IllegalStateException(
	  "Not enough heatmaps supplied (min > supplied): " + minNumSheetsRequired() + " > " + maps.length);
    }

    if (maxNumSheetsRequired() > 0) {
      if (maps.length > maxNumSheetsRequired())
	throw new IllegalStateException(
	  "Too many heatmaps supplied (max < supplied): " + maxNumSheetsRequired() + " < " + maps.length);
    }
  }

  /**
   * Performs the actual processing of the heatmaps.
   *
   * @param maps	the heatmaps to process
   * @param errors	for collecting errors
   * @return		the generated data
   */
  protected abstract O doProcess(Heatmap[] maps, MessageCollection errors);

  /**
   * Processes the heatmaps.
   *
   * @param maps	the heatmaps to process
   * @param errors	for collecting errors
   * @return		the generated data
   */
  public O process(Heatmap[] maps, MessageCollection errors) {
    check(maps);
    return doProcess(maps, errors);
  }
}
