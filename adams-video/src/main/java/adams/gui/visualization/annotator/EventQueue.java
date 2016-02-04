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
import adams.flow.core.RunnableWithLogging;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A queue that ensures that events are added to the trail one at a time. This is to prevent race conditions
 *
 * @author sjb90
 * @version $Revision$
 */
public class EventQueue implements AnnotationListener {

  /** a constant defining the amount of time to sleep for */
  public static final long SLEEP_TIME = 4000;

  /** the internal queue */
  protected ConcurrentLinkedQueue<Step> m_Steps;

  /** the trail we add the steps to */
  protected Trail m_Trail;

  /** the runable we use for the internal thread */
  protected RunnableWithLogging m_Runnable;

  /** a flag that says if we're paused or not */
  protected boolean m_Playing;

  /**
   * Constructs a queue that adds steps to a trail.
   */
  public EventQueue() {
    m_Trail 	= new Trail();
    m_Steps	= new ConcurrentLinkedQueue<>();
    m_Playing = false;
    start();
  }

  /**
   * Resets the trail to an empty one. Clears the queue so any left over steps are not added mistakenly to the new
   * trail.
   */
  public void resetTrail() {
    clearQueue();
    m_Trail 	= new Trail();
  }

  /**
   * A getter for the trail EventQueue adds steps to
   * @return The trail we've been adding steps to
   */
  public Trail getTrail() {
    return m_Trail;
  }

  /**
   * Loads a saved trail EventQueue adds steps to. Clears the steps queue so any remaining steps are not mistakenly
   * added to the newly loaded queue.
   * @param trail The trail we're loading in
   */
  public void loadTrail(Trail trail) {
    m_Trail = trail;
    clearQueue();
  }

  protected void clearQueue() {
    m_Steps.clear();
  }

  protected void start() {
    m_Runnable = new RunnableWithLogging() {
      @Override
      protected void doRun() {
	while(!m_Stopped) {
	  if(m_Steps.peek() == null) {
	    try {
	      Thread.sleep(SLEEP_TIME);
	    }
	    catch (Exception e) {
	      //don't care
	    }
	  }
	  else {
	    Step step = m_Steps.poll();
	    Step oldStep = m_Trail.getStep(step.getTimestamp());
	    if (oldStep != null) {
	      if (oldStep.hasMetaData())
		step.getMetaData().putAll(oldStep.getMetaData());
	    }
	    m_Trail.add(step);
	  }
	}
      }
    };
    Thread t = new Thread(m_Runnable);
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

  /**
   * Cleans up any loose ends
   */
  public void cleanUp() {
    m_Runnable.stopExecution();
  }

  /**
   * sets the background image to be stored with the trail.
   * @param backgroundImage
   */
  public void setBackgroundImage(BufferedImage backgroundImage) {
    m_Trail.setBackground(backgroundImage);
  }

  /**
   * a getter for the background image stored with the trail
   * @return the image
   */
  public BufferedImage getBackgroundImage() {
    return m_Trail.getBackground();
  }
}
