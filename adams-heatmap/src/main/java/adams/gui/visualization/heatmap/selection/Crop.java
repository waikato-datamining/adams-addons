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
 * Crop.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap.selection;

import adams.data.filter.heatmapcrop.Submap;
import adams.data.heatmap.Heatmap;
import adams.data.report.Report;
import adams.gui.visualization.heatmap.HeatmapPanel;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Point;

/**
 <!-- globalinfo-start -->
 * Crops the heatmap to the current selection and stores crop information in the report (prefix: Crop.). Offers undo&#47;redo.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Crop
  extends AbstractSelectionProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -657789971297807743L;

  /** the prefix for the crop coordinates. */
  public final static String PREFIX = "Crop.";

  /** the x key. */
  public final static String KEY_X = "x";

  /** the y key. */
  public final static String KEY_Y = "y";

  /** the width key. */
  public final static String KEY_WIDTH = "width";

  /** the height key. */
  public final static String KEY_HEIGHT = "height";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Crops the heatmap to the current selection and stores crop information "
	+ "in the report (prefix: " + PREFIX + "). Offers undo/redo.";
  }

  /**
   * Process the selection that occurred in the heatmap panel.
   * 
   * @param panel	the origin
   * @param topLeft	the top-left position of the selection
   * @param bottomRight	the bottom-right position of the selection
   * @param modifiersEx	the associated modifiers
   */
  @Override
  protected void doProcessSelection(HeatmapPanel panel, Point topLeft, Point bottomRight, int modifiersEx) {
    Heatmap 		cropped;
    Submap		submap;
    PaintPanel 		ppanel;
    Report 		additional;
    int			x;
    int			y;
    int			width;
    int			height;

    ppanel  = panel.getImagePanel().getPaintPanel();
    x       = ppanel.mouseToPixelLocation(topLeft).x;
    y       = ppanel.mouseToPixelLocation(topLeft).y;
    width   = ppanel.mouseToPixelLocation(bottomRight).x - ppanel.mouseToPixelLocation(topLeft).x + 1;
    height  = ppanel.mouseToPixelLocation(bottomRight).y - ppanel.mouseToPixelLocation(topLeft).y + 1;

    submap = new Submap();
    submap.setRow(y);
    submap.setColumn(x);
    submap.setHeight(height);
    submap.setWidth(width);

    cropped = submap.crop(panel.getHeatmap());

    additional = cropped.getReport();
    additional.setNumericValue(PREFIX + KEY_X, x);
    additional.setNumericValue(PREFIX + KEY_Y, y);
    additional.setNumericValue(PREFIX + KEY_WIDTH, width);
    additional.setNumericValue(PREFIX + KEY_HEIGHT, height);

    if (panel.getOwner() != null)
      panel.getOwner().newTab(cropped, "Cropped");
    else
      panel.setHeatmap(cropped);
  }
}
