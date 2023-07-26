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
 * Copyright (C) 2014-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.Utils;
import adams.core.io.FileUtils;
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
import adams.flow.core.displaytype.AbstractDisplayType;
import adams.flow.core.displaytype.DisplayInEditor;
import adams.flow.standalone.ratcontrol.AbstractControlPanel;
import adams.flow.standalone.ratcontrol.AbstractControlState;
import adams.flow.standalone.ratcontrol.RatControlPanel;
import adams.flow.standalone.ratcontrol.RatControlState;
import adams.flow.standalone.ratcontrol.RatsControlPanel;
import adams.flow.standalone.ratcontrol.RatsControlState;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.ConsolePanel;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.ConsolePanelEvent;
import adams.gui.event.ConsolePanelListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-display-type &lt;adams.flow.core.displaytype.AbstractDisplayType&gt; (property: displayType)
 * &nbsp;&nbsp;&nbsp;Determines how to show the display, eg as standalone frame (default) or
 * &nbsp;&nbsp;&nbsp;in the Flow editor window.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.displaytype.DisplayInEditor
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
 * <pre>-bulk-actions &lt;boolean&gt; (property: bulkActions)
 * &nbsp;&nbsp;&nbsp;If enabled, bulk actions can be performed on the checked rats.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-console-line-limit &lt;int&gt; (property: consoleLineLimit)
 * &nbsp;&nbsp;&nbsp;The line limit for the console output, &lt;=0 for no limit.
 * &nbsp;&nbsp;&nbsp;default: 10000
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RatControl
  extends AbstractDisplay
  implements FlowPauseStateListener, RatStateListener, ConsolePanelListener {

  /** for serialization. */
  private static final long serialVersionUID = 2777897240842864503L;

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

  /** the textarea with console output. */
  protected BaseTextArea m_TextAreaConsole;

  /** the line limit for the console output. */
  protected int m_ConsoleLineLimit;

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

    m_OptionManager.add(
      "console-line-limit", "consoleLineLimit",
      10000, -1, null);
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
   * Sets the line limit for the console output.
   *
   * @param value	the limit, <= 0 for unlimited
   */
  public void setConsoleLineLimit(int value) {
    m_ConsoleLineLimit = value;
    reset();
  }

  /**
   * Returns the line limit for the console output.
   *
   * @return		the limit, <= 0 for unlimited
   */
  public int getConsoleLineLimit() {
    return m_ConsoleLineLimit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String consoleLineLimitTipText() {
    return "The line limit for the console output, <=0 for no limit.";
  }

  /**
   * Returns the default value for displaying the panel in the editor
   * rather than in a separate frame.
   * 
   * @return		the default
   */
  @Override
  protected AbstractDisplayType getDefaultDisplayType() {
    return new DisplayInEditor();
  }

  /**
   * Returns whether to de-register in {@link #wrapUp()} or wait till 
   * {@link #cleanUpGUI()}.
   * 
   * @return		true if to deregister already in {@link #wrapUp()}
   */
  @Override
  public boolean deregisterInWrapUp() {
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
    BaseButton			buttonConsole;
    final BaseComboBox<String> 	comboBulkActions;
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
      comboBulkActions = new BaseComboBox<>(new DefaultComboBoxModel<>(new String[]{
        BULKACTION_NONE,
	BULKACTION_PAUSE,
	BULKACTION_RESUME,
	BULKACTION_START,
	BULKACTION_STOP,
      }));
      panel.add(comboBulkActions);
      buttonApply = new BaseButton(ImageManager.getIcon("validate.png"));
      buttonApply.setToolTipText("Apply bulk action");
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
    panelBottom.add(panel, BorderLayout.EAST);

    buttonStop = new BaseButton(ImageManager.getIcon("stop_blue.gif"));
    buttonStop.setToolTipText("Stop flow");
    buttonStop.addActionListener((ActionEvent e) -> getRoot().stopExecution());
    panel.add(buttonStop);

    buttonConsole = new BaseButton(ImageManager.getIcon("log.gif"));
    buttonConsole.setToolTipText("Show console output");
    buttonConsole.addActionListener((ActionEvent e) -> showConsole());
    panel.add(buttonConsole);

    m_TextAreaConsole = new BaseTextArea();
    m_TextAreaConsole.setFont(Fonts.getMonospacedFont());
    m_TextAreaConsole.setLineWrap(false);

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
      if (!panel.isChecked())
        continue;

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
        m_DisplayType.show(RatControl.this);
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

    if (result == null) {
      if (!isHeadless()) {
        ConsolePanel.getSingleton().removeListener(this);
        ConsolePanel.getSingleton().addListener(this);
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
   * Gets called when the {@link ConsolePanel} receives a message.
   *
   * @param e		the generated event
   */
  public void consolePanelMessageReceived(ConsolePanelEvent e) {
    String	line;

    if (m_TextAreaConsole != null) {
      line = Utils.indent(e.getMessage(), "[" + e.getLevel() + "] ");
      m_TextAreaConsole.append(line, m_ConsoleLineLimit);
    }
  }

  /**
   * Shows the text area with the console output.
   */
  protected void showConsole() {
    ApprovalDialog	dialog;
    JMenuBar		menubar;
    JMenu 		menu;
    JMenuItem		menuitem;

    menubar = new JMenuBar();
    menu = new JMenu("File");
    menu.setMnemonic('F');
    menubar.add(menu);

    menuitem = new JMenuItem("Save as...", ImageManager.getIcon("save.gif"));
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
    menuitem.addActionListener((ActionEvent e) -> {
      TextFileChooser fileChooser = new TextFileChooser();
      int retVal = fileChooser.showSaveDialog(getParentComponent());
      if (retVal != TextFileChooser.APPROVE_OPTION)
        return;
      String outputFile = fileChooser.getSelectedFile().getAbsolutePath();
      String msg = FileUtils.writeToFileMsg(outputFile, m_TextAreaConsole.getText(), false, null);
      if (msg != null)
        GUIHelper.showErrorMessage(getParentComponent(), "Failed to write to:\n" + outputFile + "\n" + msg);
    });
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Close", ImageManager.getIcon("exit.png"));
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
    menuitem.addActionListener((ActionEvent e) -> GUIHelper.closeParent(m_TextAreaConsole));
    menu.add(menuitem);

    dialog = ApprovalDialog.getInformationDialog(null, false);
    dialog.setJMenuBar(menubar);
    dialog.setTitle("Console: " + getName());
    dialog.getContentPane().add(new BaseScrollPane(m_TextAreaConsole), BorderLayout.CENTER);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setSize(GUIHelper.getDefaultDialogDimension());
    dialog.setLocationRelativeTo(getParentComponent());
    dialog.setVisible(true);
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

    if (m_TextAreaConsole != null) {
      ConsolePanel.getSingleton().removeListener(this);
      m_TextAreaConsole = null;
    }

    super.cleanUp();
  }
}
