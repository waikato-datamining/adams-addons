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
 * MOARegressing.java
 * Copyright (C) 2015-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.flow.container.MOAPredictionContainer;
import adams.flow.core.AbstractModelLoader;
import adams.flow.core.MOARegressorModelLoader;
import adams.flow.core.Token;
import com.yahoo.labs.samoa.instances.Instance;

/**
 <!-- globalinfo-start -->
 * Uses a serialized MOA model to perform predictions on the data being passed through.<br>
 * The following order is used to obtain the model (when using AUTO):<br>
 * 1. model file present?<br>
 * 2. source actor present?<br>
 * 3. storage item present?<br>
 * Optionally, the model can be updated with data being passed through.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;com.yahoo.labs.samoa.instances.Instance<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.MOAPredictionContainer<br>
 * &nbsp;&nbsp;&nbsp;com.yahoo.labs.samoa.instances.Instance<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.MOAPredictionContainer: Instance, Classification, Classification label, Distribution, Range check, Abstention classification, Abstention classification label, Abstention distribution, Report
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
 * &nbsp;&nbsp;&nbsp;default: MOARegressing
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-model-loading-type &lt;AUTO|FILE|SOURCE_ACTOR|STORAGE&gt; (property: modelLoadingType)
 * &nbsp;&nbsp;&nbsp;Determines how to load the model, in case of AUTO, first the model file
 * &nbsp;&nbsp;&nbsp;is checked, then the callable actor and then the storage.
 * &nbsp;&nbsp;&nbsp;default: AUTO
 * </pre>
 *
 * <pre>-model &lt;adams.core.io.PlaceholderFile&gt; (property: modelFile)
 * &nbsp;&nbsp;&nbsp;The file to load the model from, ignored if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-model-actor &lt;adams.flow.core.CallableActorReference&gt; (property: modelActor)
 * &nbsp;&nbsp;&nbsp;The callable actor (source) to obtain the model from, ignored if not present.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-model-storage &lt;adams.flow.control.StorageName&gt; (property: modelStorage)
 * &nbsp;&nbsp;&nbsp;The storage item to obtain the model from, ignored if not present.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-on-the-fly &lt;boolean&gt; (property: onTheFly)
 * &nbsp;&nbsp;&nbsp;If set to true, the model file is not required to be present at set up time
 * &nbsp;&nbsp;&nbsp;(eg if built on the fly), only at execution time.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-use-model-reset-variable &lt;boolean&gt; (property: useModelResetVariable)
 * &nbsp;&nbsp;&nbsp;If enabled, chnages to the specified variable are monitored in order to
 * &nbsp;&nbsp;&nbsp;reset the model, eg when a storage model changed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-model-reset-variable &lt;adams.core.VariableName&gt; (property: modelResetVariable)
 * &nbsp;&nbsp;&nbsp;The variable to monitor for changes in order to reset the model, eg when
 * &nbsp;&nbsp;&nbsp;a storage model changed.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 * <pre>-output-instance &lt;boolean&gt; (property: outputInstance)
 * &nbsp;&nbsp;&nbsp;Whether to output weka.core.Instance objects or PredictionContainer objects.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-update-model &lt;boolean&gt; (property: updateModel)
 * &nbsp;&nbsp;&nbsp;Whether to update the model with the Instance (in case its class value isn't
 * &nbsp;&nbsp;&nbsp;missing) after making the prediction.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOARegressing<T extends moa.classifiers.Classifier & moa.classifiers.Regressor>
  extends AbstractProcessMOAInstanceWithModel<T> {

  /** for serialization. */
  private static final long serialVersionUID = 5781363684886301467L;

  /** whether to output weka.core.Instance objects or PredictionContainers. */
  protected boolean m_OutputInstance;

  /** whether to update the model after making the prediction. */
  protected boolean m_UpdateModel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses a serialized MOA model to perform predictions on the data being "
        + "passed through.\n"
        + m_ModelLoader.automaticOrderInfo() + "\n"
        + "Optionally, the model can be updated with data being passed through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-instance", "outputInstance",
      false);

    m_OptionManager.add(
      "update-model", "updateModel",
      false);
  }

  /**
   * Instantiates the model loader to use.
   *
   * @return		the model loader to use
   */
  @Override
  protected AbstractModelLoader newModelLoader() {
    return new MOARegressorModelLoader();
  }

  /**
   * Sets whether to output Instance objects instead of PredictionContainer
   * ones.
   *
   * @param value	if true then Instance objects are output
   */
  public void setOutputInstance(boolean value) {
    m_OutputInstance = value;
    reset();
  }

  /**
   * Returns whether Instance objects are output instead of PredictionContainer
   * ones.
   *
   * @return		true if Instance objects are output
   */
  public boolean getOutputInstance() {
    return m_OutputInstance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputInstanceTipText() {
    return "Whether to output weka.core.Instance objects or PredictionContainer objects.";
  }

  /**
   * Sets whether to update the model after making the prediction (in case the
   * class value isn't missing).
   *
   * @param value	if true then update model
   */
  public void setUpdateModel(boolean value) {
    m_UpdateModel = value;
    reset();
  }

  /**
   * Returns whether update the model after making the prediction (in case the
   * class value isn't missing).
   *
   * @return		true if model gets updated
   */
  public boolean getUpdateModel() {
    return m_UpdateModel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updateModelTipText() {
    return "Whether to update the model with the Instance (in case its class value isn't missing) after making the prediction.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.MOAPredictionContainer.class, com.yahoo.labs.samoa.instances.Instance.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{MOAPredictionContainer.class, Instance.class};
  }

  /**
   * Processes the instance and generates the output token.
   *
   * @param inst	the instance to process
   * @return		the generated output token (e.g., container)
   * @throws Exception	if processing fails
   */
  @Override
  protected Token processInstance(Instance inst) throws Exception {
    Token			result;
    MOAPredictionContainer	cont;
    double[]			votes;

    votes = m_Model.getVotesForInstance(inst);
    cont  = new MOAPredictionContainer(
      inst,
      votes[0],
      votes);
    if (m_UpdateModel && !inst.classIsMissing())
      m_Model.trainOnInstance(inst);
    if (m_OutputInstance) {
      inst = (Instance) ((Instance) cont.getValue(MOAPredictionContainer.VALUE_INSTANCE)).copy();
      inst.setClassValue((Double) cont.getValue(MOAPredictionContainer.VALUE_CLASSIFICATION));
      result = new Token(inst);
    }
    else {
      result = new Token(cont.getClone());
    }

    return result;
  }
}