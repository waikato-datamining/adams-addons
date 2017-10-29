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
 * WekaWSTrainClusterer.java
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
import adams.flow.source.WekaClustererSetup;
import nz.ac.waikato.adams.webservice.weka.Dataset;
import weka.core.Instances;

/**
 * Trains a clusterer.
 * 
 * @author msf8
 * @version $Revision$
 */
public class WekaWSTrainClusterer 
extends AbstractTransformer{

  /** for serialization*/
  private static final long serialVersionUID = -8770423757728884076L;

  /** callable actor containing the clusterer to use */
  protected CallableActorReference m_Clusterer;

  /** name of the model to use */
  protected String m_ModelName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
   return "Receives a dataset and prepares a traincluster input object, using the Weka web-service.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"clusterer", "clusterer", 
	new CallableActorReference(WekaClustererSetup.class.getSimpleName()));

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

    result  = QuickInfoHelper.toString(this, "clusterer", m_Clusterer, "clusterer: ");
    result += QuickInfoHelper.toString(this, "modelName", m_ModelName, ", model: ");
    
    return result;
  }

  /**
   * set the clusterer to use for training
   * @param c		callable actor containing the classifer
   */
  public void setClusterer(CallableActorReference c) {
    m_Clusterer = c;
    reset();
  }

  /**
   * get the clusterer used for training
   * @return		callable actor containing the clusterer
   */
  public CallableActorReference getClusterer() {
    return m_Clusterer;
  }

  /**
   * description of this option 
   * @return		description of the clusterer option
   */
  public String clustererTipText() {
    return "Clusterer to use for training";
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
    return new Class[]{nz.ac.waikato.adams.webservice.weka.TrainClusterer.class};
  }

  /**
   * get the clusterer from the callable actor containing the clusterer.
   * 
   * @return		clusterer to use 
   */
  protected weka.clusterers.Clusterer getClustererInstance() {
    weka.clusterers.Clusterer	result;
    MessageCollection		errors;

    errors = new MessageCollection();
    result = (weka.clusterers.Clusterer) CallableActorHelper.getSetup(weka.clusterers.Clusterer.class, m_Clusterer, this, errors);
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
    nz.ac.waikato.adams.webservice.weka.TrainClusterer t = new nz.ac.waikato.adams.webservice.weka.TrainClusterer();
    t.setClusterer(OptionUtils.getCommandLine(getClustererInstance()));
    t.setModelName(m_ModelName);
    Dataset d = WekaDatasetHelper.fromInstances((Instances)m_InputToken.getPayload());
    t.setDataset(d);
    m_OutputToken = new Token(t);
    return null;
  }
}
