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
 * ImageMagick.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.heatmap.plugins;

import adams.core.base.BaseText;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.Token;
import adams.flow.transformer.ImageMagickTransformer;
import adams.gui.core.TextEditorPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.heatmap.HeatmapPanel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

/**
 * Ancestor for plugins that work on the image rather than the heatmap.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageMagick
  extends AbstractSelectedHeatmapsViewerPlugin {

  private static final long serialVersionUID = -8466066949642677596L;

  /** the editor with the commands. */
  protected TextEditorPanel m_Editor;

  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "ImageMagick...";
  }

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "imagemagick.png";
  }

  /**
   * Creates the log message.
   *
   * @return		the message, null if none available
   */
  @Override
  protected String createLogEntry() {
    return getClass().getSimpleName() + ": " + m_Editor.getContent();
  }

  /**
   * Processes the image.
   *
   * @param image	the image to process
   * @return		the processed image, null if failed to process
   */
  protected BufferedImage process(BufferedImage image) {
    BufferedImage		result;
    BufferedImageContainer 	input;
    ImageMagickTransformer 	transformer;
    String error;

    result = null;

    setLastSetup(m_Editor.getContent());
    transformer = new ImageMagickTransformer();
    transformer.setCommands(new BaseText(m_Editor.getContent()));
    error = transformer.setUp();
    if (error == null) {
      input = new BufferedImageContainer();
      input.setImage(image);
      transformer.input(new Token(input));
      error = transformer.execute();
      if ((error == null) && (transformer.hasPendingOutput()))
	result = ((BufferedImageContainer) transformer.output().getPayload()).getImage();
      transformer.wrapUp();
      transformer.cleanUp();
    }

    if (error != null)
      getLogger().severe(error);

    return result;
  }

  /**
   * Creates the panel with the configuration (return null to suppress display).
   *
   * @param dialog	the dialog that is being created
   * @return		the generated panel, null to suppress
   */
  @Override
  protected JPanel createConfigurationPanel(ApprovalDialog dialog) {
    JPanel		result;

    result = new JPanel(new BorderLayout());
    m_Editor = new TextEditorPanel();
    if (hasLastSetup())
      m_Editor.setContent((String) getLastSetup());
    else
      m_Editor.setContent("");
    result.add(new JLabel("Please enter the commands"), BorderLayout.NORTH);
    result.add(m_Editor, BorderLayout.CENTER);

    return result;
  }

  /**
   * Processes the specified panel.
   *
   * @param panel	the panel to process
   * @return		null if successful, error message otherwise
   */
  protected String process(HeatmapPanel panel) {
    BufferedImage	current;

    current = panel.getImagePanel().getCurrentImage();
    current = process(current);
    if (current == null)
      return "Failed to process image!";

    panel.getImagePanel().setCurrentImage(current);
    return null;
  }
}
