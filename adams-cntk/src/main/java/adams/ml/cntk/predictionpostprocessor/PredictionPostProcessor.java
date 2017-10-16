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
 * PredictionPostProcessor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.predictionpostprocessor;

/**
 * Interface for classes that post-process CNTK predictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface PredictionPostProcessor {

  /**
   * Post-processes the predictions.
   *
   * @param preds	the predictions
   * @return		the processed predictions
   */
  public float[] postProcessPrediction(float[] preds);
}
