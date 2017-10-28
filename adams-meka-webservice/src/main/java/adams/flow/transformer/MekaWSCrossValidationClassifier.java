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
 * MekaWSCrossValidationClassifier.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.MekaDatasetHelper;
import adams.flow.core.Token;
import adams.flow.source.MekaClassifierSetup;
import nz.ac.waikato.adams.webservice.meka.CrossValidateClassifier;
import nz.ac.waikato.adams.webservice.meka.Dataset;
import weka.core.Instances;

/**
 * Performs cross-validation of a classifier on a dataset.
 * 
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MekaWSCrossValidationClassifier
extends AbstractTransformer {

  /** for serialization*/
  private static final long serialVersionUID = -1600923751846355040L;

  /** seed to use for the cross validation */
  protected int m_Seed;

  /** number of folds for the cross validation */
  protected int m_Folds;

  /** classifier to use for the cross validation */
  protected meka.classifiers.multilabel.MultiLabelClassifier m_Classifier;

  /** for getting the classifier as a callable actor */
  protected CallableActorReference m_ClassifierActor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs cross-validation of a classifier on a dataset, using the Meka web-service.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"classifier", "classifier", 
	new CallableActorReference(MekaClassifierSetup.class.getSimpleName()));

    m_OptionManager.add(
	"folds", "folds", 
	10, 2, null);

    m_OptionManager.add(
	"seed", "seed", 
	1);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "classifier", m_Classifier, "classifier: ");
    result += QuickInfoHelper.toString(this, "folds", m_Folds, ", folds: ");
    result += QuickInfoHelper.toString(this, "seed", m_Seed, ", seed: ");
    
    return result;
  }

  /**
   * set the number of folds used for the cross validation
   * 
   * @param f
   *          Number of folds for cross validation
   */
  public void setFolds(int f) {
    if (f >= 2) {
      m_Folds = f;
      reset();
    }
    else {
      getLogger().severe("At least 2 folds required, provided: " + f);
    }
  }

  /**
   * Get the number of folds used for the cross validation
   * 
   * @return NUmber of folds in the cross validation
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Description of this option
   * 
   * @return Description of the folds option
   */
  public String foldsTipText() {
    return "number of folds for the cross validation";
  }

  /**
   * Set the seed for the cross validation
   * 
   * @param s
   *          Seed for cross validation
   */
  public void setSeed(int s) {
    m_Seed = s;
    reset();
  }

  /**
   * get the seed used for the cross validation
   * 
   * @return seed for cross validation
   */
  public int getSeed() {
    return m_Seed;
  }

  /**
   * description for this option
   * 
   * @return Description of the seed option
   */
  public String seedTipText() {
    return "seed for the cross validation";
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
    return "Global actore for the classifier to use for cross validation";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[] {Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[] {CrossValidateClassifier.class};
  }
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    CrossValidateClassifier cV = new CrossValidateClassifier();
    m_Classifier = getClassifierInstance();
    cV.setClassifier(OptionUtils.getCommandLine(m_Classifier));
    Dataset d = MekaDatasetHelper.fromInstances((Instances)m_InputToken.getPayload());
    cV.setDataset(d);
    cV.setSeed(m_Seed);
    cV.setFolds(m_Folds);
    m_OutputToken = new Token(cV);
    return null;
  }  

  /**
   * get the classifier from the callable actor containing the classifier
   * 
   * @return Classifier object
   */
  protected meka.classifiers.multilabel.MultiLabelClassifier getClassifierInstance() {
    meka.classifiers.multilabel.MultiLabelClassifier	result;
    MessageCollection			errors;

    errors = new MessageCollection();
    result = (meka.classifiers.multilabel.MultiLabelClassifier) CallableActorHelper.getSetup(
	meka.classifiers.multilabel.MultiLabelClassifier.class, m_ClassifierActor, this, errors);
    if (result == null) {
      if (!errors.isEmpty())
	getLogger().severe(errors.toString());
    }

    return result;
  }
}
