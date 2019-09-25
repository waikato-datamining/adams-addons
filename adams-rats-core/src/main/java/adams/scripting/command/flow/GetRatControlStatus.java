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
 * GetRatControlStatus.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package adams.scripting.command.flow;

import adams.core.Pausable;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.Rat;
import adams.flow.standalone.RatControl;
import adams.flow.standalone.ratcontrol.AbstractControlState;
import adams.flow.standalone.ratcontrol.RatControlState;
import adams.scripting.command.AbstractRemoteCommandOnFlowWithResponse;

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
  extends AbstractRemoteCommandOnFlowWithResponse {

  private static final long serialVersionUID = -3350680106789169314L;

  /** the column of the flow ID. */
  public final static int COL_ID = 0;

  /** the column of the Rat name. */
  public final static int COL_RAT = 1;

  /** the column of the pausable flag. */
  public final static int COL_PAUSABLE = 2;

  /** the column of the paused flag. */
  public final static int COL_PAUSED = 3;

  /** the column of the stoppable flag. */
  public final static int COL_STOPPABLE = 4;

  /** the column of the stopped flag. */
  public final static int COL_STOPPED = 5;

  /** the column of the interactive flag. */
  public final static int COL_INTERACTIVE = 6;

  /** the status. */
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
	+ "- isstopped -- whether the Rat is currently stopped\n"
	+ "- isinteractive -- whether the Rat contains at least one interactive actor";
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
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String IDTipText() {
    return "The ID of the flow to query; -1 if to use the only one.";
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

    flow = retrieveFlow(false);

    // get RatControl actors
    if (flow != null) {
      sheet = new DefaultSpreadSheet();
      row   = sheet.getHeaderRow();
      row.addCell("" + COL_ID).setContent("flowid");
      row.addCell("" + COL_RAT).setContent("rat");
      row.addCell("" + COL_PAUSABLE).setContent("pausable");
      row.addCell("" + COL_PAUSED).setContent("ispaused");
      row.addCell("" + COL_STOPPABLE).setContent("stoppable");
      row.addCell("" + COL_STOPPED).setContent("isstopped");
      row.addCell("" + COL_INTERACTIVE).setContent("isinteractive");
      for (Actor a : ActorUtils.enumerate(flow, new Class[]{RatControl.class})) {
	rc = (RatControl) a;
	for (AbstractControlState state: rc.getControlStates()) {
	  actor = state.getActor();
	  row = sheet.addRow();
	  row.addCell(COL_ID).setContent(m_ID);
	  row.addCell(COL_RAT).setContent(state.getActor().getFullName());
	  row.addCell(COL_PAUSABLE).setContent(state.isPausable());
	  row.addCell(COL_PAUSED).setContent(((Pausable) actor).isPaused());
	  if ((state instanceof RatControlState)) {
	    row.addCell(COL_STOPPABLE).setContent(((RatControlState) state).isStoppable());
	    row.addCell(COL_STOPPED).setContent(!((Rat) actor).isRunnableActive());
	  }
	  else {
	    row.addCell(COL_STOPPABLE).setContent(false);
	    row.addCell(COL_STOPPED).setContent(false);
	  }
	  row.addCell(COL_INTERACTIVE).setContent(ActorUtils.isInteractive(actor));
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
