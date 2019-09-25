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
 * Switch.java
 * Copyright (C) 2014-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.IndexedBooleanCondition;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.standalone.Rat;

/**
 <!-- globalinfo-start -->
 * Forwards the input data to the sub-branch of the condition that evaluates to 'true'.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-condition &lt;adams.flow.condition.bool.BooleanCondition&gt; [-condition ...] (property: conditions)
 * &nbsp;&nbsp;&nbsp;The switch conditions to evaluate - the first condition that evaluates to 
 * &nbsp;&nbsp;&nbsp;'true' triggers the execution of the corresponding 'case' actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-case &lt;adams.flow.standalone.rats.RatOutput&gt; [-case ...] (property: cases)
 * &nbsp;&nbsp;&nbsp;The transmitters to send the data to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Switch
  extends AbstractRatOutput {

  /** for serialization. */
  private static final long serialVersionUID = -3300963022239958581L;

  /** the "conditions" for the various switch cases. */
  protected BooleanCondition[] m_Conditions;
  
  /** the {@link RatOutput} schemes to pass the data on to. */
  protected RatOutput[] m_Cases;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Forwards the input data to the sub-branch of the condition that evaluates to 'true'.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "condition", "conditions",
	    new BooleanCondition[0]);

    m_OptionManager.add(
	    "case", "cases",
	    new RatOutput[0]);
  }
  
  /**
   * Sets the actor the transmitter belongs to.
   * 
   * @param value	the owner
   */
  @Override
  public void setOwner(Rat value) {
    super.setOwner(value);
    for (RatOutput output: m_Cases)
      output.setOwner(getOwner());
  }

  /**
   * Sets the conditions to evaluate.
   *
   * @param value	the conditions
   */
  public void setConditions(BooleanCondition[] value) {
    int		i;
    
    // check for IndexedBooleanCondition
    if (value.length > 1) {
      for (i = 0; i < value.length; i++) {
	if (value[i] instanceof IndexedBooleanCondition) {
	  getLogger().severe("When using " + IndexedBooleanCondition.class.getName() + " conditions, only a single one is allowed!");
	  return;
	}
      }
    }
    
    m_Conditions = value;
    reset();
  }

  /**
   * Returns the conditions to evaluate.
   *
   * @return		the conditions
   */
  public BooleanCondition[] getConditions() {
    return m_Conditions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conditionsTipText() {
    return
        "The switch conditions to evaluate - the first condition that "
      + "evaluates to 'true' triggers the execution of the corresponding "
      + "'case' actor.";
  }

  /**
   * Sets the base transmitters to use.
   *
   * @param value	the transmitters
   */
  public void setCases(RatOutput[] value) {
    m_Cases = value;
    for (RatOutput output: m_Cases)
      output.setOwner(getOwner());
    reset();
  }

  /**
   * Returns the base transmitters to use.
   *
   * @return		the transmitters
   */
  public RatOutput[] getCases() {
    return m_Cases;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String casesTipText() {
    return "The transmitters to send the data to.";
  }

  /**
   * Returns the type of data that gets accepted.
   * 
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }
  
  /**
   * Hook method for performing checks at setup time.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    int		i;
    
    result = super.setUp();
    
    if (result == null) {
      if (m_Conditions.length != m_Cases.length)
	result = "Number of conditions and cases differ: " + m_Conditions.length + " != " + m_Cases.length;
    }
    
    if (result == null) {
      for (i = 0; i < m_Cases.length; i++) {
	result = m_Cases[i].setUp();
	if (result != null) {
	  result = "Case #" + (i+1) + ": ";
	  break;
	}
      }
    }
    
    return result;
  }

  /**
   * Performs the actual transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String	result;
    Token	input;
    int		i;
    
    result = null;

    input = new Token(m_Input);
    for (i = 0; i < m_Conditions.length; i++) {
      if (m_Conditions[i].evaluate(getOwner(), input)) {
	if (isLoggingEnabled())
	  getLogger().info("Condition #" + (i+1) + ": matches");
	m_Cases[i].input(m_Input);
	result = m_Cases[i].transmit();
	if (result != null) {
	  result = "Case #" + (i+1) + " failed with transmitting: " + result;
	  break;
	}
	break;
      }
      else {
	if (isLoggingEnabled())
	  getLogger().info("Condition #" + (i+1) + ": does not match");
      }
    }
    
    return result;
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    for (BooleanCondition cond: m_Conditions)
      cond.stopExecution();
    super.stopExecution();
  }
}
