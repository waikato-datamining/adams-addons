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
 * Branch.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.flow.core.Unknown;
import adams.flow.standalone.Rat;

/**
 <!-- globalinfo-start -->
 * Forwards the input data to all defined sub-outputs ('sub-branches').
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output &lt;adams.flow.standalone.rats.RatOutput&gt; [-output ...] (property: outputs)
 * &nbsp;&nbsp;&nbsp;The transmitters to send the data to.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Branch
  extends AbstractRatOutput {

  /** for serialization. */
  private static final long serialVersionUID = -3300963022239958581L;
  
  /** the {@link RatOutput} schemes to pass the data on to. */
  protected RatOutput[] m_Outputs;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Forwards the input data to all defined sub-outputs ('sub-branches').";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output", "outputs",
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
    for (RatOutput output: m_Outputs)
      output.setOwner(getOwner());
  }
  
  /**
   * Sets the base transmitters to use.
   *
   * @param value	the transmitters
   */
  public void setOutputs(RatOutput[] value) {
    m_Outputs = value;
    for (RatOutput output: m_Outputs)
      output.setOwner(getOwner());
    reset();
  }

  /**
   * Returns the base transmitters to use.
   *
   * @return		the transmitters
   */
  public RatOutput[] getOutputs() {
    return m_Outputs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputsTipText() {
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
      for (i = 0; i < m_Outputs.length; i++) {
	result = m_Outputs[i].setUp();
	if (result != null) {
	  result = "Output #" + (i+1) + ": ";
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
    int		i;
    
    result = null;

    for (i = 0; i < m_Outputs.length; i++) {
      m_Outputs[i].input(m_Input);
      result = m_Outputs[i].transmit();
      if (result != null) {
	result = "Output #" + (i+1) + " failed with transmitting: " + result;
	break;
      }
    }
    
    return result;
  }
}
