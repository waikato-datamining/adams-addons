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
 * Normalize.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.predictionpostprocessor;

import adams.core.Utils;
import adams.data.statistics.StatUtils;

/**
 * Normalizes the predictions, making them sum up to 1 with the individual
 * values ranging from 0 to 1.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Normalize
  extends AbstractPredictionPostProcessor {

  private static final long serialVersionUID = -7840089137915934092L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Normalizes the predictions, making them sum up to 1 with the individual values ranging from 0 to 1.";
  }

  /**
   * Post-processes the predictions.
   *
   * @param preds	the predictions
   * @return		the processed predictions
   */
  @Override
  public float[] postProcessPrediction(float[] preds) {
    float[]	result;
    float	range;
    float	sum;
    int		i;
    Number	min;
    Number	max;

    min   = StatUtils.min(StatUtils.toNumberArray(preds));
    max   = StatUtils.max(StatUtils.toNumberArray(preds));
    if ((min == null) || (max == null)) {
      getLogger().severe("Failed to determine min and/or max: " + Utils.arrayToString(preds));
      return preds;
    }
    if (min.equals(max))
      return preds;

    range  = max.floatValue() - min.floatValue();
    result = new float[preds.length];
    sum    = 0.0f;
    for (i = 0; i < preds.length; i++) {
      result[i] = (preds[i] - min.floatValue()) / range;
      sum += result[i];
    }
    if (sum > 0) {
      for (i = 0; i < result.length; i++)
	result[i] /= sum;
    }

    return result;
  }
}
