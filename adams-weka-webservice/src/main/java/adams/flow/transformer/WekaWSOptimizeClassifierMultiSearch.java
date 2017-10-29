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
 * WekaWSOptimizeClassifierMultiSearch.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import adams.flow.core.WekaDatasetHelper;
import adams.flow.source.WekaClassifierSetup;
import weka.classifiers.meta.MultiSearch;
import weka.core.Instances;
import weka.core.setupgenerator.AbstractParameter;

/**
 * Performs cross-validation of a classifier on a dataset.
 * 
 * @author msf8
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaWSOptimizeClassifierMultiSearch
extends AbstractTransformer {

  /** for serialization*/
  private static final long serialVersionUID = -1600923751846355040L;

  /**
   * The type of evaluation to perform.
   * 
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Evaluation {
    CORRELATION_COEFFICIENT,
    ROOT_MEAN_SQUARED_ERROR,
    ROOT_RELATIVE_SQUARED_ERROR,
    MEAN_ABSOLUTE_ERROR,
    RELATIVE_ABSOLUTE_ERROR,
    COMBINED,
    ACCURACY,
    KAPPA
  }
  
  /** classifier to use for the cross validation */
  protected weka.classifiers.Classifier m_Classifier;

  /** for getting the classifier as a callable actor */
  protected CallableActorReference m_ClassifierActor;

  /** the search parameters. */
  protected AbstractParameter[] m_Parameters;
  
  /** the statistic to evaluate on. */
  protected Evaluation m_Evaluation;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an optimization request using " + MultiSearch.class.getName() + ", using the Weka web-service.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"classifier", "classifier", 
	new CallableActorReference(WekaClassifierSetup.class.getSimpleName()));

    m_OptionManager.add(
	"parameter", "parameters", 
	new AbstractParameter[0]);

    m_OptionManager.add(
	"evaluation", "evaluation", 
	Evaluation.ACCURACY);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "classifier", m_Classifier, "classifier: ");
    result += QuickInfoHelper.toString(this, "evaluation", m_Evaluation, ", evaluation: ");
    
    return result;
  }

  /**
   * Set the search parameters.
   * 
   * @param value
   *          the search parameters
   */
  public void setParameters(AbstractParameter[] value) {
    m_Parameters = value;
    reset();
  }

  /**
   * Returns the search parameters.
   * 
   * @return 	the search parameters
   */
  public AbstractParameter[] getParameters() {
    return m_Parameters;
  }

  /**
   * description for this option.
   * 
   * @return Description of the seed option
   */
  public String parametersTipText() {
    return "The search parameters to use for the optimization.";
  }

  /**
   * set the classifier to use
   * 
   * @param c
   *          callable actor classifier for cross validation
   */
  public void setClassifier(CallableActorReference c) {
    m_ClassifierActor = c;
    reset();
  }

  /**
   * get the classifier to use
   * 
   * @return Global actor containing the classifier
   */
  public CallableActorReference getClassifier() {
    return m_ClassifierActor;
  }

  /**
   * description of this option
   * 
   * @return description of the classifier option
   */
  public String classifierTipText() {
    return "Global actore for the base classifier to use optimize.";
  }

  /**
   * Sets the type of statistic to evaluate the performance on.
   * 
   * @param value
   *          the type of statistic to use
   */
  public void setEvaluation(Evaluation value) {
    m_Evaluation = value;
    reset();
  }

  /**
   * Sets the type of statistic to evaluate the performance on.
   * 
   * @return		the evaluation
   */
  public Evaluation getEvaluation() {
    return m_Evaluation;
  }

  /**
   * description of this option.
   * 
   * @return description of the classifier option
   */
  public String evaluationTipText() {
    return "The type of statistic to evaluate the performance of a setup with.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{nz.ac.waikato.adams.webservice.weka.OptimizeClassifierMultiSearch.class};
  }

  /**
   * get the classifier from the callable actor containing the classifier
   * 
   * @return Classifier object
   */
  protected weka.classifiers.Classifier getClassifierInstance() {
    weka.classifiers.Classifier		result;
    MessageCollection			errors;

    errors = new MessageCollection();
    result = (weka.classifiers.Classifier) CallableActorHelper.getSetup(
	weka.classifiers.Classifier.class, m_ClassifierActor, this, errors);
    if (result == null) {
      if (!errors.isEmpty())
	getLogger().severe(errors.toString());
    }

    return result;
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    nz.ac.waikato.adams.webservice.weka.OptimizeClassifierMultiSearch 	optimize;
    
    optimize     = new nz.ac.waikato.adams.webservice.weka.OptimizeClassifierMultiSearch();
    m_Classifier = getClassifierInstance();
    optimize.setClassifier(OptionUtils.getCommandLine(m_Classifier));
    for (int i = 0; i < m_Parameters.length; i++)
      optimize.getSearchParameters().add(OptionUtils.getCommandLine(m_Parameters[i]));
    optimize.setDataset(WekaDatasetHelper.fromInstances((Instances)m_InputToken.getPayload()));
    switch (m_Evaluation) {
      case ACCURACY:
	optimize.setEvaluation("ACC");
	break;
      case COMBINED:
	optimize.setEvaluation("COMBINED");
	break;
      case CORRELATION_COEFFICIENT:
	optimize.setEvaluation("CC");
	break;
      case KAPPA:
	optimize.setEvaluation("KAPPA");
	break;
      case MEAN_ABSOLUTE_ERROR:
	optimize.setEvaluation("MAE");
	break;
      case RELATIVE_ABSOLUTE_ERROR:
	optimize.setEvaluation("RAE");
	break;
      case ROOT_MEAN_SQUARED_ERROR:
	optimize.setEvaluation("RMSE");
	break;
      case ROOT_RELATIVE_SQUARED_ERROR:
	optimize.setEvaluation("RRSE");
	break;
      default:
	throw new IllegalStateException("Unhandled evaluation: " + m_Evaluation);
    }
    m_OutputToken = new Token(optimize);
    
    return null;
  }  
}
