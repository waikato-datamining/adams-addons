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
 * DelayedCriterion.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.trainstopcriterion;

import adams.core.MessageCollection;
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
 * <pre>-base-criterion &lt;adams.ml.dl4j.trainstopcriterion.AbstractTrainStopCriterion&gt; (property: baseCriterion)
 * &nbsp;&nbsp;&nbsp;The criterion to apply once the delay one has triggered.
 * &nbsp;&nbsp;&nbsp;default: adams.ml.dl4j.trainstopcriterion.MaxEpoch
 * </pre>
 * 
 * <pre>-delay-criterion &lt;adams.ml.dl4j.trainstopcriterion.AbstractTrainStopCriterion&gt; (property: delayCriterion)
 * &nbsp;&nbsp;&nbsp;The criterion that determines the delay, ie once this criterion has triggered,
 * &nbsp;&nbsp;&nbsp; the base criterion gets applied; eg adams.ml.dl4j.trainstopcriterion.MaxEpoch 
 * &nbsp;&nbsp;&nbsp;is used to execute a number iterations before checking whether a certain 
 * &nbsp;&nbsp;&nbsp;statistic goes up again using adams.ml.dl4j.trainstopcriterion.Statistic.
 * &nbsp;&nbsp;&nbsp;default: adams.ml.dl4j.trainstopcriterion.Statistic
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DelayedCriterion
  extends AbstractTrainStopCriterionEnhancer {

  private static final long serialVersionUID = 6975594226423139162L;

  /** the delay criterion. */
  protected AbstractTrainStopCriterion m_DelayCriterion;

  /** whether the delay criterion has triggered. */
  protected boolean m_Triggered;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "The base criterion only gets applied after the delay criterion triggered.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "delay-criterion", "delayCriterion",
      new MaxEpoch());
  }

  /**
   * Returns the default base criterion.
   *
   * @return		the criterion
   */
  @Override
  protected AbstractTrainStopCriterion getDefaultBaseCriterion() {
    return new Statistic();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String baseCriterionTipText() {
    return "The criterion to apply once the delay one has triggered.";
  }

  /**
   * Sets the criterion to use for delaying the base criterion. Only once the
   * delay criterion has triggered.
   *
   * @param value	the criterion
   */
  public void setDelayCriterion(AbstractTrainStopCriterion value) {
    m_DelayCriterion = value;
    reset();
  }

  /**
   * Returns the criterion to use for delaying the base criterion. Only once the
   * delay criterion has triggered.
   *
   * @return  		the criterion
   */
  public AbstractTrainStopCriterion getDelayCriterion() {
    return m_DelayCriterion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String delayCriterionTipText() {
    return
      "The criterion that determines the delay, ie once this criterion "
        + "has triggered, the base criterion gets applied; "
        + "eg " + MaxEpoch.class.getName() + " is used to execute a number "
        + "iterations before checking whether a certain statistic goes up "
        + "again using " + Statistic.class.getName() + ".";
  }

  /**
   * Returns whether a flow context is required or optional.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresFlowContext() {
    return super.requiresFlowContext() || m_DelayCriterion.requiresFlowContext();
  }

  /**
   * For initializing the scheme.
   */
  public void start() {
    super.start();
    m_Triggered = false;
    if (m_DelayCriterion != null)
      m_DelayCriterion.start();
  }

  /**
   * Performs the actual checking for stopping the training.
   *
   * @param cont	the container to use for stopping
   * @param triggers	for storing trigger messages
   * @return		true if to stop training
   */
  @Override
  protected boolean doCheckStopping(DL4JModelContainer cont, MessageCollection triggers) {
    boolean	result;

    result = false;

    if (!m_Triggered) {
      m_Triggered = m_DelayCriterion.checkStopping(cont, new MessageCollection());
      if (m_Triggered && isLoggingEnabled())
	getLogger().info("Delay criterion triggered: " + m_DelayCriterion);
    }

    if (m_Triggered)
      result = m_BaseCriterion.checkStopping(cont, triggers);

    return result;
  }
}
