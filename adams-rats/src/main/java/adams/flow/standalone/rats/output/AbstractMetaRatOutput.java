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
 * AbstractMetaRatOutput.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.flow.standalone.Rat;

/**
 * Ancestor for {@link RatOutput} schemes that wrap another {@link RatOutput}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMetaRatOutput
  extends AbstractRatOutput {

  /** for serialization. */
  private static final long serialVersionUID = 2519428538902758907L;

  /** the base RatOutput to use. */
  protected RatOutput m_Output;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output", "output",
	    new DummyOutput());
  }
  
  /**
   * Sets the actor the transmitter belongs to.
   * 
   * @param value	the owner
   */
  @Override
  public void setOwner(Rat value) {
    super.setOwner(value);
    m_Output.setOwner(value);
  }
  
  /**
   * Sets the base transmitter to use.
   *
   * @param value	the transmitter
   */
  public void setOutput(RatOutput value) {
    m_Output = value;
    m_Output.setOwner(getOwner());
    reset();
  }

  /**
   * Returns the base transmitter to use.
   *
   * @return		the transmitter
   */
  public RatOutput getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return "The transmitter to wrap.";
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "output", m_Output);
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
      result = m_Output.setUp();
    
    return result;
  }

  /**
   * Hook method before calling the base-output's transmit() method.
   * <p/>
   * Default implementation does nothing.
   * 
   * @return		null if successful, otherwise error message
   */
  protected String preTransmit() {
    return null;
  }
  
  /**
   * Hook method that calls the base-input's transmit() method.
   * 
   * @return		null if successful, otherwise error message
   */
  protected String callTransmit() {
    m_Output.input(m_Input);
    return m_Output.transmit();
  }
  
  /**
   * Hook method after calling the base-output's transmit() method.
   * <p/>
   * Default implementation does nothing.
   * 
   * @return		null if successful, otherwise error message
   */
  protected String postTransmit() {
    return null;
  }
  
  /**
   * Performs the actual reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String	result;
    
    result = preTransmit();
    if (result == null)
      result = callTransmit();
    if (result == null)
      result = postTransmit();
    
    return result;
  }
}
