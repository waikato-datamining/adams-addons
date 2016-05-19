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
 * DL4JScoring.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.container.DL4JPredictionContainer;
import adams.flow.core.Token;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JScoring
  extends AbstractProcessDL4JDatasetWithModel<Model> {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** whether to output weka.core.Instance objects or PredictionContainers. */
  protected boolean m_AddRegularizationTerms;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Uses a serialized model to perform predictions on the data being "
      + "passed through.\n"
      + "The model can also be obtained from a callable actor, if the model "
      + "file is pointing to a directory.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "add-regularization-terms", "addRegularizationTerms",
      false);
  }

  /**
   * Sets whether to add regularization terms.
   *
   * @param value	if true to add terms
   */
  public void setAddRegularizationTerms(boolean value) {
    m_AddRegularizationTerms = value;
    reset();
  }

  /**
   * Returns whether to add regularization terms.
   *
   * @return		true if to add terms
   */
  public boolean getAddRegularizationTerms() {
    return m_AddRegularizationTerms;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addRegularizationTermsTipText() {
    return "Whether to add regularization terms.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result = super.getQuickInfo();

    value = QuickInfoHelper.toString(this, "addRegularizationTerms", m_AddRegularizationTerms, "add regularization terms", ", ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.PredictionContainer.class, weka.core.Instance.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{DL4JPredictionContainer.class};
  }

  /**
   * Processes the instance and generates the output token.
   *
   * @param data	the instance to process
   * @return		the generated output token (e.g., container)
   * @throws Exception	if processing fails
   */
  @Override
  protected Token processDataset(DataSet data) throws Exception {
    Token			result;
    DL4JPredictionContainer	cont;
    INDArray			scores;

    result = null;

    if (m_Model instanceof MultiLayerNetwork) {
      scores = ((MultiLayerNetwork) m_Model).scoreExamples(data, m_AddRegularizationTerms);
      cont   = new DL4JPredictionContainer(data, scores);
      result = new Token(cont);
    }
    else {
      getLogger().severe("Can only use " + MultiLayerNetwork.class.getName() + " for scoring!");
    }

    return result;
  }
}
