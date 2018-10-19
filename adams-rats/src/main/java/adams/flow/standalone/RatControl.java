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
 * RatControl.java
 * Copyright (C) 2014-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.Pausable;
import adams.core.logging.LoggingObject;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateListener;
import adams.event.RatStateEvent;
import adams.event.RatStateListener;
import adams.flow.core.AbstractDisplay;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.PauseStateHandler;
import adams.flow.core.PauseStateManager;
import adams.flow.core.RatMode;
import adams.flow.core.RatState;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
   */
  public static abstract class AbstractControlPanel<T extends Actor & Pausable>
    extends BasePanel {
    
    /** for serialization. */
    private static final long serialVersionUID = -5965060223206287867L;

    /** the owner. */
    protected RatControl m_Owner;

    /** the rats group this belongs to. */
    protected Rats m_Group;

    /** the actor to manage. */
    protected T m_Actor;

    /** the checkbox for bulk actions. */
    protected BaseCheckBox m_CheckBoxBulkAction;

    /** the button for pausing/resuming. */
    protected BaseButton m_ButtonPauseResume;
    
    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();
      
      setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));

      m_CheckBoxBulkAction = new BaseCheckBox();
      m_CheckBoxBulkAction.setVisible(false);
      m_CheckBoxBulkAction.addActionListener((ActionEvent e) -> checkBoxBulkActionTrigger());
      m_CheckBoxBulkAction.setToolTipText(getCheckBoxBulkActionToolTipText());
      add(m_CheckBoxBulkAction);

      m_ButtonPauseResume = new BaseButton(GUIHelper.getIcon("pause.gif"));
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
     * Sets the RatControl actor this control belongs to.
     *
     * @param value	the owner
     */
    public void setOwner(RatControl value) {
      m_Owner = value;
    }

    /**
     * Returns the RatControl actor this control belongs to.
     *
     * @return		the owner
     */
    public RatControl getOwner() {
      return m_Owner;
    }

    /**
     * Sets the Rats groups this control belongs to.
     *
     * @param value	the group
     */
    public void setGroup(Rats value) {
      m_Group = value;
    }

    /**
     * Returns the Rats group this control belongs to.
     *
     * @return		the group
     */
    public Rats getGroup() {
      return m_Group;
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
     * Returns the tool tip for the bulk action checkbox.
     * <br>
     * Default implementation is null.
     *
     * @return		the tip text
     */
    protected String getCheckBoxBulkActionToolTipText() {
      return null;
    }

    /**
     * For custom actions when the bulk action checkbox is selected/unselected.
     * <br>
     * Default implementation does nothing.
     */
    protected void checkBoxBulkActionTrigger() {
    }

    /**
     * Checks whether bulk action is enabled.
     *
     * @return		true if enabled
     */
    public boolean isBulkActionEnabled() {
      return m_CheckBoxBulkAction.isVisible();
    }

    /**
     * Sets whether bulk action is enabled.
     *
     * @param value	true if enabled
     */
    public void setBulkActionEnabled(boolean value) {
      m_CheckBoxBulkAction.setVisible(value);
    }

    /**
     * Checks whether bulk action checkbox is selected.
     * Only works if {@link #isBulkActionEnabled()}.
     *
     * @return		true if checked
     * @see		#isBulkActionEnabled()
     */
    public boolean isChecked() {
      return isBulkActionEnabled() && m_CheckBoxBulkAction.isSelected();
    }

    /**
     * Sets the selected state of the bulk action checkbox.
     * Only works if {@link #isBulkActionEnabled()}.
     *
     * @param value 	true if to check
     * @see		#isBulkActionEnabled()
     */
    public void setChecked(boolean value) {
      if (isBulkActionEnabled())
        m_CheckBoxBulkAction.setSelected(value);
    }

    /**
     * Pauses the rat.
     */
    public void pause() {
      SwingWorker	worker;

      if (m_Actor == null)
	return;

      worker = new SwingWorker() {
	@Override
	protected Object doInBackground() throws Exception {
	  m_Actor.pauseExecution();
	  updateButtons();
	  return null;
	}
      };
      worker.execute();
    }

    /**
     * Resumes the rat.
     */
    public void resume() {
      SwingWorker	worker;

      if (m_Actor == null)
	return;

      worker = new SwingWorker() {
	@Override
	protected Object doInBackground() throws Exception {
	  m_Actor.resumeExecution();
	  updateButtons();
	  return null;
	}
      };
      worker.execute();
    }

    /**
     * Pauses/resumes the rat.
     */
    public void pauseOrResume() {
      if (m_Actor == null)
	return;

      if (m_Actor.isPaused())
	resume();
      else
	pause();
    }
    
    /**
     * Updates the state of the buttons.
     */
    public void updateButtons() {
      if (m_Actor == null)
	return;
      m_ButtonPauseResume.setEnabled(true);
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
   */
  public static class RatsControlPanel
    extends AbstractControlPanel<Rats> {
    
    /** for serialization. */
    private static final long serialVersionUID = 4516229240505598425L;

    /**
     * Returns the tool tip for the bulk action checkbox.
     *
     * @return		the tip text
     */
    protected String getCheckBoxBulkActionToolTipText() {
      return "Checks/unchecks all rat actors in this group";
    }

    /**
     * For custom actions when the bulk action checkbox is selected/unselected.
     */
    protected void checkBoxBulkActionTrigger() {
      List<AbstractControlPanel>	panels;

      if (getOwner() == null)
        return;
      if (getGroup() == null)
        return;

      panels = getOwner().getControlPanelsPerRats().get(getGroup());
      for (AbstractControlPanel panel: panels) {
        if (panel instanceof RatControlPanel)
	  panel.setChecked(m_CheckBoxBulkAction.isSelected());
      }
    }
  }
  
  /**
   * Control panel for {@link Rat} actor.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class RatControlPanel
    extends AbstractControlPanel<Rat> {
    
    /** for serialization. */
    private static final long serialVersionUID = 4516229240505598425L;

    /** the button for stopping/starting. */
    protected BaseButton m_ButtonStopStart;

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      m_ButtonStopStart = new BaseButton(GUIHelper.getIcon("run.gif"));
      m_ButtonStopStart.addActionListener((ActionEvent e) -> stopOrStart());
      add(m_ButtonStopStart);
    }

    /**
     * Stops the rat.
     */
    public void stop() {
      SwingWorker	worker;

      if (m_Actor == null)
	return;

      worker = new SwingWorker() {
	@Override
	protected Object doInBackground() throws Exception {
	  m_ButtonStopStart.setEnabled(false);
	  if (m_Actor.isRunnableActive())
	    m_Actor.stopRunnable();
	  updateButtons();
	  return null;
	}
      };
      worker.execute();
    }

    /**
     * Starts the rat.
     */
    public void start() {
      SwingWorker	worker;

      if (m_Actor == null)
	return;

      worker = new SwingWorker() {
	@Override
	protected Object doInBackground() throws Exception {
	  m_ButtonStopStart.setEnabled(false);
	  if (!m_Actor.isRunnableActive()) {
	    if (m_Actor.getInitialState() == RatState.PAUSED)
	      m_Actor.setInitialState(RatState.RUNNING);
	    m_Actor.startRunnable();
	  }
	  updateButtons();
	  return null;
	}
      };
      worker.execute();
    }

    /**
     * Stops/starts the rat.
     */
    public void stopOrStart() {
      SwingWorker	worker;

      if (m_Actor == null)
	return;

      if (m_Actor.isRunnableActive())
	stop();
      else
	start();
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

      m_ButtonStopStart.setEnabled(true);
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

  /**
   * Ancestor for control states.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static abstract class AbstractControlState<T extends Actor & Pausable>
    extends LoggingObject {

    /** for serialization. */
    private static final long serialVersionUID = -5965060223206287867L;

    /** the owner. */
    protected RatControl m_Owner;

    /** the rats group this belongs to. */
    protected Rats m_Group;

    /** the actor to manage. */
    protected T m_Actor;

    /** the button for pausing/resuming. */
    protected boolean m_PauseResume;

    /**
     * Sets the RatControl actor this control belongs to.
     *
     * @param value	the owner
     */
    public void setOwner(RatControl value) {
      m_Owner = value;
    }

    /**
     * Returns the RatControl actor this control belongs to.
     *
     * @return		the owner
     */
    public RatControl getOwner() {
      return m_Owner;
    }

    /**
     * Sets the Rats groups this control belongs to.
     *
     * @param value	the group
     */
    public void setGroup(Rats value) {
      m_Group = value;
    }

    /**
     * Returns the Rats group this control belongs to.
     *
     * @return		the group
     */
    public Rats getGroup() {
      return m_Group;
    }

    /**
     * Sets the actor to manage.
     *
     * @param value	the actor
     */
    public void setActor(T value) {
      m_Actor = value;
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
     * Pauses the rat.
     */
    public void pause() {
      if (m_Actor == null)
	return;
      m_Actor.pauseExecution();
    }

    /**
     * Resumes the rat.
     */
    public void resume() {
      if (m_Actor == null)
	return;
      m_Actor.resumeExecution();
    }

    /**
     * Pauses/resumes the rat.
     */
    public void pauseOrResume() {
      if (m_Actor == null)
	return;
      if (m_Actor.isPaused())
	resume();
      else
	pause();
    }

    /**
     * Sets the "pauseable" state of the control panel.
     *
     * @param value	true if to enable
     */
    public void setPausable(boolean value) {
      m_PauseResume = value;
    }

    /**
     * Returns whether the control panel is enabled.
     *
     * @return		true if enabled
     */
    public boolean isPausable() {
      return m_PauseResume;
    }
  }

  /**
   * Control state for {@link Rats} actor.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class RatsControlState
    extends AbstractControlState<Rats> {

    /** for serialization. */
    private static final long serialVersionUID = 4516229240505598425L;
  }

  /**
   * Control state for {@link Rat} actor.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class RatControlState
    extends AbstractControlState<Rat> {

    /** for serialization. */
    private static final long serialVersionUID = 4516229240505598425L;

    /** the button for stopping/starting. */
    protected boolean m_StopStart;

    /**
     * Stops the rat.
     *
     * @return		null if successful, otherwise error message
     */
    public String stop() {
      if (m_Actor == null)
	return null;

      if (m_Actor.isRunnableActive())
	m_Actor.stopRunnable();
      return null;
    }

    /**
     * Starts the rat.
     *
     * @return		null if successful, otherwise error message
     */
    public String start() {
      String	result;

      if (m_Actor == null)
	return null;

      result = null;
      if (!m_Actor.isRunnableActive())
	result = m_Actor.startRunnable();

      return result;
    }

    /**
     * Stops/starts the rat.
     *
     * @return		null if successful, otherwise error message
     */
    public String stopOrStart() {
      if (m_Actor == null)
	return null;

      if (m_Actor.isRunnableActive())
	return stop();
      else
	return start();
    }

    /**
     * Updates the state of the buttons.
     */
    public void updateButtons() {
      if (m_Actor == null)
	return;

      m_PauseResume = m_Actor.isRunnableActive();
    }

    /**
     * Sets the "stoppable" state of the control staet.
     *
     * @param value	true if to enable
     */
    public void setStoppable(boolean value) {
      m_StopStart = value;
    }

    /**
     * Returns whether the "stoppable" state of the control state is enabled.
     *
     * @return		true if enabled
     */
    public boolean isStoppable() {
      return m_StopStart;
    }
  }

  /** Caption for no bulk action. */
  public final static String BULKACTION_NONE = "---";

  /** Caption for "pause" bulk action. */
  public final static String BULKACTION_PAUSE = "Pause";

  /** Caption for "resume" bulk action. */
  public final static String BULKACTION_RESUME = "Resume";

  /** Caption for "stop" bulk action. */
  public final static String BULKACTION_STOP = "Stop";

  /** Caption for "start" bulk action. */
  public final static String BULKACTION_START = "Start";

  /** whether to allow bulk actions. */
  protected boolean m_BulkActions;

  /** the control panels. */
  protected List<AbstractControlPanel> m_ControlPanels;

  /** the control panels per Rats actor. */
  protected Map<Rats, List<AbstractControlPanel>> m_ControlPanelsPerRats;

  /** the control states. */
  protected List<AbstractControlState> m_ControlStates;

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
    
    m_ControlPanels        = new ArrayList<>();
    m_ControlPanelsPerRats = new HashMap<>();
    m_ControlStates        = new ArrayList<>();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "bulk-actions", "bulkActions",
      false);
  }

  /**
   * Sets whether to enable bulk actions.
   *
   * @param value	true if to enable bulk actions
   */
  public void setBulkActions(boolean value) {
    m_BulkActions = value;
    reset();
  }

  /**
   * Returns whether to enable bulk actions.
   *
   * @return		true if bulk actions enabled
   */
  public boolean getBulkActions() {
    return m_BulkActions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bulkActionsTipText() {
    return "If enabled, bulk actions can be performed on the checked rats.";
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
   * Returns the current control panels.
   *
   * @return		the panels
   */
  public List<AbstractControlPanel> getControlPanels() {
    return m_ControlPanels;
  }

  /**
   * Returns the current control panels, grouped by RatControl actor.
   *
   * @return		the panels
   */
  public Map<Rats,List<AbstractControlPanel>> getControlPanelsPerRats() {
    return m_ControlPanelsPerRats;
  }

  /**
   * Returns the current control states.
   *
   * @return		the states
   */
  public List<AbstractControlState> getControlStates() {
    return m_ControlStates;
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
   * @see		#m_ControlPanels
   * @see		#m_ControlPanelsPerRats
   * @see		#setUpControlStates()
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
    JPanel			panelBottom;
    BaseButton			buttonStop;
    final JComboBox<String> 	comboBulkActions;
    BaseButton			buttonApply;
    int				i;
    boolean			inControl;
    
    param = new ParameterPanel();
    param.setUseMnemonicIndicators(false);
    list  = ActorUtils.findClosestTypes(this, Rats.class, true);
    for (Actor item: list) {
      rats = (Rats) item;
      cpanel = new RatsControlPanel();
      cpanel.setOwner(this);
      cpanel.setGroup(rats);
      cpanel.setBulkActionEnabled(m_BulkActions);
      cpanel.setActor(rats);
      param.addParameter(rats.getName(), cpanel);
      m_ControlPanels.add(cpanel);
      m_ControlPanelsPerRats.put(rats, new ArrayList<>());
      m_ControlPanelsPerRats.get(rats).add(cpanel);
      // the individual Rat actors
      inControl = false;
      for (i = 0; i < rats.size(); i++) {
	rat = (Rat) rats.get(i);
	if (!rat.getShowInControl())
	  continue;
	rat.addRatStateListener(this);
	inControl = true;
        subcpanel = new RatControlPanel();
        subcpanel.setOwner(this);
        subcpanel.setGroup(rats);
        subcpanel.setBulkActionEnabled(m_BulkActions);
        subcpanel.setPausable(rat.getMode() == RatMode.CONTINUOUS);
	subcpanel.setActor(rat);
	param.addParameter(" - " + rat.getName(), subcpanel);
	m_ControlPanels.add(subcpanel);
	m_ControlPanelsPerRats.get(rats).add(subcpanel);
      }
      cpanel.setPausable(!inControl);
    }

    panelBottom = new JPanel(new BorderLayout(0, 0));
    panelBottom.setBorder(BorderFactory.createEmptyBorder());

    // bulk actions
    if (m_BulkActions) {
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      panelBottom.add(panel, BorderLayout.WEST);
      comboBulkActions = new JComboBox<>(new DefaultComboBoxModel<>(new String[]{
        BULKACTION_NONE,
	BULKACTION_PAUSE,
	BULKACTION_RESUME,
	BULKACTION_START,
	BULKACTION_STOP,
      }));
      panel.add(comboBulkActions);
      buttonApply = new BaseButton("Apply");
      buttonApply.addActionListener((ActionEvent e) -> {
        SwingWorker worker = new SwingWorker() {
	  @Override
	  protected Object doInBackground() throws Exception {
	    buttonApply.setEnabled(false);
	    applyBulkAction((String) comboBulkActions.getSelectedItem());
	    return null;
	  }
	  @Override
	  protected void done() {
	    super.done();
	    buttonApply.setEnabled(true);
	  }
	};
        worker.execute();
      });
      panel.add(buttonApply);
    }

    // general buttons
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonStop = new BaseButton("Stop");
    buttonStop.addActionListener((ActionEvent e) -> getRoot().stopExecution());
    panel.add(buttonStop);
    panelBottom.add(panel, BorderLayout.EAST);
    
    result = new BasePanel(new BorderLayout());
    result.add(new BaseScrollPane(param), BorderLayout.CENTER);
    result.add(panelBottom, BorderLayout.SOUTH);

    setUpControlStates();

    return result;
  }

  /**
   * Configures the control states.
   *
   * @see		#m_ControlStates
   */
  protected void setUpControlStates() {
    List<Actor> 		list;
    Rats			rats;
    Rat				rat;
    AbstractControlState	cstate;
    AbstractControlState	subcstate;
    int				i;
    boolean			inControl;

    list  = ActorUtils.findClosestTypes(this, Rats.class, true);
    for (Actor item: list) {
      rats = (Rats) item;
      cstate = new RatsControlState();
      cstate.setOwner(this);
      cstate.setGroup(rats);
      cstate.setActor(rats);
      m_ControlStates.add(cstate);
      // the individual Rat actors
      inControl = false;
      for (i = 0; i < rats.size(); i++) {
	rat = (Rat) rats.get(i);
	if (!rat.getShowInControl())
	  continue;
	rat.addRatStateListener(this);
	inControl = true;
        subcstate = new RatControlState();
        subcstate.setOwner(this);
        subcstate.setGroup(rats);
        subcstate.setPausable(rat.getMode() == RatMode.CONTINUOUS);
	subcstate.setActor(rat);
	m_ControlStates.add(subcstate);
      }
      cstate.setPausable(!inControl);
    }
  }

  /**
   * Applies the bulk action.
   *
   * @param action 	the action to execute
   */
  protected void applyBulkAction(String action) {
    RatControlPanel	rcpanel;

    if (!m_BulkActions)
      return;
    if (action.equals(BULKACTION_NONE))
      return;

    for (AbstractControlPanel panel: getControlPanels()) {
      switch (action) {
	case BULKACTION_PAUSE:
	  if (panel.isPausable())
	    panel.pause();
	  break;

	case BULKACTION_RESUME:
	  if (panel.isPausable())
	    panel.resume();
	  break;

	case BULKACTION_STOP:
	  if (panel instanceof RatControlPanel) {
	    rcpanel = (RatControlPanel) panel;
	    if (rcpanel.isStoppable())
	      rcpanel.stop();
	  }
	  break;

	case BULKACTION_START:
	  if (panel instanceof RatControlPanel) {
	    rcpanel = (RatControlPanel) panel;
	    if (rcpanel.isStoppable())
	      rcpanel.start();
	  }
	  break;

	default:
	  throw new IllegalStateException("Unhandled bulk action: " + action);
      }
    }
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    if (!isHeadless()) {
      return super.doExecute();
    }
    else {
      setUpControlStates();
      return null;
    }
  }

  /**
   * Gets called when the pause state of the flow changes.
   *
   * @param e		the event
   */
  @Override
  public void flowPauseStateChanged(FlowPauseStateEvent e) {
    SwingUtilities.invokeLater(() -> {
      for (AbstractControlPanel panel : m_ControlPanels)
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
    m_ControlPanelsPerRats.clear();
    m_ControlStates.clear();

    if (getRoot() instanceof PauseStateHandler) {
      manager = ((PauseStateHandler) getRoot()).getPauseStateManager();
      if (manager != null)
	manager.removeListener(this);
    }

    super.cleanUp();
  }
}
