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
 * AbstractHeatmapFeatureGeneratorWithSkippableMissingValues.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.heatmapfeatures;

/**
 * Ancestor for feature generators that can decide whether to skip missing values.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractHeatmapFeatureGeneratorWithSkippableMissingValues
  extends AbstractHeatmapFeatureGenerator {

  /** whether to skip missing values. */
  protected boolean m_SkipMissing;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "skip-missing", "skipMissing",
      false);
  }

  /**
   * Sets whether to exclude missing values from the histogram calculation.
   *
   * @param value 	true if to skip
   */
  public void setSkipMissing(boolean value) {
    m_SkipMissing = value;
    reset();
  }

  /**
   * Returns whether to exclude missing values from the histogram calculation.
   *
   * @return 		true if to skip
   */
  public boolean getSkipMissing() {
    return m_SkipMissing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipMissingTipText() {
    return "If enabled, missing values get skipped when collecting the values for the histogram.";
  }
}
