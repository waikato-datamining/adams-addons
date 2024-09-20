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
 * Step.java
 * Copyright (C) 2015-2024 University of Waikato, Hamilton, NZ
 */

package adams.data.trail;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.data.container.AbstractDataPoint;
import adams.data.container.DataPoint;

import java.util.Date;
import java.util.HashMap;

/**
 * Represents a single step in a trail.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Step
  extends AbstractDataPoint {

  private static final long serialVersionUID = 7649750314026526010L;

  /** the timestamp of the step. */
  protected Date m_Timestamp;

  /** the X of the step. */
  protected float m_X;

  /** the Y of the step. */
  protected float m_Y;

  /** the optional meta-data. */
  protected HashMap<String,Object> m_MetaData;

  /** for formatting the timestamp. */
  protected static DateFormat m_Format;

  /**
   * Initializes the step with default values.
   */
  public Step() {
    this(new Date(), 0.0f, 0.0f);
  }

  /**
   * Initializes the step with the given timestamp and position, but no
   * meta-data.
   *
   * @param timestamp	the timestamp
   * @param x		the X
   * @param y		the Y
   */
  public Step(Date timestamp, float x, float y) {
    this(timestamp, x, y, null);
  }

  /**
   * Initializes the step with the given timestamp and position, but no
   * meta-data.
   *
   * @param timestamp	the timestamp
   * @param x		the X
   * @param y		the Y
   * @param metaData	the meta-data, can be null
   */
  public Step(Date timestamp, float x, float y, HashMap<String,Object> metaData) {
    super();
    m_Timestamp = new Date(timestamp.getTime());
    m_X         = x;
    m_Y         = y;
    m_MetaData  = null;
    if (metaData != null)
      m_MetaData = new HashMap<>(metaData);
  }

  /**
   * Returns the formatter.
   *
   * @return		the formatter
   */
  protected static synchronized DateFormat getFormat() {
    if (m_Format == null) {
      m_Format = DateUtils.getTimestampFormatterMsecs();
      //m_Format.setTimeZone(TimeZone.getDefault());
    }
    return m_Format;
  }

  /**
   * Obtains the stored variables from the other data point.
   *
   * @param other	the data point to get the values from
   */
  public void assign(DataPoint other) {
    Step 	step;

    super.assign(other);

    step = (Step) other;

    setTimestamp(step.getTimestamp());
    setX(step.getX());
    setY(step.getY());
    setMetaData(step.getMetaData());
  }

  /**
   * Sets the timestamp.
   *
   * @param value	the timestamp
   */
  public void setTimestamp(Date value) {
    m_Timestamp = new Date(value.getTime());
  }

  /**
   * Returns the timestamp.
   *
   * @return		the timestamp
   */
  public Date getTimestamp() {
    return m_Timestamp;
  }

  /**
   * Sets the X of the step.
   *
   * @param value	the X
   */
  public void setX(float value) {
    m_X = value;
  }

  /**
   * Returns the X of the step.
   *
   * @return		the X
   */
  public float getX() {
    return m_X;
  }

  /**
   * Sets the Y of the step.
   *
   * @param value	the Y
   */
  public void setY(float value) {
    m_Y = value;
  }

  /**
   * Returns the Y of the step.
   *
   * @return		the Y
   */
  public float getY() {
    return m_Y;
  }

  /**
   * Checks whether any meta-data is present.
   *
   * @return		true if meta-data present
   */
  public boolean hasMetaData() {
    return (m_MetaData != null) && (m_MetaData.size() > 0);
  }

  /**
   * Sets the meta-data to use.
   *
   * @param value	the meta-data, can be null
   */
  public void setMetaData(HashMap<String,Object> value) {
    if (value == null)
      m_MetaData = null;
    else
      m_MetaData = new HashMap<>(value);
  }

  /**
   * Returns the meta-data, if any.
   *
   * @return		the meta-data, null if none available
   */
  public HashMap<String,Object> getMetaData() {
    return m_MetaData;
  }

  /**
   * Adds the meta-data.
   *
   * @param key		the key
   * @param value	the value
   */
  public synchronized void addMetaData(String key, Object value) {
    if (m_MetaData == null)
      m_MetaData = new HashMap<>();
    m_MetaData.put(key, value);
  }

  /**
   * Compares the object to another. Only uses timestamp, x and y.
   *
   * @param o 		the object to be compared.
   * @return		-1 of smaller, 0 if equal, +1 if larger
   */
  @Override
  public int compareTo(Object o) {
    int		result;
    Step	other;

    other  = (Step) o;

    result = getTimestamp().compareTo(other.getTimestamp());
    if (result == 0)
      result = Float.compare(getX(), other.getX());
    if (result == 0)
      result = Float.compare(getY(), other.getY());

    return result;
  }

  /**
   * Checks whether the provided object is a Step with the same timestamp and X/Y.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if the same step
   * @see		#compareTo(Object)
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Step) && (compareTo(obj) == 0);
  }

  /**
   * Returns a string representation of the point.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return getFormat().format(m_Timestamp) + ": x=" + m_X + " y=" + m_Y + (hasMetaData() ? getMetaData().toString() : "");
  }
}
