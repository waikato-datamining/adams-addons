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
 * WebServiceProvider.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import adams.core.CleanUpHandler;
import adams.flow.core.AbstractActor;
import adams.flow.webservice.interceptor.incoming.AbstractInInterceptorGenerator;
import adams.flow.webservice.interceptor.outgoing.AbstractOutInterceptorGenerator;

/**
 * Interface for webservice providers, i.e., the server side.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface WebServiceProvider
  extends CleanUpHandler {
  
  /**
   * Sets the actor that executes this webservice.
   * 
   * @param value	the owner
   */
  public void setOwner(AbstractActor value);
  
  /**
   * Returns the owning actor.
   * 
   * @return		the owner
   */
  public AbstractActor getOwner();
  
  /**
   * Sets the interceptor for incoming messages 
   * (actually generator, since interceptors aren't serializable).
   * 
   * @param value	the interceptor
   */
  public void setInInterceptor(AbstractInInterceptorGenerator value);
  
  /**
   * Returns the interceptor for incoming messages
   * (actually generator, since interceptors aren't serializable).
   * 
   * @return		the interceptor
   */
  public AbstractInInterceptorGenerator getInInterceptor();
  
  /**
   * Sets the interceptor for outgoing messages
   * (actually generator, since interceptors aren't serializable).
   * 
   * @param value	the interceptor
   */
  public void setOutInterceptor(AbstractOutInterceptorGenerator value);
  
  /**
   * Returns the interceptor for outgoing messages
   * (actually generator, since interceptors aren't serializable).
   * 
   * @return		the interceptor
   */
  public AbstractOutInterceptorGenerator getOutInterceptor();

  /**
   * Returns the URL used for the service.
   * 
   * @return		the URL
   */
  public String getURL();
  
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
