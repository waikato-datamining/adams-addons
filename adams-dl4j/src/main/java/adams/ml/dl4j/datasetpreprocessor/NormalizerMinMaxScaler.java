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

/**
 * NormalizerMinMaxScaler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.datasetpreprocessor;

import org.nd4j.linalg.dataset.api.DataSetPreProcessor;

/**
 <!-- globalinfo-start -->
 * Configures an org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-min-range &lt;double&gt; (property: minRange)
 * &nbsp;&nbsp;&nbsp;The minimum range.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 * <pre>-max-range &lt;double&gt; (property: maxRange)
 * &nbsp;&nbsp;&nbsp;The maximum range.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NormalizerMinMaxScaler
  extends AbstractDataSetPreProcessorConfigurator {

  private static final long serialVersionUID = 6871564201222898901L;

  /** the min range. */
  protected double m_MinRange;

  /** the max range. */
  protected double m_MaxRange;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures an " + org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler.class.getName() + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min-range", "minRange",
      0.0);

    m_OptionManager.add(
      "max-range", "maxRange",
      1.0);
  }

  /**
   * Sets the minimum range.
   *
   * @param value	the minimum
   */
  public void setMinRange(double value) {
    if (getOptionManager().isValid("minRange", value)) {
      m_MinRange = value;
      reset();
    }
  }

  /**
   * Returns the minimum range.
   *
   * @return 		the minimum
   */
  public double getMinRange() {
    return m_MinRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String minRangeTipText() {
    return "The minimum range.";
  }

  /**
   * Sets the maximum range.
   *
   * @param value	the maximum
   */
  public void setMaxRange(double value) {
    if (getOptionManager().isValid("maxRange", value)) {
      m_MaxRange = value;
      reset();
    }
  }

  /**
   * Returns the maximum range.
   *
   * @return 		the maximum
   */
  public double getMaxRange() {
    return m_MaxRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String maxRangeTipText() {
    return "The maximum range.";
  }

  /**
   * Configures the actual {@link DataSetPreProcessor} and returns it.
   *
   * @return		the preprocessor
   */
  @Override
  protected DataSetPreProcessor doConfigurePreProcessor() {
    return new org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler(m_MinRange, m_MaxRange);
  }
}
