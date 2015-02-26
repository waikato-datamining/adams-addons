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
 * SpreadSheetView.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap.plugins;

import adams.core.Properties;
import adams.data.conversion.HeatmapToSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.visualization.heatmap.HeatmapPanel;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;

/**
 * Displays the heatmap as spreadsheet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetView
  extends AbstractHeatmapViewerPlugin {
  
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
    return "Spreadsheet view";
  }

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "spreadsheet.png";
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
   * Processes the heatmap.
   */
  @Override
  protected String doExecute() {
    BaseDialog 			dialog;
    SpreadSheetTable 		table;
    HeatmapToSpreadSheet 	convert;
    String			result;
    Properties 			props;

    table   = new SpreadSheetTable(new SpreadSheetTableModel());
    convert = new HeatmapToSpreadSheet();
    convert.setInput(m_CurrentPanel.getHeatmap());
    result  = convert.convert();
    if (result != null)
      return "Failed to generate spreadsheet: " + result;
    props = m_CurrentPanel.getProperties();
    table.setModel(new SpreadSheetTableModel((SpreadSheet) convert.getOutput()));
    table.setNumDecimals(props.getInteger("SpreadSheet.NumDecimals", 3));

    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new BaseDialog(m_CurrentPanel.getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new BaseDialog(m_CurrentPanel.getParentFrame(), false);
    dialog.setTitle("Heatmap #" + m_CurrentPanel.getHeatmap().getID());
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(new BaseScrollPane(table), BorderLayout.CENTER);
    dialog.setSize(
	props.getInteger("View.SpreadSheet.Width", 800),
	props.getInteger("View.SpreadSheet.Height", 600));
    dialog.setLocationRelativeTo(m_CurrentPanel);
    dialog.setVisible(true);

    return null;
  }
}
