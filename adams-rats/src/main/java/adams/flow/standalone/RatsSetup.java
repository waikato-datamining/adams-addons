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
 * RatsSetup.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import java.util.ArrayList;
import java.util.List;

import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;

/**
 <!-- globalinfo-start -->
 * Configuration for a reception and transmission system.
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
 * &nbsp;&nbsp;&nbsp;default: RatsSetup
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
 * <pre>-receptions &lt;adams.flow.core.AbstractActor&gt; (property: receptions)
 * &nbsp;&nbsp;&nbsp;The reception setups.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.RatsReceptions -name receptions
 * </pre>
 * 
 * <pre>-transmissions &lt;adams.flow.core.AbstractActor&gt; (property: transmissions)
 * &nbsp;&nbsp;&nbsp;The transmission setups.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.RatsTransmissions -name transmissions
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@Deprecated
public class RatsSetup
  extends AbstractStandaloneGroup {

  /** for serialization. */
  private static final long serialVersionUID = -8492682468633306831L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configuration for a reception and transmission system.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "receptions", "receptions",
	    new RatsReceptions());

    m_OptionManager.add(
	    "transmissions", "transmissions",
	    new RatsTransmissions());
  }
  
  /**
   * Returns the list of default actors.
   * 
   * @return		the default actors
   */
  @Override
  protected List<Actor> getDefaultActors() {
    List<Actor>		result;
    
    result = new ArrayList<Actor>();
    result.add(new RatsReceptions());
    result.add(new RatsTransmissions());
    
    return result;
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
    String	result;
    
    result = null;
    
    if (index == 0) {
      if (!(actor instanceof RatsReceptions))
	result = "Receptions actor (#" + (index+1) + ") must be " + RatsReceptions.class.getName() + ", provided: " + actor.getClass().getName();
    }
    else if (index == 1) {
      if (!(actor instanceof RatsTransmissions))
	result = "Transmissions actor (#" + (index+1) + ") must be " + RatsTransmissions.class.getName() + ", provided: " + actor.getClass().getName();
    }
    else {
      result = "Invalid index: " + index;
    }
    
    return result;
  }

  /**
   * Sets the receptions to use.
   *
   * @param value	the receptions
   */
  public void setReceptions(AbstractActor value) {
    String	msg;
    
    msg = checkActor(value, 0);
    if (msg == null) {
      value.setName("receptions");
      set(0, value);
    }
    else {
      getLogger().warning(msg);
    }
 }

  /**
   * Returns the receptions to use.
   *
   * @return		the receptions
   */
  public AbstractActor getReceptions() {
    return get(0);
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
   * Sets the transmissions to use.
   *
   * @param value	the transmissions
   */
  public void setTransmissions(AbstractActor value) {
    String	msg;
    
    msg = checkActor(value, 1);
    if (msg == null) {
      value.setName("transmissions");
      set(1, value);
    }
    else {
      getLogger().warning(msg);
    }
  }

  /**
   * Returns the transmissions to use.
   *
   * @return		the transmissions
   */
  public AbstractActor getTransmissions() {
    return get(1);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String transmissionsTipText() {
    return "The transmission setups.";
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(true, false, ActorExecution.UNDEFINED, false, new Class[]{RatsReceptions.class, RatsTransmissions.class});
  }

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything is fine, otherwise the error
   */
  @Override
  public String check() {
    String	result;
    
    result = null;
    
    if (!getReceptions().getSkip())
      result = ((RatsReceptions) getReceptions()).check();
    else if (!getTransmissions().getSkip())
      result = ((RatsTransmissions) getTransmissions()).check();
    
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
    
    result = null;
    
    if (!getReceptions().getSkip())
      result = getReceptions().execute();
    
    if ((result == null) && (!getTransmissions().getSkip()))
      result = getTransmissions().execute();
    
    return result;
  }
}
