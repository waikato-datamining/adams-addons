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
 * SendRatControlCommand.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.flow;

import adams.core.Pausable;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.Rat;
import adams.flow.standalone.RatControl;
import adams.flow.standalone.RatControl.AbstractControlPanel;
import adams.flow.standalone.RatControl.RatControlPanel;
import adams.scripting.command.AbstractRemoteCommandOnFlowWithResponse;

/**
 * Sends a control command for a Rat to a remote flow.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SendRatControlCommand
  extends AbstractRemoteCommandOnFlowWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /**
   * Enumeration of available commands.
   */
  public enum Command {
    PAUSE,
    RESUME,
    STOP,
    START,
  }

  /** response: success. */
  public final static String RESPONSE_SUCCESS = "Success";

  /** response: rat not found. */
  public final static String RESPONSE_NOT_FOUND = "Actor not found";

  /** response: command not supported. */
  public final static String RESPONSE_NO_SUPPORTED = "Command not supported";

  /** response: already paused. */
  public final static String RESPONSE_ALREADY_PAUSED = "Already paused";

  /** response: already running. */
  public final static String RESPONSE_ALREADY_RUNNING = "Already running";

  /** response: already stopped. */
  public final static String RESPONSE_ALREADY_STOPPED = "Already stopped";

  /** the rat name. */
  protected String m_Rat;

  /** the command. */
  protected Command m_Command;

  /** the response. */
  protected String m_Response;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sends a Rat command to a remote flow, identified by its ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "rat", "rat",
      "");

    m_OptionManager.add(
      "command", "command",
      Command.PAUSE);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Response = null;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String IDTipText() {
    return "The ID of the flow to operate on; -1 if to use the only one.";
  }

  /**
   * Sets the full name of the Rat to send the command to.
   *
   * @param value	the full name
   */
  public void setRat(String value) {
    m_Rat = value;
    reset();
  }

  /**
   * Returns the full name of the Rat to send the command to.
   *
   * @return		the full name
   */
  public String getRat() {
    return m_Rat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String ratTipText() {
    return "The full name of the Rat actor to send the command to.";
  }

  /**
   * Sets the command to send.
   *
   * @param value	the command
   */
  public void setCommand(Command value) {
    m_Command = value;
    reset();
  }

  /**
   * Returns the command to send.
   *
   * @return		the command
   */
  public Command getCommand() {
    return m_Command;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String commandTipText() {
    return "The command to send.";
  }

  /**
   * Ignored.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
  }

  /**
   * Always zero-length array.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    return new byte[0];
  }

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[0];
  }

  /**
   * Sets the payload for the response.
   *
   * @param value	the payload
   */
  @Override
  public void setResponsePayload(byte[] value) {
    if (value.length == 0) {
      m_Response = null;
      return;
    }

    m_Response = new String(value);
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    if (m_Response == null)
      return new byte[0];
    else
      return m_Response.getBytes();
  }

  /**
   * Hook method for preparing the response payload,
   */
  @Override
  protected void prepareResponsePayload() {
    Actor 	flow;
    RatControl	rc;
    Actor	actor;
    Rat		rat;

    super.prepareResponsePayload();

    flow = retrieveFlow(false);

    // get RatControl actors
    m_Response = RESPONSE_NOT_FOUND;
    if (flow != null) {
      for (Actor a : ActorUtils.enumerate(flow, new Class[]{RatControl.class})) {
	rc = (RatControl) a;
	for (AbstractControlPanel panel: rc.getControlPanels()) {
	  actor = panel.getActor();
	  if (!actor.getFullName().equals(m_Rat))
	    continue;
	  switch (m_Command) {
	    case PAUSE:
	      if (((Pausable) actor).isPaused()) {
		m_Response = RESPONSE_ALREADY_PAUSED;
	      }
	      else {
		panel.pauseOrResume();
		m_Response = RESPONSE_SUCCESS;
	      }
	      break;

	    case RESUME:
	      if (!((Pausable) actor).isPaused()) {
		m_Response = RESPONSE_ALREADY_RUNNING;
	      }
	      else {
		panel.pauseOrResume();
		m_Response = RESPONSE_SUCCESS;
	      }
	      break;

	    case STOP:
	      if (panel instanceof RatControlPanel) {
		rat = (Rat) actor;
		if (!rat.isRunnableActive()) {
		  m_Response = RESPONSE_ALREADY_STOPPED;
		}
		else {
		  rat.stopRunnable();
		  m_Response = RESPONSE_SUCCESS;
		}
	      }
	      else {
		m_Response = RESPONSE_NO_SUPPORTED;
	      }
	      break;

	    case START:
	      if (panel instanceof RatControlPanel) {
		rat = (Rat) actor;
		if (rat.isRunnableActive()) {
		  m_Response = RESPONSE_ALREADY_RUNNING;
		}
		else {
		  rat.startRunnable();
		  m_Response = RESPONSE_SUCCESS;
		}
	      }
	      else {
		m_Response = RESPONSE_NO_SUPPORTED;
	      }
	      break;

	    default:
	      m_Response = RESPONSE_NO_SUPPORTED;
	      break;
	  }
	}
      }
    }
  }

  /**
   * Returns the objects that represent the response payload.
   *
   * @return		the objects
   */
  public Object[] getResponsePayloadObjects() {
    return new Object[]{m_Response};
  }
}
