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
 * RatsTransmission.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.standalone.rats.DummyTransmitter;
import adams.flow.standalone.rats.Transmitter;

/**
 <!-- globalinfo-start -->
 * Defines a single transmission setup.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: RatsTransmission
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-transmitter &lt;adams.flow.standalone.rats.Transmitter&gt; (property: transmitter)
 * &nbsp;&nbsp;&nbsp;The transmitter to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.rats.DummyTransmitter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RatsTransmission
  extends AbstractStandaloneGroupItem {

  /** for serialization. */
  private static final long serialVersionUID = -3804847090140976683L;

  /** the transmitter to use. */
  protected Transmitter m_Transmitter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines a single transmission setup.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "transmitter", "transmitter",
	    new DummyTransmitter());
  }

  /**
   * Sets the transmitter to use.
   *
   * @param value	the transmitter
   */
  public void setTransmitter(Transmitter value) {
    m_Transmitter = value;
    reset();
  }

  /**
   * Returns the transmitter to use.
   *
   * @return		the transmitter
   */
  public Transmitter getTransmitter() {
    return m_Transmitter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String transmitterTipText() {
    return "The transmitter to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "transmitter", m_Transmitter, "transmitter: ");
  }

  /**
   * Checks the transmitter.
   *
   * @return		null if everything is fine, otherwise the error
   */
  public String check() {
    try {
      m_Transmitter.check();
      return null;
    }
    catch (Exception e) {
      return Utils.throwableToString(e);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    
    result = null;
    
    try {
      m_Transmitter.setOwner(this);
      m_Transmitter.transmit();
    }
    catch (Exception e) {
      result = handleException("Failed to transmit!", e);
    }
    
    return result;
  }
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (!m_Stopped)
      m_Transmitter.stopExecution();
    super.stopExecution();
  }
}
