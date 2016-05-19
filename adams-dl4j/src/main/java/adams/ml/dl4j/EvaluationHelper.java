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
 * EvaluationHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.ml.dl4j;

import org.deeplearning4j.eval.Evaluation;

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
      default:
        throw new IllegalArgumentException("Unhandled statistic field: " + statistic);
    }
  }
}
