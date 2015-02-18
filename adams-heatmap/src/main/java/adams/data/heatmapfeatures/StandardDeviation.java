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
 * StandardDeviation.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.heatmapfeatures;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.statistics.StatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Determines the standard deviation.
 * <p/>
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
 * <pre>-is-sample &lt;boolean&gt; (property: isSample)
 * &nbsp;&nbsp;&nbsp;If set to true, the arrays are treated as samples and not as populations.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision: 9598 $
 */
public class StandardDeviation
  extends AbstractHeatmapFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /** whether the arrays are samples or populations. */
  protected boolean m_IsSample;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Determines the standard deviation.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "is-sample", "isSample",
	    false);
  }

  /**
   * Sets whether the arrays represent samples instead of populations.
   *
   * @param value	true if arrays are samples and not populations
   */
  public void setIsSample(boolean value) {
    m_IsSample = value;
    reset();
  }

  /**
   * Returns whether the arrays represent samples instead of populations.
   *
   * @return		true if arrays are samples and not populations
   */
  public boolean getIsSample() {
    return m_IsSample;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String isSampleTipText() {
    return "If set to true, the arrays are treated as samples and not as populations.";
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
    result.add("stdev" + (getIsSample() ? "" : "p"), DataType.NUMERIC);

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
    Double[]		values;

    result    = new List[1];
    result[0] = new ArrayList<Object>();
    values    = map.toDoubleArray();
    result[0].add(StatUtils.stddev(values, getIsSample()));

    return result;
  }
}
