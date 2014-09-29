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
 * ErrorContainer.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container to send in case of errors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ErrorContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = -8029101724054801333L;

  /** the key for the payload. */
  public final static String VALUE_PAYLOAD = "Payload";

  /** the key for the error message. */
  public final static String VALUE_ERROR = "Error";

  /** the key for the origin. */
  public final static String VALUE_SOURCE = "Source";

  /**
   * Initializes the container.
   */
  public ErrorContainer() {
    super();
  }

  /**
   * Initializes the container.
   * 
   * @param payload	the token's payload to forward
   * @param error	the associated error
   * @param source	the source of the error
   */
  public ErrorContainer(Object payload, String error, String source) {
    this();
    store(VALUE_PAYLOAD, payload);
    store(VALUE_ERROR, error);
    store(VALUE_SOURCE, source);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<String>();

    result.add(VALUE_PAYLOAD);
    result.add(VALUE_ERROR);
    result.add(VALUE_SOURCE);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_PAYLOAD) && hasValue(VALUE_ERROR) && hasValue(VALUE_SOURCE);
  }
}
