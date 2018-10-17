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
 * SpreadSheetView.java
 * Copyright (C) 2015-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap.plugins;

import adams.core.Properties;
import adams.data.conversion.HeatmapToSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.MultiPagePane;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.heatmap.HeatmapPanel;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays the heatmap as spreadsheet.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetView
  extends AbstractSelectedHeatmapsViewerPlugin {
  
  /** for serialization. */
  private static final long serialVersionUID = 3286345601880725626L;

  /** the list of spreadsheet tables. */
  protected List<SpreadSheetTable> m_TableList;

  /** the titles for the panels. */
  protected List<String> m_TitleList;

  /** the properties to use for display parameters. */
  protected Properties m_Properties;

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
   * Creates the panel with the configuration (return null to suppress display).
   *
   * @param dialog	the dialog that is being created
   * @return		the generated panel, null to suppress
   */
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

    if (result == null) {
      m_TableList  = new ArrayList<>();
      m_TitleList  = new ArrayList<>();
      m_Properties = null;
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
    String			result;
    SpreadSheetTable 		table;
    HeatmapToSpreadSheet 	convert;

    table   = new SpreadSheetTable(new SpreadSheetTableModel());
    convert = new HeatmapToSpreadSheet();
    convert.setInput(panel.getHeatmap());
    result  = convert.convert();
    if (result != null)
      return "Failed to generate spreadsheet: " + result;
    if (m_Properties == null)
      m_Properties = panel.getProperties();
    table.setModel(new SpreadSheetTableModel((SpreadSheet) convert.getOutput()));
    table.setNumDecimals(m_Properties.getInteger("SpreadSheet.NumDecimals", 3));

    m_TableList.add(table);
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
    MultiPagePane 	multipane;
    ApprovalDialog	dialog;
    int			i;

    result = super.processFinish();

    if (result == null) {
      multipane = new MultiPagePane();
      multipane.setReadOnly(true);
      for (i = 0; i < m_TableList.size(); i++)
	multipane.addPage(m_TitleList.get(i), new BaseScrollPane(m_TableList.get(i)));
      if (multipane.getPageCount() > 0)
        multipane.setSelectedIndex(0);

      if (m_CurrentPanel.getParentDialog() != null)
	dialog = new ApprovalDialog(m_CurrentPanel.getParentDialog());
      else
	dialog = new ApprovalDialog(m_CurrentPanel.getParentFrame());
      dialog.setTitle("Spreadsheet");
      dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
      dialog.setApproveVisible(true);
      dialog.setCancelVisible(false);
      dialog.setDiscardVisible(false);
      dialog.getContentPane().add(multipane, BorderLayout.CENTER);
      dialog.setSize(
	m_Properties.getInteger("View.SpreadSheet.Width", 800),
	m_Properties.getInteger("View.SpreadSheet.Height", 600));
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
