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
 * HeatmapStatistic.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.heatmap;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.InformativeStatistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Generates some statistics for a heatmap.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapStatistic
  implements InformativeStatistic {

  /** the width. */
  public static String WIDTH = "Map width";

  /** the height. */
  public static String HEIGHT = "Map height";

  /** the minimum value. */
  public static String MINIMUM = "Minimum";

  /** the maximum value. */
  public static String MAXIMUM = "Maximum";

  /** the zeroes count. */
  public static String COUNT_ZEROES = "Zeroes count";

  /** the non-zeroes count. */
  public static String COUNT_NONZEROES = "Non-zeroes count";

  /** the missing value count. */
  public static String COUNT_MISSING = "Missing values count";

  /** the statistics. */
  protected Hashtable<String,Double> m_Statistics;

  /** the heatmap to create the statistics for. */
  protected Heatmap m_Heatmap;

  /**
   * Initializes the statistics.
   */
  public HeatmapStatistic() {
    super();

    m_Statistics = new Hashtable<String,Double>();
    m_Heatmap    = null;
  }

  /**
   * Initializes the statistics with the specified heatmap.
   *
   * @param map		the heatmap to generate the stats for
   */
  public HeatmapStatistic(Heatmap map) {
    this();
    setHeatmap(map);
  }

  /**
   * Sets the heatmap to generate the statistics for.
   *
   * @param value	the heatmap to use
   */
  public void setHeatmap(Heatmap value) {
    m_Heatmap = value;
    calculate();
  }

  /**
   * Returns the underlying heatmap.
   *
   * @return		the heatmap, null if none set
   */
  public Heatmap getHeatmap() {
    return m_Heatmap;
  }

  /**
   * Generates the statistics.
   */
  protected void calculate() {
    double	zeroes;
    double	nonZeroes;
    double	missing;
    int		i;

    m_Statistics.clear();
    m_Statistics.put(HEIGHT, 0.0);
    m_Statistics.put(WIDTH, 0.0);
    m_Statistics.put(MINIMUM, 0.0);
    m_Statistics.put(MAXIMUM, 0.0);
    m_Statistics.put(COUNT_ZEROES, 0.0);
    m_Statistics.put(COUNT_NONZEROES, 0.0);
    m_Statistics.put(COUNT_MISSING, 0.0);

    if (m_Heatmap == null)
      return;

    zeroes    = 0.0;
    nonZeroes = 0.0;
    missing   = 0.0;
    for (i = 0; i < m_Heatmap.size(); i++) {
      if (m_Heatmap.isMissing(i))
	missing++;
      else if (m_Heatmap.get(i) == 0.0)
	zeroes++;
      else
	nonZeroes++;
    }

    m_Statistics.put(HEIGHT, (double) m_Heatmap.getHeight());
    m_Statistics.put(WIDTH, (double) m_Heatmap.getWidth());
    m_Statistics.put(MINIMUM, m_Heatmap.getMin());
    m_Statistics.put(MAXIMUM, m_Heatmap.getMax());
    m_Statistics.put(COUNT_ZEROES, zeroes);
    m_Statistics.put(COUNT_NONZEROES, nonZeroes);
    m_Statistics.put(COUNT_MISSING, missing);
  }

  /**
   * Returns a description for this statistic.
   *
   * @return		the description
   */
  public String getStatisticDescription() {
    if (m_Heatmap == null)
      return "Statistics";
    else
      return m_Heatmap.getID();
  }

  /**
   * Returns all the names of the available statistical values.
   *
   * @return		the enumeration of names
   */
  public Iterator<String> statisticNames() {
    List<String>	result;

    result = new ArrayList<String>(m_Statistics.keySet());
    Collections.sort(result);

    return result.iterator();
  }

  /**
   * Returns the statistical value for the given statistic name.
   *
   * @param name	the name of the statistical value
   * @return		the corresponding value
   */
  public double getStatistic(String name) {
    return m_Statistics.get(name).doubleValue();
  }

  /**
   * Returns a string representation of the statistic.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    StringBuilder	result;
    Iterator<String>	names;
    String		name;

    result = new StringBuilder();
    result.append(getStatisticDescription());
    result.append("\n");

    names = statisticNames();
    while (names.hasNext()) {
      name = names.next();
      result.append(name + ": " + getStatistic(name));
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Returns the content as spreadsheet.
   *
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    SpreadSheet		result;
    Row row;
    Iterator<String>	names;
    String		name;

    result = new SpreadSheet();

    // header
    row = result.getHeaderRow();
    row.addCell("N").setContentAsString("Name");
    row.addCell("V").setContentAsString("Value");

    // data
    names = statisticNames();
    while (names.hasNext()) {
      name = names.next();
      row  = result.addRow();
      row.addCell("N").setContentAsString(name);
      row.addCell("V").setContent(getStatistic(name));
    }

    return result;
  }
}
