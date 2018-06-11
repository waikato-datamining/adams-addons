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
 * EventQueue.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.audioannotator;

import adams.data.audioannotations.AudioAnnotation;
import adams.data.audioannotations.AudioAnnotations;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.RunnableWithLogging;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A queue that ensures that events are added to the annotations one at a time. This is to prevent race conditions
 *
 * @author sjb90
 */
public class EventQueue implements AnnotationListener {

  /** a constant defining the amount of time to sleep for */
  public static final long SLEEP_TIME = 4000;

  /** the internal queue */
  protected ConcurrentLinkedQueue<AudioAnnotation> m_AnnotationQueue;

  /** the annotations we add the steps to */
  protected AudioAnnotations m_Annotations;

  /** the runable we use for the internal thread */
  protected RunnableWithLogging m_Runnable;

  /** a flag that says if we're paused or not */
  protected boolean m_Playing;

  /**
   * Constructs a queue that adds steps to a annotations.
   */
  public EventQueue() {
    m_Annotations = new AudioAnnotations();
    m_AnnotationQueue = new ConcurrentLinkedQueue<>();
    m_Playing = false;
    start();
  }

  /**
   * Resets the annotations to an empty one. Clears the queue so any left over steps are not added mistakenly to the new
   * annotations.
   */
  public void resetAnnotations() {
    clearQueue();
    m_Annotations = new AudioAnnotations();
  }

  /**
   * A getter for the annotations EventQueue adds steps to
   * @return The annotations we've been adding steps to
   */
  public AudioAnnotations getAnnotations() {
    return m_Annotations;
  }

  /**
   * Loads a saved annotations EventQueue adds steps to. Clears the steps queue so any remaining steps are not mistakenly
   * added to the newly loaded queue.
   * @param annotations The annotations we're loading in
   */
  public void loadAnnotations(AudioAnnotations annotations) {
    m_Annotations = annotations;
    clearQueue();
  }

  protected void clearQueue() {
    m_AnnotationQueue.clear();
  }

  protected void start() {
    m_Runnable = new RunnableWithLogging() {
      @Override
      protected void doRun() {
	while(!m_Stopped) {
	  if(m_AnnotationQueue.peek() == null) {
	    try {
	      Thread.sleep(SLEEP_TIME);
	    }
	    catch (Exception e) {
	      //don't care
	    }
	  }
	  else {
	    AudioAnnotation step = m_AnnotationQueue.poll();
	    AudioAnnotation oldStep = m_Annotations.getStep(step.getTimestamp());
	    if (oldStep != null) {
	      if (oldStep.hasMetaData())
		step.getMetaData().putAll(oldStep.getMetaData());
	    }
	    m_Annotations.add(step);
	  }
	}
      }
    };
    Thread t = new Thread(m_Runnable);
    t.start();
  }

  /**
   * Turns the inner annotations into a spreadsheet.
   * @return the spread sheet object
   */
  public SpreadSheet toSpreadSheet() {
    return m_Annotations.toSpreadSheet();
  }

  @Override
  public void annotationOccurred(AnnotationEvent e) {
    m_AnnotationQueue.add(e.getAnnotation());
  }

  /**
   * Cleans up any loose ends
   */
  public void cleanUp() {
    m_Runnable.stopExecution();
  }
}
