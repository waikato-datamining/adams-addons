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
 * AbstractMekaThresholdVisualizePanelPlot.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.flow.container.MekaResultContainer;
import meka.core.Result;
import weka.core.Instances;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

/**
 * Ancestor for plots using {@link ThresholdVisualizePanel} plots.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMekaThresholdVisualizePanelPlot
  extends AbstractGraphicalDisplay {

  private static final long serialVersionUID = -8227153847798098749L;

  /**
   * Returns the name of the default X column to display.
   *
   * @return              the name of the column
   */
  protected abstract String getDefaultXColumn();

  /**
   * Returns the name of the default Y column to display.
   *
   * @return              the name of the column
   */
  protected abstract String getDefaultYColumn();

  /**
   * Sets the combobox indices.
   *
   * @param data          the threshold curve data
   * @param panel         the panel
   * @throws Exception    if setting of indices fails
   */
  protected void setComboBoxIndices(Instances data, ThresholdVisualizePanel panel) throws Exception {
    if (data.attribute(getDefaultXColumn()) != null)
      panel.setXIndex(data.attribute(getDefaultXColumn()).index());
    if (data.attribute(getDefaultYColumn()) != null)
      panel.setYIndex(data.attribute(getDefaultYColumn()).index());
  }

  /**
   * Creates a panel displaying the ROC data.
   *
   * @param data          the threshold curve data
   * @param title         the title of the plot
   * @return              the panel
   * @throws Exception    if plot generation fails
   */
  protected ThresholdVisualizePanel createPanel(Instances data, String title) throws Exception {
    ThresholdVisualizePanel 	result;
    PlotData2D 			plot;
    boolean[] 			connectPoints;
    int 			cp;

    result = new ThresholdVisualizePanel();
    plot   = new PlotData2D(data);
    plot.setPlotName(title);
    plot.m_displayAllPoints = true;
    connectPoints = new boolean [data.numInstances()];
    for (cp = 1; cp < connectPoints.length; cp++)
      connectPoints[cp] = true;
    plot.setConnectPoints(connectPoints);
    result.addPlot(plot);
    setComboBoxIndices(data, result);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->meka.core.Result.class, adams.flow.container.MekaResultContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Result.class, MekaResultContainer.class};
  }
}
