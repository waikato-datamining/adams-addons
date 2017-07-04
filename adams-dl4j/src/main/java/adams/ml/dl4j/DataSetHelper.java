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
 * DataSetHelper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j;

import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Random;

/**
 * Helper class for DataSet-related operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataSetHelper {

  /**
   * Compares the structure of the two datasets.
   *
   * @param data1	the first dataset
   * @param data2	the second dataset
   * @return		true if compatible
   */
  public static boolean equalStructure(DataSet data1, DataSet data2) {
    return (equalStructureMsg(data1, data2) == null);
  }

  /**
   * Compares the structure of the two datasets.
   *
   * @param data1	the first dataset
   * @param data2	the second dataset
   * @return		null if compatible, otherwise error message
   */
  public static String equalStructureMsg(DataSet data1, DataSet data2) {
    if (data1.numInputs() != data2.numInputs())
      return "Number of inputs differ: " + data1.numInputs() + " != " + data2.numInputs();
    if (data1.numOutcomes() != data2.numOutcomes())
      return "Number of outcomes differ: " + data1.numOutcomes() + " != " + data2.numOutcomes();
    if ((data1.getLabels() == null) && (data2.getLabels() != null))
      return "First dataset has no labels, but second does!";
    if ((data1.getLabels() != null) && (data2.getLabels() == null))
      return "First dataset has labels, but second doesn't!";
    if (data1.getLabels() != null) {
      if (data1.getLabelNamesList().size() != data2.getLabelNamesList().size())
	return "Number of labels differ: " + data1.getLabelNamesList().size() + " != " + data2.getLabelNamesList().size();
    }
    return null;
  }

  /**
   * Performs a train/test split, preserving order.
   *
   * @param data	the data to split
   * @param perc	the percentage (0-1)
   * @return		the split
   */
  public static DataSet[] split(DataSet data, double perc) {
    return split(data, perc, null);
  }

  /**
   * Performs a train/test split.
   *
   * @param data	the data to split
   * @param perc	the percentage (0-1)
   * @param seed 	the seed for randomization, preserves order if null
   * @return		the split
   */
  public static DataSet[] split(DataSet data, double perc, Long seed) {
    SplitTestAndTrain	split;

    if (seed != null) {
      Nd4j.shuffle(data.getFeatureMatrix(), new Random(seed), 1);
      if (data.getLabels() != null)
        Nd4j.shuffle(data.getLabels(), new Random(seed), 1);
    }

    split = data.splitTestAndTrain(perc);
    return new DataSet[]{split.getTrain(), split.getTest()};
  }
}
