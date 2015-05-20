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
 * Heatmap.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.heatmap;

import adams.core.Constants;
import adams.core.Utils;
import adams.data.Notes;
import adams.data.NotesHandler;
import adams.data.container.DataContainer;
import adams.data.container.DataPointComparator;
import adams.data.id.MutableIDHandler;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.statistics.InformativeStatisticSupporter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Simple wrapper around a 2-D array representing a heatmap.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Heatmap
  implements Serializable, MutableIDHandler, MutableReportHandler<Report>,
             NotesHandler, DataContainer<HeatmapValue>,
             InformativeStatisticSupporter<HeatmapStatistic>{

  /** for serialization. */
  private static final long serialVersionUID = 2380816899974969042L;

  /** the field for the "filename" meta-data entry. */
  public final static String FIELD_FILENAME = "Filename";

  /** the field for the "timestamp" meta-data entry. */
  public final static String FIELD_TIMESTAMP = "Timestamp";

  /** the missing value. */
  public final static double MISSING_VALUE = Double.NaN;

  /** the singleton comparator. */
  protected static DataPointComparator<HeatmapValue> m_Comparator;

  /** the ID of the heatmap (basically the filename). */
  protected String m_ID;

  /** the actual heat map. */
  protected double[][] m_Map;

  /** meta-information on the heatmap. */
  protected Report m_Report;

  /** the attached notes. */
  protected Notes m_Notes;

  /** the minimum intensity value. */
  protected HeatmapValue m_Min;

  /** the maximum intensity value. */
  protected HeatmapValue m_Max;

  /**
   * Initializes the heatmap.
   */
  private Heatmap() {
    m_ID     = "";
    m_Report = createEmptyReport();
    m_Notes  = new Notes();
    resetMinMax();
  }

  /**
   * Initializes an empty heatmap with a given size.
   *
   * @param rows	the number of rows in the map
   * @param cols	the number of cols in the map
   */
  public Heatmap(int rows, int cols) {
    this();
    m_Map = new double[rows][cols];
  }

  /**
   * Initializes the heatmap with the 2-D data.
   *
   * @param map		the map data
   */
  public Heatmap(double[][] map) {
    this();
    m_Map = map.clone();
    resetMinMax();
    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++)
	updateMinMax(y, x, m_Map[y][x]);
    }
  }

  /**
   * Sets the ID.
   *
   * @param value	the ID
   */
  @Override
  public void setID(String value) {
    m_ID = value;
  }

  /**
   * Returns the ID.
   *
   * @return		the ID
   */
  @Override
  public String getID() {
    return m_ID;
  }

  /**
   * Sets a new report.
   *
   * @param value	the new report
   */
  @Override
  public void setReport(Report value) {
    m_Report = value;
  }

  /**
   * Checks whether a report is present.
   *
   * @return		true if a report is present
   */
  @Override
  public boolean hasReport() {
    return (m_Report != null);
  }

  /**
   * Returns the report.
   *
   * @return		the report, can be null if none available
   */
  @Override
  public Report getReport() {
    return m_Report;
  }

  /**
   * Resets the min/max values.
   */
  protected void resetMinMax() {
    m_Min = new HeatmapValue(0, 0, Double.MAX_VALUE);
    m_Max = new HeatmapValue(0, 0, Double.MIN_VALUE);
  }

  /**
   * Updates the min/max values.
   *
   * @param value	the new value
   */
  protected void updateMinMax(int row, int col, double value) {
    HeatmapValue  val;

    if (!isMissingValue(value)) {
      val = new HeatmapValue(row, col, value);
      if (val.getValue() < m_Min.getValue())
        m_Min = val;
      if (val.getValue() > m_Max.getValue())
        m_Max = val;
    }
  }

  /**
   * Returns the smallest value in the heatmap.
   *
   * @return		the minimum, {@link Double#MAX_VALUE} if only zeroes
   * 			in the heatmp
   */
  public double getMin() {
    return m_Min.getValue();
  }

  /**
   * Returns the smallest value in the heatmap.
   *
   * @return		the minimum, {@link Double#MAX_VALUE} if only zeroes
   * 			in the heatmp
   */
  public HeatmapValue getMinValue() {
    return m_Min;
  }

  /**
   * Returns the largest value in the heatmap.
   *
   * @return		the maximum, {@link Double#MIN_VALUE} if only zeroes
   * 			in the heatmp
   */
  public double getMax() {
    return m_Max.getValue();
  }

  /**
   * Returns the largest value in the heatmap.
   *
   * @return		the maximum, {@link Double#MIN_VALUE} if only zeroes
   * 			in the heatmp
   */
  public HeatmapValue getMaxValue() {
    return m_Max;
  }

  /**
   * Returns the height of the map.
   *
   * @return		the height
   */
  public int getHeight() {
    return m_Map.length;
  }

  /**
   * Returns the width of the map.
   *
   * @return		the width
   */
  public int getWidth() {
    if (m_Map.length > 0)
      return m_Map[0].length;
    else
      return 0;
  }

  /**
   * Returns the total amount of data points in the map.
   *
   * @return		the total size
   */
  @Override
  public int size() {
    return getWidth() * getHeight();
  }

  /**
   * Returns the X location from the position.
   *
   * @param pos		the position to get the X location for
   * @return		the X location
   */
  public int getX(int pos) {
    int		x;
    int		y;

    y = pos / getWidth();
    x = pos - y * getWidth();

    return x;
  }

  /**
   * Returns Y location from the position.
   *
   * @param pos		the position to retrieve the Y location for
   * @return		the Y location
   */
  public int getY(int pos) {
    return pos / getWidth();
  }

  /**
   * Returns the map value at the specified location.
   *
   * @param row		the row index
   * @param col		the column index
   * @return		the heat value at the location
   */
  public double get(int row, int col) {
    return m_Map[row][col];
  }

  /**
   * Returns the map value the specified position from the top left corner
   * of the map, wlkaing through row-wise.
   *
   * @param pos		the position to retrieve
   * @return		the heat value at the position
   */
  public double get(int pos) {
    return get(getY(pos), getX(pos));
  }

  /**
   * Sets the map value at the specified location.
   *
   * @param row		the row index
   * @param col		the column index
   * @param value	the heat value to set (>= 0.0)
   */
  public void set(int row, int col, double value) {
    updateMinMax(row, col, value);

    m_Map[row][col] = value;
  }

  /**
   * Sets the map value at the specified position from the top left corner
   * of the map, walking through row-wise.
   *
   * @param pos		the position in the map
   * @param value	the heat value to set
   */
  public void set(int pos, double value) {
    set(getY(pos), getX(pos), value);
  }

  /**
   * Sets all the values, if the size of the array matches this heatmap.
   * The array is assumed to have the values stored row-wise.
   *
   * @param values	the values to set
   * @throws IllegalArgumentException	if array length and size of heatmap don't match
   */
  public void set(double[] values) {
    int		x;
    int		y;
    int		index;

    if (values.length != size())
      throw new IllegalArgumentException(
	  "Length of array does not match heatmap size: " + values.length + " != " + size());

    index = 0;
    for (y = 0; y < getHeight(); y++) {
      for (x = 0; x < getWidth(); x++) {
	set(y, x, values[index]);
	index++;
      }
    }
  }

  /**
   * Sets all the values, if the size of the array matches this heatmap.
   * The array is assumed to have the values stored row-wise.
   *
   * @param values	the values to set
   * @throws IllegalArgumentException	if array length and size of heatmap don't match
   */
  public void set(Double[] values) {
    int		x;
    int		y;
    int		index;

    if (values.length != size())
      throw new IllegalArgumentException(
	  "Length of array does not match heatmap size: " + values.length + " != " + size());

    index = 0;
    for (y = 0; y < getHeight(); y++) {
      for (x = 0; x < getWidth(); x++) {
	set(y, x, values[index]);
	index++;
      }
    }
  }

  /**
   * Sets the map value at the specified location.
   *
   * @param row		the row index
   * @param col		the column index
   */
  public void setMissing(int row, int col) {
    set(row, col, MISSING_VALUE);
  }

  /**
   * Sets the map value at the specified position to missing from the top left corner
   * of the map, walking through row-wise.
   *
   * @param pos		the position in the map
   */
  public void setMissing(int pos) {
    set(pos, MISSING_VALUE);
  }

  /**
   * Sets the map value at the specified location to missing.
   *
   * @param row		the row index
   * @param col		the column index
   */
  public boolean isMissing(int row, int col) {
    return isMissingValue(get(row, col));
  }

  /**
   * Sets the map value at the specified position from the top left corner
   * of the map, walking through row-wise.
   *
   * @param pos		the position in the map
   */
  public boolean isMissing(int pos) {
    return isMissingValue(get(pos));
  }

  /**
   * Returns the currently stored notes.
   *
   * @return		the current notes
   */
  @Override
  public Notes getNotes() {
    return m_Notes;
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  @Override
  public Heatmap getClone() {
    Heatmap	result;

    result = new Heatmap(m_Map.clone());
    result.setID(new String(getID()));
    result.setReport(getReport().getClone());
    result.m_Notes = getNotes().getClone();

    return result;
  }

  /**
   * Returns a short string representation.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return "ID=" + m_ID + ", width=" + getWidth() + ", height=" + getHeight();
  }

  /**
   * Turns the heatmap into an array (row wise).
   *
   * @return		the heat values as array
   */
  @Override
  public HeatmapValue[] toArray() {
    HeatmapValue[]	result;
    int			x;
    int			y;
    int			index;

    result = new HeatmapValue[size()];
    index  = 0;
    for (y = 0; y < getHeight(); y++) {
      for (x = 0; x < getWidth(); x++) {
	result[index] = new HeatmapValue(y, x, get(y, x));
	result[index].setParent(this);
	index++;
      }
    }

    return result;
  }

  /**
   * Turns the heatmap into a Double array (row wise).
   *
   * @return		the heat values as Double array
   */
  public Double[] toDoubleArray() {
    return toDoubleArray(false);
  }

  /**
   * Turns the heatmap into a Double array (row wise).
   *
   * @param skipMissing	whether to skip missing values
   * @return		the heat values as Double array
   */
  public Double[] toDoubleArray(boolean skipMissing) {
    List<Double>	result;
    int			x;
    int			y;

    result = new ArrayList<Double>();
    for (y = 0; y < getHeight(); y++) {
      for (x = 0; x < getWidth(); x++) {
	if (skipMissing && isMissing(y, x))
	  continue;
	result.add(get(y, x));
      }
    }

    return result.toArray(new Double[result.size()]);
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
  @Override
  public int compareTo(Object o) {
    int				result;
    Heatmap			other;
    Iterator<HeatmapValue>	iter;
    Iterator<HeatmapValue>	iterOther;

    if (o == null)
      return 1;
    else
      result = 0;

    if (!(o instanceof Heatmap))
      return -1;

    other = (Heatmap) o;

    result = new Integer(size()).compareTo(new Integer(other.size()));

    if (result == 0)
      result = compareToHeader(o);

    if (result == 0) {
      iter      = iterator();
      iterOther = other.iterator();

      while (iter.hasNext() && (result == 0))
	result = iter.next().compareTo(iterOther.next());
    }

    return result;
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
  @Override
  public int compareToHeader(Object o) {
    int		result;
    Heatmap	c;

    if (o == null)
      return 1;
    else
      result = 0;

    c = (Heatmap) o;

    if (result == 0)
      result = Utils.compare(getID(), c.getID());

    if (result == 0)
      result = Utils.compare(getReport(), c.getReport());

    return result;
  }

  /**
   * Indicates whether some other chromatogram's header is "equal to" this ones.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  @Override
  public boolean equalsHeader(Object obj) {
    return (compareToHeader(obj) == 0);
  }

  /**
   * Checks whether the specified object has the same content as this one.
   * 
   * @param obj		the object to compare
   * @return		true if the same content
   * @see		#compareTo(Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Heatmap)
      return (compareTo(obj) == 0);
    else
      return false;
  }
  
  /**
   * Returns an iterator over the elements in this collection.  There are no
   * guarantees concerning the order in which the elements are returned
   * (unless this collection is an instance of some class that provides a
   * guarantee).
   *
   * @return an <tt>Iterator</tt> over the elements in this collection
   */
  @Override
  public Iterator<HeatmapValue> iterator() {
    Iterator	result;

    result = new Iterator() {
      int index = 0;
      @Override
      public boolean hasNext() {
        return (index < size());
      }
      @Override
      public Object next() {
	int x = getX(index);
	int y = getY(index);
	HeatmapValue result = new HeatmapValue(y, x, get(y, x));
	index++;
        return result;
      }
      @Override
      public void remove() {
	throw new UnsupportedOperationException();
      }
    };

    return result;
  }

  /**
   * Sets all values to missing.
   * <br><br>
   * Note: the size of the collection won't be 0, as defined by the Collection
   * interface.
   */
  @Override
  public void clear() {
    int		x;
    int		y;

    resetMinMax();

    for (y = 0; y < getHeight(); y++) {
      for (x = 0; x < getWidth(); x++)
	setMissing(y, x);
    }
  }

  /**
   * Only false if all values are missing.
   *
   * @return		true if all values are missing
   */
  @Override
  public boolean isEmpty() {
    int		x;
    int		y;

    for (y = 0; y < getHeight(); y++) {
      for (x = 0; x < getWidth(); x++) {
	if (!isMissing(y, x))
	  return false;
      }
    }

    return true;
  }

  /**
   * Returns the stored points as array.
   *
   * @param a		ignored
   * @return		the points as array
   */
  @Override
  public <HeatmapValue> HeatmapValue[] toArray(HeatmapValue[] a) {
    return (HeatmapValue[]) toArray();
  }

  /**
   * Adds the value specified by this value object.
   *
   * @param e		the value to add
   * @return		true if the value was different from the previous one
   */
  @Override
  public boolean add(HeatmapValue e) {
    double	old;

    old = get(e.getY(), e.getX());
    set(e.getY(), e.getX(), e.getValue());

    return (old != e.getValue());
  }

  /**
   * Sets all the values stored in the collection in this heatmap.
   *
   * @param c		the collection to use
   * @return		true if heatmap was modified
   */
  @Override
  public boolean addAll(Collection<? extends HeatmapValue> c) {
    boolean	result;

    result = false;

    for (HeatmapValue v: c)
      result = add(v) || result;

    return result;
  }

  /**
   * Sets the value at the location of the provided object to missing.
   *
   * @param o		the heatmap value with the coordinates to remove
   * @return		true if the value changed
   */
  @Override
  public boolean remove(Object o) {
    HeatmapValue	v;
    double		old;

    v   = (HeatmapValue) o;
    old = get(v.getY(), v.getX());
    setMissing(v.getY(), v.getX());

    return !isMissingValue(old);
  }

  /**
   * Sets all values in this heatmap to 0.0 for locations stored in the
   * collection provided.
   *
   * @param c		the collection of heatmap values, which locations
   * 			should be set to 0.0 in this heatmap
   * @return		true if collection modified
   */
  @Override
  public boolean removeAll(Collection<?> c) {
    boolean	result;
    Iterator	iter;

    result = false;
    iter   = c.iterator();
    while (iter.hasNext())
      result = remove(iter.next()) || result;

    return result;
  }

  /**
   * Checks whether the heatmap contains the specified object. For a heatmap
   * value, the exact same value must be stored in the heatmap.
   *
   * @param o		the object to look for
   * @return		true if object stored
   */
  @Override
  public boolean contains(Object o) {
    boolean		result;
    HeatmapValue	v;

    result = false;

    if (o instanceof HeatmapValue) {
      v = (HeatmapValue) o;
      if ((v.getY() < getHeight()) && (v.getX() < getWidth())) {
	result =
	  (v.isMissingValue() && isMissing(v.getY(), v.getX()))
	  || (v.getValue() == get(v.getY(), v.getX()));
      }
    }

    return result;
  }

  /**
   * Checks whether all of the items in the collection are stored in this
   * heatmap.
   *
   * @param c		the collection to check
   * @return		true if all items are present
   */
  @Override
  public boolean containsAll(Collection<?> c) {
    boolean	result;
    Iterator	iter;

    result = true;
    iter   = c.iterator();
    while (iter.hasNext()) {
      result = contains(iter.next());
      if (!result)
	break;
    }

    return result;
  }

  /**
   * Always throws UnsupportedOperationException.
   *
   * @throws UnsupportedOperationException if the <tt>clear</tt> operation
   *         is not supported by this collection
   */
  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  /**
   * Merges its own data with the one provided by the specified object.
   * Only adds a value from the other heatmap, if this one has a missing value
   * at the specified location.
   *
   * @param other		the object to merge with
   */
  @Override
  public void mergeWith(DataContainer other) {
    Iterator<HeatmapValue>	iter;
    HeatmapValue		v;

    iter = other.iterator();
    while (iter.hasNext()) {
      v = iter.next();
      if ((v.getY() < getHeight()) && (v.getX() < getWidth())) {
	if (isMissing(v.getY(), v.getX()))
	  set(v.getY(), v.getX(), v.getValue());
      }
    }
  }

  /**
   * Returns a new instance of the default comparator.
   *
   * @return		the comparator instance
   */
  @Override
  public DataPointComparator<HeatmapValue> newComparator() {
    return new DataPointComparator<HeatmapValue>() {
      private static final long serialVersionUID = -7729686147234670766L;
      @Override
      public int compare(adams.data.heatmap.HeatmapValue o1, adams.data.heatmap.HeatmapValue o2) {
	int result;
	result = new Integer(o1.getY()).compareTo(new Integer(o2.getY()));
	if (result == 0)
	  result = new Integer(o1.getX()).compareTo(new Integer(o2.getX()));
	if (result == 0) {
	  if (o1.isMissingValue() && o2.isMissingValue())
	    result = 0;
	  else if (o1.isMissingValue())
	    result = -1;
	  else if (o2.isMissingValue())
	    result = 1;
	  else
	    result = new Double(o1.getValue()).compareTo(new Double(o2.getValue()));
	}
        return result;
      }
    };
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator in use
   */
  @Override
  public synchronized DataPointComparator<HeatmapValue> getComparator() {
    if (m_Comparator == null)
      m_Comparator = newComparator();
    return m_Comparator;
  }

  /**
   * Returns a new instance of a DataContainer point.
   *
   * @return		the new DataContainer point
   */
  @Override
  public HeatmapValue newPoint() {
    return new HeatmapValue();
  }

  /**
   * Returns an empty container with the same meta-data as this one.
   *
   * @return		a clone of the payload
   */
  @Override
  public Heatmap getHeader() {
    return getHeader(getHeight(), getWidth());
  }

  /**
   * Returns an empty container with the same meta-data as this one,
   * but with different dimensions of the map.
   *
   * @param height	the new height of the map
   * @param width	the new width of the map
   * @return		a clone of the payload
   */
  public Heatmap getHeader(int height, int width) {
    Heatmap	result;

    result = new Heatmap(height, width);
    result.assign(this);

    return result;
  }

  /**
   * Obtains the stored variables from the other data point, but not the
   * actual data points.
   *
   * @param other	the data point to get the values from
   */
  @Override
  public void assign(DataContainer<HeatmapValue> other) {
    Heatmap	map;

    map = (Heatmap) other;
    setID(map.getID());
    setReport(map.getReport().getClone());
    m_Notes.mergeWith(map.getNotes());
  }

  /**
   * Returns a list with the points.
   *
   * @return		a list with all the points
   */
  @Override
  public List<HeatmapValue> toList() {
    ArrayList<HeatmapValue>	result;
    int				x;
    int				y;

    result = new ArrayList<HeatmapValue>();
    for (y = 0; y < getHeight(); y++) {
      for (x = 0; x < getWidth(); x++) {
	result.add(new HeatmapValue(y, x, get(y, x)));
      }
    }

    return result;
  }

  /**
   * Returns a list with the points.
   *
   * @return		a list with all the points
   */
  @Override
  public List<HeatmapValue> toList(DataPointComparator comparator) {
    ArrayList<HeatmapValue>	result;

    result = new ArrayList<HeatmapValue>(toList());
    Collections.sort(result, comparator);

    return result;
  }

  /**
   * Returns a treeset with the points.
   *
   * @return		a treeset with all the points
   */
  @Override
  public TreeSet<HeatmapValue> toTreeSet() {
    return toTreeSet(null);
  }

  /**
   * Returns a treeset with the points, sorted according to the given
   * comparator.
   *
   * @param comparator	the comparator to use
   * @return		a treeset with all the points
   */
  @Override
  public TreeSet<HeatmapValue> toTreeSet(DataPointComparator<HeatmapValue> comparator) {
    TreeSet<HeatmapValue>	result;
    int				x;
    int				y;

    if (comparator == null)
      result = new TreeSet<HeatmapValue>();
    else
      result = new TreeSet<HeatmapValue>(comparator);
    for (y = 0; y < getHeight(); y++) {
      for (x = 0; x < getWidth(); x++) {
	result.add(new HeatmapValue(y, x, get(y, x)));
      }
    }

    return result;
  }

  /**
   * Returns the specified submap.
   *
   * @param row		the row of the top-left corner
   * @param col		the column of the top-left corner
   * @param height	the height of the submap
   * @param width	the width of the submap
   */
  public Heatmap submap(int row, int col, int height, int width) {
    Heatmap	result;
    int		x;
    int		y;

    if (row + height > getHeight())
      throw new IllegalArgumentException("Submap exceeds height: " + (row + height) + " > " + getHeight());
    if (col + width > getWidth())
      throw new IllegalArgumentException("Submap exceeds width: " + (col + width) + " > " + getWidth());

    result = getHeader(height, width);
    for (y = row; y < row + height; y++) {
      for (x = col; x < col + width; x++)
	result.set(y - row, x - col, get(y, x));
    }

    return result;
  }

  /**
   * Returns a comma-separated string of all the intensity values.
   *
   * @return		the intensity values as string
   */
  public String toIntensityString() {
    StringBuilder	result;
    int			x;
    int			y;

    result = new StringBuilder();

    for (y = 0; y < getHeight(); y++) {
      for (x = 0; x < getWidth(); x++) {
	if (result.length() > 0)
	  result.append(",");
	result.append(Utils.doubleToString(get(y, x), 6));
      }
    }

    return result.toString();
  }

  /**
   * Returns a statistic object for this object.
   *
   * @return		statistics for this object
   */
  @Override
  public HeatmapStatistic toStatistic() {
    return new HeatmapStatistic(this);
  }

  /**
   * Creates a heatmap from the intensity string using the specified dimensions.
   *
   * @param rows	the height of the heatmap
   * @param cols	the width of the heatmap
   * @param intensity	the comma-separated list of intensity values
   * @return		the generated heatmap
   */
  public static Heatmap fromIntensityString(int rows, int cols, String intensity) {
    Heatmap	result;
    String[]	parts;
    int		i;
    int		y;
    int		x;
    double[][]	map;

    map    = new double[rows][cols];
    parts  = intensity.split(",");
    x      = 0;
    y      = 0;
    for (i = 0; i < parts.length; i++) {
      map[y][x] = Utils.toDouble(parts[i]);
      x++;
      if (x == cols) {
	y++;
	x = 0;
	if (y == rows)
	  break;
      }
    }
    result = new Heatmap(map);

    return result;
  }

  /**
   * Returns an empty report.
   *
   * @return		the empty report
   */
  public static Report createEmptyReport() {
    Report	result;

    result = new Report();

    result.addField(new Field(Report.PROPERTY_PARENTID, DataType.NUMERIC));
    result.addField(new Field(FIELD_FILENAME,           DataType.STRING));
    result.addField(new Field(FIELD_TIMESTAMP,          DataType.STRING));

    result.setValue(new Field(Report.PROPERTY_PARENTID, DataType.NUMERIC), Constants.NO_ID);

    return result;
  }

  /**
   * Checks whether the value represents a missing value.
   *
   * @param value	the value to check
   * @return		true if missing value
   */
  public static boolean isMissingValue(double value) {
    return Double.isNaN(value);
  }
}
