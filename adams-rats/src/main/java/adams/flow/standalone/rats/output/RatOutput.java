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
 * RatOutput.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.core.StoppableWithFeedback;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeListener;
import adams.flow.standalone.Rat;

/**
 * Interface for output transmitters for the RATS framework.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RatOutput
  extends StoppableWithFeedback, VariableChangeListener {

  /**
   * Returns the full name of the receiver.
   * 
   * @return		the name
   */
  public String getFullName();

  /**
   * Sets the actor the transmitter belongs to.
   * 
   * @param value	the owner
   */
  public void setOwner(Rat value);

  /**
   * Returns the actor the transmitter belongs to.
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
   * Returns the type of data that gets accepted.
   * 
   * @return		the type of data
   */
  public Class[] accepts();
  
  /**
   * Whether input can be supplied at the moment.
   * 
   * @return		true if input is accepted
   */
  public boolean canInput();
  
  /**
   * The data to transmit.
   * 
   * @param obj		the data
   */
  public void input(Object obj);
  
  /**
   * Performs the transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  public String transmit();

  /**
   * Stops the execution.
   */
  public void stopExecution();

  /**
   * Returns whether the transmitter has been stopped.
   * 
   * @return		true if stopped
   */
  public boolean isStopped();

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  public void variableChanged(VariableChangeEvent e);
}
