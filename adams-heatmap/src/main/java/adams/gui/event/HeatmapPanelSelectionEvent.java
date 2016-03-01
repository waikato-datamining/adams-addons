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
 * HeatmapPanelSelectionEvent.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import adams.gui.visualization.heatmap.HeatmapPanel;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * Event that gets sent in case of a box selection event in the {@link HeatmapPanel}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapPanelSelectionEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 722590085059939598L;

  /** the top-left of the selection. */
  protected Point m_TopLeft;

  /** the bottom-right of the selection. */
  protected Point m_BottomRight;
  
  /** the associated modifiers. */
  protected int m_ModifiersEx;
  
  /**
   * Initializes the event.
   *
   * @param source		the source of the event
   * @param topLeft		the top-left of the selection
   * @param bottomRight		the bottom-right of the selection
   * @param modifiersEx	the extended modifiers associated with the event when releasing the mouse
   */
  public HeatmapPanelSelectionEvent(HeatmapPanel source, Point topLeft, Point bottomRight, int modifiersEx) {
    super(source);
    
    m_TopLeft     = topLeft;
    m_BottomRight = bottomRight;
    m_ModifiersEx = modifiersEx;
  }
  
  /**
   * Returns the heatmap panel that triggered the event.
   * 
   * @return		the heatmap panel
   */
  public HeatmapPanel getHeatmapPanel() {
    return (HeatmapPanel) getSource();
  }
  
  /**
   * Returns the top-left position.
   * 
   * @return		the position
   */
  public Point getTopLeft() {
    return m_TopLeft;
  }
  
  /**
   * Returns the bottom-right position.
   * 
   * @return		the position
   */
  public Point getBottomRight() {
    return m_BottomRight;
  }
  
  /**
   * Returns the associated modifiers.
   * 
   * @return		the modifiers
   * @see		MouseEvent#getModifiersEx()
   */
  public int getModifiersEx() {
    return m_ModifiersEx;
  }
}
