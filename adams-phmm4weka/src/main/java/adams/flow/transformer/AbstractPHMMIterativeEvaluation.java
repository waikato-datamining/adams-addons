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
 * AbstractPHMMIterativeEvaluation.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.classifiers.evaluation.output.prediction.Null;
import weka.classifiers.meta.IterativeHMMClassifier;
import weka.core.Instances;
import weka.utils.AbstractIterativeEvaluation;
import weka.utils.AbstractIterativeEvaluation.EvaluationStep;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 * Ancestor for evaluators using IterativeEvaluation schemes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPHMMIterativeEvaluation<T extends IterativeHMMClassifier, E extends AbstractIterativeEvaluation>
  extends AbstractGlobalPHMMClassifierEvaluator<T>
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 7476193204422224840L;

  /** the number of folds. */
  protected int m_Folds;

  /** the number of iterations to perform. */
  protected int m_NumIterations;

  /** the step size for the iterations. */
  protected int m_IterationStepSize;

  /** the evaluation iterator. */
  protected transient AbstractIterativeEvaluation.EvaluationIterator m_Iterator;

  /** the current evaluation step. */
  protected EvaluationStep m_CurrentStep;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "folds", "folds",
	    10, -1, null);

    m_OptionManager.add(
	    "num-iterations", "numIterations",
	    1, 1, null);

    m_OptionManager.add(
	    "iteration-step-size", "iterationStepSize",
	    1, 1, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "classifier", m_Classifier);
    result += QuickInfoHelper.toString(this, "folds", m_Folds, ", ");
    result += QuickInfoHelper.toString(this, "numIterations", m_NumIterations, ", ");
    result += QuickInfoHelper.toString(this, "iterationStepSize", m_IterationStepSize, "/");

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
    return "The global PHMM classifier actor to evaluate on the input data.";
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
   * Sets the number of iterations to perform.
   *
   * @param value	the number of iterations
   */
  public void setNumIterations(int value) {
    m_NumIterations = value;
    reset();
  }

  /**
   * Returns the number of iterations to perform.
   *
   * @return		the number of iterations
   */
  public int getNumIterations() {
    return m_NumIterations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numIterationsTipText() {
    return "The number of iterations to perform.";
  }

  /**
   * Sets the step size for the iterations.
   *
   * @param value	the step size
   */
  public void setIterationStepSize(int value) {
    m_IterationStepSize = value;
    reset();
  }

  /**
   * Returns the step size for the iterations.
   *
   * @return		the step size
   */
  public int getIterationStepSize() {
    return m_IterationStepSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String iterationStepSizeTipText() {
    return "The step size for the iterations.";
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
   * Instantiates a new evaluation object.
   *
   * @return		the evaluation object
   */
  protected abstract E newEvaluation();

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Instances		data;
    T			cls;
    int			folds;
    E			eval;

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

      eval = newEvaluation();
      eval.setOutput(m_Output);
      eval.setClassifier(cls);
      eval.setFolds(folds);
      eval.setUpperBoundIterations(m_NumIterations);
      eval.setIterationSteps(m_IterationStepSize);
      eval.setTrainingSet(data);
      m_Iterator = eval.iterator();

      m_CurrentStep = m_Iterator.next();
    }
    catch (Exception e) {
      result = handleException("Failed to evaluate: ", e);
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_CurrentStep != null) || ((m_Iterator != null) && m_Iterator.hasNext());
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = null;

    if ((m_CurrentStep == null) && (m_Iterator != null) && m_Iterator.hasNext())
      m_CurrentStep = m_Iterator.next();

    // broadcast result
    if (m_CurrentStep != null) {
      if (m_Output instanceof Null)
	result = new Token(new WekaEvaluationContainer(m_CurrentStep.getEvaluation()));
      else
	result = new Token(m_Output.getBuffer().toString());

      if (m_OutputToken != null)
	updateProvenance(result);

      m_CurrentStep = null;
    }

    return result;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Iterator != null) {
      m_Iterator.stop();

      while (!m_Iterator.isStopped()) {
	try {
	  synchronized(this) {
	    wait(100);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }

      m_Iterator = null;
    }

    super.stopExecution();
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
      cont.addProvenance(new ProvenanceInformation(ActorType.EVALUATOR, m_InputToken.getPayload().getClass(), this, ((Token) cont).getPayload().getClass()));
    }
  }
}
