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
 * GetRatControlStatus.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.flow;

import adams.core.Pausable;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.RunningFlowsRegistry;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.Rat;
import adams.flow.standalone.RatControl;
import adams.flow.standalone.RatControl.AbstractControlPanel;
import adams.flow.standalone.RatControl.RatControlPanel;
import adams.scripting.command.AbstractCommandWithResponse;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * Retrieves status of Rat actors managed by RatControl actor from a
 * running/registered flow using its ID.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GetRatControlStatus
  extends AbstractCommandWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the ID of the flow to retrieve. */
  protected Integer m_ID;

  /** the flow. */
  protected SpreadSheet m_Status;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Retrieves the status of Rat actors controlled by RatControl actors from "
	+ "a running/registered flow using its ID.\n"
	+ "Uses a spreadsheet for the information with the following columns:\n"
	+ "- flowid -- the ID of the flow\n"
	+ "- rat -- the full name of the Rat actor\n"
	+ "- pausable -- whether the Rat can be paused\n"
	+ "- ispaused -- whether the Rat is currently paused\n"
	+ "- stoppable -- whether the Rat can be stopped\n"
	+ "- isstopped -- whether the Rat is currently stopped";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "id", "ID",
      1, -1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Status = null;
  }

  /**
   * Sets the ID of the flow to get.
   *
   * @param value	the ID, -1 if to retrieve the only one
   */
  public void setID(int value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the ID of the flow to get.
   *
   * @return		the ID, -1 if to retrieve the only one
   */
  public int getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String IDTipText() {
    return "The ID of the flow to get; -1 if to retrieve the only one.";
  }

  /**
   * Ignored.
   *
   * @param value	the payload
   */
  @Override
  public void setRequestPayload(byte[] value) {
  }

  /**
   * Always zero-length array.
   *
   * @return		the payload
   */
  @Override
  public byte[] getRequestPayload() {
    return new byte[0];
  }

  /**
   * Returns the objects that represent the request payload.
   *
   * @return		the objects
   */
  public Object[] getRequestPayloadObjects() {
    return new Object[0];
  }

  /**
   * Sets the payload for the response.
   *
   * @param value	the payload
   */
  @Override
  public void setResponsePayload(byte[] value) {
    SpreadSheet			status;
    CsvSpreadSheetReader 	csv;

    if (value.length == 0) {
      m_Status = null;
      return;
    }

    csv = new CsvSpreadSheetReader();
    status = csv.read(new StringReader(new String(value)));
    if (status == null)
      getLogger().severe("Failed to read status:\n" + new String(value));

    m_Status = status;
  }

  /**
   * Returns the payload of the response, if any.
   *
   * @return		the payload
   */
  @Override
  public byte[] getResponsePayload() {
    StringWriter 		swriter;
    CsvSpreadSheetWriter	csv;

    if (m_Status == null)
      return new byte[0];

    swriter = new StringWriter();
    csv     = new CsvSpreadSheetWriter();
    csv.write(m_Status, swriter);

    return swriter.toString().getBytes();
  }

  /**
   * Hook method for preparing the response payload,
   */
  @Override
  protected void prepareResponsePayload() {
    Actor 	flow;
    SpreadSheet	sheet;
    Row		row;
    RatControl	rc;
    Actor	actor;

    super.prepareResponsePayload();

    flow = null;
    if (m_ID == -1) {
      if (RunningFlowsRegistry.getSingleton().size() == 1)
        flow = RunningFlowsRegistry.getSingleton().flows()[0];
    }
    else {
      flow = RunningFlowsRegistry.getSingleton().getFlow(m_ID);
    }

    // get RatControl actors
    if (flow != null) {
      sheet = new DefaultSpreadSheet();
      row   = sheet.getHeaderRow();
      row.addCell("flowid").setContent("flowid");
      row.addCell("rat").setContent("rat");
      row.addCell("pausable").setContent("pausable");
      row.addCell("ispaused").setContent("ispaused");
      row.addCell("stoppable").setContent("stoppable");
      row.addCell("isstopped").setContent("isstopped");
      for (Actor a : ActorUtils.enumerate(flow, new Class[]{RatControl.class})) {
	rc = (RatControl) a;
	for (AbstractControlPanel panel: rc.getControlPanels()) {
	  actor = panel.getActor();
	  row = sheet.addRow();
	  row.addCell("flowid").setContent(m_ID);
	  row.addCell("rat").setContent(panel.getActor().getFullName());
	  row.addCell("pausable").setContent(panel.isPausable());
	  row.addCell("ispaused").setContent(((Pausable) actor).isPaused());
	  if ((panel instanceof RatControlPanel)) {
	    row.addCell("stoppable").setContent(((RatControlPanel) panel).isStoppable());
	    row.addCell("isstopped").setContent(!((Rat) actor).isRunnableActive());
	  }
	  else {
	    row.addCell("stoppable").setContent(false);
	    row.addCell("isstopped").setContent(false);
	  }
	}
      }
      m_Status = sheet;
    }
  }

  /**
   * Returns the objects that represent the response payload.
   *
   * @return		the objects
   */
  public Object[] getResponsePayloadObjects() {
    return new Object[]{m_Status};
  }
}
