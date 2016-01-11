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
 * AnnotationPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;

import adams.data.trail.Step;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.BasePanel;
import adams.gui.visualization.video.vlcjplayer.VLCjPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * An Annotation panel that allows for the manual, i.e. non-toggleable, input from a binding
 *
 * @author sjb90
 * @version $Revision$
 */
public class AnnotationPanel extends BasePanel {

  /** The thickness of the border around the button */
  private static final int BORDER_THICKNESS = 5;

  /** The list of listeners registered with this panel */
  protected List<AnnotationListener> m_Listeners;

  /** The binding this panel manages */
  protected Binding m_Binding;

  /** The video player we're annotating for */
  protected VLCjPanel m_VideoPlayer;

  /** the action to perform when the binding is activate */
  protected AbstractBaseAction m_Action;

  /** The Interval which this toggle runs at */
  protected long m_Interval;

  /** A scheduler to execute our command */
  protected ScheduledExecutorService m_Scheduler;

  /** a bool to say if this is a toggleable binding or not */
  protected boolean m_IsToggleable;

  /** a bool to say if this binding is currently toggled on */
  protected boolean m_IsToggled;

  /** A scheduled future to keep track of our task */
  protected ScheduledFuture<?> m_ScheduleHandler;

  /** a button to indicate when a binding is pressed or toggled on */
  protected JButton m_Button;

  /** Listener for the button to change boarder */
  protected AnnotationListener m_Listener;

  /**
   * Constructs a AnnotationPanel
   * @param binding the binding this panel manages
   */
  public void configureAnnotationPanel(Binding binding, VLCjPanel videoPlayer) {
    m_Binding 		= binding;
    m_VideoPlayer	= videoPlayer;
    m_Interval 		= binding.getInterval();
    m_IsToggleable	= binding.isToggleable();
    addKeyBinding(m_Binding);
    start();
  }

  protected void makeStep() {
    long msec = m_VideoPlayer.getTimeStamp();
    if (msec == -1)
      return;
    HashMap<String,Object> meta = new HashMap<>();
    meta.put(m_Binding.getName(), (m_Binding.isInverted() ^ m_IsToggled));
    Date timestamp = new Date(msec);
    Step step = new Step(timestamp, 0.0f, 0.0f, meta);
    notifyListeners(step);
  }

  @Override
  protected void initialize() {
    super.initialize();
    m_IsToggled		= false;
    m_Listeners 	= new ArrayList<>();
    m_Scheduler 	= Executors.newScheduledThreadPool(1);
  }

  @Override
  protected void initGUI() {
    super.initGUI();
    setLayout(new FlowLayout(FlowLayout.CENTER));
    setBorder(new LineBorder(getBackground(), BORDER_THICKNESS));
    m_Button = new JButton();
    add(m_Button);
  }

  @Override
  protected void finishInit() {
    super.finishInit();
  }

  /**
   * Adds a listener to this panel
   * @param listener the listener to add
   */
  public void addListener(AnnotationListener listener) {
    if(m_Listeners.contains(listener))
      return;
    m_Listeners.add(listener);
  }

  /**
   * Adds a binding to the panel
   * @param binding: binding to add
   * @return
   */
  private AbstractBaseAction addKeyBinding(Binding binding) {
    KeyStroke keyStroke = binding.getBinding();
    if(m_IsToggleable) {
      m_Action = new AbstractBaseAction(binding.getName()  +  " (" + binding.getBinding() + ")") {
	@Override
	protected void doActionPerformed(ActionEvent e) {
	  m_IsToggled = !m_IsToggled;
	  if(m_IsToggled) {
	    Runnable run = () -> {
	      setBorder(new LineBorder(Color.YELLOW, BORDER_THICKNESS));
	      revalidate();
	    };
	    SwingUtilities.invokeLater(run);
	  }
	  else {
	    Runnable run = () -> {
	      setBorder(new LineBorder(getBackground(), BORDER_THICKNESS));
	      revalidate();
	    };
	    SwingUtilities.invokeLater(run);
	  }
	}
      };
    }
    else {
      m_Action = new AbstractBaseAction(binding.getName()) {
	@Override
	protected void doActionPerformed(ActionEvent e) {
	  makeStep();
	}
      };
    }
    // Set up the button for this binding
    m_Button.setAction(m_Action);
    getInputMap(WHEN_IN_FOCUSED_WINDOW).put(keyStroke, binding.getName());
    getActionMap().put(binding.getName(), m_Action);
    return m_Action;
  }

  /**
   * Notifies all listeners
   * @param step the step to send in the notification
   */
  private void notifyListeners(Step step) {
    if(m_Listeners == null) {
      return;
    }
    AnnotationEvent e = new AnnotationEvent(this, step);
    for (AnnotationListener listener : m_Listeners) {
      listener.annotationOccurred(e);
    }
  }

  /**
   * Starts the internal thread that handles adding events to the queue
   */
  private void start() {
    Runnable run = () -> {
      makeStep();
    };
    m_ScheduleHandler = m_Scheduler.scheduleAtFixedRate(run, 0, m_Interval, TimeUnit.MILLISECONDS);
  }

  /**
   * Shuts down the
   */
  public void cleanUp() {
    if(m_ScheduleHandler != null)
      m_ScheduleHandler.cancel(false);
  }
}
