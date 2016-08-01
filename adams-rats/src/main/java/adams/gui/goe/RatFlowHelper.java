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
 * RatFlowHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.goe;

import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.ExternalActorHandler;
import adams.flow.standalone.Rats;
import adams.flow.standalone.Standalones;
import adams.gui.flow.tree.Node;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Helper class for flow related searches.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RatFlowHelper
  extends FlowHelper {

  /**
   * Locates all nodes representing {@link Rats} actors.
   *
   * @param cont	the container to start the search from
   * @return		the nodes with {@link Rats} found
   */
  public static List<Node> findRats(Container cont) {
    Node	current;
    Node	parent;

    current = getEditedNode(cont);
    if (current != null)
      parent = (Node) current.getParent();
    else
      parent = getEditedParent(cont);

    return findRats(parent);
  }

  /**
   * Locates all nodes representing {@link Rats} actors.
   *
   * @param parent	the parent node
   * @return		the nodes with {@link Rats} found
   */
  public static List<Node> findRats(Node parent) {
    return findRats(parent, true, null);
  }

  /**
   * Locates all nodes representing {@link Rats} actors.
   *
   * @param parent	the parent node
   * @param restrict	the classes to restrict the results to
   * @return		the nodes with {@link Rats} found
   */
  public static List<Node> findRats(Node parent, Class[] restrict) {
    return findRats(parent, true, new HashSet<Class>(Arrays.asList(restrict)));
  }

  /**
   * Locates all nodes representing {@link Rats} actors.
   *
   * @param parent	the parent node
   * @param up		whether to go up in the actor tree
   * @param restrict	the classes to restrict the results to, null if no restrictions
   * @return		the nodes with {@link Rats} found
   */
  protected static List<Node> findRats(Node parent, boolean up, HashSet<Class> restrict) {
    List<Node>		result;
    ActorHandler handler;
    Actor actor;
    Actor		subactor;
    int			i;
    int			n;
    Node		current;

    result = new ArrayList<>();

    if (parent == null)
      return result;

    while (parent != null) {
      if (parent.getActor() instanceof ActorHandler) {
	handler = (ActorHandler) parent.getActor();

	if (handler.getActorHandlerInfo().canContainStandalones()) {
	  for (i = 0; i < parent.getChildCount(); i++) {
	    current = (Node) parent.getChildAt(i);
	    actor   = current.getActor();

	    if (ActorUtils.isStandalone(actor)) {
	      if (!actor.getSkip() && (actor instanceof Rats)) {
		if ((restrict == null) || isRestricted(actor.getClass(), restrict))
		  result.add(current);
	      }
	      else if (actor instanceof Standalones) {
		for (n = 0; n < current.getChildCount(); n++) {
		  subactor = ((Node) current.getChildAt(n)).getActor();
		  if (!subactor.getSkip() && (subactor instanceof Rats)) {
		    if ((restrict == null) || isRestricted(subactor.getClass(), restrict))
		      result.add((Node) current.getChildAt(n));
		  }
		}
	      }
	      else if (actor instanceof ExternalActorHandler) {
		// load in external actor
		current.expand();
		for (n = 0; n < current.getChildCount(); n++)
		  result.addAll(findRats((Node) current.getChildAt(n), false, restrict));
	      }
	    }
	    else {
	      // finished inspecting standalone actors
	      break;
	    }
	  }
	}
      }

      if (up)
	parent = (Node) parent.getParent();
      else
	parent = null;
    }

    return result;
  }

  /**
   * Locates all top nodes representing Rats actors.
   *
   * @param cont	the container to get the root node from
   * @return		the nodes with Rats found
   */
  public static List<Node> findTopRats(Container cont) {
    Node	current;
    Node	parent;

    current = getEditedNode(cont);
    if (current != null)
      parent = (Node) current.getParent();
    else
      parent = getEditedParent(cont);

    // find root
    if (parent != null)
      parent = (Node) parent.getRoot();

    return findTopRats(parent);
  }

  /**
   * Locates all top nodes representing Rats actors.
   *
   * @param parent	the parent node
   * @return		the nodes with Rats found
   */
  public static List<Node> findTopRats(Node parent) {
    List<Node>		result;
    ActorHandler	handler;
    Actor		actor;
    Actor		subactor;
    int			i;
    int			n;
    Node		current;

    result = new ArrayList<>();

    if (parent == null)
      return result;

    if (parent.getActor() instanceof ActorHandler) {
      handler = (ActorHandler) parent.getActor();

      if (handler.getActorHandlerInfo().canContainStandalones()) {
	for (i = 0; i < parent.getChildCount(); i++) {
	  current = (Node) parent.getChildAt(i);
	  actor   = current.getActor();

	  if (ActorUtils.isStandalone(actor)) {
	    if (!actor.getSkip() && (actor instanceof Rats)) {
	      result.add(current);
	    }
	    else if (actor instanceof Standalones) {
	      for (n = 0; n < current.getChildCount(); n++) {
		subactor = ((Node) current.getChildAt(n)).getActor();
		if (!subactor.getSkip() && (subactor instanceof Rats)) {
		  result.add((Node) current.getChildAt(n));
		}
	      }
	    }
	    else if (actor instanceof ExternalActorHandler) {
	      // load in external actor
	      current.expand();
	      for (n = 0; n < current.getChildCount(); n++)
		result.addAll(findTopRats((Node) current.getChildAt(n)));
	    }
	  }
	  else {
	    // finished inspecting standalone actors
	    break;
	  }
	}
      }
    }

    return result;
  }

}
