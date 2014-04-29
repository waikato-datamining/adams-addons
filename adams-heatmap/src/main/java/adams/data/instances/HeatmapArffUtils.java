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
 * ArffUtils.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.instances;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

/**
 * A helper class for turning heatmap data into ARFF files and vice versa.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapArffUtils
  extends adams.data.weka.ArffUtils {

  /** the prefix for intensity values. */
  public final static String PREFIX_INTENSITY = "Intensity-";

  /**
   * Generates the attribute name for an intensity value.
   *
   * @param row		the row of the intensity
   * @param col		the column of the intensity
   * @return		the attribute name
   */
  public static String getIntensityName(int row, int col) {
    return PREFIX_INTENSITY + row + "." + col;
  }

  /**
   * Initializes the Remove filter for removing all IDs from the dataset.
   *
   * @param data	the data to use for the analysis
   * @return		the configured filter, null if no filtering required
   * @throws Exception	if filter setup fails
   */
  public static Remove getRemoveFilter(Instances data) {
    Remove		result;
    List<String>	atts;
    Attribute		att;

    data = new Instances(data);
    data.deleteWithMissingClass();

    // Filter data if necessary
    atts = new ArrayList<String>();
    if ((att = data.attribute(HeatmapArffUtils.getDBIDName())) != null)
      atts.add("" +(att.index() + 1));
      atts.add("" + (att.index() + 1));
    if (atts.size() > 0) {
      result = new Remove();
      result.setAttributeIndices(adams.core.Utils.flatten(atts, ","));
    }
    else {
      result = null;
    }

    return result;
  }
}
