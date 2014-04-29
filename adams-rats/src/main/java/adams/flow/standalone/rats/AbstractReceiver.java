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
 * AbstractReceiver.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import adams.core.QuickInfoSupporter;
import adams.core.ShallowCopySupporter;
import adams.core.Stoppable;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.core.AbstractActor;

/**
 * Ancestor for schemes that receive data in some fashion.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractReceiver
  extends AbstractOptionHandler
  implements Receiver, ShallowCopySupporter<AbstractReceiver>, Stoppable, QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3207873627185842452L;

  /** the owner. */
  protected AbstractActor m_Owner;
  
  /** whether the reception was stopped. */
  protected boolean m_Stopped;

  /** the logging prefix. */
  protected String m_LoggingPrefix;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LoggingPrefix = "";
  }

  /**
   * Initializes the logger.
   */
  @Override
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(m_LoggingPrefix);
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }
  
  /**
   * Updates the prefix of the logger.
   */
  protected void updatePrefix() {
    if (getOwner() != null) {
      m_LoggingPrefix = getOwner().getFullName() + "$" + getClass().getSimpleName();
      m_Logger        = null;
    }
  }

  /**
   * Sets the actor the receiver belongs to.
   * 
   * @param value	the owner
   */
  public void setOwner(AbstractActor value) {
    m_Owner = value;
    updatePrefix();
  }

  /**
   * Returns the actor the receiver belongs to.
   * 
   * @return		the owner
   */
  public AbstractActor getOwner() {
    return m_Owner;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method for performing checks.
   * <p/>
   * Default implementation does nothing.
   * 
   * @throws Exception	if checks fail
   */
  public void check() throws Exception {
  }

  /**
   * Performs the actual reception of data.
   * 
   * @throws Execption	if receiving of data fails
   */
  protected abstract void doReceive() throws Exception;

  /**
   * Starts the receiving of data.
   * 
   * @throws Execption	if receiving of data fails
   */
  public void receive() throws Exception {
    m_Stopped = false;
    check();
    doReceive();
  }
  
  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractReceiver shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractReceiver shallowCopy(boolean expand) {
    return (AbstractReceiver) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }
}
