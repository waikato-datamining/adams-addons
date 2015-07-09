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
 * QuadrilateralLocationsOverlayFromReport.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import adams.core.base.BaseRegExp;
import adams.core.base.QuadrilateralLocation;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays the quadrilateral locations of tracked objects in the image, using data from the attached report.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-enabled &lt;boolean&gt; (property: enabled)
 * &nbsp;&nbsp;&nbsp;If enabled, this overlay is painted over the image.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression to match against the fields in the report for identifying 
 * &nbsp;&nbsp;&nbsp;object locations.
 * &nbsp;&nbsp;&nbsp;default: Tracker\\\\..*
 * </pre>
 * 
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to use for the objects.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 198 $
 */
public class QuadrilateralLocationsOverlayFromReport
  extends AbstractImageOverlay {

  /** for serialization. */
  private static final long serialVersionUID = 6356419097401574024L;

  /** the regexp for the locations in the report. */
  protected BaseRegExp m_RegExp;

  /** the color for the locataions. */
  protected Color m_Color;
  
  /** the cached locations. */
  protected List<QuadrilateralLocation> m_Locations;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the quadrilateral locations of tracked objects in the image, using data from the attached report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp("Tracker\\..*"));

    m_OptionManager.add(
      "color", "color",
      Color.RED);
  }

  /**
   * Sets the regular expression for the locations in the report.
   *
   * @param value 	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for the locations in the report.
   *
   * @return 		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to match against the fields in the report for identifying object locations.";
  }

  /**
   * Sets the color to use for the objects.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to use for the objects.
   *
   * @return 		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color to use for the objects.";
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected synchronized void doImageChanged(PaintPanel panel) {
    m_Locations = null;
  }
  
  /**
   * Determines the locations of the bugs.
   * 
   * @param report	the report to inspect
   */
  protected void determineLocations(Report report) {
    List<AbstractField>	  fields;
    QuadrilateralLocation loc;

    if (m_Locations != null)
      return;

    fields = report.getFields();
    
    m_Locations = new ArrayList<QuadrilateralLocation>();
    for (AbstractField field: fields) {
      if (m_RegExp.isMatch(field.getName())) {
	try {
          loc = new QuadrilateralLocation(report.getStringValue(field));
          m_Locations.add(loc);
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
  }

  /**
   * Performs the actual painting of the overlay.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  protected synchronized void doPaintOverlay(PaintPanel panel, Graphics g) {
    int[]	x;
    int[]	y;
    double[]	pos;

    determineLocations(panel.getOwner().getAdditionalProperties());
    
    if (m_Locations.size() > 0) {
      g.setColor(m_Color);
      for (QuadrilateralLocation loc : m_Locations) {
	pos = loc.doubleValue();
	x = new int[]{(int) pos[0], (int) pos[2], (int) pos[4], (int) pos[6]};
	y = new int[]{(int) pos[1], (int) pos[3], (int) pos[5], (int) pos[7]};
	g.drawPolygon(x, y, 4);
      }
    }
  }
}
