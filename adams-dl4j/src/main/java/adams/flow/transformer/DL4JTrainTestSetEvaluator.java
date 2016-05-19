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
 * DL4JTrainTestSetEvaluator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.flow.container.DL4JEvaluationContainer;
import adams.flow.container.DL4JTrainTestSetContainer;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.ml.dl4j.model.ModelConfigurator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.DataSet;

/**
 <!-- globalinfo-start -->
 * Trains a model on an incoming training dataset (from a container) and then evaluates it on the test set (also from a container).<br>
 * The model setup being used in the evaluation is obtained from the callable actor returning a model configurator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.DL4JTrainTestSetContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.DL4JTrainTestSetContainer: Train, Test, Seed
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DL4JTrainTestSetEvaluator
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-always-use-container &lt;boolean&gt; (property: alwaysUseContainer)
 * &nbsp;&nbsp;&nbsp;If enabled, always outputs an evaluation container.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-model &lt;adams.flow.core.CallableActorReference&gt; (property: model)
 * &nbsp;&nbsp;&nbsp;The callable model configurator actor to obtain the model from to train 
 * &nbsp;&nbsp;&nbsp;and evaluate on the test data.
 * &nbsp;&nbsp;&nbsp;default: DL4JModelConfigurator
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JTrainTestSetEvaluator
  extends AbstractCallableDL4JModelEvaluator
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -1092101024095887007L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Trains a model on an incoming training dataset (from a container) "
        + "and then evaluates it on the test set (also from a container).\n"
        + "The model setup being used in the evaluation is obtained from the "
        + "callable actor returning a model configurator.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String modelTipText() {
    return "The callable model configurator actor to obtain the model from to train and evaluate on the test data.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.DL4JTrainTestSetContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{DL4JTrainTestSetContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    DataSet 			train;
    DataSet			test;
    ModelConfigurator 		conf;
    Model			model;
    Evaluation 			eval;
    DL4JTrainTestSetContainer	cont;

    result = null;

    try {
      conf = getModelConfiguratorInstance();
      if (conf == null)
	throw new IllegalStateException("Classifier '" + getModel() + "' not found!");

      cont  = (DL4JTrainTestSetContainer) m_InputToken.getPayload();
      train = (DataSet) cont.getValue(DL4JTrainTestSetContainer.VALUE_TRAIN);
      test  = (DataSet) cont.getValue(DL4JTrainTestSetContainer.VALUE_TEST);
      model = conf.configureModel(train.numInputs(), train.numOutcomes());
      eval  = null;
      if (model instanceof MultiLayerNetwork)
	((MultiLayerNetwork) model).fit(train);
      else
        result = "Can only evaluate " + MultiLayerNetwork.class.getName() + "!";
      if (result == null) {
	eval = new Evaluation(train.numOutcomes());
	eval.eval(test.getLabels(), ((MultiLayerNetwork) model).output(test.getFeatureMatrix(), Layer.TrainingMode.TEST));
      }

      // broadcast result
      if (eval != null) {
	if (m_AlwaysUseContainer)
	  m_OutputToken = new Token(new DL4JEvaluationContainer(eval, conf));
	else
	  m_OutputToken = new Token(eval.stats());
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to evaluate: ", e);
    }

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.EVALUATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
