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
import adams.gui.visualization.video.vlcjplayer.VLCjDirectRenderPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * An Annotation panel that allows for the manual, i.e. non-toggleable, input from a binding
 *
 * @author sjb90
 * @version $Revision$
 */
public class AnnotationPanel extends BasePanel implements TickListener {

  private static final long serialVersionUID = -8145795884307782157L;

  /** The thickness of the border around the button */
  public static final int BORDER_THICKNESS = 5;

  /** the off colour for a togglable button's boarder */
  public static final java.awt.Color OFF_COLOUR = Color.RED;

  /** the on colour for a togglable button's boarder */
  public static final java.awt.Color ON_COLOUR = Color.GREEN;

  /** The list of listeners registered with this panel */
  protected List<AnnotationListener> m_Listeners;

  /** The binding this panel manages */
  protected Binding m_Binding;

  /** The video player we're annotating for */
  protected VLCjDirectRenderPanel m_VideoPlayer;

  /** the action to perform when the binding is activate */
  protected AbstractBaseAction m_Action;

  /** The Interval which this toggle runs at */
  protected long m_Interval;

  /** a bool to say if this is a toggleable binding or not */
  protected boolean m_IsToggleable;

  /** a bool to say if this binding is currently toggled on */
  protected boolean m_IsToggled;

  /** a button to indicate when a binding is pressed or toggled on */
  protected JButton m_Button;

  /**
   * Constructs a AnnotationPanel
   * @param binding the binding this panel manages
   */
  public void configureAnnotationPanel(Binding binding, VLCjDirectRenderPanel videoPlayer) {
    m_Binding 		= binding;
    m_VideoPlayer	= videoPlayer;
    m_Interval 		= binding.getInterval();
    m_IsToggleable	= binding.isToggleable();
    addKeyBinding(m_Binding);
  }

  protected void makeStep(Date timestamp) {
    HashMap<String,Object> meta = new HashMap<>();
    if (m_IsToggleable)
      meta.put(m_Binding.getName(), (m_Binding.isInverted() ^ m_IsToggled));
    else
      meta.put(m_Binding.getName(), (!m_Binding.isInverted()));
    Step step = new Step(timestamp, 0.0f, 0.0f, meta);
    notifyListeners(step);
  }

  @Override
  protected void initialize() {
    super.initialize();
    m_IsToggled		= false;
    m_Listeners 	= new ArrayList<>();
  }

  @Override
  protected void initGUI() {
    super.initGUI();
    setLayout(new FlowLayout(FlowLayout.CENTER));
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
  protected AbstractBaseAction addKeyBinding(Binding binding) {
    KeyStroke keyStroke = binding.getBinding();
    if(m_IsToggleable) {
      setBorder(new LineBorder(OFF_COLOUR, BORDER_THICKNESS));
      m_Action = new AbstractBaseAction(binding.getName()  +  " (" + binding.getBinding() + ")") {
	@Override
	protected void doActionPerformed(ActionEvent e) {
	  m_IsToggled = !m_IsToggled;
	  if(m_IsToggled) {
	    Runnable run = () -> {
	      setBorder(new LineBorder(ON_COLOUR, BORDER_THICKNESS));
	      revalidate();
	    };
	    SwingUtilities.invokeLater(run);
	  }
	  else {
	    Runnable run = () -> {
	      setBorder(new LineBorder(OFF_COLOUR, BORDER_THICKNESS));
	      revalidate();
	    };
	    SwingUtilities.invokeLater(run);
	  }
	}
      };
    }
    else {
      setBorder(new LineBorder(getBackground(), BORDER_THICKNESS));
      m_Action = new AbstractBaseAction(binding.getName()  +  " (" + binding.getBinding() + ")") {
	@Override
	protected void doActionPerformed(ActionEvent e) {
	  Date timestamp = new Date(m_VideoPlayer.getTimeStamp());
	  makeStep(timestamp);
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
  protected void notifyListeners(Step step) {
    if(m_Listeners == null) {
      return;
    }
    AnnotationEvent e = new AnnotationEvent(this, step);
    for (AnnotationListener listener : m_Listeners) {
      listener.annotationOccurred(e);
    }
  }

  @Override
  public void tickHappened(TickEvent e) {
    makeStep(e.getTimeStamp());
  }

  @Override
  public long getInterval() {
    return m_Interval;
  }
}
