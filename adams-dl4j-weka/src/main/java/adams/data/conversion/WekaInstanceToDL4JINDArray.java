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
 * WekaInstanceToDL4JINDArray.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import weka.core.Instance;

/**
 <!-- globalinfo-start -->
 * Converts a Weka Instance object to a DL4J INDArray.<br>
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
public class WekaInstanceToDL4JINDArray
  extends AbstractConversion {

  private static final long serialVersionUID = -7278857064645982416L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a Weka Instance object to a DL4J INDArray.\n"
      + "Assumes missing values to be imputed and nominal attributes to be binarized.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Instance.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return INDArray.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Instance	inst;
    INDArray 	result;
    double[] 	independent;
    int 	index;
    int 	i;

    inst        = (Instance) m_Input;
    result      = Nd4j.ones(1, inst.numAttributes() - 1);
    independent = new double[inst.numAttributes() - 1];
    index       = 0;
    for (i = 0; i < inst.numAttributes(); i++) {
      if (i != inst.classIndex())
        independent[index++] = inst.value(i);
    }
    result.putRow(0, Nd4j.create(independent));

    return result;
  }
}
