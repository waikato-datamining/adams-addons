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
 * AbstractMetaRatInput.java
 * Copyright (C) 2014-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.core.QuickInfoHelper;
import adams.flow.standalone.Rat;

import java.util.ArrayList;

/**
 * Ancestor for {@link RatInput} schemes that wrap another {@link RatInput}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMetaRatInput
  extends AbstractRatInput {

  /** for serialization. */
  private static final long serialVersionUID = 2519428538902758907L;

  /** the base RatInput to use. */
  protected RatInput m_Input;

  /** the data queue. */
  protected ArrayList m_Data;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "input", "input",
	    new DummyInput());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Data = new ArrayList();
  }
  
  /**
   * Sets the actor the receiver belongs to.
   * 
   * @param value	the owner
   */
  @Override
  public void setOwner(Rat value) {
    super.setOwner(value);
    m_Input.setOwner(value);
  }

  /**
   * Sets the base receiver to use.
   *
   * @param value	the receiver
   */
  public void setInput(RatInput value) {
    m_Input = value;
    m_Input.setOwner(getOwner());
    reset();
  }

  /**
   * Returns the base receiver to use.
   *
   * @return		the receiver
   */
  public RatInput getInput() {
    return m_Input;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputTipText() {
    return "The receiver to wrap.";
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "input", m_Input);
  }
  
  /**
   * Hook method for performing checks at setup time.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    
    result = super.setUp();
    
    if (result == null)
      result = m_Input.setUp();
    
    return result;
  }

  /**
   * Initializes the reception.
   */
  @Override
  public void initReception() {
    super.initReception();
    m_Input.initReception();
  }

  /**
   * Hook method before calling the base-input's receive() method.
   * <br><br>
   * Default implementation just clears the data queue.
   * 
   * @return		null if successful, otherwise error message
   * @see		#m_Data
   */
  protected String preReceive() {
    m_Data.clear();
    return null;
  }
  
  /**
   * Hook method that calls the base-input's receive() method.
   * 
   * @return		null if successful, otherwise error message
   */
  protected String callReceive() {
    return m_Input.receive();
  }

  /**
   * Hook method after calling the base-input's receive() method.
   * <br><br>
   * Default implementation collects all the pending data from the base-input.
   * 
   * @return		null if successful, otherwise error message
   * @see		#m_Data
   */
  protected String postReceive() {
    while (m_Input.hasPendingOutput())
      m_Data.add(m_Input.output());
    return null;
  }

  /**
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   * @see		#preReceive()
   * @see		#callReceive()
   * @see		#postReceive()
   */
  @Override
  protected String doReceive() {
    String	result;
    
    result = preReceive();
    if (result == null)
      result = callReceive();
    if (result == null)
      result = postReceive();
    
    return result;
  }

  /**
   * Checks whether any output can be collected.
   * 
   * @return		true if output available
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Data.size() > 0);
  }
  
  /**
   * Returns the received data.
   * 
   * @return		the data
   */
  @Override
  public Object output() {
    return m_Data.remove(0);
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    m_Input.stopExecution();
  }
}
