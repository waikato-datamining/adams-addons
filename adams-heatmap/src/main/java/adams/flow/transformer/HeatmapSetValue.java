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
 * HeatmapSetValue.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.data.heatmap.Heatmap;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Sets the value of the specified positions in a heatmap.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.heatmap.Heatmap<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.heatmap.Heatmap<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: HeatmapSetValue
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the heatmap is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-row &lt;adams.core.Range&gt; (property: row)
 * &nbsp;&nbsp;&nbsp;The row(s) of the position(s) to set.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-col &lt;adams.core.Range&gt; (property: column)
 * &nbsp;&nbsp;&nbsp;The column(s) of the position(s) to set
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-value &lt;java.lang.Double&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value to set in the position(s).
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapSetValue
  extends AbstractInPlaceHeatmapTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -6250232085303020849L;

  /** the row of the cell to obtain. */
  protected Range m_Row;

  /** the column of the cell to obtain. */
  protected Range m_Column;

  /** the value to set. */
  protected Double m_Value;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sets the value of the specified positions in a heatmap.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "row", "row",
      new Range("1"));

    m_OptionManager.add(
      "col", "column",
      new Range("1"));

    m_OptionManager.add(
      "value", "value",
      0.0);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Row    = new Range();
    m_Column = new Range();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "row", m_Row, "row: ");
    result += QuickInfoHelper.toString(this, "column", m_Column, "/col: ");
    result += QuickInfoHelper.toString(this, "value", m_Value, ", value: ");

    return result;
  }

  /**
   * Sets the row(s) of the position(s).
   *
   * @param value	the row(s)
   */
  public void setRow(Range value) {
    m_Row = value;
    reset();
  }

  /**
   * Returns the row(s) of the position(s).
   *
   * @return		the row(s)
   */
  public Range getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowTipText() {
    return "The row(s) of the position(s) to set.";
  }

  /**
   * Sets the column(s) of the position(s).
   *
   * @param value	the column(s)
   */
  public void setColumn(Range value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column(s) of the position(s).
   *
   * @return		the column(s)
   */
  public Range getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The column(s) of the position(s) to set";
  }

  /**
   * Sets the value to set in the position(s).
   *
   * @param value	the value
   */
  public void setValue(Double value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value to set in the position(s).
   *
   * @return		the value
   */
  public Double getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The value to set in the position(s).";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Heatmap     map;
    int[]	rows;
    int[]	cols;

    result = null;

    map = ((Heatmap) m_InputToken.getPayload());
    if (!m_NoCopy)
      map = map.getClone();
    m_Row.setMax(map.getHeight());
    m_Column.setMax(map.getWidth());

    rows = m_Row.getIntIndices();
    cols = m_Column.getIntIndices();
    if (rows.length == 0) {
      result = "No row(s) selected?";
    }
    else if (cols.length == 0) {
      result = "No column(s) selected?";
    }
    else {
      for (int r: rows) {
	for (int c: cols)
          map.set(r, c, m_Value);
      }
      m_OutputToken = new Token(map);
    }

    return result;
  }
}
