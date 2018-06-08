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
 * AudioStep.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.audiotrail;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.data.container.AbstractDataPoint;
import adams.data.container.DataPoint;

import java.util.Date;
import java.util.HashMap;

/**
 * Represents a single step in an audio trail.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AudioStep
  extends AbstractDataPoint {

  private static final long serialVersionUID = 7649750314026526010L;

  /** the timestamp of the step. */
  protected Date m_Timestamp;

  /** the optional meta-data. */
  protected HashMap<String,Object> m_MetaData;

  /** for formatting the timestamp. */
  protected static DateFormat m_DateFormat;
  static {
    m_DateFormat = DateUtils.getTimeFormatterMsecs();
  }

  /**
   * Initializes the step with default values.
   */
  public AudioStep() {
    this(new Date(), null);
  }

  /**
   * Initializes the step with the given timestamp, but no meta-data.
   *
   * @param timestamp	the timestamp
   */
  public AudioStep(Date timestamp) {
    this(new Date(), null);
  }

  /**
   * Initializes the step with the given timestamp and meta-data.
   *
   * @param timestamp	the timestamp
   * @param metaData	the meta-data, can be null
   */
  public AudioStep(Date timestamp, HashMap<String,Object> metaData) {
    super();
    m_Timestamp = new Date(timestamp.getTime());
    m_MetaData  = null;
    if (metaData != null)
      m_MetaData = new HashMap<>(metaData);
  }

  /**
   * Obtains the stored variables from the other data point.
   *
   * @param other	the data point to get the values from
   */
  public void assign(DataPoint other) {
    AudioStep step;

    super.assign(other);

    step = (AudioStep) other;

    setTimestamp(step.getTimestamp());
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
    AudioStep other;

    other  = (AudioStep) o;

    result = getTimestamp().compareTo(other.getTimestamp());

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
    return (obj instanceof AudioStep) && (compareTo(obj) == 0);
  }

  /**
   * Returns a string representation of the point.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return m_DateFormat.format(m_Timestamp) + ": meta=" + (hasMetaData() ? getMetaData().toString() : "");
  }
}
