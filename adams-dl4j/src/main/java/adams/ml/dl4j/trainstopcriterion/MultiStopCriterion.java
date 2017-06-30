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
 * MultiStopCriterion.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.trainstopcriterion;

import adams.flow.container.DL4JModelContainer;

/**
 <!-- globalinfo-start -->
 * Applies the specified criteria sequentially, combining the results according to the specified combination type.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-criterion &lt;adams.ml.dl4j.trainstopcriterion.AbstractTrainStopCriterion&gt; [-criterion ...] (property: criteria)
 * &nbsp;&nbsp;&nbsp;The criteria to apply and combine.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-combination-type &lt;AND|OR&gt; (property: combinationType)
 * &nbsp;&nbsp;&nbsp;How to combine the results of the criteria.
 * &nbsp;&nbsp;&nbsp;default: OR
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiStopCriterion
  extends AbstractTrainStopCriterion {

  private static final long serialVersionUID = 6975594226423139162L;

  /**
   * Defines how to combine the results from the base criteria.
   */
  public enum CombinationType {
    AND,
    OR,
  }

  /** the criteria. */
  protected AbstractTrainStopCriterion[] m_Criteria;

  /** the combination. */
  protected CombinationType m_CombinationType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Applies the specified criteria sequentially, combining the results "
	+ "according to the specified combination type.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "criterion", "criteria",
      new AbstractTrainStopCriterion[0]);

    m_OptionManager.add(
      "combination-type", "combinationType",
      CombinationType.OR);
  }

  /**
   * Sets the criteria to apply.
   *
   * @param value	the criteria
   */
  public void setCriteria(AbstractTrainStopCriterion[] value) {
    m_Criteria = value;
    reset();
  }

  /**
   * Returns the criteria to apply.
   *
   * @return  		the criteria
   */
  public AbstractTrainStopCriterion[] getCriteria() {
    return m_Criteria;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String criteriaTipText() {
    return "The criteria to apply and combine.";
  }

  /**
   * Sets how to combine the results of the criteria.
   *
   * @param value	the type
   */
  public void setCombinationType(CombinationType value) {
    m_CombinationType = value;
    reset();
  }

  /**
   * Returns how to combine the results of the criteria.
   *
   * @return  		the type
   */
  public CombinationType getCombinationType() {
    return m_CombinationType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String combinationTypeTipText() {
    return "How to combine the results of the criteria.";
  }

  /**
   * Returns whether a flow context is required or optional.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresFlowContext() {
    boolean	result;

    result = false;

    for (AbstractTrainStopCriterion c: m_Criteria)
      result = result || c.requiresFlowContext();

    return result;
  }

  /**
   * Performs the actual checking for stopping the training.
   *
   * @param cont	the container to use for stopping
   * @return		true if to stop training
   */
  @Override
  protected boolean doCheckStopping(DL4JModelContainer cont) {
    boolean	result;
    boolean	sub;
    int		i;

    result = false;

    for (i = 0; i < m_Criteria.length; i++) {
      sub = m_Criteria[i].checkStopping(cont);
      if (i == 0) {
	result = sub;
      }
      else {
	switch (m_CombinationType) {
	  case AND:
	    result = result && sub;
	    break;
	  case OR:
	    result = result || sub;
	    break;
	  default:
	    throw new IllegalStateException("Unhandled combination type: " + m_CombinationType);
	}
      }
    }

    return result;
  }
}
