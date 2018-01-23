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
 * RatControl.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.control;

import adams.core.MessageCollection;
import adams.core.Pausable;
import adams.core.Utils;
import adams.flow.control.Flow;
import adams.flow.control.RunningFlowsRegistry;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.rest.AbstractRESTPluginWithFlowContext;
import adams.flow.standalone.Rat;
import adams.flow.standalone.RatControl.AbstractControlPanel;
import adams.flow.standalone.RatControl.AbstractControlState;
import adams.flow.standalone.RatControl.RatControlPanel;
import adams.flow.standalone.RatControl.RatControlState;
import adams.flow.standalone.Rats;
import adams.scripting.command.flow.SendRatControlCommand;
import adams.scripting.command.flow.SendRatControlCommand.Command;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Controls Rat actors in flows with {@link RatControl} actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RatControl
  extends AbstractRESTPluginWithFlowContext {

  private static final long serialVersionUID = -3247606641885793684L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows control of Rat actors in running flows with active " + Utils.classToString(RatControl.class) + " actors.";
  }

  /**
   * Retrieves the running flow with the specified ID.
   *
   * @param id		the ID, use -1 for only running flow
   * @param errors	for collecting errors
   * @return		the flow, null if failed to retrieve
   */
  protected Actor getFlow(int id, MessageCollection errors) {
    Actor 	result;

    result = null;
    if (id == -1) {
      if (RunningFlowsRegistry.getSingleton().size() == 1)
	result = RunningFlowsRegistry.getSingleton().flows()[0];
      else
	errors.add("Using ID '-1' is only allowed if there is just a single flow registered (registered: " + RunningFlowsRegistry.getSingleton().size() + ")");
    }
    else {
      result = RunningFlowsRegistry.getSingleton().getFlow(id);
      if (result == null)
	errors.add("Failed to retrieve flow for ID " + id + "!");
    }

    if (errors.isEmpty())
      return result;
    else
      return null;
  }

  /**
   * Returns the list of flow IDs currently running.
   */
  @GET
  @Path("/rats/control/flows")
  public String flows() {
    JsonObject	result;
    JsonArray	jflows;
    JsonObject	jflow;
    Flow 	flow;

    jflows = new JsonArray();
    result = new JsonObject();
    result.add("flows", jflows);

    for (Integer id: RunningFlowsRegistry.getSingleton().ids()) {
      flow = RunningFlowsRegistry.getSingleton().getFlow(id);
      if (flow == null)
	continue;
      jflow = new JsonObject();
      jflow.addProperty("id", id);
      jflow.addProperty("paused", flow.isPaused());
      jflow.addProperty("stopped", flow.isStopped());
      jflow.addProperty("root", flow.getRoot().getName());
      jflow.addProperty("annotation", flow.getRoot().getAnnotations().getValue());
      jflow.addProperty("path", flow.getVariables().get(ActorUtils.FLOW_FILENAME_LONG));
      jflows.add(jflow);
    }

    return result.toString();
  }

  /**
   * Returns the status of the Rat actors in the specified flow.
   * You can use -1 as shortcut to retrieve the data from the only flow running.
   *
   * @param id		the flow ID, use -1 for only one running
   * @return		the status of the Rat actors or error message
   */
  @GET
  @Path("/rats/control/status/{id}")
  public String status(@PathParam("id") int id) {
    JsonObject				result;
    JsonArray 				groups;
    JsonArray 				rats;
    JsonObject				rat;
    JsonObject				group;
    MessageCollection			errors;
    Actor				flow;
    adams.flow.standalone.RatControl 	rc;
    Actor				actor;
    String				ratspath;

    errors = new MessageCollection();
    flow   = getFlow(id, errors);
    if (!errors.isEmpty())
      return errors.toString();

    result = new JsonObject();
    groups = new JsonArray();
    result.add("groups", groups);
    result.addProperty("id", id);
    ratspath = "";
    group = new JsonObject();
    rats = new JsonArray();
    for (Actor a : ActorUtils.enumerate(flow, new Class[]{adams.flow.standalone.RatControl.class})) {
      rc = (adams.flow.standalone.RatControl) a;
      for (AbstractControlState state: rc.getControlStates()) {
	actor = state.getActor();
	if (actor instanceof Rats) {
	  ratspath = actor.getFullName();
	  group = new JsonObject();
	  group.addProperty("name", actor.getName());
	  rats = new JsonArray();
	  group.add("rats", rats);
	  groups.add(group);
	  continue;
	}
	rat = new JsonObject();
	rat.addProperty("name", state.getActor().getFullName().substring(ratspath.length() + 1));
	rat.addProperty("full", state.getActor().getFullName());
	rat.addProperty("pausable", state.isPausable());
	rat.addProperty("paused", ((Pausable) actor).isPaused());
	if ((state instanceof RatControlState)) {
	  rat.addProperty("stoppable", ((RatControlState) state).isStoppable());
	  rat.addProperty("stopped", !((Rat) actor).isRunnableActive());
	}
	else {
	  rat.addProperty("stoppable", false);
	  rat.addProperty("stopped", false);
	}
	rats.add(rat);
      }
    }

    return result.toString();
  }

  /**
   * Sends a command to a Rat actor.
   *
   * @param id 		the flow ID, use -1 for only one running
   * @param cmd		the command, see {@link Command} (in lower case)
   * @param ratpath	the full path of the rat actor this command is for
   * @return		{@link SendRatControlCommand#RESPONSE_SUCCESS} if successful, otherwise error message
   */
  @GET
  @Path("/rats/control/command/{id}/{cmd}")
  public String command(@PathParam("id") int id, @PathParam("cmd") String cmd, @FormParam("rat") String ratpath) {
    Command				command;
    MessageCollection			errors;
    Actor				flow;
    adams.flow.standalone.RatControl 	rc;
    Actor				actor;
    Rat					rat;

    cmd     = cmd.toUpperCase();
    command = Command.valueOf(cmd);

    errors = new MessageCollection();
    flow   = getFlow(id, errors);
    if (!errors.isEmpty())
      return errors.toString();

    for (Actor a : ActorUtils.enumerate(flow, new Class[]{adams.flow.standalone.RatControl.class})) {
      rc = (adams.flow.standalone.RatControl) a;
      for (AbstractControlPanel panel: rc.getControlPanels()) {
	actor = panel.getActor();
	if (!actor.getFullName().equals(ratpath))
	  continue;
	switch (command) {
	  case PAUSE:
	    if (((Pausable) actor).isPaused()) {
	      return SendRatControlCommand.RESPONSE_ALREADY_PAUSED;
	    }
	    else {
	      panel.pauseOrResume();
	      return SendRatControlCommand.RESPONSE_SUCCESS;
	    }

	  case RESUME:
	    if (!((Pausable) actor).isPaused()) {
	      return SendRatControlCommand.RESPONSE_ALREADY_RUNNING;
	    }
	    else {
	      panel.pauseOrResume();
	      return SendRatControlCommand.RESPONSE_SUCCESS;
	    }

	  case STOP:
	    if (panel instanceof RatControlPanel) {
	      rat = (Rat) actor;
	      if (!rat.isRunnableActive()) {
		return SendRatControlCommand.RESPONSE_ALREADY_STOPPED;
	      }
	      else {
		rat.stopRunnable();
		return SendRatControlCommand.RESPONSE_SUCCESS;
	      }
	    }
	    else {
	      return SendRatControlCommand.RESPONSE_NO_SUPPORTED;
	    }

	  case START:
	    if (panel instanceof RatControlPanel) {
	      rat = (Rat) actor;
	      if (rat.isRunnableActive()) {
		return SendRatControlCommand.RESPONSE_ALREADY_RUNNING;
	      }
	      else {
		rat.startRunnable();
		return SendRatControlCommand.RESPONSE_SUCCESS;
	      }
	    }
	    else {
	      return SendRatControlCommand.RESPONSE_NO_SUPPORTED;
	    }

	  default:
	    return SendRatControlCommand.RESPONSE_NO_SUPPORTED;
	}
      }
    }

    return "Failed to locate actor ('" + ratpath + "')? ";
  }
}
