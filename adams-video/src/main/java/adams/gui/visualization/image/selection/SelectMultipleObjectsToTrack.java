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
 * SelectMultipleObjectsToTrack.java
 * Copyright (C) 2015-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.selection;

import adams.core.base.QuadrilateralLocation;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.gui.visualization.image.ImagePanel;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Allows the user to select multiple regions (ie objects) to track.<br>
 * Uses 1-based index as suffix for enumerating locations.<br>
 * When also holding the CTRL key down, this will delete any encompassed region.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-location-prefix &lt;adams.data.report.Field&gt; (property: locationPrefix)
 * &nbsp;&nbsp;&nbsp;The prefix for fields to store the location of the objects in (uses 1-based 
 * &nbsp;&nbsp;&nbsp;enumeration suffix).
 * &nbsp;&nbsp;&nbsp;default: Tracker.Init.[S]
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SelectMultipleObjectsToTrack
  extends AbstractSelectionProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -657789971297807743L;

  /** the report field prefix to store the tracked locations in. */
  protected Field m_LocationPrefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Allows the user to select multiple regions (ie objects) to track.\n"
        + "Uses 1-based index as suffix for enumerating locations.\n"
        + "When also holding the CTRL key down, this will delete any encompassed region.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "location-prefix", "locationPrefix",
      new Field("Tracker.Init.", DataType.STRING));
  }

  /**
   * Sets the field prefix to store the location of the object in.
   * Uses 1-based index as suffix for enumerating locations.
   *
   * @param value	the field
   */
  public void setLocationPrefix(Field value) {
    m_LocationPrefix = value;
    reset();
  }

  /**
   * Returns the field to store the location of the object in.
   * Uses 1-based index as suffix for enumerating locations.
   *
   * @return		the field
   */
  public Field getLocationPrefix() {
    return m_LocationPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String locationPrefixTipText() {
    return "The prefix for fields to store the location of the objects in (uses 1-based enumeration suffix).";
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
    List<AbstractField>		current;
    int				lastIndex;
    int				index;
    Rectangle			rect;
    AbstractField		newField;
    double			scale;

    report = panel.getAdditionalProperties().getClone();

    // get current locations
    current   = new ArrayList<>();
    lastIndex = 0;
    for (AbstractField field: report.getFields()) {
      if (field.getName().startsWith(m_LocationPrefix.getName())) {
	try {
	  index = Integer.parseInt(field.getName().substring(m_LocationPrefix.getName().length()));
	  if (index > lastIndex)
	    lastIndex = index;
	  current.add(field);
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    if ((modifiersEx & MouseEvent.CTRL_DOWN_MASK) != 0) {
      scale = panel.getActualScale();
      rect = new Rectangle(
	(int) (topLeft.x / scale),
	(int) (topLeft.y / scale),
	(int) (bottomRight.x / scale - topLeft.x / scale) + 1,
	(int) (bottomRight.y / scale - topLeft.y / scale) + 1);
      for (AbstractField field: current) {
	loc = new QuadrilateralLocation(report.getStringValue(field));
	if (rect.contains(loc.rectangleValue()))
	  report.removeValue(field);
      }
    }
    else {
      lastIndex++;
      x1  = panel.mouseToPixelLocation(topLeft).x;
      y1  = panel.mouseToPixelLocation(topLeft).y;
      x2  = panel.mouseToPixelLocation(bottomRight).x;
      y2  = panel.mouseToPixelLocation(bottomRight).y;
      loc = new QuadrilateralLocation(
	x1, y1,
	x2, y1,
	x2, y2,
	x1, y2);
      newField = new Field(m_LocationPrefix.getName() + lastIndex, DataType.STRING);
      report.addField(newField);
      report.setValue(newField, loc.toString());
    }

    panel.setAdditionalProperties(report);
  }
}
