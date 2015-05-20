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
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.heatmapfeatures;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.heatmap.Heatmap;
import adams.data.heatmap.HeatmapValue;
import adams.data.report.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Extracts the highest value.
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
 * <pre>-output-position &lt;boolean&gt; (property: outputPosition)
 * &nbsp;&nbsp;&nbsp;If enabled, the position of the maximum gets output as well (x/y; 0-based).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision: 9598 $
 */
public class Max
  extends AbstractHeatmapFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /** whether to output the position as well. */
  protected boolean m_OutputPosition;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Extracts the highest value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-position", "outputPosition",
      false);
  }

  /**
   * Sets whether to output the position as well.
   *
   * @param value 	whether to output the position
   */
  public void setOutputPosition(boolean value) {
    m_OutputPosition = value;
    reset();
  }

  /**
   * Returns whether to output the position as well.
   *
   * @return 		true if to output the position as well
   */
  public boolean getOutputPosition() {
    return m_OutputPosition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputPositionTipText() {
    return "If enabled, the position of the maximum gets output as well (x/y; 0-based).";
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

    result = new HeaderDefinition();
    result.add("max", DataType.NUMERIC);
    if (m_OutputPosition) {
      result.add("x", DataType.NUMERIC);
      result.add("y", DataType.NUMERIC);
    }

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
    HeatmapValue        max;

    result    = new List[1];
    result[0] = new ArrayList<Object>();
    max       = map.getMaxValue();
    result[0].add(max.getValue());
    if (m_OutputPosition) {
      result[0].add(max.getX());
      result[0].add(max.getY());
    }

    return result;
  }
}
