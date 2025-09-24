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
 * HeatmapImageHandler.java
 * Copyright (C) 2011-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.io.PlaceholderFile;
import adams.data.heatmap.Heatmap;
import adams.data.io.input.DataContainerReader;
import adams.gui.chooser.HeatmapFileChooser;
import adams.gui.visualization.heatmap.HeatmapPanel;

import javax.swing.JLabel;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays a heatmap as image.
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
 */
public class HeatmapImageHandler
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
    return "Displays a heatmap as image.";
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
    HeatmapPanel			result;
    DataContainerReader<Heatmap> 	reader;
    List<Heatmap>			maps;

    result = new HeatmapPanel(null);
    result.setSearchPanelVisible(false);

    reader = new HeatmapFileChooser().getReaderForFile(file);
    if (reader == null)
      return new PreviewPanel(new JLabel("Cannot display heatmap file: " + file));

    reader.setInput(new PlaceholderFile(file));
    maps = reader.read();

    if (!maps.isEmpty())
      result.setHeatmap(maps.get(0));

    return new PreviewPanel(result);
  }
}
