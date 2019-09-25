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
 * Rats.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.terminal.menu.remotecommand;

import adams.core.Utils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.flow.GetRatControlStatus;
import adams.scripting.command.flow.ListFlows;
import adams.scripting.command.flow.SendRatControlCommand;
import adams.scripting.command.flow.SendRatControlCommand.Command;
import adams.scripting.responsehandler.AbstractResponseHandler;
import adams.terminal.application.AbstractTerminalApplication;
import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.BorderLayout.Location;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;

import java.util.ArrayList;
import java.util.List;

/**
 * For managing Rats.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Rats
  extends AbstractRemoteFlowCommandAction {

  /**
   * Custom handler for intercepting the responses from the {@link ListFlows}
   * remote command.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class RatStatusResponseHandler
    extends AbstractResponseHandler {

    private static final long serialVersionUID = 6205405220037007365L;

    /** the owner. */
    protected Rats m_Command;

    /**
     * Initializes the handler.
     *
     * @param command	the command this handler belongs to
     */
    public RatStatusResponseHandler(Rats command) {
      super();
      m_Command = command;
    }

    /**
     * Returns a string describing the object.
     *
     * @return 			a description suitable for displaying in the gui
     */
    @Override
    public String globalInfo() {
      return "Retrieves the Rats status.";
    }

    /**
     * Handles successful responses.
     *
     * @param cmd		the command with the response
     */
    @Override
    public void responseSuccessful(RemoteCommand cmd) {
      GetRatControlStatus 	cmdStatus;
      SpreadSheet 		status;

      if (cmd instanceof GetRatControlStatus) {
	cmdStatus = (GetRatControlStatus) cmd;
	if (cmdStatus.getResponsePayloadObjects().length > 0) {
	  status = (SpreadSheet) cmdStatus.getResponsePayloadObjects()[0];
	  m_Command.updateRats(status);
	}
      }
    }

    /**
     * Handles failed responses.
     *
     * @param cmd		the command with the response
     * @param msg		message, can be null
     */
    @Override
    public void responseFailed(RemoteCommand cmd, String msg) {
      if (cmd instanceof GetRatControlStatus) {
	m_Command.updateRats(null);
      }
    }
  }

  /**
   * Panel for a single Rat.
   */
  public static class RatStatusPanel
    extends Panel {

    private static final long serialVersionUID = -6090215566922900761L;

    /** the owner. */
    protected Rats m_Owner;

    /** the flow ID. */
    protected int m_ID;

    /** the name of the rat. */
    protected String m_Name;

    /** whether it is pausable. */
    protected boolean m_Pausable;

    /** whether it is paused. */
    protected boolean m_Paused;

    /** whether it is stoppable. */
    protected boolean m_Stoppable;

    /** whether it is stopped. */
    protected boolean m_Stopped;

    /** the label. */
    protected Label m_LabelName;

    /** the button for pause/resume. */
    protected Button m_ButtonPauseResume;

    /** the button for stop/start. */
    protected Button m_ButtonStopStart;

    /**
     * Initializes the panel.
     *
     * @param owner	the owner
     * @param id	the flow ID
     * @param name	the name of the rat
     * @param pausable	whether pausable at all
     * @param paused	whether currently paused
     * @param stoppable	whether stoppable at all
     * @param stopped	whether currently stopped
     */
    public RatStatusPanel(Rats owner, int id, String name, boolean pausable, boolean paused, boolean stoppable, boolean stopped) {
      super();

      m_Owner     = owner;
      m_ID        = id;
      m_Name      = name;
      m_Pausable  = pausable;
      m_Paused    = paused;
      m_Stoppable = stoppable;
      m_Stopped   = stopped;

      initGUI();
    }

    /**
     * initializes the widgets.
     */
    protected void initGUI() {
      setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

      m_LabelName = new Label(m_Name);
      addComponent(m_LabelName);

      if (m_Pausable) {
	m_ButtonPauseResume = new Button(m_Paused ? "Resume" : "Pause");
	m_ButtonPauseResume.addListener((Button button) -> pauseResume());
	addComponent(m_ButtonPauseResume);
      }

      if (m_Stoppable) {
	m_ButtonStopStart = new Button(m_Stopped ? "Run" : "Stop");
	m_ButtonStopStart.addListener((Button button) -> stopStart());
	addComponent(m_ButtonStopStart);
      }
    }

    /**
     * Returns the label with the name.
     *
     * @return		the label
     */
    public Label getLabelName() {
      return m_LabelName;
    }

    /**
     * Pauses/resumes the rat.
     */
    protected void pauseResume() {
      SendRatControlCommand cmd;

      cmd = new SendRatControlCommand();
      cmd.setID(m_ID);
      cmd.setCommand(m_Paused ? Command.RESUME : Command.PAUSE);
      cmd.setRat(m_Name);
      m_Owner.focusRefreshRatsButton();
      m_Owner.sendCommandWithReponse(cmd);
      m_Owner.refreshRats();
    }

    /**
     * Stops/starts the rat.
     */
    protected void stopStart() {
      SendRatControlCommand	cmd;

      cmd = new SendRatControlCommand();
      cmd.setID(m_ID);
      cmd.setCommand(m_Stopped ? Command.START : Command.STOP);
      cmd.setRat(m_Name);
      m_Owner.focusRefreshRatsButton();
      m_Owner.sendCommandWithReponse(cmd);
      m_Owner.refreshRats();
    }
  }

  /** the panel with rats. */
  protected Panel m_PanelRats;

  /** the button for updating the rats states. */
  protected Button m_ButtonRefreshRats;

  /**
   * Initializes the action with no owner.
   */
  public Rats() {
    super();
  }

  /**
   * Initializes the action.
   *
   * @param owner	the owning application
   */
  public Rats(AbstractTerminalApplication owner) {
    super(owner);
  }

  /**
   * Returns the title of the action.
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Rats";
  }

  /**
   * Creates the panel to display.
   *
   * @return		the panel
   */
  @Override
  protected Panel createPanel() {
    Panel	result;
    Panel 	panelAll;
    Panel 	panelRefresh;
    Panel	panel;
    Label	label;
    
    result = super.createPanel();

    panelAll = new Panel(new BorderLayout());
    m_PanelBottom.removeAllComponents();
    m_PanelBottom.addComponent(panelAll);

    panelRefresh = new Panel(new LinearLayout(Direction.HORIZONTAL));
    panelAll.addComponent(panelRefresh, Location.TOP);
    m_ButtonRefreshRats = new Button("Refresh");
    m_ButtonRefreshRats.addListener((Button button) -> refreshRats());
    label = new Label("Refresh Rats status");
    panelRefresh.addComponent(label);
    panelRefresh.addComponent(m_ButtonRefreshRats);

    m_PanelRats = new Panel(new BorderLayout());
    panel = new Panel(new BorderLayout());
    panel.addComponent(m_PanelRats, Location.TOP);
    panelAll.addComponent(panel, Location.CENTER);
    
    return result;
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    super.updateButtons();
    m_ButtonRefreshRats.setEnabled(getSelectedRows().length == 1);
  }

  /**
   * Gives the {@link #m_ButtonRefreshRats} button the focus.
   */
  public void focusRefreshRatsButton() {
    m_ButtonRefreshRats.takeFocus();
  }

  /**
   * Sends command to retrieve rats status.
   */
  public void refreshRats() {
    int[]			ids;
    GetRatControlStatus		cmd;

    ids = getSelectedFlowIDs();
    if (ids.length != 1)
      return;

    cmd = new GetRatControlStatus();
    cmd.setID(ids[0]);
    sendCommandWithReponse(cmd, new RatStatusResponseHandler(this));
  }

  /**
   * Clears the rat status panel.
   */
  protected void clearStatus() {
    Label	label;

    label = new Label("Nothing to display");
    m_PanelRats.addComponent(label, Location.CENTER);
  }

  /**
   * Updates the rat status panel.
   *
   * @param status	the current status
   */
  protected void updateRats(SpreadSheet status) {
    int				i;
    Row 			row;
    RatStatusPanel		panel;
    List<RatStatusPanel> 	panels;
    int				max;

    clearStatus();

    if (status == null)
      return;

    m_PanelRats.removeAllComponents();
    m_PanelRats.setLayoutManager(new GridLayout(1));
    panels = new ArrayList<>();
    for (i = 0; i < status.getRowCount(); i++) {
      row = status.getRow(i);
      panel = new RatStatusPanel(
	this,
	row.getCell(GetRatControlStatus.COL_ID).toLong().intValue(),
	row.getCell(GetRatControlStatus.COL_RAT).toString(),
	row.getCell(GetRatControlStatus.COL_PAUSABLE).toBoolean(),
	row.getCell(GetRatControlStatus.COL_PAUSED).toBoolean(),
	row.getCell(GetRatControlStatus.COL_STOPPABLE).toBoolean(),
	row.getCell(GetRatControlStatus.COL_STOPPED).toBoolean()
      );
      m_PanelRats.addComponent(panel);
      panels.add(panel);
    }
    // adjust label widths
    max = 0;
    for (i = 0; i < panels.size(); i++)
      max = Math.max(max, panels.get(i).getLabelName().getText().length());
    for (i = 0; i < panels.size(); i++)
      panels.get(i).getLabelName().setText(Utils.padRight(panels.get(i).getLabelName().getText(), ' ', max));

    m_PanelRats.invalidate();
  }
}
