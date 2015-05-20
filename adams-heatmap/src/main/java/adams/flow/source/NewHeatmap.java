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
 * NewHeatmap.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.data.heatmap.Heatmap;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Creates a new heatmap of the specified size and fills it with the initial value.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.heatmap.Heatmap<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: NewHeatmap
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
 * <pre>-rows &lt;int&gt; (property: rows)
 * &nbsp;&nbsp;&nbsp;The number of rows.
 * &nbsp;&nbsp;&nbsp;default: 240
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-cols &lt;int&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The number of columns.
 * &nbsp;&nbsp;&nbsp;default: 320
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-value &lt;double&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The initial value to set.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 * <pre>-id &lt;java.lang.String&gt; (property: ID)
 * &nbsp;&nbsp;&nbsp;The ID of the heatmap.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NewHeatmap
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 393593515796397502L;

  /** the number of rows. */
  protected int m_Rows;

  /** the number of columns. */
  protected int m_Columns;

  /** the initial value. */
  protected double m_Value;

  /** the ID. */
  protected String m_ID;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates a new heatmap of the specified size and fills it with the initial value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "rows", "rows",
      240, 1, null);

    m_OptionManager.add(
      "cols", "columns",
      320, 1, null);

    m_OptionManager.add(
      "value", "value",
      0.0);

    m_OptionManager.add(
      "id", "ID",
      "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "rows", m_Rows, "rows: ");
    result += QuickInfoHelper.toString(this, "columns", m_Columns, ", cols: ");
    result += QuickInfoHelper.toString(this, "value", m_Value, ", value: ");
    result += QuickInfoHelper.toString(this, "ID", "'" + m_ID + "'", ", ID: ");

    return result;
  }

  /**
   * Sets the number of rows.
   *
   * @param value	the rows
   */
  public void setRows(int value) {
    m_Rows = value;
    reset();
  }

  /**
   * Returns the number of rows.
   *
   * @return		the rows
   */
  public int getRows() {
    return m_Rows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowsTipText() {
    return "The number of rows.";
  }

  /**
   * Sets the number of columns.
   *
   * @param value	the columns
   */
  public void setColumns(int value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the number of columns.
   *
   * @return		the columns
   */
  public int getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnsTipText() {
    return "The number of columns.";
  }

  /**
   * Sets the initial value.
   *
   * @param value	the initial value
   */
  public void setValue(double value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the initial value.
   *
   * @return		the initial value
   */
  public double getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The initial value to set.";
  }

  /**
   * Sets the ID.
   *
   * @param value	the ID
   */
  public void setID(String value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the ID.
   *
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String IDTipText() {
    return "The ID of the heatmap.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Heatmap.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Heatmap		map;
    int			i;

    result = null;

    try {
      map = new Heatmap(m_Rows, m_Columns);
      map.setID(m_ID);
      for (i = 0; i < map.size(); i++)
	map.set(i, m_Value);
      m_OutputToken = new Token(map);
    }
    catch (Exception e) {
      result = handleException("Failed to initialize heatmap", e);
    }

    return result;
  }
}
