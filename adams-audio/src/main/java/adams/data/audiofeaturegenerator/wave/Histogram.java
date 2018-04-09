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
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.audiofeaturegenerator.wave;

import adams.data.audio.WaveContainer;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.report.DataType;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayHistogram;
import adams.data.statistics.ArrayHistogram.BinCalculation;
import adams.data.statistics.StatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a histogram from the audio data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Histogram
  extends AbstractWaveFeatureGenerator {

  private static final long serialVersionUID = 1096079057750734103L;

  /** how to calculate the number of bins. */
  protected BinCalculation m_BinCalculation;

  /** the number of bins in case of manual bin calculation. */
  protected int m_NumBins;

  /** the bin width - used for some calculations. */
  protected double m_BinWidth;

  /** whether to normalize the data. */
  protected boolean m_Normalize;

  /** whether to use fixed min/max for manual bin calculation. */
  protected boolean m_UseFixedMinMax;

  /** the manual minimum. */
  protected double m_ManualMin;

  /** the manual maximum. */
  protected double m_ManualMax;

  /** whether to use the ranges as bin description. */
  protected boolean m_DisplayRanges;

  /** the number of decimals to show. */
  protected int m_NumDecimals;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a histogram from the audio data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "bin-calc", "binCalculation",
      BinCalculation.MANUAL);

    m_OptionManager.add(
      "num-bins", "numBins",
      50, 1, null);

    m_OptionManager.add(
      "bin-width", "binWidth",
      1.0, 0.00001, null);

    m_OptionManager.add(
      "normalize", "normalize",
      false);

    m_OptionManager.add(
      "use-fixed-min-max", "useFixedMinMax",
      false);

    m_OptionManager.add(
      "manual-min", "manualMin",
      0.0);

    m_OptionManager.add(
      "manual-max", "manualMax",
      1.0);

    m_OptionManager.add(
      "display-ranges", "displayRanges",
      false);

    m_OptionManager.add(
      "num-decimals", "numDecimals",
      3, 0, null);
  }

  /**
   * Sets how the number of bins is calculated.
   *
   * @param value 	the bin calculation
   */
  public void setBinCalculation(BinCalculation value) {
    m_BinCalculation = value;
    reset();
  }

  /**
   * Returns how the number of bins is calculated.
   *
   * @return 		the bin calculation
   */
  public BinCalculation getBinCalculation() {
    return m_BinCalculation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binCalculationTipText() {
    return "Defines how the number of bins are calculated.";
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
   * Sets the bin width to use (for some calculations).
   *
   * @param value 	the bin width
   */
  public void setBinWidth(double value) {
    m_BinWidth = value;
    reset();
  }

  /**
   * Returns the bin width in use (for some calculations).
   *
   * @return 		the bin width
   */
  public double getBinWidth() {
    return m_BinWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String binWidthTipText() {
    return "The bin width to use for some of the calculations.";
  }

  /**
   * Sets whether to normalize the data before generating the histogram.
   *
   * @param value 	if true the data gets normalized first
   */
  public void setNormalize(boolean value) {
    m_Normalize = value;
    reset();
  }

  /**
   * Returns whether to normalize the data before generating the histogram.
   *
   * @return 		true if the data gets normalized first
   */
  public boolean getNormalize() {
    return m_Normalize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String normalizeTipText() {
    return "If set to true the data gets normalized first before the histogram is calculated.";
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
   * Sets whether to use the bin ranges as their description rather than a
   * simple index.
   *
   * @param value 	true if to display the ranges
   */
  public void setDisplayRanges(boolean value) {
    m_DisplayRanges = value;
    reset();
  }

  /**
   * Returns whether to use the bin ranges as their description rather than a
   * simple index.
   *
   * @return 		true if to display the ranges
   */
  public boolean getDisplayRanges() {
    return m_DisplayRanges;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String displayRangesTipText() {
    return "If enabled, the bins get description according to their range, rather than a simple index.";
  }

  /**
   * Sets the number of decimals to show in the bin description.
   *
   * @param value 	the number of decimals
   */
  public void setNumDecimals(int value) {
    m_NumDecimals = value;
    reset();
  }

  /**
   * Returns the number of decimals to show in the bin description.
   *
   * @return 		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals to show in the bin descriptions.";
  }

  /**
   * Configures the histogram algorithm.
   *
   * @return		the algorithm
   */
  protected ArrayHistogram configure() {
    ArrayHistogram 	result;

    result = new ArrayHistogram();
    result.setBinCalculation(m_BinCalculation);
    result.setNumBins(m_NumBins);
    result.setBinWidth(m_BinWidth);
    result.setNormalize(m_Normalize);
    result.setUseFixedMinMax(m_UseFixedMinMax);
    result.setManualMin(m_ManualMin);
    result.setManualMax(m_ManualMax);
    result.setDisplayRanges(m_DisplayRanges);
    result.setNumDecimals(m_NumDecimals);

    return result;
  }

  /**
   * Creates the header from a template container.
   *
   * @param cont	the container to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(WaveContainer cont) {
    HeaderDefinition	result;
    int			i;
    ArrayHistogram	histogram;
    StatisticContainer	stat;

    histogram = configure();
    histogram.add(StatUtils.toNumberArray(cont.getRawData()));
    stat = histogram.calculate();

    result = new HeaderDefinition();
    for (i = 0; i < stat.getColumnCount(); i++)
      result.add(stat.getHeader(i), DataType.NUMERIC);

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param cont	the container to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(WaveContainer cont) {
    List<Object>[]	result;
    int			i;
    int			n;
    ArrayHistogram	histogram;
    StatisticContainer	stat;

    histogram = configure();
    histogram.add(StatUtils.toNumberArray(cont.getRawData()));
    stat = histogram.calculate();

    result = new List[stat.getRowCount()];
    for (n = 0; n < stat.getRowCount(); n++) {
      result[n] = new ArrayList<>();
      for (i = 0; i < stat.getColumnCount(); i++)
	result[n].add(stat.getCell(n, i));
    }

    return result;
  }
}
