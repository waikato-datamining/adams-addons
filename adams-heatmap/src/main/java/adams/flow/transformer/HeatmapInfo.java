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
 * HeatmapInfo.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.heatmap.Heatmap;
import adams.data.heatmap.HeatmapStatistic;
import adams.flow.core.DataInfoActor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 <!-- globalinfo-start -->
 * Outputs statistics of a heatmap object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.heatmap.Heatmap<br>
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
 * &nbsp;&nbsp;&nbsp;default: HeatmapInfo
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the info items get output as array rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-type &lt;WIDTH|HEIGHT|MIN|MAX|ZEROES|NON_ZEROES|MISSING|VALUES&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: WIDTH
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapInfo
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
    /** the width. */
    WIDTH,
    /** the height. */
    HEIGHT,
    /** the minimum value. */
    MIN,
    /** the maximum value. */
    MAX,
    /** the number of zero values. */
    ZEROES,
    /** the number of non-zero values. */
    NON_ZEROES,
    /** the number of missing values. */
    MISSING,
    /** all (unique) values. */
    VALUES
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
    return "Outputs statistics of a heatmap object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      InfoType.WIDTH);
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
    return "If enabled, the info items get output as array rather than one-by-one.";
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
    return "The type of information to generate.";
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    switch (m_Type) {
      case WIDTH:
      case HEIGHT:
      case ZEROES:
      case NON_ZEROES:
      case MISSING:
	return Integer.class;

      case MIN:
      case MAX:
      case VALUES:
	return Double.class;

      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.heatmap.Heatmap.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
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
    HashSet<Double> 	values;
    HeatmapStatistic	stats;

    result = null;

    m_Queue = new ArrayList();
    map     = (Heatmap) m_InputToken.getPayload();

    switch (m_Type) {
      case WIDTH:
	m_Queue.add(map.getWidth());
	break;
      
      case HEIGHT:
	m_Queue.add(map.getHeight());
	break;

      case MIN:
	m_Queue.add(map.getMin());
	break;

      case MAX:
	m_Queue.add(map.getMax());
	break;

      case ZEROES:
	stats = map.toStatistic();
	m_Queue.add(stats.getStatistic(HeatmapStatistic.COUNT_ZEROES));
	break;

      case NON_ZEROES:
	stats = map.toStatistic();
	m_Queue.add(stats.getStatistic(HeatmapStatistic.COUNT_NONZEROES));
	break;

      case MISSING:
	stats = map.toStatistic();
	m_Queue.add(stats.getStatistic(HeatmapStatistic.COUNT_MISSING));
	break;

      case VALUES:
	values = new HashSet<>(Arrays.asList(map.toDoubleArray()));
	m_Queue.addAll(values);
	Collections.sort(m_Queue);
	break;

      default:
	result = "Unhandled info type: " + m_Type;
    }

    return result;
  }
}
