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
 * Standardize.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.heatmap.Heatmap;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Standardizes the values of a heatmap to have mean of 1 and stdev 1.<br>
 * NB: normally, the mean is 0 when standardizing, but heatmaps only allow positive values.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
public class HeatmapStandardize
  extends AbstractFilter<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = 2270876952032422552L;

  /** whether the arrays are samples or populations. */
  protected boolean m_IsSample;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Standardizes the values of a heatmap to have mean of 'min' and stdev 1.\n"
      + "NB: normally, the mean is 0 when standardizing, but heatmaps only "
      + "allow positive values, hence the mean is shifted yb 'min', the smallest "
      + "value of the standardized values.";
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
    int			i;
    double		min;

    result = data.getHeader();
    values = data.toDoubleArray();
    values = StatUtils.standardize(values, false);
    min    = StatUtils.min(values).doubleValue();
    for (i = 0; i < values.length; i++)
      result.set(i, values[i] - min);

    return result;
  }
}
