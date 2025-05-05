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
 * TabNetGenerator.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.djl.networkgenerator;

import adams.core.QuickInfoHelper;
import ai.djl.basicdataset.tabular.TabularDataset;
import ai.djl.basicmodelzoo.tabular.TabNet;
import ai.djl.nn.Block;
import ai.djl.zero.Performance;

/**
 * For generating a TabNet structure.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class TabularRegressionGenerator
  extends AbstractNetworkGenerator {

  private static final long serialVersionUID = -5291136428713862646L;

  /** the performance to use. */
  protected Performance m_Performance;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a TabNet network:\n"
	     + "https://github.com/deepjavalibrary/djl/blob/master/model-zoo/src/main/java/ai/djl/basicmodelzoo/tabular/TabNet.java\n"
	     + "Based on:\n"
	     + "https://github.com/deepjavalibrary/djl/blob/master/djl-zero/src/main/java/ai/djl/zero/tabular/TabularRegression.java";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "performance", "performance",
      Performance.FAST);
  }

  /**
   * Sets the performance.
   *
   * @param value	the performance
   */
  public void setPerformance(Performance value) {
    m_Performance = value;
    reset();
  }

  /**
   * Returns the performance.
   *
   * @return		the performance
   */
  public Performance getPerformance() {
    return m_Performance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String performanceTipText() {
    return "The network performance to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "performance", m_Performance, "performance: ");
  }

  /**
   * Generates the network using the supplied dataset.
   *
   * @param dataset the dataset to generate the network for
   * @return the network
   */
  @Override
  protected Block doGenerate(TabularDataset dataset) {
    int		featureSize;
    int		labelSize;

    featureSize = dataset.getFeatureSize();
    labelSize   = dataset.getLabelSize();

    switch (m_Performance) {
      case FAST:
	// for fast cases, we set the number of independent layers and shared layers lower
	return TabNet.builder()
		 .setInputDim(featureSize)
		 .setOutDim(labelSize)
		 .optNumIndependent(1)
		 .optNumShared(1)
		 .build();
      case BALANCED:
	return TabNet.builder()
		 .setInputDim(featureSize)
		 .setOutDim(labelSize)
		 .build();
      case ACCURATE:
	// for accurate cases, we set the number of independent layers and shared layers higher
	return TabNet.builder()
		 .setInputDim(featureSize)
		 .setOutDim(labelSize)
		 .optNumIndependent(4)
		 .optNumShared(4)
		 .build();
      default:
	throw new IllegalStateException("Unsupported performance: " + m_Performance);
    }
  }
}
