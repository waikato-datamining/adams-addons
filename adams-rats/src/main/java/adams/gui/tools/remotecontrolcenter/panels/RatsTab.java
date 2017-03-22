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
 * RatsTab.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.remotecontrolcenter.panels;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.flow.GetRatControlStatus;
import adams.scripting.command.flow.ListFlows;
import adams.scripting.command.flow.SendRatControlCommand;
import adams.scripting.command.flow.SendRatControlCommand.Command;
import adams.scripting.responsehandler.AbstractResponseHandler;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab for managing rats.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RatsTab
  extends AbstractRemoteFlowTab {

  private static final long serialVersionUID = 1059480818962711024L;

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
    protected RatsTab m_Tab;

    /**
     * Initializes the handler.
     *
     * @param tab	the tab this handler belongs to
     */
    public RatStatusResponseHandler(RatsTab tab) {
      super();
      m_Tab = tab;
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
	  SwingUtilities.invokeLater(() -> m_Tab.updateRats(status));
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
	m_Tab.updateRats(null);
      }
    }
  }

  /**
   * Panel for a single Rat.
   */
  public static class RatStatusPanel
    extends BasePanel {

    private static final long serialVersionUID = -6090215566922900761L;

    /** the owner. */
    protected RatsTab m_Owner;

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
    protected JLabel m_LabelName;

    /** the button for pause/resume. */
    protected JButton m_ButtonPauseResume;

    /** the button for stop/start. */
    protected JButton m_ButtonStopStart;

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
    public RatStatusPanel(RatsTab owner, int id, String name, boolean pausable, boolean paused, boolean stoppable, boolean stopped) {
      super();

      m_Owner     = owner;
      m_ID        = id;
      m_Name      = name;
      m_Pausable  = pausable;
      m_Paused    = paused;
      m_Stoppable = stoppable;
      m_Stopped   = stopped;

      initGUI();
      finishInit();
    }

    /**
     * initializes the widgets.
     */
    @Override
    protected void initGUI() {
      if (m_Owner == null)
	return;

      super.initGUI();

      setLayout(new FlowLayout(FlowLayout.LEFT));

      m_LabelName = new JLabel(m_Name);
      add(m_LabelName);

      if (m_Pausable) {
	m_ButtonPauseResume = new JButton(m_Paused ? GUIHelper.getIcon("resume.gif") : GUIHelper.getIcon("pause.gif"));
	m_ButtonPauseResume.addActionListener((ActionEvent e) -> pauseResume());
	add(m_ButtonPauseResume);
      }

      if (m_Stoppable) {
	m_ButtonStopStart = new JButton(m_Stopped ? GUIHelper.getIcon("run.gif") : GUIHelper.getIcon("stop_blue.gif"));
	m_ButtonStopStart.addActionListener((ActionEvent e) -> stopStart());
	add(m_ButtonStopStart);
      }
    }

    /**
     * Finalizes the initialization.
     */
    @Override
    protected void finishInit() {
      if (m_Owner == null)
	return;

      super.finishInit();
    }

    /**
     * Returns the label with the name.
     *
     * @return		the label
     */
    public JLabel getLabelName() {
      return m_LabelName;
    }

    /**
     * Pauses/resumes the rat.
     */
    protected void pauseResume() {
      SendRatControlCommand	cmd;

      cmd = new SendRatControlCommand();
      cmd.setID(m_ID);
      cmd.setCommand(m_Paused ? Command.RESUME : Command.PAUSE);
      cmd.setRat(m_Name);
      m_Owner.sendCommand(cmd, null);
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
      m_Owner.sendCommand(cmd, null);
      m_Owner.refreshRats();
    }
  }

  /** the panel with rats. */
  protected JPanel m_PanelRats;

  /** the button for updating the rats states. */
  protected JButton m_ButtonRefreshRats;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panelAll;
    JPanel 	panelRefresh;
    JPanel	panel;
    JLabel	label;

    super.initGUI();

    panelAll = new JPanel(new BorderLayout());
    m_SplitPane.setBottomComponent(panelAll);

    panelRefresh = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelAll.add(panelRefresh, BorderLayout.NORTH);
    m_ButtonRefreshRats = new JButton(GUIHelper.getIcon("refresh.gif"));
    m_ButtonRefreshRats.addActionListener((ActionEvent e) -> refreshRats());
    label = new JLabel("Refresh Rats status");
    label.setDisplayedMnemonic('R');
    label.setLabelFor(m_ButtonRefreshRats);
    panelRefresh.add(label);
    panelRefresh.add(m_ButtonRefreshRats);

    m_PanelRats = new JPanel(new BorderLayout());
    panel = new JPanel(new BorderLayout());
    panel.add(m_PanelRats, BorderLayout.NORTH);
    panelAll.add(new BaseScrollPane(panel), BorderLayout.CENTER);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    clearStatus();
  }

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Rats";
  }

  /**
   * Returns the name of icon to use for the tab.
   *
   * @return		the icon
   */
  @Override
  public String getTabIcon() {
    return "rats.png";
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    super.updateButtons();
    m_ButtonRefreshRats.setEnabled(m_TableFlows.getSelectedRowCount() == 1);
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
    sendCommand(cmd, new RatStatusResponseHandler(this));
  }

  /**
   * Clears the rat status panel.
   */
  protected void clearStatus() {
    JLabel	label;

    label = new JLabel("Nothing to display", SwingConstants.CENTER);
    m_PanelRats.add(label, BorderLayout.CENTER);
  }

  /**
   * Updates the rat status panel.
   *
   * @param status	the current status
   */
  protected void updateRats(SpreadSheet status) {
    int				i;
    Row				row;
    RatStatusPanel		panel;
    List<RatStatusPanel> 	panels;
    int				max;

    clearStatus();

    if (status == null)
      return;

    m_PanelRats.removeAll();
    m_PanelRats.setLayout(new GridLayout(status.getRowCount(), 1));
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
      m_PanelRats.add(panel);
      panels.add(panel);
    }
    // adjust label widths
    max = 0;
    for (i = 0; i < panels.size(); i++)
      max = Math.max(max, (int) panels.get(i).getLabelName().getPreferredSize().getWidth());
    for (i = 0; i < panels.size(); i++)
      panels.get(i).getLabelName().setPreferredSize(new Dimension(max, panels.get(i).getLabelName().getPreferredSize().height));

    m_PanelRats.invalidate();
    m_PanelRats.revalidate();
  }
}
