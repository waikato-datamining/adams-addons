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
 * SelectObjectToTrack.java
 * Copyright (C) 2015-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.selection;

import adams.core.base.QuadrilateralLocation;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.gui.visualization.image.ImagePanel;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Allows the user to select a region (ie object) to track.<br>
 * When also holding the CTRL key down, this will delete any selected region.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-location &lt;adams.data.report.Field&gt; (property: location)
 * &nbsp;&nbsp;&nbsp;The field to store the location of the object in.
 * &nbsp;&nbsp;&nbsp;default: Tracker.Init[S]
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SelectObjectToTrack
  extends AbstractSelectionProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -657789971297807743L;

  /** the report field to store the tracked location in. */
  protected Field m_Location;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Allows the user to select a region (ie object) to track.\n"
      + "When also holding the CTRL key down, this will delete any selected region.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "location", "location",
      new Field("Tracker.Init", DataType.STRING));
  }

  /**
   * Sets the field to store the location of the object in.
   *
   * @param value	the field
   */
  public void setLocation(Field value) {
    m_Location = value;
    reset();
  }

  /**
   * Returns the field to store the location of the object in.
   *
   * @return		the field
   */
  public Field getLocation() {
    return m_Location;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String locationTipText() {
    return "The field to store the location of the object in.";
  }

  /**
   * Process the selection that occurred in the image panel.
   * 
   * @param panel	the origin
   * @param topLeft	the top-left position of the selection
   * @param bottomRight	the bottom-right position of the selection
   * @param trace	the trace from the selection
   * @param modifiersEx	the associated modifiers
   */
  @Override
  protected void doProcessSelection(ImagePanel panel, Point topLeft, Point bottomRight, List<Point> trace, int modifiersEx) {
    Report 			report;
    int				x1;
    int				y1;
    int				x2;
    int				y2;
    QuadrilateralLocation	loc;

    report = panel.getAdditionalProperties().getClone();
    if ((modifiersEx & MouseEvent.CTRL_DOWN_MASK) != 0) {
      report.removeValue(m_Location);
    }
    else {
      x1  = panel.mouseToPixelLocation(topLeft).x;
      y1  = panel.mouseToPixelLocation(topLeft).y;
      x2  = panel.mouseToPixelLocation(bottomRight).x;
      y2  = panel.mouseToPixelLocation(bottomRight).y;
      loc = new QuadrilateralLocation(
	x1, y1,
	x2, y1,
	x2, y2,
	x1, y2);
      report.addField(m_Location);
      report.setValue(m_Location, loc.toString());
    }

    panel.setAdditionalProperties(report);
  }
}
