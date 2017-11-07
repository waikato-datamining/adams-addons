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
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.nd4j.linalg.dataset.DataSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

  /** the identifier for the Best Model. */
  public final static String VALUE_BEST_MODEL = "Best Model";

  /** the identifier for the Best statistics. */
  public final static String VALUE_BEST_STATISTICS = "Best Statistics";

  /** the identifier for the full dataset. */
  public final static String VALUE_DATASET = "Dataset";

  /** the identifier for the epoch. */
  public final static String VALUE_EPOCH = "Epoch";

  /** the identifier for the evaluation. */
  public final static String VALUE_EVALUATION = "Evaluation";

  /** the identifier for the train stop messages. */
  public final static String VALUE_TRAIN_STOP_MESSAGES = "Train Stop Messages";

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
    this(model, data, null);
  }

  /**
   * Initializes the container with the full dataset.
   *
   * @param model	the model to use
   * @param data	the data to use
   * @param epoch 	the epoch this model stems from
   */
  public DL4JModelContainer(Object model, DataSet data, Integer epoch) {
    this(model, data, epoch, null);
  }

  /**
   * Initializes the container with the full dataset.
   *
   * @param model	the model to use
   * @param data	the data to use
   * @param epoch 	the epoch this model stems from
   * @param evaluation  the evaluation object
   */
  public DL4JModelContainer(Object model, DataSet data, Integer epoch, Object evaluation) {
    this(model, data, epoch, evaluation, null, null);
  }

  /**
   * Initializes the container with the full dataset.
   *
   * @param model	the model to use
   * @param data	the data to use
   * @param epoch 	the epoch this model stems from
   * @param evaluation  the evaluation object
   * @param bestModel	the best model
   * @param bestStats	the statistics associated with the best model
   */
  public DL4JModelContainer(Object model, DataSet data, Integer epoch, Object evaluation, Object bestModel, Map<String,Double> bestStats) {
    super();

    store(VALUE_MODEL,           model);
    store(VALUE_BEST_MODEL,      bestModel);
    store(VALUE_BEST_STATISTICS, bestStats);
    store(VALUE_DATASET,         data);
    store(VALUE_EPOCH,           epoch);
    store(VALUE_EVALUATION,      evaluation);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_MODEL, "model object", Object.class);
    addHelp(VALUE_BEST_MODEL, "best model object", Object.class);
    addHelp(VALUE_BEST_STATISTICS, "best model statistics", Map.class);
    addHelp(VALUE_DATASET, "full dataset", DataSet.class);
    addHelp(VALUE_EPOCH, "epoch", Integer.class);
    addHelp(VALUE_EVALUATION, "evaluation", new Class[]{Evaluation.class, RegressionEvaluation.class});
    addHelp(VALUE_TRAIN_STOP_MESSAGES, "train stop criteria that triggered", String[].class);
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
    result.add(VALUE_BEST_MODEL);
    result.add(VALUE_BEST_STATISTICS);
    result.add(VALUE_DATASET);
    result.add(VALUE_EPOCH);
    result.add(VALUE_EVALUATION);
    result.add(VALUE_TRAIN_STOP_MESSAGES);

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
