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
 * MekaWSTransform.java
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.flow.core.MekaDatasetHelper;
import adams.flow.core.Token;
import nz.ac.waikato.adams.webservice.meka.Dataset;
import weka.core.Instances;

/**
 * Transforms data using a global actor offered by the webservice.
 * 
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MekaWSTransform 
extends AbstractTransformer{

  /** for serialization*/
  private static final long serialVersionUID = -7213242947554746696L;
  
  /** name of the global actor used for transforming. */
  protected String m_ActorName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs a data transformation using a transformer available through the Meka web-service.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    
    m_OptionManager.add(
	"actor-name", "actorName", "");
  }
  
  /**
   * set the name of the callable actor to use.
   * 
   * @param n	name of callable actor to use for transformation
   */
  public void setActorName(String n) {
    m_ActorName = n;
    reset();
  }
  
  /**
   * get the name of the callable actor to use.
   * 
   * @return		name of the callable actor to use for transformation
   */
  public String getActorName() {
    return m_ActorName;
  }
  
  /**
   * description of this option.
   * 
   * @return		description of the model name option
   */
  public String actorNameTipText() {
    return "name of the callable actor to use for transformation";
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
    return new Class[] { nz.ac.waikato.adams.webservice.meka.Transform.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    nz.ac.waikato.adams.webservice.meka.Transform t = new nz.ac.waikato.adams.webservice.meka.Transform();
    Dataset d = MekaDatasetHelper.fromInstances((Instances)m_InputToken.getPayload());
    t.setDataset(d);
    t.setActorName(m_ActorName);
    m_OutputToken = new Token(t);
    return null;
  }
}
