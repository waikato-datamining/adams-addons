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
 * WekaWSPredictClusterer.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.flow.core.WekaDatasetHelper;
import nz.ac.waikato.adams.webservice.weka.Dataset;
import weka.core.Instances;

/**
 * Predicts clusters using a clusterer model.
 * 
 * @author msf8
 * @version $Revision$
 */
public class WekaWSPredictClusterer 
extends AbstractTransformer{

  /** for serialization */
  private static final long serialVersionUID = -7381401447608334401L;

  /** name of the model to use for the prediction */
  protected String m_ModelName;

  @Override
  public String globalInfo() {
    return "Performs clustering of data using a built model, using the Weka web-service.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

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
    return QuickInfoHelper.toString(this, "modelName", m_ModelName, "model: ");
  }

  /**
   * set the model to use for the prediction.
   * 
   * @param n		name of the model
   */
  public void setModelName(String n) {
    m_ModelName = n;
    reset();
  }

  /**
   * get the name of the model used for prediction.
   * 
   * @return		model name
   */
  public String getModelName() {
    return m_ModelName;
  }

  /**
   * description of this option.
   * 
   * @return		description of the model option
   */
  public String modelNameTipText() {
    return "The name of the model used for storing on the server-side.";
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
   return new Class[] {nz.ac.waikato.adams.webservice.weka.PredictClusterer.class};
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
   nz.ac.waikato.adams.webservice.weka.PredictClusterer p = new nz.ac.waikato.adams.webservice.weka.PredictClusterer();
   Dataset d = WekaDatasetHelper.fromInstances((Instances) m_InputToken.getPayload());
   p.setDataset(d);
   p.setModelName(m_ModelName);
   m_OutputToken = new Token(p);
   return null;
  }
}
