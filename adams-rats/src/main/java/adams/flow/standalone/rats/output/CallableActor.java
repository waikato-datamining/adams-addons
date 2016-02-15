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
 * CallableActor.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.ActorVariablesFinder;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorUser;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.util.HashSet;

/**
 <!-- globalinfo-start -->
 * Forwards the data to the specified callable actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-callable &lt;adams.flow.core.CallableActorReference&gt; (property: callableName)
 * &nbsp;&nbsp;&nbsp;The name of the callable actor to use.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 * <pre>-optional &lt;boolean&gt; (property: optional)
 * &nbsp;&nbsp;&nbsp;If enabled, then the callable actor is optional, ie no error is raised if 
 * &nbsp;&nbsp;&nbsp;not found, merely ignored.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CallableActor
  extends AbstractRatOutput
  implements CallableActorUser {

  /** for serialization. */
  private static final long serialVersionUID = 5871927859523743161L;

  /** the callable name. */
  protected CallableActorReference m_CallableName;

  /** the callable actor. */
  protected Actor m_CallableActor;

  /** whether the callable actor has been configured. */
  protected boolean m_Configured;
  
  /** the helper class. */
  protected CallableActorHelper m_Helper;
  
  /** whether the callable actor is optional. */
  protected boolean m_Optional;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Forwards the data to the specified callable actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "callable", "callableName",
	    new CallableActorReference("unknown"));

    m_OptionManager.add(
	    "optional", "optional",
	    false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableActor = null;
    m_Configured    = false;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Sets the name of the callable actor to use.
   *
   * @param value 	the callable name
   */
  public void setCallableName(CallableActorReference value) {
    m_CallableName = value;
    reset();
  }

  /**
   * Returns the name of the callable actor in use.
   *
   * @return 		the callable name
   */
  public CallableActorReference getCallableName() {
    return m_CallableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String callableNameTipText() {
    return "The name of the callable actor to use.";
  }

  /**
   * Sets whether the callable actor is optional.
   *
   * @param value 	true if optional
   */
  public void setOptional(boolean value) {
    m_Optional = value;
    reset();
  }

  /**
   * Returns whether the callable actor is optional.
   *
   * @return 		true if optional
   */
  public boolean getOptional() {
    return m_Optional;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String optionalTipText() {
    return 
	"If enabled, then the callable actor is optional, ie no error is "
	+ "raised if not found, merely ignored.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "callableName", m_CallableName);
    result += QuickInfoHelper.toString(this, "optional", m_Optional, "optional", ", ");
    
    return result;
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    Actor	result;
    
    result = m_Helper.findCallableActorRecursive(m_Owner, getCallableName());
    
    if (result != null) {
      if (!(ActorUtils.isSink(result))) {
	getLogger().severe("Callable actor '" + result.getFullName() + "' is not a sink" + (m_CallableActor == null ? "!" : m_CallableActor.getClass().getName()));
	result = null;
      }
    }
    
    return result;
  }

  /**
   * Checks whether a reference to the callable actor is currently available.
   *
   * @return		true if a reference is available
   * @see		#getCallableActor()
   */
  public boolean hasCallableActor() {
    return (m_CallableActor != null);
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public Actor getCallableActor() {
    return m_CallableActor;
  }

  /**
   * Recursively finds all the variables used in the specified actor's setup.
   *
   * @param actor	the actor to search
   * @return		the variables that were found
   */
  protected HashSet<String> findVariables(Actor actor) {
    ActorVariablesFinder	finder;
    HashSet<String>		result;

    getLogger().finest("Locating variables in " + actor.getFullName() + "...");

    finder = new ActorVariablesFinder();
    finder.setInspection(actor);
    actor.getOptionManager().traverse(finder);
    result = finder.getResult();

    getLogger().finest("Found variables in " + actor.getFullName() + " (" + result.size() + "): " + result);

    return result;
  }

  /**
   * Configures the callable actor.
   *
   * @return		null if successful, otherwise error message
   */
  protected String setUpCallableActor() {
    String		result;

    result = null;

    m_CallableActor = findCallableActor();
    m_Configured    = true;
    if (m_CallableActor == null) {
      if (!m_Optional)
	result = "Couldn't find callable actor '" + getCallableName() + "'!";
      else
	getLogger().info("Callable actor '" + getCallableName() + "' not found, ignoring.");
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the classes that the consumer accepts
   */
  @Override
  public Class[] accepts() {
    if (m_CallableActor != null)
      return ((InputConsumer) m_CallableActor).accepts();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Performs the actual transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String		result;

    result = null;
    
    if (!m_Configured)
      result = setUpCallableActor();

    if (result == null) {
      synchronized(m_CallableActor) {
	if (isLoggingEnabled())
	  getLogger().info("Executing callable actor - start: " + m_CallableActor);
	((InputConsumer) m_CallableActor).input(new Token(m_Input));
	result = m_CallableActor.execute();
	if (isLoggingEnabled())
	  getLogger().info("Executing callable actor - end: " + result);
      }
    }

    return result;
  }
}
