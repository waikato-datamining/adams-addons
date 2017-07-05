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
 * DL4JTrainModel.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.core.Range;
import adams.core.VariableName;
import adams.core.option.AbstractOption;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;
import adams.flow.container.DL4JModelContainer;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import adams.flow.core.VariableMonitor;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.flow.source.DL4JModelConfigurator;
import adams.ml.dl4j.DataSetHelper;
import adams.ml.dl4j.EvaluationStatistic;
import adams.ml.dl4j.datasetiterator.ShufflingDataSetIterator;
import adams.ml.dl4j.iterationlistener.IterationListenerConfigurator;
import adams.ml.dl4j.model.ModelConfigurator;
import adams.ml.dl4j.trainstopcriterion.AbstractTrainStopCriterion;
import adams.ml.dl4j.trainstopcriterion.MaxEpoch;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.dataset.DataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Trains a model based on the incoming dataset and outputs the built model alongside the dataset (in a model container).<br>
 * The model can be reset using the monitor variable option, i.e, whenever this variable changes value, the model gets reset. Useful when training sequentially on multiple datasets (using the file name as monitor variable).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;org.nd4j.linalg.dataset.DataSet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.DL4JModelContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.DL4JModelContainer: Model, Best Model, Best Statistics, Dataset, Epoch, Evaluation
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DL4JTrainModel
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-model &lt;adams.flow.core.CallableActorReference&gt; (property: model)
 * &nbsp;&nbsp;&nbsp;The model to train on the input data.
 * &nbsp;&nbsp;&nbsp;default: DL4JModelConfigurator
 * </pre>
 * 
 * <pre>-train-stop &lt;adams.ml.dl4j.trainstopcriterion.AbstractTrainStopCriterion&gt; (property: trainStop)
 * &nbsp;&nbsp;&nbsp;The criterion for stopping training.
 * &nbsp;&nbsp;&nbsp;default: adams.ml.dl4j.trainstopcriterion.MaxEpoch
 * </pre>
 * 
 * <pre>-mini-batch-size &lt;int&gt; (property: miniBatchSize)
 * &nbsp;&nbsp;&nbsp;The mini-batch size to use; -1 to turn off.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value to use for randomization.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-iteration-listener &lt;adams.ml.dl4j.iterationlistener.IterationListenerConfigurator&gt; [-iteration-listener ...] (property: iterationListeners)
 * &nbsp;&nbsp;&nbsp;The iteration listeners to use (configurators).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-output-interval &lt;int&gt; (property: outputInterval)
 * &nbsp;&nbsp;&nbsp;The interval (of epochs) to output the model (use &lt;1 to turn off).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-var-name &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The variable to monitor.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 * 
 * <pre>-test-percentage &lt;double&gt; (property: testPercentage)
 * &nbsp;&nbsp;&nbsp;The percentage (0-1) of the training data to set aside for evaluating the 
 * &nbsp;&nbsp;&nbsp;model; no testing performed if 0.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 * 
 * <pre>-type &lt;CLASSIFICATION|REGRESSION&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of evaluation to perform.
 * &nbsp;&nbsp;&nbsp;default: CLASSIFICATION
 * </pre>
 * 
 * <pre>-output-best-model &lt;boolean&gt; (property: outputBestModel)
 * &nbsp;&nbsp;&nbsp;If enabled and testing is performed, the best model found so far is output 
 * &nbsp;&nbsp;&nbsp;in the container as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-statistic &lt;Accuracy|Class count (class)|F1|F1 (class)|False alarm rate|False negative rate|False negative rate (class)|False positive rate|False positive rate (class)|Precision|Precision (class)|Recall|Recall (class)|Row count|Correlation R^2|Mean absolute error|Mean squared error|Relative squared error|Root mean squared error&gt; [-statistic ...] (property: statisticValues)
 * &nbsp;&nbsp;&nbsp;The evaluation values to extract and turn into a spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: ACCURACY, F1
 * </pre>
 * 
 * <pre>-index &lt;adams.core.Range&gt; (property: classIndex)
 * &nbsp;&nbsp;&nbsp;The range of class label indices (eg used for AUC).
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-regression-columns &lt;adams.core.Range&gt; (property: regressionColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to get regression statistics for.
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JTrainModel
  extends AbstractTransformer
  implements ProvenanceSupporter, Randomizable, VariableMonitor {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the key for storing the current model in the backup. */
  public final static String BACKUP_MODEL = "model";

  /** the key for storing the current best model in the backup. */
  public final static String BACKUP_BEST_MODEL = "best model";

  /** the key for storing the current epoch in the backup. */
  public final static String BACKUP_EPOCH = "epoch";

  /** the key for storing the current training data in the backup. */
  public final static String BACKUP_TRAINDATA = "traindata";

  /** the key for storing the current test data in the backup. */
  public final static String BACKUP_TESTDATA = "testdata";

  /** the key for storing the best statistics in the backup. */
  public final static String BACKUP_BEST_STATISTICS = "best statistics";

  /** the key for storing the current training finished flag in the backup. */
  public final static String BACKUP_TRAINING_FINISHED = "training finished";

  /** the name of the callable model. */
  protected CallableActorReference m_Model;

  /** the actual model. */
  protected Model m_ActualModel;

  /** the best model. */
  protected Model m_BestModel;

  /** the currently best statistics. */
  protected Map<String,Double> m_BestStatistics;

  /** the criterion for stopping training. */
  protected AbstractTrainStopCriterion m_TrainStop;

  /** the minibatch size. */
  protected int m_MiniBatchSize;

  /** the seed value. */
  protected long m_Seed;

  /** the iteration listeners to use. */
  protected IterationListenerConfigurator[] m_IterationListeners;

  /** output the model every number of epochs. */
  protected int m_OutputInterval;

  /** the variable to listen to. */
  protected VariableName m_VariableName;

  /** the test set percentage. */
  protected double m_TestPercentage;

  /** the evaluation type. */
  protected DL4JEvaluationType m_Type;

  /** whether to output the best model in the container as well (if test percentage >0). */
  protected boolean m_OutputBestModel;

  /** the comparison fields. */
  protected EvaluationStatistic[] m_StatisticValues;

  /** the range of the class labels. */
  protected Range m_ClassIndex;

  /** the regression columns to get statistics for. */
  protected Range m_RegressionColumns;

  /** the current epoch. */
  protected int m_Epoch;

  /** the training data. */
  protected DataSet m_TrainData;

  /** the test data. */
  protected DataSet m_TestData;

  /** whether training has finished. */
  protected boolean m_TrainingFinished;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Trains a model based on the incoming dataset and outputs the "
	+ "built model alongside the dataset (in a model container).\n"
        + "The model can be reset using the monitor variable option, i.e, "
	+ "whenever this variable changes value, the model gets reset. "
	+ "Useful when training sequentially on multiple datasets (using the "
	+ "file name as monitor variable).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "model", "model",
      new CallableActorReference(DL4JModelConfigurator.class.getSimpleName()));

    m_OptionManager.add(
      "train-stop", "trainStop",
      new MaxEpoch());

    m_OptionManager.add(
      "mini-batch-size", "miniBatchSize",
      -1, -1, null);

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "iteration-listener", "iterationListeners",
      new IterationListenerConfigurator[0]);

    m_OptionManager.add(
      "output-interval", "outputInterval",
      -1, -1, null);

    m_OptionManager.add(
      "var-name", "variableName",
      new VariableName());

    m_OptionManager.add(
      "test-percentage", "testPercentage",
      0.0, 0.0, 1.0);

    m_OptionManager.add(
      "type", "type",
      DL4JEvaluationType.CLASSIFICATION);

    m_OptionManager.add(
      "output-best-model", "outputBestModel",
      false);

    m_OptionManager.add(
      "statistic", "statisticValues",
      new EvaluationStatistic[]{
        EvaluationStatistic.ACCURACY,
        EvaluationStatistic.F1
      });

    m_OptionManager.add(
      "index", "classIndex",
      new Range(Range.FIRST));

    m_OptionManager.add(
      "regression-columns", "regressionColumns",
      new Range(Range.LAST));
  }

  /**
   * Sets the name of the callable model to use.
   *
   * @param value	the name
   */
  public void setModel(CallableActorReference value) {
    m_Model = value;
    reset();
  }

  /**
   * Returns the name of the callable model in use.
   *
   * @return		the name
   */
  public CallableActorReference getModel() {
    return m_Model;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelTipText() {
    return "The model to train on the input data.";
  }

  /**
   * Sets the criterion for stopping training.
   *
   * @param value	the criterion
   */
  public void setTrainStop(AbstractTrainStopCriterion value) {
    m_TrainStop = value;
    reset();
  }

  /**
   * Returns the criterion for stopping training.
   *
   * @return  		the criterion
   */
  public AbstractTrainStopCriterion getTrainStop() {
    return m_TrainStop;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trainStopTipText() {
    return "The criterion for stopping training.";
  }

  /**
   * Sets the minibatch size to use.
   *
   * @param value	the size (-1 to turn off)
   */
  public void setMiniBatchSize(int value) {
    m_MiniBatchSize = value;
    reset();
  }

  /**
   * Returns the type of evaluation to perform.
   *
   * @return  		the type
   */
  public int getMiniBatchSize() {
    return m_MiniBatchSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String miniBatchSizeTipText() {
    return "The mini-batch size to use; -1 to turn off.";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value to use for randomization.";
  }

  /**
   * Sets the iteration listeners to use.
   *
   * @param value	the configurators
   */
  public void setIterationListeners(IterationListenerConfigurator[] value) {
    m_IterationListeners = value;
    reset();
  }

  /**
   * Returns the iteration listeners to use.
   *
   * @return		the configurators
   */
  public IterationListenerConfigurator[] getIterationListeners() {
    return m_IterationListeners;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String iterationListenersTipText() {
    return "The iteration listeners to use (configurators).";
  }

  /**
   * Sets the epoch interval to output the model.
   *
   * @param value	the number of epochs (-1 to turn off)
   */
  public void setOutputInterval(int value) {
    m_OutputInterval = value;
    reset();
  }

  /**
   * Returns the epoch interval to output the model.
   *
   * @return  		the number of epochs (-1 to turn off)
   */
  public int getOutputInterval() {
    return m_OutputInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputIntervalTipText() {
    return "The interval (of epochs) to output the model (use <1 to turn off).";
  }

  /**
   * Sets the name of the variable to monitor.
   *
   * @param value	the name
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the name of the variable to monitor.
   *
   * @return		the name
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNameTipText() {
    return "The variable to monitor.";
  }

  /**
   * Sets the percentage of the training data to set aside for testing
   * the model; not testing performed if 0.
   *
   * @param value	the percentage (0-1)
   */
  public void setTestPercentage(double value) {
    if (getOptionManager().isValid("testPercentage", value)) {
      m_TestPercentage = value;
      reset();
    }
  }

  /**
   * Returns the percentage of the training data to set aside for testing
   * the model; not testing performed if 0.
   *
   * @return  		the percentage (0-1)
   */
  public double getTestPercentage() {
    return m_TestPercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testPercentageTipText() {
    return
      "The percentage (0-1) of the training data to set aside for evaluating "
        + "the model; no testing performed if 0.";
  }

  /**
   * Sets the type of evaluation to perform.
   *
   * @param value	the type
   */
  public void setType(DL4JEvaluationType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of evaluation to perform.
   *
   * @return  		the type
   */
  public DL4JEvaluationType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of evaluation to perform.";
  }

  /**
   * Sets whether to output the best model in the container as well.
   * Requires testing to be performed.
   *
   * @param value	true if to output
   * @see		#getTestPercentage()
   */
  public void setOutputBestModel(boolean value) {
    m_OutputBestModel = value;
    reset();
  }

  /**
   * Returns whether to output the best model in the container as well.
   * Requires testing to be performed.
   *
   * @return  		true if to output
   * @see		#getTestPercentage()
   */
  public boolean getOutputBestModel() {
    return m_OutputBestModel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   * @see		#getTestPercentage()
   */
  public String outputBestModelTipText() {
    return
      "If enabled and testing is performed, the best model found so far is "
	+ "output in the container as well.";
  }

  /**
   * Sets the values to extract.
   *
   * @param value	the value
   */
  public void setStatisticValues(EvaluationStatistic[] value) {
    m_StatisticValues = value;
    reset();
  }

  /**
   * Returns the values to extract.
   *
   * @return		the value
   */
  public EvaluationStatistic[] getStatisticValues() {
    return m_StatisticValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticValuesTipText() {
    return "The evaluation values to extract and turn into a spreadsheet.";
  }

  /**
   * Sets the range of class labels indices (1-based).
   *
   * @param value	the label indices
   */
  public void setClassIndex(Range value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the current range of class label indices (1-based).
   *
   * @return		the label indices
   */
  public Range getClassIndex() {
    return m_ClassIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classIndexTipText() {
    return "The range of class label indices (eg used for AUC).";
  }

  /**
   * Sets the range of columns to get regression statistics for.
   *
   * @param value	the column indices
   */
  public void setRegressionColumns(Range value) {
    m_RegressionColumns = value;
    reset();
  }

  /**
   * Returns the range of columns to get regression statistics for.
   *
   * @return		the column indices
   */
  public Range getRegressionColumns() {
    return m_RegressionColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regressionColumnsTipText() {
    return "The range of columns to get regression statistics for.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "model", m_Model, "model: ");
    result += QuickInfoHelper.toString(this, "trainStop", m_TrainStop, ", stop: ");
    result += QuickInfoHelper.toString(this, "miniBatchSize", m_MiniBatchSize, ", minibatch: ");
    result += QuickInfoHelper.toString(this, "variableName", m_VariableName.paddedValue(), ", monitor: ");
    result += QuickInfoHelper.toString(this, "testPercentage", m_TestPercentage, ", test %: ");
    result += QuickInfoHelper.toString(this, "outputBestModel", m_OutputBestModel, "best model", ", ");

    return result;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_MODEL);
    pruneBackup(BACKUP_BEST_MODEL);
    pruneBackup(BACKUP_BEST_STATISTICS);
    pruneBackup(BACKUP_EPOCH);
    pruneBackup(BACKUP_TRAINDATA);
    pruneBackup(BACKUP_TESTDATA);
    pruneBackup(BACKUP_TRAINING_FINISHED);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();
    if (m_ActualModel != null)
      result.put(BACKUP_MODEL, m_ActualModel);
    if (m_BestModel != null)
      result.put(BACKUP_BEST_MODEL, m_BestModel);
    if (m_BestStatistics != null)
      result.put(BACKUP_BEST_STATISTICS, m_BestStatistics);
    result.put(BACKUP_EPOCH, m_Epoch);
    if (m_TrainData != null)
      result.put(BACKUP_TRAINDATA, m_TrainData);
    if (m_TestData != null)
      result.put(BACKUP_TESTDATA, m_TestData);
    result.put(BACKUP_TRAINING_FINISHED, m_TrainingFinished);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_MODEL)) {
      m_ActualModel = (Model) state.get(BACKUP_MODEL);
      state.remove(BACKUP_MODEL);
    }
    if (state.containsKey(BACKUP_BEST_MODEL)) {
      m_BestModel = (Model) state.get(BACKUP_BEST_MODEL);
      state.remove(BACKUP_BEST_MODEL);
    }
    if (state.containsKey(BACKUP_BEST_STATISTICS)) {
      m_BestStatistics = (Map<String,Double>) state.get(BACKUP_BEST_STATISTICS);
      state.remove(BACKUP_BEST_STATISTICS);
    }
    if (state.containsKey(BACKUP_EPOCH)) {
      m_Epoch = (Integer) state.get(BACKUP_EPOCH);
      state.remove(BACKUP_EPOCH);
    }
    if (state.containsKey(BACKUP_TRAINDATA)) {
      m_TrainData = (DataSet) state.get(BACKUP_TRAINDATA);
      state.remove(BACKUP_TRAINDATA);
    }
    if (state.containsKey(BACKUP_TESTDATA)) {
      m_TestData = (DataSet) state.get(BACKUP_TESTDATA);
      state.remove(BACKUP_TESTDATA);
    }
    if (state.containsKey(BACKUP_TRAINING_FINISHED)) {
      m_TrainingFinished = (Boolean) state.get(BACKUP_TRAINING_FINISHED);
      state.remove(BACKUP_TRAINING_FINISHED);
    }

    super.restoreState(state);
  }

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  @Override
  public void variableChanged(VariableChangeEvent e) {
    super.variableChanged(e);
    if ((e.getType() == Type.MODIFIED) || (e.getType() == Type.ADDED)) {
      if (e.getName().equals(m_VariableName.getValue())) {
	resetModel();
        if (isLoggingEnabled())
          getLogger().info("Reset model");
      }
    }
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    resetModel();
  }

  /**
   * Resets the model.
   */
  protected void resetModel() {
    m_ActualModel      = null;
    m_BestModel        = null;
    m_BestStatistics   = new HashMap<>();
    m_Epoch            = 0;
    m_TrainData        = null;
    m_TestData         = null;
    m_TrainingFinished = false;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->org.nd4j.linalg.dataset.DataSet.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{DataSet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.DL4JModelContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{DL4JModelContainer.class};
  }

  /**
   * Returns an instance of the callable model configurator.
   *
   * @return		the model configurator
   * @throws Exception  if fails to obtain model
   */
  protected ModelConfigurator getModelConfiguratorInstance() throws Exception {
    ModelConfigurator	result;
    MessageCollection	errors;

    errors = new MessageCollection();
    result = (ModelConfigurator) CallableActorHelper.getSetup(ModelConfigurator.class, m_Model, this, errors);
    if (result == null) {
      if (errors.isEmpty())
	throw new IllegalStateException("Failed to obtain model configurator from '" + m_Model + "'!");
      else
	throw new IllegalStateException("Failed to obtain model configurator from '" + m_Model + "':\n" + errors);
    }

    return result;
  }

  /**
   * Updates the best model, if applicable.
   *
   * @param evalCls	the evaluation in case of classification
   * @param evalReg	the evaluation in case of regression
   */
  protected void updateBestModel(Evaluation evalCls, RegressionEvaluation evalReg) {
    String			result;
    DL4JEvaluationValues	eval;
    SpreadSheet			stats;
    String			statName;
    Double			statValue;
    boolean			better;
    EvaluationStatistic		statEnum;

    eval = new DL4JEvaluationValues();
    eval.setStatisticValues(m_StatisticValues);
    eval.setClassIndex(m_ClassIndex);
    eval.setRegressionColumns(m_RegressionColumns);
    eval.input(new Token((evalCls != null) ? evalCls : evalReg));
    result = eval.execute();
    if (result == null) {
      stats = (SpreadSheet) eval.output().getPayload();
      better = true;
      if (!m_BestStatistics.isEmpty()) {
	for (Row row: stats.rows()) {
	  statName  = row.getCell(0).getContent();
	  statValue = row.getCell(1).toDouble();
	  statEnum  = EvaluationStatistic.valueOf((AbstractOption) null, statName);
	  if (statEnum.compare(statValue, m_BestStatistics.get(statName)) > 0) {
	    better = false;
	    break;
	  }
	}
      }
      // better model found?
      if (better) {
	m_BestModel = m_ActualModel;
	for (Row row: stats.rows()) {
	  statName  = row.getCell(0).getContent();
	  statValue = row.getCell(1).toDouble();
	  m_BestStatistics.put(statName, statValue);
	}
      }
    }
  }

  /**
   * Iterates through the epochs.
   *
   * @return		null if successful, otherwise error message
   */
  protected String iterate() {
    String			result;
    ShufflingDataSetIterator 	iter;
    Evaluation 			evalCls;
    RegressionEvaluation 	evalReg;
    DL4JModelContainer		cont;

    result  = null;
    evalCls = null;
    evalReg = null;

    try {
      do {
	m_Epoch++;
	if (isLoggingEnabled() && (m_Epoch % 100 == 0))
	  getLogger().info("#epoch: " + m_Epoch);
	if (m_ActualModel instanceof MultiLayerNetwork) {
	  if (m_MiniBatchSize < 1) {
	    ((MultiLayerNetwork) m_ActualModel).fit(m_TrainData);
	  }
	  else {
	    iter = new ShufflingDataSetIterator(m_TrainData, m_MiniBatchSize, (int) m_Seed);
	    while (iter.hasNext())
	      ((MultiLayerNetwork) m_ActualModel).fit(iter.next());
	  }
	}
	else {
	  if (m_MiniBatchSize < 1) {
	    m_ActualModel.fit(m_TrainData.getFeatureMatrix());
	  }
	  else {
	    iter = new ShufflingDataSetIterator(m_TrainData, m_MiniBatchSize, (int) m_Seed);
	    while (iter.hasNext() && !isStopped())
	      m_ActualModel.fit(iter.next().getFeatureMatrix());
	  }
	}
	if ((m_OutputInterval > 0) && (m_Epoch % m_OutputInterval == 0))
	  break;
	if (isStopped())
	  break;

	// check whether training should be stopped
	m_TrainingFinished = m_TrainStop.checkStopping(
	  new DL4JModelContainer(m_ActualModel, m_TrainData, m_Epoch));
      }
      while (!m_TrainingFinished);

      // testing?
      if ((m_TestData != null) && !isStopped()) {
	switch (m_Type) {
	  case CLASSIFICATION:
	    evalCls = new Evaluation(m_TrainData.numOutcomes());
	    evalCls.eval(m_TestData.getLabels(), ((MultiLayerNetwork) m_ActualModel).output(m_TestData.getFeatureMatrix(), Layer.TrainingMode.TEST));
	    break;

	  case REGRESSION:
	    evalReg = new RegressionEvaluation(m_TrainData.numOutcomes());
	    evalReg.eval(m_TestData.getLabels(), ((MultiLayerNetwork) m_ActualModel).output(m_TestData.getFeatureMatrix(), Layer.TrainingMode.TEST));
	    break;

	  default:
	    throw new IllegalStateException("Unhandled evaluation type: " + m_Type);
	}

	// best model?
	updateBestModel(evalCls, evalReg);
      }

      if (!isStopped()) {
	if (evalCls != null)
	  cont = new DL4JModelContainer(m_ActualModel, m_TrainData, m_Epoch, evalCls, (m_OutputBestModel ? m_BestModel : null), m_BestStatistics);
	else if (evalReg != null)
	  cont = new DL4JModelContainer(m_ActualModel, m_TrainData, m_Epoch, evalReg, (m_OutputBestModel ? m_BestModel : null), m_BestStatistics);
	else
	  cont = new DL4JModelContainer(m_ActualModel, m_TrainData, m_Epoch, null, (m_OutputBestModel ? m_BestModel : null), m_BestStatistics);
	m_OutputToken = new Token(cont);

	// check whether training should be stopped
	if (!m_TrainingFinished)
	  m_TrainingFinished = m_TrainStop.checkStopping(cont);
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to process data (epoch: " + m_Epoch + "):", e);
    }

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    DataSet			data;
    DataSet[]			split;
    ModelConfigurator		conf;
    List<IterationListener> 	listeners;

    result = null;

    try {
      m_TrainingFinished = false;

      // data
      data = (DataSet) m_InputToken.getPayload();
      if (m_TestPercentage > 0) {
	split       = DataSetHelper.split(data, 1.0 - m_TestPercentage, m_Seed);
	m_TrainData = split[0];
	m_TestData  = split[1];
	if (isLoggingEnabled())
	  getLogger().info("Splitting data into train/test using " + m_TestPercentage + " for training.");
      }
      else {
	m_TrainData = data;
	m_TestData  = null;
	if (isLoggingEnabled())
	  getLogger().info("Using all data for training.");
      }

      // set up model
      if (m_ActualModel == null) {
	conf          = getModelConfiguratorInstance();
	m_ActualModel = conf.configureModel(m_TrainData.numInputs(), m_TrainData.numOutcomes());
	if (m_ActualModel == null)
	  result = "Failed to obtain model?";
      }

      if (result == null) {
	// set listeners
	listeners = new ArrayList<>();
	for (IterationListenerConfigurator l: m_IterationListeners) {
	  l.setFlowContext(this);
	  listeners.addAll(l.configureIterationListeners());
	}
	m_ActualModel.setListeners(listeners);

	// init
	if (m_ActualModel instanceof MultiLayerNetwork)
	  ((MultiLayerNetwork) m_ActualModel).init();
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to process data:", e);
    }

    if (result == null) {
      m_TrainStop.start();
      result = iterate();
    }

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.MODEL_GENERATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return super.hasPendingOutput() || ((m_Epoch > 0) && !m_TrainingFinished);
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    if (m_OutputToken == null) {
      if (!m_TrainingFinished)
	iterate();
    }

    result        = m_OutputToken;
    m_OutputToken = null;

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();
    resetModel();
  }
}
