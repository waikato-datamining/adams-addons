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
 * HistogramPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap;

import adams.data.heatmap.Heatmap;
import adams.data.statistics.AbstractArrayStatistic.StatisticContainer;
import adams.data.statistics.ArrayHistogram;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.gui.visualization.core.AbstractHistogramPanel;

/**
 * Generates and displays histogram from a heatmap.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HistogramPanel
  extends AbstractHistogramPanel<Heatmap> {

  /** for serialization. */
  private static final long serialVersionUID = -8621818594275641231L;

  /** for generating the histogram. */
  protected ArrayHistogram m_ArrayHistogram;

  /** whether to skip missing values. */
  protected boolean m_SkipMissing;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ArrayHistogram = new ArrayHistogram();
    m_SkipMissing    = true;   // TODO let user change this?
  }

  /**
   * Sets the setup for calculating the histogram.
   *
   * @param value	the setup to use
   */
  public void setArrayHistogram(ArrayHistogram value) {
    m_ArrayHistogram = value;
    update();
  }

  /**
   * Returns the current setup for calculating the histogram.
   *
   * @return		the current setup
   */
  public ArrayHistogram getArrayHistogram() {
    return m_ArrayHistogram;
  }

  /**
   * Sets whether to exclude missing values from the histogram calculation.
   *
   * @param value 	true if to skip
   */
  public void setSkipMissing(boolean value) {
    m_SkipMissing = value;
    update();
  }

  /**
   * Returns whether to exclude missing values from the histogram calculation.
   *
   * @return 		true if to skip
   */
  public boolean getSkipMissing() {
    return m_SkipMissing;
  }

  /**
   * Generates the sequence(s) from the data.
   *
   * @return		the generated sequence(s)
   */
  @Override
  protected SequencePlotSequence[] createSequences() {
    SequencePlotSequence[]	result;
    Double[]			array;
    StatisticContainer 		cont;
    int				i;

    result = new SequencePlotSequence[1];
    array  = m_Data.toDoubleArray(m_SkipMissing);
    m_ArrayHistogram.clear();
    m_ArrayHistogram.add(array);
    cont = m_ArrayHistogram.calculate();
    result[0] = new SequencePlotSequence();
    result[0].setID(m_Data.getID());
    for (i = 0; i < cont.getColumnCount(); i++)
      result[0].add(new SequencePlotPoint(i, ((Number) cont.getCell(0, i)).doubleValue()));

    return result;
  }
}
