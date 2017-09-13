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
 * DL4JInitWekaClassifier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.Utils;
import adams.flow.core.Token;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import weka.classifiers.functions.DL4JMultiLayerNetwork;
import weka.core.Instances;
import weka.filters.Filter;

/**
 * TODO: What class does.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JInitWekaClassifier
  extends AbstractTransformer {

  private static final long serialVersionUID = 7480758744491379628L;

  /** default error message. */
  public final static String DEFAULT_ERROR = "Requires array of length 3: DL4J multi-layer network, trained Weka filter, original training data.";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses the incoming array to initialize a " + DL4JMultiLayerNetwork.class + " classifier.\n"
	+ "Array must consist of:\n"
	+ "- DL4J multi-layer network\n"
	+ "- trained Weka filter (used for preprocessing data)\n"
	+ "- original training data";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{DL4JMultiLayerNetwork.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Object[]			input;
    DL4JMultiLayerNetwork	network;

    result = null;

    input = (Object[]) m_InputToken.getPayload();
    if (input.length != 3) {
      result = "Array of length " + input.length + "! " + DEFAULT_ERROR;
    }
    else {
      if (!(input[0] instanceof MultiLayerNetwork))
        result = "1st array element wrong (found: " + Utils.classToString(input[0]) + ")! " + DEFAULT_ERROR;
      else if (!(input[1] instanceof Filter))
        result = "2nd array element wrong (found: " + Utils.classToString(input[1]) + ")! " + DEFAULT_ERROR;
      else if (!(input[2] instanceof Instances))
        result = "3rd array element wrong (found: " + Utils.classToString(input[2]) + ")! " + DEFAULT_ERROR;
    }

    if (result == null) {
      network = new DL4JMultiLayerNetwork();
      network.setTrainedMultiLayerNetwork((MultiLayerNetwork) input[0]);
      network.setTrainedPreFilter((Filter) input[1]);
      network.setTrainingData((Instances) input[2]);
      m_OutputToken = new Token(network);
    }

    return result;
  }
}
