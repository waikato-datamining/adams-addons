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
 * AbstractMetaHeatmapFeatureGenerator.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.heatmapfeatures;

import adams.core.QuickInfoHelper;

/**
 * Ancestor for feature generators that use a base feature generator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMetaHeatmapFeatureGenerator
  extends AbstractHeatmapFeatureGenerator {

  private static final long serialVersionUID = -3651843266591271714L;

  /** the base feature generator. */
  protected AbstractHeatmapFeatureGenerator m_Generator;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generator", "generator",
      getDefaultGenerator());
  }

  /**
   * Returns the default feature generator to use.
   *
   * @return		the default generator
   */
  protected abstract AbstractHeatmapFeatureGenerator getDefaultGenerator();

  /**
   * Sets the base feature generator to use.
   *
   * @param value 	the generator
   */
  public void setGenerator(AbstractHeatmapFeatureGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the base feature generator to use.
   *
   * @return 		the generator
   */
  public AbstractHeatmapFeatureGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String generatorTipText();

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator, "generator: ");
  }
}
