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
 * AbstractCriterionEnhancer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.trainstopcriterion;

/**
 * Ancestor for criteria that enhance a base criterion.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCriterionEnhancer
  extends AbstractTrainStopCriterion {

  private static final long serialVersionUID = 6975594226423139162L;

  /** the based criterion. */
  protected AbstractTrainStopCriterion m_BaseCriterion;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "base-criterion", "baseCriterion",
      getDefaultBaseCriterion());
  }

  /**
   * Returns the default base criterion.
   *
   * @return		the criterion
   */
  protected AbstractTrainStopCriterion getDefaultBaseCriterion() {
    return new MaxEpoch();
  }

  /**
   * Sets the base criterion to use.
   *
   * @param value	the criterion
   */
  public void setBaseCriterion(AbstractTrainStopCriterion value) {
    m_BaseCriterion = value;
    reset();
  }

  /**
   * Returns the base criterion to use.
   *
   * @return  		the criterion
   */
  public AbstractTrainStopCriterion getBaseCriterion() {
    return m_BaseCriterion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String baseCriterionTipText();

  /**
   * Returns whether a flow context is required or optional.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresFlowContext() {
    return m_BaseCriterion.requiresFlowContext();
  }

  /**
   * For initializing the scheme.
   */
  public void start() {
    super.start();
    if (m_BaseCriterion != null)
      m_BaseCriterion.start();
  }
}
