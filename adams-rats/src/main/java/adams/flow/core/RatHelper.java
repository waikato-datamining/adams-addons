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
 * RatHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.logging.LoggingObject;
import adams.flow.control.AbstractDirectedControlActor;
import adams.flow.standalone.Rats;

import java.util.List;

/**
 * Helper class for Rat actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RatHelper
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -763479272812116920L;

  /**
   * Checks a control actor's children whether they contain the rat
   * that we're looking for.
   *
   * @param group	the group to check
   * @param name	the name of the rat
   * @return		the rat or null if not found
   */
  public Actor findRat(ActorHandler group, RatReference name) {
    Actor			result;
    int				i;
    Rats 			rats;
    int				index;
    ExternalActorHandler	external;

    result = null;

    for (i = 0; i < group.size(); i++) {
      if (group.get(i) instanceof Rats) {
	rats  = (Rats) group.get(i);
	index = rats.indexOf(name.toString());
	if (index > -1) {
	  result = rats.get(index);
	  break;
	}
      }
      else if (group.get(i) instanceof ExternalActorHandler) {
	external = (ExternalActorHandler) group.get(i);
	if (external.getExternalActor() instanceof ActorHandler) {
	  result = findRat((ActorHandler) external.getExternalActor(), name);
	  if (result != null)
	    break;
	}
      }
    }

    return result;
  }

  /**
   * Tries to find the rat referenced by its name.
   *
   * @param root	the root to search in
   * @param name	the name of the rat
   * @return		the rat or null if not found
   */
  public Actor findRat(Actor root, RatReference name) {
    Actor	result;

    result = null;

    if (root == null) {
      getLogger().severe("No root container found!");
    }
    else if (!(root instanceof AbstractDirectedControlActor)) {
      getLogger().severe(
	  "Root is not a container ('" + root.getFullName() + "'/"
	  + root.getClass().getName() + ")!");
      root = null;
    }

    if (root != null)
      result = findRat((ActorHandler) root, name);

    return result;
  }

  /**
   * Tries to find the referenced rat. First all possible actor
   * handlers are located recursively (up to the root) that allow also
   * singletons. This list of actors is then searched for the rat.
   *
   * @param actor	the actor to start from
   * @param name	the name of the rat
   * @return		the rat or null if not found
   * @see		ActorUtils#findActorHandlers(Actor, boolean)
   */
  public Actor findRatRecursive(Actor actor, RatReference name) {
    Actor		result;
    List<ActorHandler>	handlers;
    int			i;

    result   = null;
    handlers = ActorUtils.findActorHandlers(actor, true);
    for (i = 0; i < handlers.size(); i++) {
      result = findRat(handlers.get(i), name);
      if (result != null)
	break;
    }

    return result;
  }

  /**
   * Returns all {@link Rats} instances that can be located in the flow
   * 
   * @param flow	the flow to use
   * @return		the {@link Rats} instances
   */
  public static List<Actor> findAllRats(Actor flow) {
    return ActorUtils.enumerate(flow.getRoot(), new Class[]{Rats.class});
  }
}
