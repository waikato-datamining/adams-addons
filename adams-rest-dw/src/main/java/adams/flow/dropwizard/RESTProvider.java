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
 * RESTProvider.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.dropwizard;

import adams.core.CleanUpHandler;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;

/**
 * Interface for REST webservice providers, i.e., the server side.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface RESTProvider
  extends CleanUpHandler, FlowContextHandler {

  /**
   * Sets the YAML config file to use.
   *
   * @param value	the file
   */
  public void setConfigFile(PlaceholderFile value);

  /**
   * Returns the YAML config file in use.
   *
   * @return		the file
   */
  public PlaceholderFile getConfigFile();

    /**
     * Sets the actor that executes this webservice.
     *
     * @param value	the owner
     */
  public void setFlowContext(Actor value);
  
  /**
   * Returns the owning actor.
   * 
   * @return		the owner
   */
  public Actor getFlowContext();

  /**
   * Starts the service.
   * 
   * @return 		null if successful, otherwise error message
   */
  public String start();
  
  /**
   * Returns whether the service is running.
   * 
   * @return		true if running
   */
  public boolean isRunning();
  
  /**
   * Stops the service.
   * 
   * @return		null if successful, otherwise error message
   */
  public String stop();

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp();
}
