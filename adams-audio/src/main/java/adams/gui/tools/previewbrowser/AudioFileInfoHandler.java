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
 * AudioFileInfoHandler.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.flow.transformer.audioinfo.AbstractAudioInfoReader;
import adams.flow.transformer.audioinfo.MP3;
import adams.flow.transformer.audioinfo.Wave;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.KeyValuePairTableModel;
import adams.gui.core.SortableAndSearchableTable;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Displays information for audio files.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class AudioFileInfoHandler
  extends AbstractContentHandler {

  private static final long serialVersionUID = 1625120959676797085L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays information for audio files: " + Utils.flatten(getExtensions(), ", ");
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"mp3", "wav"};
  }

  /**
   * Creates the actual preview.
   *
   * @param file the file to create the view for
   * @return the preview
   */
  @Override
  public PreviewPanel createPreview(File file) {
    BasePanel 			result;
    KeyValuePairTableModel 	model;
    SortableAndSearchableTable 	table;
    Object[][]			data;
    AbstractAudioInfoReader	reader;
    Map<String,Object>		info;
    List<String> 		keys;
    int				i;

    if (file.getName().toLowerCase().endsWith(".mp3"))
      reader = new MP3();
    else if (file.getName().toLowerCase().endsWith(".wav"))
      reader = new Wave();
    else
      return new NoPreviewAvailablePanel("Unhandled file format:\n" + FileUtils.getExtension(file));

    try {
      info = reader.read(file.getAbsolutePath());
      keys = new ArrayList<>(info.keySet());
      Collections.sort(keys);
      data = new Object[info.size()][2];
      for (i = 0; i < keys.size(); i++) {
        data[i][0] = keys.get(i);
        data[i][1] = info.get(keys.get(i));
      }
      model  = new KeyValuePairTableModel(data);
      table  = new SortableAndSearchableTable(model);
      table.setShowSimplePopupMenus(true);
      table.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
      table.setOptimalColumnWidth();
      result = new BasePanel(new BorderLayout());
      result.add(new BaseScrollPane(table));

      return new PreviewPanel(result, table);
    }
    catch (Exception e) {
      return new NoPreviewAvailablePanel("Failed to generate info:\n" + LoggingHelper.throwableToString(e));
    }
  }
}
