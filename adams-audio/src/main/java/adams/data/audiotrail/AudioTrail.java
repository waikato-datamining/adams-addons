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
 * AudioTrail.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.data.audiotrail;

import adams.core.TimeMsec;
import adams.data.Notes;
import adams.data.NotesHandler;
import adams.data.container.AbstractDataContainer;
import adams.data.container.DataContainer;
import adams.data.container.DataPointComparator;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.data.statistics.InformativeStatisticSupporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Encapsulates a series of audio steps, i.e., a audio trail.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AudioTrail
  extends AbstractDataContainer<AudioStep>
  implements MutableReportHandler<Report>,
  NotesHandler, SpreadSheetSupporter, InformativeStatisticSupporter<AudioTrailStatistic>  {

  private static final long serialVersionUID = 8721248965909493612L;

  public static final String PREFIX_META = "Meta-";

  /** the attached report. */
  protected Report m_Report;

  /** the notes. */
  protected Notes m_Notes;

  /** the comparator to use. */
  protected static AudioStepComparator m_Comparator;

  /**
   * Initializes the trail.
   */
  public AudioTrail() {
    super();

    m_ID     = "" + new Date();
    m_Report = new Report();
    m_Notes  = new Notes();
    if (m_Comparator == null)
      m_Comparator = newComparator();
  }

  /**
   * Returns a new instance of the default comparator.
   *
   * @return		the comparator instance
   */
  @Override
  public AudioStepComparator newComparator() {
    return new AudioStepComparator();
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator in use
   */
  @Override
  public DataPointComparator<AudioStep> getComparator() {
    return m_Comparator;
  }

  /**
   * Returns a new instance of a DataContainer point.
   *
   * @return		the new DataContainer point
   */
  @Override
  public AudioStep newPoint() {
    return new AudioStep();
  }

  /**
   * Obtains the stored variables from the other data point, but not the
   * actual data points.
   *
   * @param other	the data point to get the values from
   */
  @Override
  public void assign(DataContainer<AudioStep> other) {
    AudioTrail trail;

    super.assign(other);

    trail = (AudioTrail) other;

    if (trail.hasReport())
      setReport(trail.getReport().getClone());
    else
      setReport(new Report());

    m_Notes = new Notes();
    m_Notes.mergeWith(trail.getNotes());
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
   * Returns the step associated with the given timestamp.
   *
   * @param timestamp 	the timestamp to get the step for
   * @return		the associated step or null if none available for the timestamp
   */
  public AudioStep getStep(Date timestamp) {
    AudioStep result;
    int		pos;

    result = null;
    pos    = Collections.binarySearch(m_Points, new AudioStep(timestamp), m_Comparator);
    if (pos >= 0)
      result = m_Points.get(pos);

    return result;
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
    HashSet<String>	keys;
    List<String>	keysSorted;

    // collect meta-data keys
    keys = new HashSet<>();
    for (AudioStep step: m_Points) {
      if (step.hasMetaData())
	keys.addAll(step.getMetaData().keySet());
    }
    keysSorted = new ArrayList<>(keys);
    Collections.sort(keysSorted);

    result = new DefaultSpreadSheet();
    result.setDataRowClass(DenseDataRow.class);
    result.setName(getID());

    // header
    row = result.getHeaderRow();
    row.addCell("T").setContent("Timestamp");
    for (String key: keysSorted)
      row.addCell("M-" + key).setContent(PREFIX_META + key);

    // data
    for (AudioStep step: this) {
      row = result.addRow();
      row.addCell("T").setContent(new TimeMsec(step.getTimestamp()));
      if (step.hasMetaData()) {
	for (String key: step.getMetaData().keySet())
	  row.addCell("M-" + key).setNative(step.getMetaData().get(key));
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
  public AudioTrailStatistic toStatistic() {
    return new AudioTrailStatistic(this);
  }

  /**
   * Returns a string representation of the sequence.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return "ID=" + getID() + ", #points=" + size();
  }
}
