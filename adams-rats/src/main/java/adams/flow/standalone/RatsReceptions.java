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
 * RatsReceptions.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import java.util.ArrayList;
import java.util.List;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;

/**
 <!-- globalinfo-start -->
 * Encapsulates all reception setups.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: RatsReceptions
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-reception &lt;adams.flow.core.AbstractActor&gt; [-reception ...] (property: receptions)
 * &nbsp;&nbsp;&nbsp;The reception setups.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RatsReceptions
  extends AbstractStandaloneMutableGroup<RatsReception> {

  /** for serialization. */
  private static final long serialVersionUID = -6092821156832607603L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encapsulates all reception setups.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reception", "receptions",
	    new AbstractActor[0]);
  }

  /**
   * Returns the list of default actors.
   * 
   * @return		the default actors
   */
  @Override
  protected List<RatsReception> getDefaultActors() {
    return new ArrayList<RatsReception>();
  }

  /**
   * Sets the receptions to use.
   *
   * @param value	the receptions
   */
  public void setReceptions(AbstractActor[] value) {
    int		i;
    String	msg;
    
    for (i = 0; i < value.length; i++) {
      msg = checkActor(value[i], i);
      if (msg != null) {
	getLogger().warning(msg);
	return;
      }
    }
    
    m_Actors.clear();
    for (AbstractActor actor: value)
      m_Actors.add((RatsReception) actor);
    reset();
    updateParent();
  }

  /**
   * Returns the receptions to use.
   *
   * @return		the receptions
   */
  public AbstractActor[] getReceptions() {
    return m_Actors.toArray(new AbstractActor[m_Actors.size()]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String receptionsTipText() {
    return "The reception setups.";
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(true, false, ActorExecution.UNDEFINED, false, new Class[]{RatsReception.class});
  }

  /**
   * Checks the actor whether it is of the correct type.
   * 
   * @param actor	the actor to check
   * @param index	the index of actor, ignored if -1
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String checkActor(AbstractActor actor, int index) {
    if (!(actor instanceof RatsReception))
      return "Reception" + (index > -1 ? (" #" + (index+1)) : "") + " is not " + RatsReception.class.getName() + ", provided: " + actor.getClass().getName();
    else
      return null;
  }

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything is fine, otherwise the error
   */
  @Override
  public String check() {
    String	result;
    int		i;
    
    result = null;
    
    for (i = 0; i < m_Actors.size(); i++) {
      if (m_Actors.get(i).getSkip())
	continue;
      result = m_Actors.get(i).check();
      if (result != null) {
	result = "Reception #" + (i+1) + ": " + result;
	break;
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
    String	result;
    int		i;
    
    result = null;
    
    for (i = 0; i < m_Actors.size(); i++) {
      if (m_Actors.get(i).getSkip())
	continue;
      
      try {
	result = m_Actors.get(i).execute();
	if (result != null) {
	  result = "Reception #" + (i+1) + " failed setup: " + result;
	  break;
	}
      }
      catch (Exception e) {
	result = handleException("Failed to execute reception #" + (i+1), e);
      }
    }
      
    return result;
  }
}
