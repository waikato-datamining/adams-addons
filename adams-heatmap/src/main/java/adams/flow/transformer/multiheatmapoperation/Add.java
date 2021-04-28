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
 * Add.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.multiheatmapoperation;

import adams.core.MessageCollection;
import adams.data.heatmap.Heatmap;

/**
 * Adds the values of the heatmaps.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Add
  extends AbstractMultiHeatmapOperation<Heatmap>{

  private static final long serialVersionUID = 6124767252812041585L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Adds the values of the heatmaps.";
  }

  /**
   * Returns the minimum number of heatmaps that are required for the operation.
   *
   * @return the number of heatmaps that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumSheetsRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of heatmaps that are required for the operation.
   *
   * @return the number of heatmaps that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumSheetsRequired() {
    return 0;
  }

  /**
   * The type of data that is generated.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Heatmap.class;
  }

  /**
   * Checks the heatmaps.
   *
   * @param maps	the heatmaps to check
   */
  protected void check(Heatmap[] maps) {
    int 	i;

    super.check(maps);

    for (i = 1; i < maps.length; i++) {
      if (maps[0].getWidth() != maps[i].getWidth())
        throw new IllegalStateException("Heatmap 1 and " + (i+1) + " differ in width: " + maps[0].getWidth() + " != " + maps[i].getWidth());
      if (maps[0].getHeight() != maps[i].getHeight())
        throw new IllegalStateException("Heatmap 1 and " + (i+1) + " differ in height: " + maps[0].getHeight() + " != " + maps[i].getHeight());
    }
  }

  /**
   * Performs the actual processing of the heatmaps.
   *
   * @param maps   the heatmaps to process
   * @param errors for collecting errors
   * @return the generated data
   */
  @Override
  protected Heatmap doProcess(Heatmap[] maps, MessageCollection errors) {
    Heatmap	result;
    int		i;
    int		x;
    int		y;

    result = maps[0].getClone();
    for (i = 1; i < maps.length; i++) {
      for (y = 0; y < result.getHeight(); y++) {
	for (x = 0; x < result.getWidth(); x++)
	  result.set(y, x, result.get(y, x) + maps[i].get(y, x));
      }
    }

    return result;
  }
}
