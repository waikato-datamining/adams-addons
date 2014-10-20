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
 * AbstractRatGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.generator;

import adams.core.option.AbstractOptionHandler;
import adams.flow.control.StorageName;
import adams.flow.core.CallableActorReference;
import adams.flow.standalone.Rat;
import adams.flow.standalone.RatControl;

/**
 * Ancestor for generators that create {@link Rat} setups.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRatGenerator
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -585700514036106709L;

  /** the callable name. */
  protected CallableActorReference m_Log;

  /** the name of the (optional) queue in internal storage for sending send error to. */
  protected StorageName m_SendErrorQueue;
  
  /** whether to show in {@link RatControl}. */
  protected boolean m_ShowInControl;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "log", "log",
	    new CallableActorReference("unknown"));

    m_OptionManager.add(
	    "send-error-queue", "sendErrorQueue",
	    new StorageName("senderrors"));

    m_OptionManager.add(
	    "show-in-control", "showInControl",
	    false);
  }

  /**
   * Sets the name of the callable log actor to use.
   *
   * @param value 	the callable name
   */
  public void setLog(CallableActorReference value) {
    m_Log = value;
    reset();
  }

  /**
   * Returns the name of the callable log actor in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getLog() {
    return m_Log;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String logTipText() {
    return "The name of the callable log actor to use (logging disabled if actor not found).";
  }

  /**
   * Sets the name for the queue in internal storage to feed with send errors.
   *
   * @param value	the name
   */
  public void setSendErrorQueue(StorageName value) {
    m_SendErrorQueue = value;
    reset();
  }

  /**
   * Returns the name for the queue in internal storage to feed with send errors.
   *
   * @return		the name
   */
  public StorageName getSendErrorQueue() {
    return m_SendErrorQueue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sendErrorQueueTipText() {
    return "The name of the (optional) queue in internal storage to feed with send errors.";
  }

  /**
   * Sets whether to show in RatControl.
   * 
   * @param value	true if to show in RatControl
   */
  public void setShowInControl(boolean value) {
    m_ShowInControl = value;
    reset();
  }
  
  /**
   * Returns whether to show in RatControl.
   * 
   * @return		true if to show in RatControl
   */
  public boolean getShowInControl() {
    return m_ShowInControl;
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showInControlTipText() {
    return "If enabled, the generated Rat will be displayed in the " + RatControl.class.getName() + " control panel.";
  }

  /**
   * Hook method for checks.
   * <p/>
   * Default implementation does nothing.
   */
  protected void check() {
  }
  
  /**
   * Generates the actual setup.
   * 
   * @return		the generated setup
   */
  protected abstract Rat doGenerate();
  
  /**
   * Generates a Rat setup.
   * 
   * @return		the setup
   */
  public Rat generate() {
    check();
    return doGenerate();
  }
}
