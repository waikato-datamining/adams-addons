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
 * WekaWSTrainClassifier.java
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
import nz.ac.waikato.adams.webservice.weka.Dataset;
import weka.core.Instances;

/**
 * Trains a classifier on a dataset and makes the model available.
 * 
 * @author msf8
 * @version $Revision$
 */
public class WekaWSTrainClassifier 
extends AbstractTransformer{

  /** for serialization */
  private static final long serialVersionUID = 4879632007434246201L;

  /** callable actor containing the classifier to use */
  protected CallableActorReference m_Classifier;

  /** name of the model to use */
  protected String m_ModelName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Trains a classifier on a dataset and makes the model available, using the Weka web-service.";
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
	"model-name", "modelName", "");
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
    result += QuickInfoHelper.toString(this, "modelName", m_ModelName, ", model: ");
    
    return result;
  }

  /**
   * set the classifier to use for training
   * @param c		callable actor containing the classifer
   */
  public void setClassifier(CallableActorReference c) {
    m_Classifier = c;
    reset();
  }

  /**
   * get the classifier used for training
   * @return		global actor containing the classifier
   */
  public CallableActorReference getClassifier() {
    return m_Classifier;
  }

  /**
   * description of this option 
   * @return		description of the classifier option
   */
  public String classifierTipText() {
    return "Classifier to use for training";
  }

  /**
   * set the name of the model used in training
   * @param n		model name
   */
  public void setModelName(String n) {
    m_ModelName = n;
    reset();
  }

  /**
   * get the name of the model used for training
   * @return		model name
   */
  public String getModelName() {
    return m_ModelName;
  }

  /**
   * description of this option
   * @return		description of the model name option
   */
  public String modelNameTipText() {
    return "name";
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
    return new Class[] {nz.ac.waikato.adams.webservice.weka.TrainClassifier.class};
  }

  /**
   * get the classifier from the global actor containing the classifer
   * @return		classifier to use 
   */
  protected weka.classifiers.Classifier getClassifierInstance() {
    weka.classifiers.Classifier 	result;
    MessageCollection			errors;

    errors = new MessageCollection();
    result = (weka.classifiers.Classifier) CallableActorHelper.getSetup(weka.classifiers.Classifier.class, m_Classifier, this, errors);
    if (result == null) {
      if (!errors.isEmpty())
	getLogger().severe(errors.toString());
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    
    result = super.setUp();
    
    if (result == null) {
      if (getOptionManager().getVariableForProperty("modelName") == null) {
	if (m_ModelName.trim().length() == 0)
	  result = "No model name provided!";
      }
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
    nz.ac.waikato.adams.webservice.weka.TrainClassifier t =  new nz.ac.waikato.adams.webservice.weka.TrainClassifier();
    Dataset d = WekaDatasetHelper.fromInstances((Instances)m_InputToken.getPayload());
    t.setDataset(d);
    t.setClassifier(OptionUtils.getCommandLine(getClassifierInstance()));
    t.setName(m_ModelName);
    m_OutputToken = new Token(t);
    return null;

  }
}
