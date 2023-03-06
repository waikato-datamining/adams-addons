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
 * DEXTRjava
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.annotator;

import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.visualization.object.tools.DEXTR;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Annotator plugin that works only in conjunction with the DEXTR tool.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DEXTRMarkers
  extends AbstractAnnotator {

  private static final long serialVersionUID = -5432370868809862158L;

  /**
   * Enum for the marker shape to plot around the marker points.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum Shape {
    /** a square box. */
    BOX,
    /** a circle. */
    CIRCLE,
    /** a triangle. */
    TRIANGLE
  }

  /** the marker shape. */
  protected Shape m_Shape;

  /** the color for the markers. */
  protected Color m_Color;

  /** the maximum width/height of the shape to plot around the marker points. */
  protected int m_Extent;

  /** the points. */
  protected List<Point> m_Points;

  /** the mouse listener. */
  protected MouseListener m_MouseListener;

  /** the key listener. */
  protected KeyListener m_KeyListener;

  /** the associated tool. */
  protected DEXTR m_Tool;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Annotator plugin that works only in conjunction with the DEXTR tool.\n"
      + "Left-click to add an extreme point, CTRL and left-click clears the points.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "shape", "shape",
      Shape.CIRCLE);

    m_OptionManager.add(
      "color", "color",
      Color.RED);

    m_OptionManager.add(
      "extent", "extent",
      7, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Selecting = true;
    m_Points    = new ArrayList<>();

    m_MouseListener = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (MouseUtils.isLeftClick(e) && KeyUtils.isCtrlDown(e.getModifiersEx())) {
          m_Points.clear();
          e.consume();
	  getOwner().annotationsChanged(this);
	}
        else if (MouseUtils.isLeftClick(e) && MouseUtils.hasNoModifierKey(e)) {
          m_Points.add(new Point(
	    (int) (e.getX() / getOwner().getActualZoom()),
	    (int) (e.getY() / getOwner().getActualZoom())));
          e.consume();
          getOwner().annotationsChanged(this);
	  if (!getOwner().getCanvas().hasFocus())
	    getOwner().getCanvas().requestFocus();
	}
	super.mouseClicked(e);
      }
    };

    m_KeyListener = new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (m_Tool != null)) {
	  if (m_Points.size() == 4) {
	    e.consume();
	    m_Tool.setPoints(m_Points);
	    m_Points.clear();
	    getOwner().annotationsChanged(this);
	    m_Tool.sendData();
	  }
	}
	super.keyPressed(e);
      }
    };
  }

  /**
   * Sets the shape to use for markers.
   *
   * @param value	the shape
   */
  public void setShape(Shape value) {
    m_Shape = value;
    reset();
  }

  /**
   * Returns the shape in use for markers.
   *
   * @return		the shape
   */
  public Shape getShape() {
    return m_Shape;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapeTipText() {
    return "The shape for the point markers.";
  }

  /**
   * Sets the color to use for point markers.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color in use for point markers.
   *
   * @return		the color
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
    return "The color to use for drawing the point markers.";
  }

  /**
   * Sets the size of the markers.
   *
   * @param value	the extent
   */
  public void setExtent(int value) {
    m_Extent = value;
    reset();
  }

  /**
   * Returns the size of the markers.
   *
   * @return		the extent
   */
  public int getExtent() {
    return m_Extent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extentTipText() {
    return "The size of the markers.";
  }

  /**
   * Sets the associated tool.
   *
   * @param value	the tool
   */
  public void setTool(DEXTR value) {
    m_Tool = value;
  }

  /**
   * Returns the associated tool.
   *
   * @return		the tool
   */
  public DEXTR getTool() {
    return m_Tool;
  }

  /**
   * Installs the annotator with the owner.
   */
  @Override
  protected void doInstall() {
    getOwner().getCanvas().addMouseListener(m_MouseListener);
    getOwner().getCanvas().addKeyListener(m_KeyListener);
  }

  /**
   * Uninstalls the annotator with the owner.
   */
  @Override
  protected void doUninstall() {
    getOwner().getCanvas().removeMouseListener(m_MouseListener);
    getOwner().getCanvas().removeKeyListener(m_KeyListener);
  }

  /**
   * Paints the markers.
   *
   * @param g the graphics context
   */
  @Override
  protected void doPaintSelection(Graphics g) {
    int		currX;
    int		currY;
    int 	prevX;
    int 	prevY;

    if (m_Points.size() == 0)
      return;

    g.setColor(m_Color);

    prevX = 0;
    prevY = 0;

    for (Point p: m_Points) {
      currX = (int) p.getX();
      currY = (int) p.getY();

      if (Math.sqrt(Math.pow(currX - prevX, 2) + Math.pow(currY - prevY, 2)) > m_Extent * 2) {
	if (m_Shape == Shape.BOX) {
	  g.drawRect(
	    currX - (m_Extent / 2),
	    currY - (m_Extent / 2),
	    m_Extent - 1,
	    m_Extent - 1);
	}
	else if (m_Shape == Shape.CIRCLE) {
	  g.drawArc(
	    currX - (m_Extent / 2),
	    currY - (m_Extent / 2),
	    m_Extent - 1,
	    m_Extent - 1,
	    0,
	    360);
	}
	else if (m_Shape == Shape.TRIANGLE) {
	  int[] x = new int[3];
	  int[] y = new int[3];
	  x[0] = currX - (m_Extent / 2);
	  y[0] = currY + (m_Extent / 2);
	  x[1] = x[0] + m_Extent;
	  y[1] = y[0];
	  x[2] = currX;
	  y[2] = y[0] - m_Extent;
	  g.drawPolygon(x, y, 3);
	}

	prevX = currX;
	prevY = currY;
      }
    }
  }
}
