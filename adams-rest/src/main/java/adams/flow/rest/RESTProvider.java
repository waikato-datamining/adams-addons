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
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.rest;

import adams.core.CleanUpHandler;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;
import adams.flow.rest.interceptor.incoming.AbstractInInterceptorGenerator;
import adams.flow.rest.interceptor.outgoing.AbstractOutInterceptorGenerator;

/**
 * Interface for REST webservice providers, i.e., the server side.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface RESTProvider
  extends CleanUpHandler, FlowContextHandler {
  
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
