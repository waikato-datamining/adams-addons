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
 * DL4JDatasetInfo.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.DataInfoActor;
import org.nd4j.linalg.dataset.DataSet;

/**
 <!-- globalinfo-start -->
 * Outputs information about a DataSet object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;org.nd4j.linalg.dataset.DataSet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
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
 * &nbsp;&nbsp;&nbsp;default: DL4JDatasetInfo
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
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the values one-by-one or as array (counts or distributions 
 * &nbsp;&nbsp;&nbsp;are always output as array).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-type &lt;NUM_INPUTS|NUM_OUTCOMES|NUM_EXAMPLES|COLUMN_NAMES|LABELS&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate; NB some of the types are only available 
 * &nbsp;&nbsp;&nbsp;for numeric or nominal attributes.
 * &nbsp;&nbsp;&nbsp;default: NUM_EXAMPLES
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JDatasetInfo
  extends AbstractArrayProvider
  implements DataInfoActor {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /**
   * The type of information to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum InfoType {
    /** the number of inputs. */
    NUM_INPUTS,
    /** the number of outcomes. */
    NUM_OUTCOMES,
    /** the number of examples. */
    NUM_EXAMPLES,
    /** the column names. */
    COLUMN_NAMES,
    /** the labels. */
    LABELS,
  }

  /** the type of information to generate. */
  protected InfoType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Outputs information about a DataSet object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      InfoType.NUM_EXAMPLES);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the values one-by-one or as array (counts or distributions are always output as array).";
  }

  /**
   * Sets the type of information to generate.
   *
   * @param value	the type
   */
  public void setType(InfoType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of information to generate.
   *
   * @return		the type
   */
  public InfoType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of information to generate; NB some of the types are only available for numeric or nominal attributes.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->org.nd4j.linalg.dataset.DataSet.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{DataSet.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    switch (m_Type) {
      case NUM_EXAMPLES:
      case NUM_INPUTS:
      case NUM_OUTCOMES:
        return Integer.class;

      case COLUMN_NAMES:
      case LABELS:
	return String.class;

      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    DataSet 		data;

    result = null;

    data = (DataSet) m_InputToken.getPayload();

    m_Queue.clear();

    switch (m_Type) {
      case NUM_EXAMPLES:
	m_Queue.add(data.numExamples());
	break;

      case NUM_INPUTS:
	m_Queue.add(data.numInputs());
	break;

      case NUM_OUTCOMES:
	m_Queue.add(data.numOutcomes());
	break;

      case COLUMN_NAMES:
	m_Queue.addAll(data.getColumnNames());
	break;

      case LABELS:
	m_Queue.addAll(data.getLabelNames());
	break;

      default:
	result = "Unhandled info type: " + m_Type;
    }

    return result;
  }
}
