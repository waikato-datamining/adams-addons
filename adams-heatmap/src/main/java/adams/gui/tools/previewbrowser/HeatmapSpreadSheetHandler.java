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
 * HeatmapSpreadSheetHandler.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.io.PlaceholderFile;
import adams.data.conversion.HeatmapToSpreadSheet;
import adams.data.heatmap.Heatmap;
import adams.data.io.input.AbstractDataContainerReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.HeatmapFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays a heatmap as a spreadsheet.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapSpreadSheetHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -2780002972029225999L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays a heatmap as a spreadsheet.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"*"};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel createPreview(File file) {
    BasePanel					result;
    AbstractDataContainerReader<Heatmap> 	reader;
    List<Heatmap>				maps;
    HeatmapToSpreadSheet			hm2ss;
    SpreadSheetTable				table;
    String					msg;
    SpreadSheetTableModel			model;

    result = new BasePanel(new BorderLayout());

    reader = new HeatmapFileChooser().getReaderForFile(file);
    if (reader == null)
      return new PreviewPanel(new JLabel("Cannot display heatmap file: " + file));

    reader.setInput(new PlaceholderFile(file));
    maps = reader.read();

    if (maps.size() > 0) {
      hm2ss = new HeatmapToSpreadSheet();
      hm2ss.setInput(maps.get(0));
      msg = hm2ss.convert();
      if (msg == null)
	model = new SpreadSheetTableModel((SpreadSheet) hm2ss.getOutput());
      else
	model = new SpreadSheetTableModel();
      table = new SpreadSheetTable(model);
      table.setNumDecimals(3);
      result.add(new BaseScrollPane(table), BorderLayout.CENTER);
    }

    return new PreviewPanel(result);
  }
}
