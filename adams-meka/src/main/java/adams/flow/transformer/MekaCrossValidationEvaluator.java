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
 * MekaCrossValidationEvaluator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.flow.container.MekaResultContainer;
import adams.flow.core.Token;
import meka.classifiers.multilabel.Evaluation;
import meka.core.Result;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Cross-validates a Meka classifier on an incoming dataset. The classifier setup being used in the evaluation is a callable source outputting a Meka classifier.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.MekaResultContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.MekaResultContainer: Result, Model
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
 * &nbsp;&nbsp;&nbsp;default: MekaCrossValidationEvaluator
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
 * <pre>-classifier &lt;adams.flow.core.CallableActorReference&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The callable source actor for obtaining the classifier setup to cross-validate 
 * &nbsp;&nbsp;&nbsp;on the input data.
 * &nbsp;&nbsp;&nbsp;default: MekaClassifierSetup
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the cross-validation (used for randomization).
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The number of folds to use in the cross-validation; use -1 for leave-one-out 
 * &nbsp;&nbsp;&nbsp;cross-validation (LOOCV).
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MekaCrossValidationEvaluator
  extends AbstractCallableMekaClassifierEvaluator
  implements Randomizable {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;
  
  /** the number of folds. */
  protected int m_Folds;

  /** the seed value. */
  protected long m_Seed;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Cross-validates a Meka classifier on an incoming dataset. The classifier "
      + "setup being used in the evaluation is a callable source outputting a Meka classifier.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "seed", "seed",
	    1L);

    m_OptionManager.add(
	    "folds", "folds",
	    10, -1, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();

    result += QuickInfoHelper.toString(this, "folds", m_Folds, ", folds: ");
    result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");

    return result;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String classifierTipText() {
    return "The callable source actor for obtaining the classifier setup to cross-validate on the input data.";
  }

  /**
   * Sets the number of folds.
   *
   * @param value	the folds, -1 for LOOCV
   */
  public void setFolds(int value) {
    if ((value == -1) || (value >= 2)) {
      m_Folds = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Number of folds must be >=2 or -1 for LOOCV, provided: " + value);
    }
  }

  /**
   * Returns the number of folds.
   *
   * @return		the folds
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText() {
    return "The number of folds to use in the cross-validation; use -1 for leave-one-out cross-validation (LOOCV).";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value for the cross-validation (used for randomization).";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String						result;
    Instances						data;
    meka.classifiers.multilabel.MultiLabelClassifier	cls;
    int							folds;
    Result 						results;

    result = null;

    try {
      // evaluate classifier
      cls = getClassifierInstance();
      if (cls == null)
	throw new IllegalStateException("Classifier '" + getClassifier() + "' not found!");

      data = (Instances) m_InputToken.getPayload();
      folds = m_Folds;
      if (folds == -1)
	folds = data.numInstances();

      results       = Evaluation.cvModel(cls, data, folds, "PCut1", "3");   // TODO options?
      m_OutputToken = new Token(new MekaResultContainer(results));
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to cross-validate classifier: ", e);
    }

    return result;
  }
}
