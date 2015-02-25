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
 * Manual.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.filter.heatmapthreshold;

import adams.data.heatmap.Heatmap;

/**
 <!-- globalinfo-start -->
 * Performs no calculation, simply returns the user-specified threshold.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-threshold &lt;double&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold to use.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Manual
  extends AbstractHeatmapThreshold {

  private static final long serialVersionUID = -1205777458822555932L;

  /** the manual threshold. */
  protected double m_Threshold;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs no calculation, simply returns the user-specified threshold.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"threshold", "threshold",
	0.0);
  }

  /**
   * Sets the threshold to use.
   *
   * @param value 	the threshold
   */
  public void setThreshold(double value) {
    m_Threshold = value;
    reset();
  }

  /**
   * Returns the threshold to use.
   *
   * @return 		the threshold
   */
  public double getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdTipText() {
    return "The threshold to use.";
  }

  /**
   * Performs the actual calculation of the threshold.
   *
   * @param map		the map to base the calculation on
   * @return		the threshold
   */
  @Override
  protected double doCalcThreshold(Heatmap map) {
    return m_Threshold;
  }
}
