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
 * RatControl.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.Pausable;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateListener;
import adams.event.RatStateEvent;
import adams.event.RatStateListener;
import adams.flow.core.AbstractDisplay;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.PauseStateHandler;
import adams.flow.core.PauseStateManager;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Control actor for Rats&#47;Rat actors.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: RatControl
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-display-in-editor &lt;boolean&gt; (property: displayInEditor)
 * &nbsp;&nbsp;&nbsp;If enabled displays the panel in a tab in the flow editor rather than in 
 * &nbsp;&nbsp;&nbsp;a separate frame.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RatControl
  extends AbstractDisplay
  implements FlowPauseStateListener, RatStateListener {

  /** for serialization. */
  private static final long serialVersionUID = 2777897240842864503L;
  
  /**
   * Ancestor for control panels.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 107 $
   */
  public static abstract class AbstractControlPanel<T extends Actor & Pausable>
    extends BasePanel {
    
    /** for serialization. */
    private static final long serialVersionUID = -5965060223206287867L;
    
    /** the actor to manage. */
    protected T m_Actor;
    
    /** the button for pausing/resuming. */
    protected JButton m_ButtonPauseResume;
    
    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();
      
      setLayout(new FlowLayout(FlowLayout.LEFT));
      
      m_ButtonPauseResume = new JButton(GUIHelper.getIcon("pause.gif"));
      m_ButtonPauseResume.addActionListener((ActionEvent e) -> pauseOrResume());
      add(m_ButtonPauseResume);
    }

    /**
     * Finishes the initialization.
     */
    @Override
    protected void finishInit() {
      super.finishInit();
      updateButtons();
    }

    /**
     * Sets the actor to manage.
     * 
     * @param value	the actor
     */
    public void setActor(T value) {
      m_Actor = value;
      updateButtons();
    }
    
    /**
     * Returns the actor in use.
     * 
     * @return		the actor
     */
    public T getActor() {
      return m_Actor;
    }
    
    /**
     * Pauses/resumes the processor applier.
     */
    public void pauseOrResume() {
      if (m_Actor == null)
	return;
      if (m_Actor.isPaused())
	m_Actor.resumeExecution();
      else
	m_Actor.pauseExecution();
      updateButtons();
    }
    
    /**
     * Updates the state of the buttons.
     */
    public void updateButtons() {
      if (m_Actor == null)
	return;
      if (m_Actor.isPaused())
	m_ButtonPauseResume.setIcon(GUIHelper.getIcon("resume.gif"));
      else
	m_ButtonPauseResume.setIcon(GUIHelper.getIcon("pause.gif"));
    }
    
    /**
     * Sets the "pauseable" state of the control panel.
     * 
     * @param value	true if to enable
     */
    public void setPausable(boolean value) {
      m_ButtonPauseResume.setVisible(value);
    }
    
    /**
     * Returns whether the control panel is enabled.
     * 
     * @return		true if enabled
     */
    public boolean isPausable() {
      return m_ButtonPauseResume.isVisible();
    }
  }
  
  /**
   * Control panel for {@link Rats} actor.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 107 $
   */
  public static class RatsControlPanel
    extends AbstractControlPanel<Rats> {
    
    /** for serialization. */
    private static final long serialVersionUID = 4516229240505598425L;
  }
  
  /**
   * Control panel for {@link Rat} actor.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 107 $
   */
  public static class RatControlPanel
    extends AbstractControlPanel<Rat> {
    
    /** for serialization. */
    private static final long serialVersionUID = 4516229240505598425L;

    /** the button for stopping/starting. */
    protected JButton m_ButtonStopStart;

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      m_ButtonStopStart = new JButton(GUIHelper.getIcon("run.gif"));
      m_ButtonStopStart.addActionListener((ActionEvent e) -> stopOrStart());
      add(m_ButtonStopStart);
    }
    
    /**
     * Stops/starts the rat.
     *
     * @return		null if successful, otherwise error message
     */
    public String stopOrStart() {
      String	result;

      if (m_Actor == null)
	return null;

      result = null;
      if (m_Actor.isRunnableActive())
	m_Actor.stopRunnable();
      else
	result = m_Actor.startRunnable();

      updateButtons();

      return result;
    }
    
    /**
     * Updates the state of the buttons.
     */
    public void updateButtons() {
      if (m_Actor == null)
	return;

      m_ButtonPauseResume.setEnabled(m_Actor.isRunnableActive());
      if (m_Actor.isRunnableActive() && m_Actor.isPaused())
	m_ButtonPauseResume.setIcon(GUIHelper.getIcon("resume.gif"));
      else
	m_ButtonPauseResume.setIcon(GUIHelper.getIcon("pause.gif"));

      if (m_Actor.isRunnableActive())
	m_ButtonStopStart.setIcon(GUIHelper.getIcon("stop_blue.gif"));
      else
	m_ButtonStopStart.setIcon(GUIHelper.getIcon("run.gif"));
    }
    
    /**
     * Sets the "stoppable" state of the control panel.
     * 
     * @param value	true if to enable
     */
    public void setStoppable(boolean value) {
      m_ButtonStopStart.setVisible(value);
    }
    
    /**
     * Returns whether the "stoppable" state of the control panel is enabled.
     * 
     * @return		true if enabled
     */
    public boolean isStoppable() {
      return m_ButtonStopStart.isVisible();
    }
  }
  
  /** the control panels. */
  protected List<AbstractControlPanel> m_ControlPanels;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Control actor for Rats/Rat actors.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_ControlPanels = new ArrayList<>();
  }
  
  /**
   * Returns the default value for displaying the panel in the editor
   * rather than in a separate frame.
   * 
   * @return		the default
   */
  @Override
  protected boolean getDefaultDisplayInEditor() {
    return true;
  }

  /**
   * Returns whether to de-register in {@link #wrapUp()} or wait till 
   * {@link #cleanUpGUI()}.
   * 
   * @return		true if to deregister already in {@link #wrapUp()}
   */
  @Override
  protected boolean deregisterInWrapUp() {
    return true;
  }
  
  /**
   * Does nothing.
   */
  @Override
  public void clearPanel() {
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel			result;
    List<Actor> 		list;
    Rats			rats;
    Rat				rat;
    AbstractControlPanel	cpanel;
    AbstractControlPanel	subcpanel;
    ParameterPanel		param;
    JPanel			panel;
    JButton			buttonStop;
    int				i;
    boolean			inControl;
    
    param = new ParameterPanel();
    list  = ActorUtils.findClosestTypes(this, Rats.class, true);
    for (Actor item: list) {
      rats = (Rats) item;
      cpanel = new RatsControlPanel();
      cpanel.setActor(rats);
      param.addParameter(rats.getName(), cpanel);
      m_ControlPanels.add(cpanel);
      // the individual Rat actors
      inControl = false;
      for (i = 0; i < rats.size(); i++) {
	rat = (Rat) rats.get(i);
	if (!rat.getShowInControl())
	  continue;
	inControl = true;
	subcpanel = new RatControlPanel();
	subcpanel.setActor(rat);
	rat.addRatStateListener(this);
	param.addParameter(" - " + rat.getName(), subcpanel);
	m_ControlPanels.add(subcpanel);
      }
      cpanel.setPausable(!inControl);
    }
    
    // general buttons
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonStop = new JButton("Stop");
    buttonStop.addActionListener((ActionEvent e) -> getRoot().stopExecution());
    panel.add(buttonStop);
    
    result = new BasePanel(new BorderLayout());
    result.add(new BaseScrollPane(param), BorderLayout.CENTER);
    result.add(panel, BorderLayout.SOUTH);

    return result;
  }

  /**
   * Returns a runnable that displays frame, etc.
   * Must call notifyAll() on the m_Self object and set m_Updating to false.
   *
   * @return		the runnable
   * @see		#m_Updating
   */
  @Override
  protected Runnable newDisplayRunnable() {
    Runnable	result;

    result = new Runnable() {
      public void run() {
	if (getCreateFrame() && !m_Frame.isVisible())
	  m_Frame.setVisible(true);
	for (AbstractControlPanel panel: m_ControlPanels)
	  panel.updateButtons();
	synchronized(m_Self) {
	  m_Self.notifyAll();
	}
	m_Updating = false;
      }
    };

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    PauseStateManager 	manager;

    result = super.setUp();

    if (result == null) {
      if (getRoot() instanceof PauseStateHandler) {
	manager = ((PauseStateHandler) getRoot()).getPauseStateManager();
	if (manager != null)
	  manager.addListener(this);
      }
    }

    return result;
  }

  /**
   * Gets called when the pause state of the flow changes.
   *
   * @param e		the event
   */
  @Override
  public void flowPauseStateChanged(FlowPauseStateEvent e) {
    SwingUtilities.invokeLater(() -> {
      for (AbstractControlPanel panel: m_ControlPanels)
	panel.updateButtons();
    });
  }

  /**
   * Gets called in case the state of a Rat actor changes.
   *
   * @param e		the event
   */
  public void ratStateChanged(RatStateEvent e) {
    SwingUtilities.invokeLater(() -> {
      for (AbstractControlPanel panel: m_ControlPanels) {
	if (panel instanceof RatControlPanel) {
	  RatControlPanel rpanel = (RatControlPanel) panel;
	  if (rpanel.getActor() == e.getRat()) {
	    panel.updateButtons();
	    break;
	  }
	}
      }
    });
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    PauseStateManager	manager;

    m_ControlPanels.clear();

    if (getRoot() instanceof PauseStateHandler) {
      manager = ((PauseStateHandler) getRoot()).getPauseStateManager();
      if (manager != null)
	manager.removeListener(this);
    }

    super.cleanUp();
  }
}
