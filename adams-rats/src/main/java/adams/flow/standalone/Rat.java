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
 * Rat.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.core.Compatibility;
import adams.flow.standalone.rats.DummyInput;
import adams.flow.standalone.rats.DummyOutput;
import adams.flow.standalone.rats.RatInput;
import adams.flow.standalone.rats.RatOutput;
import adams.flow.standalone.rats.RatRunnable;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Rat
  extends AbstractStandaloneGroupItem {

  /** for serialization. */
  private static final long serialVersionUID = -154461277343021604L;

  /** the receiver to use. */
  protected RatInput m_Receiver;
  
  /** the transmitter to use. */
  protected RatOutput m_Transmitter;
  
  /** the runnable doing the work. */
  protected RatRunnable m_Runnable;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines a single reception/transmission setup.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "receiver", "receiver",
	    new DummyInput());

    m_OptionManager.add(
	    "transmitter", "transmitter",
	    new DummyOutput());
  }

  /**
   * Sets the receiver to use.
   *
   * @param value	the receiver
   */
  public void setReceiver(RatInput value) {
    m_Receiver = value;
    m_Receiver.setOwner(this);
    reset();
  }

  /**
   * Returns the receiver to use.
   *
   * @return		the receiver
   */
  public RatInput getReceiver() {
    return m_Receiver;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String receiverTipText() {
    return "The receiver to use.";
  }

  /**
   * Sets the transmitter to use.
   *
   * @param value	the transmitter
   */
  public void setTransmitter(RatOutput value) {
    m_Transmitter = value;
    m_Transmitter.setOwner(this);
    reset();
  }

  /**
   * Returns the transmitter to use.
   *
   * @return		the transmitter
   */
  public RatOutput getTransmitter() {
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
    String	result;
    
    result  = QuickInfoHelper.toString(this, "receiver", m_Receiver, "receiver: ");
    result += QuickInfoHelper.toString(this, "transmitter", m_Transmitter, ", transmitter: ");
    
    return result;
  }
  
  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    Compatibility	comp;
    
    result = super.setUp();
    
    if (result == null) {
      comp = new Compatibility();
      if (!comp.isCompatible(new Class[]{m_Receiver.generates()}, m_Transmitter.accepts()))
	result = "Receiver not compatible with transmitter: " 
	    + Utils.classToString(m_Receiver.generates()) 
	    + " != " 
	    + Utils.classesToString(m_Transmitter.accepts());
      if (result == null)
	result = m_Receiver.setUp();
      if (result == null)
	result = m_Transmitter.setUp();
    }
    
    return result;
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
      if (isLoggingEnabled()) {
	getLogger().fine(OptionUtils.getCommandLine(m_Receiver));
	getLogger().fine(OptionUtils.getCommandLine(m_Transmitter));
      }
      m_Runnable = new RatRunnable(this);
      m_Runnable.setLoggingLevel(getLoggingLevel());
      new Thread(m_Runnable).start();
    }
    catch (Exception e) {
      result = handleException("Failed to execute!", e);
    }
    
    return result;
  }
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (!m_Stopped) {
      m_Receiver.stopExecution();
      m_Transmitter.stopExecution();
      if (m_Runnable != null) {
	m_Runnable.stopExecution();
	while (m_Runnable.isRunning()) {
	  try {
	    synchronized(this) {
	      wait(100);
	    }
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}
	m_Runnable = null;
      }
    }
    super.stopExecution();
  }
}
