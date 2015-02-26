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
 * AbstractApplyImageTransformer.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.heatmap.plugins;

import adams.gui.visualization.heatmap.HeatmapPanel;

import java.awt.image.BufferedImage;

/**
 * Ancestor for plugins that work on the image rather than the heatmap.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractApplyImageTransformer
  extends AbstractSelectedHeatmapsViewerPluginWithGOE {

  private static final long serialVersionUID = -8466066949642677596L;

  /**
   * Processes the image.
   *
   * @param image	the image to process
   * @return		the processed image, null if failed to process
   */
  protected abstract BufferedImage process(BufferedImage image);

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
