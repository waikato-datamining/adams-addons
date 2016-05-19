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
 * DL4JTestSetEvaluator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.container.DL4JEvaluationContainer;
import adams.flow.container.DL4JModelContainer;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.flow.source.CallableSource;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.DataSet;

/**
 <!-- globalinfo-start -->
 * Evaluates a trained model (obtained from input) on the dataset obtained from the callable actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;org.deeplearning4j.nn.api.Model<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.DL4JModelContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.DL4JModelContainer: Model, Dataset
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
 * &nbsp;&nbsp;&nbsp;default: DL4JTestSetEvaluator
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
 * <pre>-testset &lt;adams.flow.core.CallableActorReference&gt; (property: testset)
 * &nbsp;&nbsp;&nbsp;The callable actor to use for obtaining the test set.
 * &nbsp;&nbsp;&nbsp;default: Testset
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DL4JTestSetEvaluator
  extends AbstractDL4JModelEvaluator
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -8528709957864675275L;

  /** the name of the callable trainset provider. */
  protected CallableActorReference m_Testset;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates a trained model (obtained from input) on the dataset "
      + "obtained from the callable actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "testset", "testset",
      new CallableActorReference("Testset"));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "testset", m_Testset);
  }

  /**
   * Sets the name of the callable testset to use.
   *
   * @param value	the name
   */
  public void setTestset(CallableActorReference value) {
    m_Testset = value;
    reset();
  }

  /**
   * Returns the name of the callable testset in use.
   *
   * @return		the name
   */
  public CallableActorReference getTestset() {
    return m_Testset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testsetTipText() {
    return "The callable actor to use for obtaining the test set.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->org.deeplearning4j.nn.api.Model.class, adams.flow.container.DL4JModelContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Model.class, DL4JModelContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    DataSet 			test;
    Evaluation 			eval;
    Model 			model;
    CallableSource		gs;
    Token			output;

    result = null;

    try {
      // get test set
      test = null;
      gs   = new CallableSource();
      gs.setCallableName(m_Testset);
      gs.setParent(getParent());
      gs.setUp();
      gs.execute();
      output = gs.output();
      if (output != null)
	test = (DataSet) output.getPayload();
      else
	result = "No test set available!";
      gs.wrapUp();

      // evaluate model
      if (result == null) {
	if (m_InputToken.getPayload() instanceof Model)
	  model = (Model) m_InputToken.getPayload();
	else
	  model = (Model) ((DL4JModelContainer) m_InputToken.getPayload()).getValue(DL4JModelContainer.VALUE_MODEL);
	eval = null;
	if (model instanceof MultiLayerNetwork) {
	  eval = new Evaluation(test.numOutcomes());
	  eval.eval(test.getLabels(), ((MultiLayerNetwork) model).output(test.getFeatureMatrix(), Layer.TrainingMode.TEST));
	}
	else {
	  result = "Can only evaluate " + MultiLayerNetwork.class.getName() + "!";
	}

	// broadcast result
	if (eval != null) {
	  if (m_AlwaysUseContainer)
	    m_OutputToken = new Token(new DL4JEvaluationContainer(eval, model));
	  else
	    m_OutputToken = new Token(eval.stats());
	}
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
