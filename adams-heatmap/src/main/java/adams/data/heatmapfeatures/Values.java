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
 * Values.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.heatmapfeatures;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Gets all the values of the heatmap.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class Values
  extends AbstractHeatmapFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Gets all the values of the heatmap.";
  }

  /**
   * Creates the header from a template heatmap.
   *
   * @param map		the heatmap to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(Heatmap map) {
    HeaderDefinition		result;
    int				i;
    int				numPixels;

    numPixels = map.getWidth() * map.getHeight();
    result    = new HeaderDefinition();
    for (i = 0; i < numPixels; i++)
      result.add("att_" + (i+1), DataType.NUMERIC);

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param map		the heatmap to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(Heatmap map) {
    List<Object>[]	result;
    int			i;
    int			n;
    int			height;
    int			width;

    result    = new List[1];
    result[0] = new ArrayList<Object>();
    height    = map.getHeight();
    width     = map.getWidth();

    for (n = 0; n < height; n++) {
      for (i = 0; i < width; i++)
	result[0].add(map.get(n, i));
    }

    return result;
  }
}
