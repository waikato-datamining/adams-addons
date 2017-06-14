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
 * WekaInstancesToDL4JDataSet.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import weka.core.Instance;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Converts a Weka Instances object to a DL4J DataSet.<br>
 * Assumes missing values to be imputed and nominal attributes to be binarized.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 * @version $Revision$
 */
public class WekaInstancesToDL4JDataSet
  extends AbstractConversion {

  private static final long serialVersionUID = -7278857064645982416L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Converts a Weka Instances object to a DL4J DataSet.\n"
      + "Assumes missing values to be imputed and nominal attributes to be binarized.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Instances.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return DataSet.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Instances		insts;
    INDArray 		data;
    double[][] 		outcomes;
    int			i;
    int			j;
    double[] 		independent;
    int 		index;
    Instance 		current;

    insts    = (Instances) m_Input;
    data     = Nd4j.ones(insts.numInstances(), insts.numAttributes() - 1);
    outcomes = new double[insts.numInstances()][(insts.classAttribute().numValues() == 0) ? 1 : insts.classAttribute().numValues()];

    for (i = 0; i < insts.numInstances(); i++) {
      independent = new double[insts.numAttributes() - 1];
      index       = 0;
      current     = insts.instance(i);
      for (j = 0; j < insts.numAttributes(); j++) {
        if (j != insts.classIndex()) {
          independent[index++] = current.value(j);
        }
	else {
          // if classification
          if (insts.numClasses() > 1)
            outcomes[i][(int) current.classValue()] = 1;
          else
            outcomes[i][0] = current.classValue();
        }
      }
      data.putRow(i, Nd4j.create(independent));
    }

    return new DataSet(data, Nd4j.create(outcomes));
  }
}
