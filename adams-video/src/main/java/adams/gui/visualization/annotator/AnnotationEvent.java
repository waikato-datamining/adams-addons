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
 * AnnotationEvent.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;

import adams.data.trail.Step;

import java.util.EventObject;

/**
 * An action that is raised when an annotation is required
 *
 * @author sjb90
 * @version $Revision$
 */
public class AnnotationEvent extends EventObject{

  private static final long serialVersionUID = 6962111937607944093L;

  /** The step we're passing to the listeners */
  protected Step m_Step;

  /**
   * Constructs an Annotation Event
   *
   * @param source The object on which the Event initially occurred.
   * @param step The step object the listener will make use of
   * @throws IllegalArgumentException if source is null or if step is null
   */
  public AnnotationEvent(Object source, Step step) {
    super(source);
    if(step == null)
      throw new IllegalArgumentException("null step");
    m_Step = step;
  }

  /**
   * A getter for the step this event is for
   * @return the step associated with this event
   */
  public Step getStep() {
    return m_Step;
  }
}
