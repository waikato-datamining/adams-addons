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
 * Max.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.featuregenerator;

import adams.data.heatmap.Heatmap;

/**
 <!-- globalinfo-start -->
 * Outputs the largest intensity value in the heatmap as feature.
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
public class Max
  extends AbstractFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -3929486803259468016L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Outputs the largest intensity value in the heatmap as feature.";
  }

  /**
   * Performs the actual feature generation.
   *
   * @param data	the data to process
   * @return		the processed data
   */
  protected Heatmap processData(Heatmap data) {
    addFeature(data, data.getMax());
    return data;
  }
}
