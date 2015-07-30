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
 * Trail.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.trail;

import adams.core.DateTimeMsec;
import adams.core.option.OptionUtils;
import adams.data.Notes;
import adams.data.NotesHandler;
import adams.data.container.AbstractDataContainer;
import adams.data.container.DataContainer;
import adams.data.container.DataPointComparator;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.spreadsheet.DenseFloatDataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.data.statistics.InformativeStatisticSupporter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
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

  /** the report field for the width. */
  public final static String FIELD_WIDTH = "Trail.Width";

  /** the report field for the height. */
  public final static String FIELD_HEIGHT = "Trail.Height";

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

  /** the background image. */
  protected BufferedImage m_Background;

  /** the comparator to use. */
  protected static StepComparator m_Comparator;

  /**
   * Initializes the trail.
   */
  public Trail() {
    super();

    m_ID         = "" + new Date();
    m_Report     = new Report();
    m_Notes      = new Notes();
    m_Background = null;
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
    return (hasReport() && getReport().hasValue(FIELD_WIDTH));
  }

  /**
   * Sets the optional width of the area for the trail.
   *
   * @param value	the width, null to unset
   */
  public void setWidth(Float value) {
    if (hasReport()) {
      if (value == null)
	getReport().removeValue(new Field(FIELD_WIDTH, DataType.NUMERIC));
      else
	getReport().setNumericValue(FIELD_WIDTH, value.doubleValue());
    }
  }

  /**
   * Returns the optional width of the area for the trail.
   *
   * @return		the width, null if not set
   */
  public Float getWidth() {
    if (hasWidth())
      return getReport().getDoubleValue(FIELD_WIDTH).floatValue();
    else
      return null;
  }

  /**
   * Checks whether a height is set.
   *
   * @return		true if height is set
   */
  public boolean hasHeight() {
    return (hasReport() && getReport().hasValue(FIELD_HEIGHT));
  }

  /**
   * Sets the optional height of the area for the trail.
   *
   * @param value	the height, null to unset
   */
  public void setHeight(Float value) {
    if (hasReport()) {
      if (value == null)
	getReport().removeValue(new Field(FIELD_HEIGHT, DataType.NUMERIC));
      else
	getReport().setNumericValue(FIELD_HEIGHT, value.doubleValue());
    }
  }

  /**
   * Returns the optional height of the area for the trail.
   *
   * @return		the height, null if not set
   */
  public Float getHeight() {
    if (hasWidth())
      return getReport().getDoubleValue(FIELD_HEIGHT).floatValue();
    else
      return null;
  }

  /**
   * Checks whether a background is set.
   *
   * @return		true if background is set
   */
  public boolean hasBackground() {
    return (m_Background != null);
  }

  /**
   * Sets the optional background image.
   *
   * @param value	the background, null to unset
   */
  public void setBackground(BufferedImage value) {
    m_Background = value;
  }

  /**
   * Returns the optional background image.
   *
   * @return		the background, null if not set
   */
  public BufferedImage getBackground() {
    return m_Background;
  }

  /**
   * Creates a new background image with white as background color.
   * Uses either the width/height parameters stored in the report or if these
   * are not present, ensures that the trail fits onto the image.
   *
   * @see		#newBackground(Color)
   */
  public void newBackground() {
    newBackground(Color.WHITE);
  }

  /**
   * Creates a new background image.
   * Uses either the width/height parameters stored in the report or if these
   * are not present, ensures that the trail fits onto the image.
   *
   * @param bgcolor	the background color
   * @see 		BufferedImage#TYPE_INT_ARGB
   */
  public void newBackground(Color bgcolor) {
    BufferedImage 	image;
    int			width;
    int			height;
    Graphics 		g;

    width  = getWidth().intValue();
    if (width <= 0)
      width = (int) getMaxX().getX() + 10;
    height = getHeight().intValue();
    if (height <= 0)
      height = (int) getMaxY().getY() + 10;
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    g     = image.getGraphics();
    g.setColor(bgcolor);
    g.fillRect(0, 0, image.getWidth(), image.getHeight());
    g.dispose();

    setBackground(image);
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
    if (m_Report != null) {
      m_Report.addField(new Field(FIELD_WIDTH, DataType.NUMERIC));
      m_Report.addField(new Field(FIELD_HEIGHT, DataType.NUMERIC));
    }
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
    result.setDataRowClass(DenseFloatDataRow.class);
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
      row.addCell("T").setContent(new DateTimeMsec(step.getTimestamp()));
      row.addCell("X").setContent(step.getX());
      row.addCell("Y").setContent(step.getY());
      if (step.hasMetaData()) {
	meta = new ArrayList<>();
	for (String key: step.getMetaData().keySet())
	  meta.add(key + "=" + step.getMetaData().get(key));
	row.addCell("M").setContentAsString(OptionUtils.joinOptions(meta.toArray(new String[meta.size()])));
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
