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
 * AudioFilePlaybackHandler.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.gui.audio.AudioPlaybackPanel;
import adams.gui.core.BasePanel;

import java.awt.BorderLayout;
import java.io.File;

/**
 * Allows the playback of audio files.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class AudioFilePlaybackHandler
    extends AbstractContentHandler {

  private static final long serialVersionUID = -5395091340759344923L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the playback of audio files: " + Utils.flatten(getExtensions(), ", ");
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
    BasePanel			result;
    AudioPlaybackPanel		panel;

    panel = new AudioPlaybackPanel();
    panel.open(file);
    result = new BasePanel(new BorderLayout());
    result.add(panel, BorderLayout.NORTH);
    return new PreviewPanel(result, panel);
  }
}
