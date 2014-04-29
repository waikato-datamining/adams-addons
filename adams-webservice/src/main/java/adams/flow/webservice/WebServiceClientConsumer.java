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
 * WebServiceClientConsumer.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

/**
 * Interface for classes that use webservices, sending data to them.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of input data to use
 */
public interface WebServiceClientConsumer<T> 
  extends WebServiceClient {

  /**
   * Returns the classes that are accepted input.
   * 
   * @return		the classes that are accepted
   */
  public Class[] accepts();

  /**
   * Sets the data for the request, if any.
   * 
   * @param value	the request data
   */
  public void setRequestData(T value);
}
