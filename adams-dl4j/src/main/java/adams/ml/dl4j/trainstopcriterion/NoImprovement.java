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
 * NoImprovement.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.trainstopcriterion;

import adams.core.Index;
import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.flow.container.DL4JModelContainer;
import adams.ml.dl4j.EvaluationHelper;
import adams.ml.dl4j.EvaluationStatistic;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.RegressionEvaluation;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NoImprovement
  extends AbstractTrainStopCriterion {

  private static final long serialVersionUID = 6975594226423139162L;

  /** the statistics to check. */
  protected EvaluationStatistic[] m_Statistics;

  /** the range of the class labels. */
  protected Index m_ClassIndex;

  /** the regression columns to get statistics for. */
  protected Index m_RegressionColumns;

  /** the historic statistics. */
  protected Map<EvaluationStatistic,Double> m_History;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Monitors one or more statistics, whether they improve at all over time.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "statistic", "statistics",
      new EvaluationStatistic[]{EvaluationStatistic.ACCURACY});

    m_OptionManager.add(
      "index", "classIndex",
      new Index(Index.FIRST));

    m_OptionManager.add(
      "regression-columns", "regressionColumns",
      new Index(Index.LAST));
  }

  /**
   * Sets the statistics to monitor.
   *
   * @param value	the statistics
   */
  public void setStatistics(EvaluationStatistic[] value) {
    m_Statistics = value;
    reset();
  }

  /**
   * Returns the statistics to monitor.
   *
   * @return  		the statistics
   */
  public EvaluationStatistic[] getStatistics() {
    return m_Statistics;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticsTipText() {
    return "The statistics to monitor.";
  }

  /**
   * Sets the range of class labels indices (1-based).
   *
   * @param value	the label indices
   */
  public void setClassIndex(Index value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the current range of class label indices (1-based).
   *
   * @return		the label indices
   */
  public Index getClassIndex() {
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
  public void setRegressionColumns(Index value) {
    m_RegressionColumns = value;
    reset();
  }

  /**
   * Returns the range of columns to get regression statistics for.
   *
   * @return		the column indices
   */
  public Index getRegressionColumns() {
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

    result = QuickInfoHelper.toString(this, "statistics", m_Statistics.length + " statistic" + (m_Statistics.length == 1 ? "" : "s"));
    result += QuickInfoHelper.toString(this, "classIndex", m_ClassIndex, ", class labels: ");
    result += QuickInfoHelper.toString(this, "regressionColumns", m_RegressionColumns, ", reg cols: ");

    return result;
  }

  /**
   * For initializing the scheme.
   */
  @Override
  public void start() {
    super.start();
    m_History = new HashMap<>();
  }

  /**
   * Returns whether a flow context is required or optional.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresFlowContext() {
    return false;
  }

  /**
   * Performs the actual checking for stopping the training.
   *
   * @param cont	the container to use for stopping
   * @param triggers	for storing trigger messages
   * @return		true if to stop training
   */
  @Override
  protected boolean doCheckStopping(DL4JModelContainer cont, MessageCollection triggers) {
    boolean 			result;
    Object			eval;
    Evaluation			evalCls;
    RegressionEvaluation	evalReg;
    double 			value;

    if (!cont.hasValue(DL4JModelContainer.VALUE_EVALUATION))
      return false;

    eval   = cont.getValue(DL4JModelContainer.VALUE_EVALUATION);
    result = true;
    for (EvaluationStatistic stat : m_Statistics) {
      try {
	if (eval instanceof Evaluation) {
	  evalCls = (Evaluation) eval;
	  m_ClassIndex.setMax(evalCls.falseNegatives().size());  // TODO better way of retrieving numClasses???
	  value = EvaluationHelper.getValue(evalCls, stat, m_ClassIndex.getIntIndex());
	}
	else if (eval instanceof RegressionEvaluation) {
	  evalReg = (RegressionEvaluation) eval;
	  m_RegressionColumns.setMax(evalReg.numColumns());
	  value = EvaluationHelper.getValue(evalReg, stat, m_RegressionColumns.getIntIndex());
	}
	else {
	  throw new IllegalStateException("Unhandled evaluation class: " + eval.getClass().getName());
	}
	if (m_History.isEmpty()) {
	  result = false;
	}
	else {
	  if (m_History.get(stat) != value)
	    result = false;
	}
	if (!result)
	  m_History.put(stat, value);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to obtain statistic " + stat + " from " + eval.getClass().getName() + " object!", e);
	result = false;
      }
    }

    if (isLoggingEnabled()) {
      if (result)
	getLogger().info("No improvement: " + m_History);
      else
	getLogger().fine("Change: " + m_History);
    }

    return result;
  }
}
