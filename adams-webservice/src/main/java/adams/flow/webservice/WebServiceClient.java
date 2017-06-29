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
 * WebServiceClient.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import adams.core.CleanUpHandler;
import adams.core.ErrorProvider;
import adams.core.option.OptionHandler;
import adams.flow.core.Actor;

/**
 * Interface for classes that use webservices.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface WebServiceClient
  extends OptionHandler, CleanUpHandler, ErrorProvider {
  
  /**
   * Sets the actor that executes this webservice.
   * 
   * @param value	the owner
   */
  public void setOwner(Actor value);
  
  /**
   * Returns the owning actor.
   * 
   * @return		the owner
   */
  public Actor getOwner();
  
  /**
   * Sets the timeout for the http connection in msec.
   * 
   * @param value	the timeout in msec, 0 is infinite
   */
  public void setConnectionTimeout(int value);
  
  /**
   * Returns the timeout for the http connection in msec.
   * 
   * @return		the timeout in msec, 0 is infinite
   */
  public int getConnectionTimeout();
  
  /**
   * Sets the timeout for receiving in msec.
   * 
   * @param value	the timeout in msec, 0 is infinite
   */
  public void setReceiveTimeout(int value);
  
  /**
   * Returns the timeout for receiving in msec.
   * 
   * @return		the timeout in msec, 0 is infinite
   */
  public int getReceiveTimeout();
  
  /**
   * Queries the webservice.
   * 
   * @throws Exception	if accessing webservice fails for some reason
   */
  public void query() throws Exception;
}
