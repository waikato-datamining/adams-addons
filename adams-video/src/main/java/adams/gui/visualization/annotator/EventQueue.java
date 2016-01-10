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
 * EventQueue.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.trail.Step;
import adams.data.trail.Trail;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A queue that ensures that events are added to the trail one at a time. This is to prevent race conditions
 *
 * @author sjb90
 * @version $Revision$
 */
public class EventQueue implements AnnotationListener {

  /** the internal queue */
  private ConcurrentLinkedQueue<Step> m_Steps;

  /** the trail we add the steps to */
  private Trail m_Trail;

  /** a bool to let the thread know when to stop */
  private boolean m_Go;

  /**
   * Constructs a queue that adds steps to a trail.
   */
  public EventQueue() {
    m_Trail 	= new Trail();
    m_Go	= true;
    m_Steps	= new ConcurrentLinkedQueue<>();
    start();
  }

  /**
   * Resets the trail to an empty one.
   */
  public void resetTrail() {
    m_Trail 	= new Trail();
  }
  /**
   * A getter for the trail EventQueue adds steps to
   * @return The trail we've been adding steps to
   */
  public Trail getTrail() {
    return m_Trail;
  }

  private void start() {
    Runnable run;
    run = () -> {
      while(m_Go) {
	if(m_Steps.peek() == null) {
	  try {
	    System.out.println("Sleeping for 4 seconds");
	    Thread.sleep(4000);
	  }
	  catch (Exception e) {
	    //don't care
	  }
	}
	else {
	  System.out.println("Getting step from steps");
	  Step step = m_Steps.poll();
	  System.out.println("Step retrieved " + step.toString());

	  Step oldStep = m_Trail.getStep(step.getTimestamp());
	  if (oldStep != null) {
	    if (oldStep.hasMetaData())
	      step.getMetaData().putAll(oldStep.getMetaData());
	  }
	  m_Trail.add(step);
	  System.out.println("Step added");

	}
      }
    };
    Thread t = new Thread(run);
    t.start();
  }

  /**
   * Turns the inner trail into a spreadsheet.
   * @return the spread sheet object
   */
  public SpreadSheet toSpreadSheet() {
    return m_Trail.toSpreadSheet();
  }

  @Override
  public void annotationOccurred(AnnotationEvent e) {
    m_Steps.add(e.getStep());
  }


}
