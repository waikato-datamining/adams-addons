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
 * Histogram.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.heatmapfeatures;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayHistogram;
import adams.data.statistics.ArrayHistogram.BinCalculation;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Generates a histogram.
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
 * <pre>-skip-missing &lt;boolean&gt; (property: skipMissing)
 * &nbsp;&nbsp;&nbsp;If enabled, missing values get skipped when collecting the values for the 
 * &nbsp;&nbsp;&nbsp;histogram.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-num-bins &lt;int&gt; (property: numBins)
 * &nbsp;&nbsp;&nbsp;The number of bins to use in case of manual bin calculation.
 * &nbsp;&nbsp;&nbsp;default: 50
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-use-fixed-min-max &lt;boolean&gt; (property: useFixedMinMax)
 * &nbsp;&nbsp;&nbsp;If enabled, then the user-specified min&#47;max values are used for the bin 
 * &nbsp;&nbsp;&nbsp;calculation rather than the min&#47;max from the data (allows comparison of 
 * &nbsp;&nbsp;&nbsp;histograms when generating histograms over a range of arrays).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-manual-min &lt;double&gt; (property: manualMin)
 * &nbsp;&nbsp;&nbsp;The minimum to use when using manual binning with user-supplied min&#47;max 
 * &nbsp;&nbsp;&nbsp;enabled.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 * <pre>-manual-max &lt;double&gt; (property: manualMax)
 * &nbsp;&nbsp;&nbsp;The maximum to use when using manual binning with user-supplied max&#47;max 
 * &nbsp;&nbsp;&nbsp;enabled.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision: 9598 $
 */
public class Histogram
  extends AbstractHeatmapFeatureGeneratorWithSkippableMissingValues {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /** the number of bins in case of manual bin calculation. */
  protected int m_NumBins;

  /** whether to use fixed min/max for manual bin calculation. */
  protected boolean m_UseFixedMinMax;

  /** the manual minimum. */
  protected double m_ManualMin;

  /** the manual maximum. */
  protected double m_ManualMax;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a histogram.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-bins", "numBins",
      50, 1, null);

    m_OptionManager.add(
      "use-fixed-min-max", "useFixedMinMax",
      false);

    m_OptionManager.add(
      "manual-min", "manualMin",
      0.0);

    m_OptionManager.add(
      "manual-max", "manualMax",
      1.0);
  }

  /**
   * Sets the number of bins to use in manual calculation.
   *
   * @param value 	the number of bins
   */
  public void setNumBins(int value) {
    m_NumBins = value;
    reset();
  }

  /**
   * Returns the number of bins to use in manual calculation.
   *
   * @return 		the number of bins
   */
  public int getNumBins() {
    return m_NumBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBinsTipText() {
    return "The number of bins to use in case of manual bin calculation.";
  }

  /**
   * Sets whether to use user-supplied min/max for bin calculation rather
   * than obtain min/max from data.
   *
   * @param value 	true if to use user-supplied min/max
   */
  public void setUseFixedMinMax(boolean value) {
    m_UseFixedMinMax = value;
    reset();
  }

  /**
   * Returns whether to use user-supplied min/max for bin calculation rather
   * than obtain min/max from data.
   *
   * @return 		true if to use user-supplied min/max
   */
  public boolean getUseFixedMinMax() {
    return m_UseFixedMinMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useFixedMinMaxTipText() {
    return
	"If enabled, then the user-specified min/max values are used for the "
	+ "bin calculation rather than the min/max from the data (allows "
	+ "comparison of histograms when generating histograms over a range "
	+ "of arrays).";
  }

  /**
   * Sets the minimum to use when using manual binning with user-supplied
   * min/max enabled.
   *
   * @param value 	the minimum
   */
  public void setManualMin(double value) {
    m_ManualMin = value;
    reset();
  }

  /**
   * Returns the minimum to use when using manual binning with user-supplied
   * min/max enabled.
   *
   * @return 		the minimum
   */
  public double getManualMin() {
    return m_ManualMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String manualMinTipText() {
    return "The minimum to use when using manual binning with user-supplied min/max enabled.";
  }

  /**
   * Sets the maximum to use when using manual binning with user-supplied
   * max/max enabled.
   *
   * @param value 	the maximum
   */
  public void setManualMax(double value) {
    m_ManualMax = value;
    reset();
  }

  /**
   * Returns the maximum to use when using manual binning with user-supplied
   * max/max enabled.
   *
   * @return 		the maximum
   */
  public double getManualMax() {
    return m_ManualMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String manualMaxTipText() {
    return "The maximum to use when using manual binning with user-supplied max/max enabled.";
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

    result = new HeaderDefinition();
    for (i = 0; i < m_NumBins; i++)
      result.add("bin_" + (i+1), DataType.NUMERIC);

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
    ArrayHistogram	histo;
    StatisticContainer	cont;
    int			i;

    result    = new List[1];
    result[0] = new ArrayList<Object>();
    values    = map.toDoubleArray(m_SkipMissing);
    histo     = new ArrayHistogram();
    histo.setBinCalculation(BinCalculation.MANUAL);
    histo.setNumBins(m_NumBins);
    histo.setUseFixedMinMax(m_UseFixedMinMax);
    histo.setManualMin(m_ManualMin);
    histo.setManualMax(m_ManualMax);
    histo.add(values);
    cont = histo.calculate();
    for (i = 0; i < cont.getColumnCount(); i++)
      result[0].add(((Number) cont.getCell(0, i)).doubleValue());

    return result;
  }
}
