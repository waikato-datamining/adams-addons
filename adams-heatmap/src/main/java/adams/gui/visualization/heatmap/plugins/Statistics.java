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
 * Statistics.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap.plugins;

import adams.data.heatmap.HeatmapStatistic;
import adams.data.statistics.InformativeStatistic;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.heatmap.HeatmapPanel;
import adams.gui.visualization.statistics.InformativeStatisticFactory;

import javax.swing.JPanel;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays the statistics about heatmaps.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Statistics
  extends AbstractSelectedHeatmapsViewerPlugin {
  
  /** for serialization. */
  private static final long serialVersionUID = 3286345601880725626L;

  /** the collected statistics. */
  protected List<InformativeStatistic> m_StatsList;

  /**
   * Returns the text for the menu to place the plugin beneath.
   *
   * @return		the menu
   */
  @Override
  public String getMenu() {
    return "View";
  }

  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "Statistics";
  }

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "properties.gif";
  }

  /**
   * Checks whether the plugin can be executed given the specified image panel.
   *
   * @param panel	the panel to use as basis for decision
   * @return		true if plugin can be executed
   */
  @Override
  public boolean canExecute(HeatmapPanel panel) {
    return (panel != null) && (panel.getHeatmap() != null);
  }

  @Override
  protected JPanel createConfigurationPanel(ApprovalDialog dialog) {
    return null;
  }

  /**
   * Initializes the processing.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String processInit() {
    String	result;

    result = super.processInit();

    if (result == null)
      m_StatsList = new ArrayList<>();

    return result;
  }

  /**
   * Processes the specified panel.
   *
   * @param panel	the panel to process
   * @return		null if successful, error message otherwise
   */
  @Override
  protected String process(HeatmapPanel panel) {
    HeatmapStatistic stats;

    stats = new HeatmapStatistic(panel.getHeatmap());
    m_StatsList.add(stats);

    return null;
  }

  /**
   * Finishes up the processing.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String processFinish() {
    String				result;
    InformativeStatisticFactory.Dialog	dialog;

    result = super.processFinish();

    if (result == null) {
      if (m_CurrentPanel.getParentDialog() != null)
	dialog = InformativeStatisticFactory.getDialog(m_CurrentPanel.getParentDialog(), ModalityType.MODELESS);
      else
	dialog = InformativeStatisticFactory.getDialog(m_CurrentPanel.getParentFrame(), false);
      dialog.setStatistics(m_StatsList);
      dialog.setTitle("Heatmap statistics");
      dialog.pack();
      dialog.setLocationRelativeTo(m_CurrentPanel);
      dialog.setVisible(true);
    }

    return result;
  }

  /**
   * Creates the log message.
   *
   * @return		the message, null if none available
   */
  @Override
  protected String createLogEntry() {
    return null;
  }
}
