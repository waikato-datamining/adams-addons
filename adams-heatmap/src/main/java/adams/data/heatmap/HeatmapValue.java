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
 * HeatmapValue.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.heatmap;

import adams.data.container.DataContainer;
import adams.data.container.DataPoint;

/**
 * Wrapper class for a value in a heatmap.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapValue
  implements DataPoint {

  /** for serialization. */
  private static final long serialVersionUID = 7356588902030284681L;

  /** the parent. */
  protected Heatmap m_Parent;

  /** the heatmap value. */
  protected double m_Value;

  /** the X position in the heatmap. */
  protected int m_X;

  /** the Y position in the heatmap. */
  protected int m_Y;

  /**
   * Initializes the heatmap value with 0.0 and coordinates of 0,0.
   */
  public HeatmapValue() {
    this(0, 0, 0.0);
  }

  /**
   * Initializes the heatmap value.
   *
   * @param y		the Y position in the map
   * @param x		the X position in the map
   * @param value	the heatmap value
   */
  public HeatmapValue(int y, int x, double value) {
    super();

    if (y < 0)
      throw new IllegalArgumentException("y cannot be less than 0!");
    if (x < 0)
      throw new IllegalArgumentException("x cannot be less than 0!");

    m_Parent = null;
    m_Y      = y;
    m_X      = x;
    m_Value  = value;
  }

  /**
   * Sets the heatmap this point belongs to.
   *
   * @param value	the heatmap
   */
  public void setParent(DataContainer value) {
    m_Parent = (Heatmap) value;
  }

  /**
   * Returns the heatmap this point belongs to.
   *
   * @return		the heatmap, can be null
   */
  public Heatmap getParent() {
    return m_Parent;
  }

  /**
   * Returns whether the point belongs to a heatmap.
   *
   * @return		true if the point belongs to a heatmap
   */
  public boolean hasParent() {
    return (m_Parent != null);
  }

  /**
   * Returns the X position in the map.
   *
   * @return		the X position
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the Y position in the map.
   *
   * @return		the Y position
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the value in the heatmap.
   *
   * @return		the value
   */
  public double getValue() {
    return m_Value;
  }

  /**
   * Obtains the stored variables from the other data point.
   *
   * @param other	the data point to get the values from
   */
  public void assign(DataPoint other) {
    HeatmapValue	value;

    value    = (HeatmapValue) other;
    m_X      = value.getX();
    m_Y      = value.getY();
    m_Value  = value.getValue();
    m_Parent = value.getParent();
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(Object o) {
    int			result;
    HeatmapValue	p;

    if (o == null)
      return 1;
    else
      result = 0;

    p = (HeatmapValue) o;

    if (result == 0)
      result = new Integer(getY()).compareTo(new Integer(p.getY()));

    if (result == 0)
      result = new Integer(getX()).compareTo(new Integer(p.getX()));

    if (result == 0)
      result = new Double(getValue()).compareTo(new Double(p.getValue()));

    return result;
  }

  /**
   * Returns a clone of itself. Parent gets set to null!
   *
   * @return		the clone
   */
  public Object getClone() {
    DataPoint	result;

    result = new HeatmapValue();
    result.assign(this);
    result.setParent(null);

    return result;
  }

  /**
   * Returns a string representation of the value.
   *
   * @return		a string representation
   */
  public String toString() {
    String	result;

    result  = "y=" + getY();
    result += ", x=" + getX();
    result += ", value=" + getValue();

    return result;
  }
}
