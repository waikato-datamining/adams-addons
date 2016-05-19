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
 * DL4JPredictionContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for predictions made by a model.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JPredictionContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 872330681430825295L;

  /** the identifier for the Dataset. */
  public final static String VALUE_DATASET = "Dataset";

  /** the identifier for the Scores. */
  public final static String VALUE_SCORES = "Scores";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public DL4JPredictionContainer() {
    this(null, null);
  }

  /**
   * Initializes the container.
   *
   * @param data	the dataset that was used for prediction
   * @param scores	the scores
   */
  public DL4JPredictionContainer(DataSet data, INDArray scores) {
    super();

    if (data != null)
      store(VALUE_DATASET, data.copy());
    store(VALUE_SCORES, scores);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_DATASET, "data used for prediction; " + DataSet.class.getName());
    addHelp(VALUE_SCORES, "predicted scores; " + INDArray.class.getName());
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

    result.add(VALUE_DATASET);
    result.add(VALUE_SCORES);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_DATASET) && hasValue(VALUE_SCORES);
  }
}
