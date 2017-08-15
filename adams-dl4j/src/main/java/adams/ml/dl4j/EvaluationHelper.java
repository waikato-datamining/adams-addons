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
 * EvaluationHelper.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.ml.dl4j;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.eval.RegressionEvaluation;

/**
 * A helper class for Evaluation related things.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EvaluationHelper {

  /**
   * Returns a statistical value from the evaluation object.
   *
   * @param eval	the evaluation object to get the value from
   * @param statistic	the type of value to return
   * @param classIndex	the class label index, for statistics like AUC
   * @return		the determined value
   * @throws Exception	if evaluation fails
   */
  public static double getValue(Evaluation eval, EvaluationStatistic statistic, int classIndex) throws Exception {
    switch (statistic) {
      case ACCURACY:
        return eval.accuracy();
      case CLASS_COUNT:
        return eval.classCount(classIndex);
      case F1:
        return eval.f1();
      case F1_CLASS:
        return eval.f1(classIndex);
      case FALSE_ALARM_RATE:
        return eval.falseAlarmRate();
      case FALSE_NEGATIVE_RATE:
        return eval.falseNegativeRate();
      case FALSE_NEGATIVE_RATE_CLASS:
        return eval.falseNegativeRate(classIndex);
      case FALSE_POSITIVE_RATE:
        return eval.falsePositiveRate();
      case FALSE_POSITIVE_RATE_CLASS:
        return eval.falsePositiveRate(classIndex);
      case PRECISION:
        return eval.precision();
      case PRECISION_CLASS:
        return eval.precision(classIndex);
      case RECALL:
        return eval.recall();
      case RECALL_CLASS:
        return eval.recall(classIndex);
      case ROW_COUNT:
	return eval.getNumRowCounter();
      case AVG_F1_NUM_CLASSES_EXCLUDED:
        return eval.averageF1NumClassesExcluded();
      case AVG_F_BETA_NUM_CLASSES_EXCLUDED:
        return eval.averageFBetaNumClassesExcluded();
      case AVG_PRECISION_NUM_CLASSES_EXCLUDED:
        return eval.averagePrecisionNumClassesExcluded();
      case AVG_RECALL_NUM_CLASSES_EXCLUDED:
        return eval.averageRecallNumClassesExcluded();
      default:
        if (statistic.isRegression() && !statistic.isClassification())
          throw new IllegalArgumentException("Regression statistic cannot be used on classification evaluation: " + statistic);
        else
          throw new IllegalArgumentException("Unhandled statistic field: " + statistic);
    }
  }

  /**
   * Returns a statistical value from the evaluation object.
   *
   * @param eval	the evaluation object to get the value from
   * @param statistic	the type of value to return
   * @param column	the column
   * @return		the determined value
   * @throws Exception	if evaluation fails
   */
  public static double getValue(RegressionEvaluation eval, EvaluationStatistic statistic, int column) throws Exception {
    switch (statistic) {
      case CORRELATION_R_SQUARED:
        return eval.correlationR2(column);
      case MEAN_ABSOLUTE_ERROR:
        return eval.meanAbsoluteError(column);
      case MEAN_SQUARED_ERROR:
        return eval.meanSquaredError(column);
      case RELATIVE_SQUARED_ERROR:
        return eval.relativeSquaredError(column);
      case ROOT_MEAN_SQUARED_ERROR:
        return eval.rootMeanSquaredError(column);
      case AVG_CORRELATION_R_SQUARED:
        return eval.averagecorrelationR2();
      case AVG_MEAN_ABSOLUTE_ERROR:
        return eval.averageMeanAbsoluteError();
      case AVG_MEAN_SQUARED_ERROR:
        return eval.averageMeanSquaredError();
      case AVG_RELATIVE_SQUARED_ERROR:
        return eval.averagerelativeSquaredError();
      case AVG_ROOT_MEAN_SQUARED_ERROR:
        return eval.averagerootMeanSquaredError();
      default:
        if (!statistic.isRegression() && statistic.isClassification())
          throw new IllegalArgumentException("Classification statistic cannot be used on regression evaluation: " + statistic);
        else
          throw new IllegalArgumentException("Unhandled statistic field: " + statistic);
    }
  }
}
