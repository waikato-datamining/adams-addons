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
 * AbstractTrailPaintlet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.trail.paintlet;

import adams.core.ClassLister;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.trail.Trail;
import adams.gui.visualization.image.paintlet.AbstractPaintlet;
import adams.gui.visualization.trail.TrailPanel;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Ancestor for trail paintlets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTrailPaintlet
  extends AbstractPaintlet
  implements TrailPaintlet {

  private static final long serialVersionUID = 8036940792107897639L;

  /** the trail panel. */
  protected TrailPanel m_TrailPanel;

  /** Color of the stroke for the paintlet */
  protected Color m_Color;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color", "color",
      getDefaultColor());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_TrailPanel = null;
  }

  /**
   * Returns the default color for the stroke.
   *
   * @return		the default
   */
  protected Color getDefaultColor() {
    return Color.RED;
  }

  /**
   * Get the stroke color for the paintlet.
   *
   * @return		Color of the stroke
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Set the stroke color for the paintlet.
   *
   * @param value	Color of the stroke
   */
  public void setColor(Color value) {
    m_Color = value;
    memberChanged();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The stroke color.";
  }

  /**
   * Sets the panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  public void setPanel(TrailPanel value) {
    m_TrailPanel = value;
    if (m_TrailPanel != null)
      setPanel(m_TrailPanel.getImagePanel());
  }

  /**
   * Returns the underlying trail panel.
   *
   * @return		the panel, null if not set
   */
  public TrailPanel getTrailPanel() {
    return m_TrailPanel;
  }

  /**
   * Paints the given data.
   *
   * @param g		the graphics context to use for painting
   * @param trail	the data to paint
   */
  public abstract void paintData(Graphics g, Trail trail);

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   */
  protected void performPaint(Graphics g) {
    Trail	trail;

    if (getTrailPanel() == null)
      return;

    trail = getTrailPanel().getTrail();
    if (trail != null)
      paintData(g, trail);
  }

  /**
   * Returns a list with classnames of paintlets.
   *
   * @return		the paintlet classnames
   */
  public static String[] getPaintlets() {
    return ClassLister.getSingleton().getClassnames(TrailPaintlet.class);
  }

  /**
   * Instantiates the paintlet with the given options.
   *
   * @param classname	the classname of the paintlet to instantiate
   * @param options	the options for the paintlet
   * @return		the instantiated paintlet or null if an error occurred
   */
  public static TrailPaintlet forName(String classname, String[] options) {
    TrailPaintlet	result;

    try {
      result = (TrailPaintlet) OptionUtils.forName(TrailPaintlet.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the paintlet from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			paintlet to instantiate
   * @return		the instantiated paintlet
   * 			or null if an error occurred
   */
  public static TrailPaintlet forCommandLine(String cmdline) {
    return (TrailPaintlet) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
