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
 * ChangeRatState.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.ControlActor;
import adams.flow.core.RatHelper;
import adams.flow.core.RatReference;
import adams.flow.core.RatState;
import adams.flow.core.Unknown;
import adams.flow.standalone.Rat;
import adams.flow.transformer.AbstractTransformer;

/**
 <!-- globalinfo-start -->
 * Changes the state of the specified Rat actors when a token passes through.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: ChangeRatState
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-rat &lt;adams.flow.core.RatReference&gt; [-rat ...] (property: rats)
 * &nbsp;&nbsp;&nbsp;The Rat actors to change the state for.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-state &lt;PAUSED|RUNNING&gt; (property: state)
 * &nbsp;&nbsp;&nbsp;The new state for the Rat actors.
 * &nbsp;&nbsp;&nbsp;default: PAUSED
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ChangeRatState
  extends AbstractTransformer
  implements ControlActor {

  private static final long serialVersionUID = 7078570350728159543L;

  /** the rats to change. */
  protected RatReference[] m_Rats;

  /** the state to change the rat to. */
  protected RatState m_State;

  /** the helper for the rat actors. */
  protected RatHelper m_RatHelper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Changes the state of the specified Rat actors when a token passes through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "rat", "rats",
      new RatReference[0]);

    m_OptionManager.add(
      "state", "state",
      RatState.PAUSED);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RatHelper = new RatHelper();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "state", m_State, "new state: ");
    result += QuickInfoHelper.toString(this, "rats", m_Rats, ", rats: ");

    return result;
  }

  /**
   * Sets the rats to change.
   *
   * @param value	the rats
   */
  public void setRats(RatReference[] value) {
    m_Rats = value;
    reset();
  }

  /**
   * Returns the rats to change.
   *
   * @return		the condition
   */
  public RatReference[] getRats() {
    return m_Rats;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String ratsTipText() {
    return "The Rat actors to change the state for.";
  }

  /**
   * Sets the new state for the Rat actors.
   *
   * @param value	the state
   */
  public void setState(RatState value) {
    m_State = value;
    reset();
  }

  /**
   * Returns the new state for the Rat actors.
   *
   * @return		the state
   */
  public RatState getState() {
    return m_State;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stateTipText() {
    return "The new state for the Rat actors.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    int		i;
    Actor	rat;

    result = super.setUp();

    if (result == null) {
      if (!getOptionManager().hasVariableForProperty("rats")) {
	if (m_Rats.length == 0) {
	  result = "No Rat actor references defined!";
	}
	else {
	  for (i = 0; i < m_Rats.length; i++) {
	    rat = m_RatHelper.findRatRecursive(this, m_Rats[i]);
	    if (rat == null) {
	      result = "Failed to locate Rat #" + (i + 1) + ": " + m_Rats[i];
	      break;
	    }
	    else if (!(rat instanceof Rat)) {
	      result = "Rat #" + (i + 1) + " '" + m_Rats[i] + "' is not of type " + Rat.class.getName();
	      break;
	    }
	  }
	}
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
    Actor	rat;

    result = null;

    for (i = 0; i < m_Rats.length; i++) {
      rat = m_RatHelper.findRatRecursive(this, m_Rats[i]);
      if (rat == null) {
	result = "Failed to locate Rat #" + (i+1) + ": " + m_Rats[i];
      }
      else {
	switch (m_State) {
	  case PAUSED:
	    if (rat instanceof Rat)
	      ((Rat) rat).pauseExecution();
	    else
	      result = "Unhandled actor: " + rat.getClass().getName();
	    break;
	  case RUNNING:
	    if (rat instanceof Rat)
	      ((Rat) rat).resumeExecution();
	    else
	      result = "Unhandled actor: " + rat.getClass().getName();
	    break;
	  default:
	    result = "Unhandled state: " + m_State;
	}
	if ((result == null) && isLoggingEnabled())
	  getLogger().info("New state set for Rat #" + (i+1) + " '" + m_Rats[i] + "': " + m_State);
      }
      if (result != null)
	break;
    }

    if (result == null)
      m_OutputToken = m_InputToken;

    return result;
  }
}
