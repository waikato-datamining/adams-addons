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
 * AudioAnnotationsStatistic.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.data.audioannotations;

import adams.data.statistics.AbstractDataStatistic;
import adams.data.statistics.StatUtils;

import java.util.Date;
import java.util.List;

/**
 * Statistical information specific to a audio annotations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AudioAnnotationsStatistic
  extends AbstractDataStatistic<AudioAnnotations> {

  /** for serialization. */
  private static final long serialVersionUID = -2482267274581297567L;

  public static final String MEDIAN_DELTA_TIMESTAMP = "median delta timestamp";
  public static final String STDEV_DELTA_TIMESTAMP = "stdev delta timestampX";
  public static final String MEAN_DELTA_TIMESTAMP = "mean delta timestamp";
  public static final String MAX_DELTA_TIMESTAMP = "max delta timestamp";
  public static final String MIN_DELTA_TIMESTAMP = "min delta timestamp";
  public static final String LAST_TIMESTAMP = "Last timestamp (msec)";
  public static final String FIRST_TIMESTAMP = "First timestamp (msec)";
  public static final String NUMBER_OF_POINTS = "Number of points";

  /**
   * Initializes the statistic.
   */
  public AudioAnnotationsStatistic() {
    super();
  }

  /**
   * Initializes the statistic.
   *
   * @param data	the trail to generate the statistics for
   */
  public AudioAnnotationsStatistic(AudioAnnotations data) {
    super(data);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Calculates a view statistics for an audio trail.";
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    m_Data = null;
  }

  /**
   * Sets the data to use as basis for the calculations.
   *
   * @param value	the trail to use, can be null
   */
  @Override
  public void setData(AudioAnnotations value) {
    m_Calculated = false;
    m_Data       = value;
  }

  /**
   * Returns the currently stored trail.
   *
   * @return		the trail, can be null
   */
  @Override
  public AudioAnnotations getData() {
    return m_Data;
  }

  /**
   * Returns a description for this statistic, i.e., trail ID.
   *
   * @return		the description
   */
  public String getStatisticDescription() {
    return m_Data.getID();
  }

  /**
   * calculates the statistics.
   */
  @Override
  protected void calculate() {
    List<AudioAnnotation> 		points;
    int			i;
    Long[] 		deltaTimestamp;
    Date 		firstTimestamp;
    Date 		lastTimestamp;

    super.calculate();

    if (m_Data == null)
      return;

    points         = m_Data.toList();
    firstTimestamp = new Date(0);
    lastTimestamp  = new Date(0);
    deltaTimestamp = new Long[0];

    // gather statistics
    if (points.size() > 0) {
      firstTimestamp = points.get(0).getTimestamp();
      if (points.size() > 1)
	lastTimestamp = points.get(points.size() - 1).getTimestamp();
      deltaTimestamp = new Long[points.size() - 1];
      for (i = 0; i < points.size(); i++) {
	if (i > 0) {
	  deltaTimestamp[i - 1] = points.get(i).getTimestamp().getTime() - points.get(i - 1).getTimestamp().getTime();
	}
      }
    }

    add(NUMBER_OF_POINTS, points.size());
    add(FIRST_TIMESTAMP, firstTimestamp.getTime());
    add(LAST_TIMESTAMP, lastTimestamp.getTime());
    add(MIN_DELTA_TIMESTAMP, numberToDouble(StatUtils.min(deltaTimestamp)));
    add(MAX_DELTA_TIMESTAMP, numberToDouble(StatUtils.max(deltaTimestamp)));
    add(MEAN_DELTA_TIMESTAMP, numberToDouble(StatUtils.mean(deltaTimestamp)));
    add(STDEV_DELTA_TIMESTAMP, numberToDouble(StatUtils.stddev(deltaTimestamp, true)));
    add(MEDIAN_DELTA_TIMESTAMP, numberToDouble(StatUtils.median(deltaTimestamp)));
  }
}
