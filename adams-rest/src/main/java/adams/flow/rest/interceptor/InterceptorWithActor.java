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
 * InterceptorWithActor.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.rest.interceptor;

import adams.flow.core.Actor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptor;

/**
 * Interceptor that has access to an actor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface InterceptorWithActor
  extends PhaseInterceptor<Message> {

  /**
   * Sets the actor to use.
   * 
   * @param value	the actor to use
   */
  public void setActor(Actor value);
  
  /**
   * Returns the actor in use.
   * 
   * @return		the actor in use
   */
  public Actor getActor();
}
