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
 * Min.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.featuregenerator;

import adams.data.heatmap.Heatmap;

/**
 <!-- globalinfo-start -->
 * Outputs the smallest intensity value in the heatmap as feature.
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
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use in the field for the generated features.
 * &nbsp;&nbsp;&nbsp;default: Feature
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Min
  extends AbstractFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 8646651693938769168L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Outputs the smallest intensity value in the heatmap as feature.";
  }

  /**
   * Performs the actual feature generation.
   *
   * @param data	the data to process
   * @return		the processed data
   */
  protected Heatmap processData(Heatmap data) {
    addFeature(data, data.getMin());
    return data;
  }
}
