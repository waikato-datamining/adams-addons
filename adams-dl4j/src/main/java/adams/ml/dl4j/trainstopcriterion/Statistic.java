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
 * Statistic.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.trainstopcriterion;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.flow.container.DL4JModelContainer;
import adams.ml.dl4j.EvaluationHelper;
import adams.ml.dl4j.EvaluationStatistic;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.RegressionEvaluation;

import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Monitors a statistic, whether it goes below or above a threshold.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-statistic &lt;ACCURACY|CLASS_COUNT|F1|F1_CLASS|FALSE_ALARM_RATE|FALSE_NEGATIVE_RATE|FALSE_NEGATIVE_RATE_CLASS|FALSE_POSITIVE_RATE|FALSE_POSITIVE_RATE_CLASS|PRECISION|PRECISION_CLASS|RECALL|RECALL_CLASS|ROW_COUNT|CORRELATION_R_SQUARED|MEAN_ABSOLUTE_ERROR|MEAN_SQUARED_ERROR|RELATIVE_SQUARED_ERROR|ROOT_MEAN_SQUARED_ERROR&gt; (property: statistic)
 * &nbsp;&nbsp;&nbsp;The name of the variable to monitor.
 * &nbsp;&nbsp;&nbsp;default: ACCURACY
 * </pre>
 * 
 * <pre>-index &lt;adams.core.Index&gt; (property: classIndex)
 * &nbsp;&nbsp;&nbsp;The range of class label indices (eg used for AUC).
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-regression-columns &lt;adams.core.Index&gt; (property: regressionColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to get regression statistics for.
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-threshold &lt;double&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The threshold for the statistic.
 * &nbsp;&nbsp;&nbsp;default: 0.9
 * </pre>
 * 
 * <pre>-threshold-check &lt;GREATER_OR_EQUAL|GREATER|LESS|LESS_OR_EQUAL&gt; (property: thresholdCheck)
 * &nbsp;&nbsp;&nbsp;Determines how to interpret the threshold.
 * &nbsp;&nbsp;&nbsp;default: GREATER_OR_EQUAL
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Statistic
  extends AbstractTrainStopCriterion {

  private static final long serialVersionUID = 6975594226423139162L;

  /**
   * How to interpret the threshold.
   */
  public enum ThresholdCheck {
    GREATER_OR_EQUAL,
    GREATER,
    LESS,
    LESS_OR_EQUAL,
  }

  /** the statistic to check. */
  protected EvaluationStatistic m_Statistic;

  /** the range of the class labels. */
  protected Index m_ClassIndex;

  /** the regression columns to get statistics for. */
  protected Index m_RegressionColumns;

  /** the threshold. */
  protected double m_Threshold;

  /** how to interpret the threshold. */
  protected ThresholdCheck m_ThresholdCheck;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Monitors a statistic, whether it goes below or above a threshold.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "statistic", "statistic",
      EvaluationStatistic.ACCURACY);

    m_OptionManager.add(
      "index", "classIndex",
      new Index(Index.FIRST));

    m_OptionManager.add(
      "regression-columns", "regressionColumns",
      new Index(Index.LAST));

    m_OptionManager.add(
      "threshold", "threshold",
      0.9);

    m_OptionManager.add(
      "threshold-check", "thresholdCheck",
      ThresholdCheck.GREATER_OR_EQUAL);
  }

  /**
   * Sets the name of the statistic to monitor.
   *
   * @param value	the statistic
   */
  public void setStatistic(EvaluationStatistic value) {
    m_Statistic = value;
    reset();
  }

  /**
   * Returns the name of the statistic to monitor.
   *
   * @return  		the name
   */
  public EvaluationStatistic getStatistic() {
    return m_Statistic;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticTipText() {
    return "The name of the variable to monitor.";
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
   * Sets the threshold to use for the statistic.
   *
   * @param value	the threshold
   */
  public void setThreshold(double value) {
    m_Threshold = value;
    reset();
  }

  /**
   * Returns the threshold to use for the statistic.
   *
   * @return  		the threshold
   */
  public double getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdTipText() {
    return "The threshold for the statistic.";
  }

  /**
   * Sets how to interepte the threshold.
   *
   * @param value	the check
   */
  public void setThresholdCheck(ThresholdCheck value) {
    m_ThresholdCheck = value;
    reset();
  }

  /**
   * Returns how to interpret the threshold.
   *
   * @return  		the check
   */
  public ThresholdCheck getThresholdCheck() {
    return m_ThresholdCheck;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdCheckTipText() {
    return "Determines how to interpret the threshold.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "statistic", m_Statistic);
    result += QuickInfoHelper.toString(this, "thresholdCheck", m_ThresholdCheck, " ");
    result += QuickInfoHelper.toString(this, "threshold", m_Threshold, " ");
    result += QuickInfoHelper.toString(this, "classIndex", m_ClassIndex, ", class labels: ");
    result += QuickInfoHelper.toString(this, "regressionColumns", m_RegressionColumns, ", reg cols: ");
    
    return result;
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
   * @return		true if to stop training
   */
  @Override
  protected boolean doCheckStopping(DL4JModelContainer cont) {
    Object			eval;
    Evaluation			evalCls;
    RegressionEvaluation	evalReg;
    double			stat;

    if (!cont.hasValue(DL4JModelContainer.VALUE_EVALUATION)) {
      if (isLoggingEnabled())
        getLogger().warning("No evaluation object in container, cannot evaluate statistic!");
      return false;
    }

    eval = cont.getValue(DL4JModelContainer.VALUE_EVALUATION);
    try {
      if (eval instanceof Evaluation) {
	evalCls = (Evaluation) eval;
	m_ClassIndex.setMax(evalCls.falseNegatives().size());  // TODO better way of retrieving numClasses???
	stat = EvaluationHelper.getValue(evalCls, m_Statistic, m_ClassIndex.getIntIndex());
      }
      else if (eval instanceof RegressionEvaluation) {
	evalReg = (RegressionEvaluation) eval;
	m_RegressionColumns.setMax(evalReg.numColumns());
	stat = EvaluationHelper.getValue(evalReg, m_Statistic, m_RegressionColumns.getIntIndex());
      }
      else {
	throw new IllegalStateException("Unhandled evaluation class: " + eval.getClass().getName());
      }
      switch (m_ThresholdCheck) {
	case GREATER:
	  return (stat > m_Threshold);
	case GREATER_OR_EQUAL:
	  return (stat >= m_Threshold);
	case LESS:
	  return (stat < m_Threshold);
	case LESS_OR_EQUAL:
	  return (stat <= m_Threshold);
	default:
	  throw new IllegalStateException("Unhandled threshold check: " + m_ThresholdCheck);
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to obtain statistic " + m_Statistic + " from " + eval.getClass().getName() + " object!", e);
      return false;
    }
  }
}
