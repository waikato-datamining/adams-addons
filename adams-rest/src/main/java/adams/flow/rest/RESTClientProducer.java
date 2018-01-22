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
 * RESTProducer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.rest;

import adams.event.RESTClientProducerResponseDataListener;

/**
 * Interface for classes that use webRESTservices.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of output data to use
 */
public interface RESTClientProducer<T>
  extends RESTClient {

  /**
   * Adds the listener for response data being received.
   *
   * @param l		the listener to add
   */
  public void addResponseDataListener(RESTClientProducerResponseDataListener l);

  /**
   * Removes the listener for response data being received.
   *
   * @param l		the listener to remove
   */
  public void removeResponseDataListener(RESTClientProducerResponseDataListener l);

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
}
