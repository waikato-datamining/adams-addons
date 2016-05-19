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
 * DL4JModelContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import org.nd4j.linalg.dataset.DataSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for models.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JModelContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 5581530171877321061L;

  /** the identifier for the Model. */
  public final static String VALUE_MODEL = "Model";

  /** the identifier for the full dataset. */
  public final static String VALUE_DATASET = "Dataset";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public DL4JModelContainer() {
    this(null);
  }

  /**
   * Initializes the container with no dataset.
   *
   * @param model	the model to use
   */
  public DL4JModelContainer(Object model) {
    this(model, null);
  }

  /**
   * Initializes the container with the full dataset.
   *
   * @param model	the model to use
   * @param data	the data to use
   */
  public DL4JModelContainer(Object model, DataSet data) {
    super();

    store(VALUE_MODEL, model);
    store(VALUE_DATASET, data);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_MODEL, "model object; " + Object.class.getName());
    addHelp(VALUE_DATASET, "full dataset; " + DataSet.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<>();

    result.add(VALUE_MODEL);
    result.add(VALUE_DATASET);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_MODEL);
  }
}
