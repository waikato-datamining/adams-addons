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
 * Movement.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.trail;

import adams.core.DateTime;
import adams.core.option.OptionUtils;
import adams.data.Notes;
import adams.data.NotesHandler;
import adams.data.container.AbstractDataContainer;
import adams.data.container.DataContainer;
import adams.data.container.DataPointComparator;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.data.statistics.InformativeStatisticSupporter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Encapsulates a series of steps, i.e., a trail.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Trail
  extends AbstractDataContainer<Step>
  implements MutableReportHandler<Report>,
  NotesHandler, SpreadSheetSupporter, InformativeStatisticSupporter<TrailStatistic>  {

  private static final long serialVersionUID = 8721248965909493612L;

  /** the width of the underlying canvas (null if not set). */
  protected Float m_Width;

  /** the height of the underlying canvas (null if not set). */
  protected Float m_Height;

  /** the attached report. */
  protected Report m_Report;

  /** the notes. */
  protected Notes m_Notes;

  /** the step with the minimum X. */
  protected Step m_MinX;

  /** the step with the maximum X. */
  protected Step m_MaxX;

  /** the step with the minimum Y. */
  protected Step m_MinY;

  /** the step with the maximum Y. */
  protected Step m_MaxY;

  /** the comparator to use. */
  protected static StepComparator m_Comparator;

  /**
   * Initializes the trail.
   */
  public Trail() {
    super();

    m_ID     = "" + new Date();
    m_Report = new Report();
    m_Notes  = new Notes();
    m_Width  = null;
    m_Height = null;
    if (m_Comparator == null)
      m_Comparator = newComparator();
  }

  /**
   * Returns a new instance of the default comparator.
   *
   * @return		the comparator instance
   */
  @Override
  public StepComparator newComparator() {
    return new StepComparator();
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator in use
   */
  @Override
  public DataPointComparator<Step> getComparator() {
    return m_Comparator;
  }

  /**
   * Returns a new instance of a DataContainer point.
   *
   * @return		the new DataContainer point
   */
  @Override
  public Step newPoint() {
    return new Step();
  }

  /**
   * Obtains the stored variables from the other data point, but not the
   * actual data points.
   *
   * @param other	the data point to get the values from
   */
  @Override
  public void assign(DataContainer<Step> other) {
    Trail trail;

    super.assign(other);

    trail = (Trail) other;

    if (trail.hasReport())
      setReport(trail.getReport().getClone());
    m_Notes = new Notes();
    m_Notes.mergeWith(trail.getNotes());
  }

  /**
   * Method that gets notified about changes in the collection of data points.
   * Just passes the modified state through.
   *
   * @param modified	whether the action modified the collection
   * @return		the same as the input
   */
  @Override
  protected boolean modifiedListener(boolean modified) {
    if (modified)
      invalidateMinMax();

    return modified;
  }

  /**
   * Invalidates the min/max X/Y members.
   */
  protected synchronized void invalidateMinMax() {
    m_MinX = null;
    m_MaxX = null;
    m_MinY = null;
    m_MaxY = null;
  }

  /**
   * Initializes the min/max X/Y points.
   */
  protected synchronized void validateMinMax() {
    if (m_MinX != null)
      return;

    for (Step step : this) {
      if (    (m_MaxX == null)
	   || (step.getX() > m_MaxX.getX()) )
	m_MaxX = step;
      if (    (m_MinX == null)
	   || (step.getX() < m_MinX.getX()))
	m_MinX = step;
      if (    (m_MaxY== null)
	   || (step.getY() > m_MaxY.getY()) )
	m_MaxY = step;
      if (    (m_MinY == null)
 	   || (step.getY() < m_MinY.getY()) )
	m_MinY = step;
    }
  }

  /**
   * Returns the step with the smallest X value.
   *
   * @return		the smallest X, null if no steps
   */
  public Step getMinX() {
    validateMinMax();
    return m_MinX;
  }

  /**
   * Returns the step with the largest X value.
   *
   * @return		the largest X, null if no steps
   */
  public Step getMaxX() {
    validateMinMax();
    return m_MaxX;
  }

  /**
   * Returns the step with the smallest Y value.
   *
   * @return		the smallest Y, null if no steps
   */
  public Step getMinY() {
    validateMinMax();
    return m_MinY;
  }

  /**
   * Returns the step with the largest Y value.
   *
   * @return		the largest Y, null if no steps
   */
  public Step getMaxY() {
    validateMinMax();
    return m_MaxY;
  }

  /**
   * Checks whether a width is set.
   *
   * @return		true if width is set
   */
  public boolean hasWidth() {
    return (m_Width != null);
  }

  /**
   * Sets the optional width of the area for the trail.
   *
   * @param value	the width, null to unset
   */
  public void setWidth(Float value) {
    m_Width = value;
  }

  /**
   * Returns the optional width of the area for the trail.
   *
   * @return		the width, null if not set
   */
  public Float getWidth() {
    return m_Width;
  }

  /**
   * Checks whether a height is set.
   *
   * @return		true if height is set
   */
  public boolean hasHeight() {
    return (m_Height != null);
  }

  /**
   * Sets the optional height of the area for the trail.
   *
   * @param value	the height, null to unset
   */
  public void setHeight(Float value) {
    m_Height = value;
  }

  /**
   * Returns the optional height of the area for the trail.
   *
   * @return		the height, null if not set
   */
  public Float getHeight() {
    return m_Height;
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
   * Returns the content as spreadsheet.
   *
   * @return		the content
   */
  @Override
  public SpreadSheet toSpreadSheet() {
    SpreadSheet		result;
    Row 		row;
    List<String> meta;

    result = new SpreadSheet();
    result.setName(getID());

    // header
    row = result.getHeaderRow();
    row.addCell("T").setContent("Timestamp");
    row.addCell("X").setContent("X");
    row.addCell("Y").setContent("Y");
    row.addCell("M").setContent("Meta-data");

    // data
    for (Step step: this) {
      row = result.addRow();
      row.addCell("T").setContent(new DateTime(step.getTimestamp()));
      row.addCell("X").setContent(step.getX());
      row.addCell("Y").setContent(step.getY());
      if (step.hasMetaData()) {
	meta = new ArrayList<>();
	for (String key: step.getMetaData().keySet())
	  meta.add(key + "=" + step.getMetaData().get(key));
	row.addCell("M").setContentAsString(OptionUtils.joinOptions(meta.toArray(new String[0])));
      }
    }

    return result;
  }

  /**
   * Returns a statistic object for this object.
   *
   * @return		statistics for this object
   */
  @Override
  public TrailStatistic toStatistic() {
    return new TrailStatistic(this);
  }

  /**
   * Returns a string representation of the sequence.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return "ID=" + getID() + ", width=" + (hasWidth() ? getWidth() : "-none") + ", height=" + (hasHeight() ? getHeight() : "-none") + ", #points=" + size();
  }
}
