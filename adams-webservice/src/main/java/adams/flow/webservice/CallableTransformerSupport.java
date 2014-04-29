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
 * CallableTransformerSupport.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice;

import adams.flow.core.CallableActorReference;

/**
 * Interface for webservice clients/servers that use a callable transformer for
 * further processing the data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <I> the type of data that the callable transformer gets as input
 * @param <O> the type of data that the callable transformer generates
 */
public interface CallableTransformerSupport<I,O> {

  /**
   * Sets the callable transformer to use.
   * 
   * @param value	the reference
   */
  public void setTransformer(CallableActorReference value);
  
  /**
   * Returns the callable transformer in use.
   * 
   * @return		the reference
   */
  public CallableActorReference getTransformer();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String transformerTipText();
  
  /**
   * Applies the transformer to the data.
   * 
   * @param data	the data to process
   * @return		the processed data
   * @throws Exception	if it fails for some reason
   */
  public O applyTransformer(I data) throws Exception;
}
