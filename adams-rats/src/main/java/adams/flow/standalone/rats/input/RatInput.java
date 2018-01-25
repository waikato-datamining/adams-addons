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
 * RatInput.java
 * Copyright (C) 2014-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.input;

import adams.core.AdditionalInformationHandler;
import adams.core.StoppableWithFeedback;
import adams.core.option.OptionHandler;
import adams.flow.standalone.Rat;

/**
 * Interface for input receivers for the RATS framework.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface RatInput
  extends StoppableWithFeedback, OptionHandler, AdditionalInformationHandler {

  /**
   * Returns the full name of the receiver.
   * 
   * @return		the name
   */
  public String getFullName();
  
  /**
   * Sets the actor the receiver belongs to.
   * 
   * @param value	the owner
   */
  public void setOwner(Rat value);

  /**
   * Returns the actor the receiver belongs to.
   * 
   * @return		the owner
   */
  public Rat getOwner();

  /**
   * Hook method for performing checks at setup time.
   * 
   * @return		null if successful, otherwise error message
   */
  public String setUp();

  /**
   * Returns the type of data this scheme generates.
   * 
   * @return		the type of data
   */
  public Class generates();
  
  /**
   * Checks whether any output can be collected.
   * 
   * @return		true if output available
   */
  public boolean hasPendingOutput();
  
  /**
   * Returns the received data.
   * 
   * @return		the data
   */
  public Object output();

  /**
   * Initializes the reception.
   */
  public void initReception();

  /**
   * Initiates the reception of data.
   * 
   * @return		null if successful, otherwise error message
   */
  public String receive();

  /**
   * Interrupts the reception (eg when pausing).
   */
  public void interruptReception();

  /**
   * Returns whether the reception was interrupted.
   *
   * @return		true if interrupted
   */
  public boolean getReceptionInterrupted();

  /**
   * Stops the execution.
   */
  public void stopExecution();

  /**
   * Returns whether the receiver has been stopped.
   * 
   * @return		true if stopped
   */
  public boolean isStopped();
}
