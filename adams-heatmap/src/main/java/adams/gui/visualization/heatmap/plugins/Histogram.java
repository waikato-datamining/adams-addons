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
 * Histogram.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap.plugins;

import adams.data.heatmap.Heatmap;
import adams.data.statistics.ArrayHistogram;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.heatmap.HeatmapPanel;
import adams.gui.visualization.heatmap.HistogramPanel;

import java.awt.BorderLayout;

/**
 * Displays the histogram(s) for an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Histogram
  extends AbstractHeatmapViewerPluginWithGOE {
  
  /** for serialization. */
  private static final long serialVersionUID = 3286345601880725626L;

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
    return "Histogram...";
  }

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "histogram.png";
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

  /**
   * Creates the log message.
   * 
   * @return		the message, null if none available
   */
  @Override
  protected String createLogEntry() {
    return null;
  }

  /**
   * Returns whether the class can be changed in the GOE.
   *
   * @return		true if class can be changed by the user
   */
  protected boolean getCanChangeClassInDialog() {
    return false;
  }

  /**
   * Returns the class to use as type (= superclass) in the GOE.
   *
   * @return		the class
   */
  @Override
  protected Class getEditorType() {
    return ArrayHistogram.class;
  }

  /**
   * Returns the default object to use in the GOE if no last setup is yet
   * available.
   *
   * @return		the object
   */
  @Override
  protected Object getDefaultValue() {
    return new ArrayHistogram();
  }

  /**
   * Processes the heatmap.
   */
  @Override
  protected String process() {
    Heatmap 		map;
    HistogramPanel	panel;
    ApprovalDialog	dialog;

    map = m_CurrentPanel.getHeatmap();
    panel = new HistogramPanel();
    panel.setArrayHistogram((ArrayHistogram) getLastSetup());
    panel.setData(map);

    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new ApprovalDialog(m_CurrentPanel.getParentDialog());
    else
      dialog = new ApprovalDialog(m_CurrentPanel.getParentFrame());
    dialog.setTitle("Histogram - " + m_CurrentPanel.getTitle());
    dialog.setApproveVisible(true);
    dialog.setCancelVisible(false);
    dialog.setDiscardVisible(false);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(m_CurrentPanel);
    dialog.setVisible(true);

    return null;
  }
}
