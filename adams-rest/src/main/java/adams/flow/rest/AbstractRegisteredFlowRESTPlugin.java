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
 * AbstractRegisteredFlowRESTPlugin.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest;

import adams.core.MessageCollection;
import adams.core.VariablesHandler;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.control.RunningFlowsRegistry;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;

/**
 * Ancestor for REST plugins that .
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRegisteredFlowRESTPlugin
  extends AbstractRESTPlugin {

  private static final long serialVersionUID = -3741993388570979031L;

  /**
   * Retrieves the running flow with the specified ID.
   *
   * @param id		the ID, use -1 for only running flow
   * @param errors	for collecting errors
   * @return		the flow, null if failed to retrieve
   */
  protected Actor getFlow(int id, boolean loadFromDisk, MessageCollection errors) {
    Actor 	result;
    String	flowFile;

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
    if ((errors.isEmpty()) && loadFromDisk) {
      flowFile = result.getVariables().get(ActorUtils.FLOW_FILENAME_LONG);
      if (flowFile == null) {
	errors.add("Variable '" + ActorUtils.FLOW_FILENAME_LONG + "' not set, cannot load from disk!");
      }
      else {
	if (FileUtils.fileExists(flowFile)) {
	  result = ActorUtils.read(flowFile);
	  if (result == null)
	    errors.add("Failed to load flow from  '" + flowFile + "'!");
	  else
	    ActorUtils.updateProgrammaticVariables((VariablesHandler & Actor) result, new PlaceholderFile(flowFile));
	}
	else {
	  errors.add("Flow '" + flowFile + "' does not exist!");
	}
      }
    }

    if (errors.isEmpty())
      return result;
    else
      return null;
  }
}
