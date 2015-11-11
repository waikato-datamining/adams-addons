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
 * MekaResultValues.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import meka.core.Result;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.MekaResultContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Extracts values from a meka.core.Result object and generates a spreadsheet.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;meka.core.Result<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.MekaResultContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.MekaResultContainer: Result, Model
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
 * &nbsp;&nbsp;&nbsp;default: MekaResultValues
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
 * <pre>-info-value &lt;adams.core.base.BaseString&gt; [-info-value ...] (property: infoValues)
 * &nbsp;&nbsp;&nbsp;The info values to retrieve.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-metric-value &lt;adams.core.base.BaseString&gt; [-metric-value ...] (property: metricValues)
 * &nbsp;&nbsp;&nbsp;The metric values to retrieve.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6830 $
 */
public class MekaResultValues
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8082115424369061977L;

  /** the info values to extract. */
  protected BaseString[] m_InfoValues;

  /** the metric values to extract. */
  protected BaseString[] m_MetricValues;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Extracts values from a " + Result.class.getName() + " object and "
        + "generates a spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "info-value", "infoValues",
	    new BaseString[0]);

    m_OptionManager.add(
	    "metric-value", "metricValues",
	    new BaseString[0]);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "infoValues", (m_InfoValues.length == 0 ?  "-none-" : Utils.flatten(m_InfoValues, ", ")), "info: ");
    result += QuickInfoHelper.toString(this, "metricValues", (m_MetricValues.length == 0 ?  "-none-" : Utils.flatten(m_MetricValues, ", ")), ", metric: ");

    return result;
  }

  /**
   * Sets the info values to retrieve.
   *
   * @param value	the values
   */
  public void setInfoValues(BaseString[] value) {
    m_InfoValues = value;
    reset();
  }

  /**
   * Returns the info values to retrieve.
   *
   * @return		the values
   */
  public BaseString[] getInfoValues() {
    return m_InfoValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String infoValuesTipText() {
    return "The info values to retrieve.";
  }

  /**
   * Sets the metric values to retrieve.
   *
   * @param value	the values
   */
  public void setMetricValues(BaseString[] value) {
    m_MetricValues = value;
    reset();
  }

  /**
   * Returns the metric values to retrieve.
   *
   * @return		the values
   */
  public BaseString[] getMetricValues() {
    return m_MetricValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metricValuesTipText() {
    return "The metric values to retrieve.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->meka.core.Result.class, adams.flow.container.MekaResultContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Result.class, MekaResultContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.spreadsheet.SpreadSheet.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Result		res;
    SpreadSheet		sheet;
    Row			row;

    result = null;

    if (m_InputToken.getPayload() instanceof MekaResultContainer)
      res = (Result) ((MekaResultContainer) m_InputToken.getPayload()).getValue(MekaResultContainer.VALUE_RESULT);
    else
      res = (Result) m_InputToken.getPayload();

    sheet = new SpreadSheet();
    
    // header
    row = sheet.getHeaderRow();
    row.addCell("K").setContent("Key");
    row.addCell("V").setContent("Value");
    
    // data
    for (BaseString value: m_InfoValues) {
      row = sheet.addRow();
      row.addCell("K").setContent(value.getValue());
      row.addCell("V").setContent(res.getInfo(value.getValue()));
    }
    for (BaseString value: m_MetricValues) {
      row = sheet.addRow();
      row.addCell("K").setContent(value.getValue());
      row.addCell("V").setNative(res.output.get(value.getValue()));
    }
    
    m_OutputToken = new Token(sheet);

    return result;
  }
}
