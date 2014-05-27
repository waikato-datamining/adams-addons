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
 * MekaResultContainer.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import meka.core.Result;

/**
 * A container for {@link Result} objects, with optional trained model.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7987 $
 */
public class MekaResultContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = -4976094375833503035L;

  /** the identifier for the Result. */
  public final static String VALUE_RESULT = "Result";

  /** the identifier for the Model. */
  public final static String VALUE_MODEL = "Model";

  /**
   * Initializes the container.
   * <p/>
   * Only used for generating help information.
   */
  public MekaResultContainer() {
    this(null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param result	the result to use
   */
  public MekaResultContainer(Result result) {
    this(result, null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param result	the result to use
   * @param model	the model to use
   */
  public MekaResultContainer(Result result, Object model) {
    super();

    store(VALUE_RESULT, result);
    store(VALUE_MODEL,  model);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<String>();

    result.add(VALUE_RESULT);
    result.add(VALUE_MODEL);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_RESULT);
  }
}
