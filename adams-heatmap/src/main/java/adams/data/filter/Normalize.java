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
 * Normalize.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.heatmap.Heatmap;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Normalizes the values of a heatmap to have a range of 0 to 1.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Normalize
  extends AbstractFilter<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = 2270876952032422552L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Normalizes the values of a heatmap to have a range of 0 to 1.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected Heatmap processData(Heatmap data) {
    Heatmap		result;
    Double[]		values;

    result = data.getHeader();
    values = data.toDoubleArray();
    values = StatUtils.normalize(values);
    if (values == null) {
      result.getNotes().addError(getClass(), "Failed to normalize, sum = 0!");
      result.set(data.toDoubleArray());
    }
    else {
      result.set(values);
    }

    return result;
  }
}
