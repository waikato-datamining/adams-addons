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
 * ReceiverWithPreProcessing.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import adams.flow.core.CallableActorReference;

/**
 * A receiver that performs some pre-processing on the data it obtains
 * before passing it on. 
 * Pre-processing is done using a callable transformer.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ReceiverWithPreProcessing
  extends Receiver {

  /**
   * Sets the name of the callable transformer to use.
   *
   * @param value 	the callable name
   */
  public void setPreProcessing(CallableActorReference value);

  /**
   * Returns the name of the callable transformer in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getPreProcessing();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preProcessingTipText();
}
