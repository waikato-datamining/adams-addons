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

import adams.core.option.OptionUtils;
import adams.data.heatmap.Heatmap;
import adams.data.statistics.ArrayHistogram;
import adams.gui.core.BaseMultiPagePane;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.heatmap.HeatmapPanel;
import adams.gui.visualization.heatmap.HistogramPanel;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays the histogram for heatmap(s).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Histogram
  extends AbstractSelectedHeatmapsViewerPluginWithGOE {
  
  /** for serialization. */
  private static final long serialVersionUID = 3286345601880725626L;

  /** the list of histogram panels. */
  protected List<HistogramPanel> m_HistogramList;

  /** the titles for the panels. */
  protected List<String> m_TitleList;

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
   * Initializes the processing.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String processInit() {
    String	result;

    result = super.processInit();

    if (result == null) {
      m_HistogramList = new ArrayList<>();
      m_TitleList     = new ArrayList<>();
    }

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
    Heatmap		map;
    HistogramPanel	histo;

    map   = panel.getHeatmap();
    histo = new HistogramPanel();
    histo.setArrayHistogram((ArrayHistogram) getLastSetup());
    histo.setData(map);
    m_HistogramList.add(histo);
    m_TitleList.add(panel.getTitle());

    return null;
  }

  /**
   * Finishes up the processing.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String processFinish() {
    String		result;
    BaseMultiPagePane	multipane;
    ApprovalDialog	dialog;
    int			i;

    result = super.processFinish();

    if (result == null) {
      multipane = new BaseMultiPagePane();
      for (i = 0; i < m_HistogramList.size(); i++)
	multipane.addPage(m_TitleList.get(i), m_HistogramList.get(i));

      if (m_CurrentPanel.getParentDialog() != null)
	dialog = new ApprovalDialog(m_CurrentPanel.getParentDialog());
      else
	dialog = new ApprovalDialog(m_CurrentPanel.getParentFrame());
      dialog.setTitle("Histogram");
      dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
      dialog.setApproveVisible(true);
      dialog.setCancelVisible(false);
      dialog.setDiscardVisible(false);
      dialog.getContentPane().add(multipane, BorderLayout.CENTER);
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
    return "Histogram: " + OptionUtils.getCommandLine(getLastSetup());
  }
}
