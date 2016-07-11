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
 * WebServiceClientProducer.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import adams.event.WebServiceClientProducerResponseDataListener;
import adams.flow.webservice.interceptor.incoming.AbstractInInterceptorGenerator;

/**
 * Interface for classes that use webservices.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of output data to use
 */
public interface WebServiceClientProducer<T> 
  extends WebServiceClient {

  /**
   * Adds the listener for response data being received.
   *
   * @param l		the listener to add
   */
  public void addResponseDataListener(WebServiceClientProducerResponseDataListener l);

  /**
   * Removes the listener for response data being received.
   *
   * @param l		the listener to remove
   */
  public void removeResponseDataListener(WebServiceClientProducerResponseDataListener l);

  /**
   * Returns the classes that this client generates.
   * 
   * @return		the classes
   */
  public Class[] generates();
  
  /**
   * Checks whether there is any response data to be collected.
   * 
   * @return		true if data can be collected
   * @see		#getResponseData()
   */
  public boolean hasResponseData();

  /**
   * Sets the response data.
   *
   * @param value	the response data
   */
  public void setResponseData(T value);

  /**
   * Returns the response data, if any.
   * 
   * @return		the response data
   */
  public T getResponseData();
  
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
}
