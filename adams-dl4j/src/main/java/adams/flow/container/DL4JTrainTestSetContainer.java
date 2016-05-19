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
 * DL4JTrainTestSetContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import org.nd4j.linalg.dataset.DataSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for storing train and test set.
 * <br><br>
 * Optionally, random seed can be stored as well.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JTrainTestSetContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 7650097276531433711L;

  /** the identifier for the training data. */
  public final static String VALUE_TRAIN = "Train";

  /** the identifier for the test data. */
  public final static String VALUE_TEST = "Test";

  /** the identifier for the random seed. */
  public final static String VALUE_SEED = "Seed";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public DL4JTrainTestSetContainer() {
    this(null, null);
  }

  /**
   * Initializes the container.
   *
   * @param train	the training set
   * @param test	the test data
   */
  public DL4JTrainTestSetContainer(DataSet train, DataSet test) {
    this(train, test, null);
  }

  /**
   * Initializes the container.
   *
   * @param train	the training set
   * @param test	the test data
   * @param seed	the seed value, can be null
   */
  public DL4JTrainTestSetContainer(DataSet train, DataSet test, Long seed) {
    super();

    store(VALUE_TRAIN, train);
    store(VALUE_TEST, test);
    store(VALUE_SEED, seed);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_TRAIN, "training set; " + DataSet.class.getName());
    addHelp(VALUE_TEST, "test set; " + DataSet.class.getName());
    addHelp(VALUE_SEED, "seed value; " + Long.class.getName());
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

    result.add(VALUE_TRAIN);
    result.add(VALUE_TEST);
    result.add(VALUE_SEED);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return (hasValue(VALUE_TRAIN) && hasValue(VALUE_TEST));
  }
}
