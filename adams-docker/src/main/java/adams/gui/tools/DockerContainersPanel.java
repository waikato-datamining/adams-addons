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
 * DockerContainersPanel.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import adams.core.Range;
import adams.data.conversion.StringToSpreadSheet;
import adams.data.io.input.AutoWidthTabularSpreadSheetReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.docker.DockerContainer;
import adams.docker.simpledocker.ListContainers;
import adams.docker.simpledocker.RemoveContainers;
import adams.docker.simpledocker.StopContainers;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.source.StringConstants;
import adams.flow.standalone.SimpleDockerConnection;
import adams.flow.transformer.Convert;
import adams.flow.transformer.SetStorageValue;
import adams.flow.transformer.StringJoin;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanelWithButtons;
import adams.gui.core.SearchParameters;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.JLabel;
import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Management panel for Docker containers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DockerContainersPanel
  extends AbstractManagementPanel<DockerContainer> {

  public static class TableModel
    extends AbstractManagementTableModel<DockerContainer> {

    private static final long serialVersionUID = 5144740737933551956L;

    /**
     * default constructor.
     */
    public TableModel() {
      super();
    }

    /**
     * the constructor.
     *
     * @param values	the containers to display
     */
    public TableModel(DockerContainer[] values) {
      super(values);
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return the number of columns
     */
    @Override
    public int getColumnCount() {
      return 8;
    }

    /**
     * Returns the name of the column.
     *
     * @param column the column to get the name for
     * @return the name of the column
     */
    @Override
    public String getColumnName(int column) {
      switch (column) {
	case 0:
	  return "Index";
	case 1:
	  return "Container ID";
	case 2:
	  return "Image";
	case 3:
	  return "Command";
	case 4:
	  return "Created";
	case 5:
	  return "Status";
	case 6:
	  return "Ports";
	case 7:
	  return "Names";
	default:
	  throw new IllegalStateException("Unsupported column index: " + column);
      }
    }

    /**
     * Returns the class type of the column.
     *
     * @param columnIndex the column to get the class for
     * @return the class for the column
     */
    @Override
    public Class getColumnClass(int columnIndex) {
      if (columnIndex == 0)
	return Integer.class;
      else
	return String.class;
    }

    /**
     * Returns the Object at the given position.
     *
     * @param row    the row
     * @param column the column
     * @return the Object
     */
    @Override
    public Object getValueAt(int row, int column) {
      DockerContainer	container;

      container = m_Values.get(row);

      switch (column) {
	case 0:
	  return (row+1);
	case 1:
	  return container.getContainerID();
	case 2:
	  return container.getImage();
	case 3:
	  return container.getCommand();
	case 4:
	  return container.getCreated();
	case 5:
	  return container.getStatus();
	case 6:
	  return container.getPorts();
	case 7:
	  return container.getNames();
	default:
	  throw new IllegalStateException("Unsupported column index: " + column);
      }
    }

    /**
     * Tests whether the search matches the specified row.
     *
     * @param params the search parameters
     * @param row    the row of the underlying, unsorted model
     * @return true if the search matches this row
     */
    @Override
    public boolean isSearchMatch(SearchParameters params, int row) {
      // container ID
      if (params.matches(m_Values.get(row).getContainerID()))
	return true;
      // image
      if (params.matches(m_Values.get(row).getImage()))
	return true;
      // command
      if (params.matches(m_Values.get(row).getCommand()))
	return true;
      // created
      if (params.matches(m_Values.get(row).getCreated()))
	return true;
      // status
      if (params.matches(m_Values.get(row).getStatus()))
	return true;
      // ports
      if (params.matches(m_Values.get(row).getPorts()))
	return true;
      // names
      if (params.matches(m_Values.get(row).getNames()))
	return true;

      return false;
    }
  }

  /** the container ID field. */
  protected BaseTextField m_TextContainerID;

  /** the image field. */
  protected BaseTextField m_TextImage;

  /** the command field. */
  protected BaseTextField m_TextCommand;

  /** the created field. */
  protected BaseTextField m_TextCreated;

  /** the status field. */
  protected BaseTextField m_TextStatus;

  /** the ports field. */
  protected BaseTextField m_TextPorts;

  /** the names field. */
  protected BaseTextField m_TextNames;

  /** the button for stopping selected containers. */
  protected BaseButton m_ButtonStop;

  /** the button for deleting selected containers. */
  protected BaseButton m_ButtonDelete;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_ButtonStop = new BaseButton("Stop");
    m_ButtonStop.addActionListener((ActionEvent e) -> stopContainers());
    m_TableValues.addToButtonsPanel(m_ButtonStop);
    
    m_TableValues.addToButtonsPanel(new JLabel());
    
    m_ButtonDelete = new BaseButton("Delete");
    m_ButtonDelete.addActionListener((ActionEvent e) -> deleteContainers());
    m_TableValues.addToButtonsPanel(m_ButtonDelete);
  }

  /**
   * For adding all the fields.
   *
   * @param panel the panel to add the fields to
   */
  @Override
  protected void addFields(ParameterPanelWithButtons panel) {
    m_TextContainerID = new BaseTextField();
    panel.addParameter("Container ID", m_TextContainerID);

    m_TextImage = new BaseTextField();
    panel.addParameter("Image", m_TextImage);

    m_TextCommand = new BaseTextField();
    panel.addParameter("Command", m_TextCommand);

    m_TextCreated = new BaseTextField();
    panel.addParameter("Created", m_TextCreated);

    m_TextStatus = new BaseTextField();
    panel.addParameter("Status", m_TextStatus);

    m_TextPorts = new BaseTextField();
    panel.addParameter("Ports", m_TextPorts);

    m_TextNames = new BaseTextField();
    panel.addParameter("Names", m_TextNames);
  }

  /**
   * Returns an instance of a new table model.
   *
   * @return the table model
   */
  @Override
  protected TableModel newTableModel() {
    return new TableModel();
  }

  /**
   * Returns the class that is being managed.
   *
   * @return the class being managed
   */
  @Override
  protected Class getManagedClass() {
    return DockerContainer.class;
  }

  /**
   * Resets the input fields.
   */
  @Override
  protected void clear() {
    m_TextContainerID.setText("");
    m_TextImage.setText("");
    m_TextCommand.setText("");
    m_TextCreated.setText("");
    m_TextStatus.setText("");
    m_TextPorts.setText("");
    m_TextNames.setText("");
  }

  /**
   * Turns the fields into an object.
   *
   * @return the generated object
   */
  @Override
  protected DockerContainer fieldsToObject() {
    return new DockerContainer(
      m_TextContainerID.getText(),
      m_TextImage.getText(),
      m_TextCommand.getText(),
      m_TextCreated.getText(),
      m_TextStatus.getText(),
      m_TextPorts.getText(),
      m_TextNames.getText()
    );
  }

  /**
   * Updates the field with the specified object.
   *
   * @param value the object to display
   */
  @Override
  protected void objectToFields(DockerContainer value) {
    m_TextContainerID.setText(value.getContainerID());
    m_TextImage.setText(value.getImage());
    m_TextCommand.setText(value.getCommand());
    m_TextCreated.setText(value.getCreated());
    m_TextStatus.setText(value.getStatus());
    m_TextPorts.setText(value.getPorts());
    m_TextNames.setText(value.getNames());
  }

  /**
   * Updates the enabled state of the widgets.
   */
  @Override
  protected void updateButtons() {
    super.updateButtons();
    m_ButtonDelete.setEnabled(m_TableValues.getSelectedRowCount() > 0);
  }

  /**
   * Creates the flow for retrieving the docker image data.
   *
   * @return		the flow
   */
  protected Flow getListFlow() {
    Flow 	result;

    result = new Flow();
    result.add(new SimpleDockerConnection());

    ListContainers list = new ListContainers();
    list.setAll(true);
    list.setShowOnlyIDs(false);
    adams.flow.source.SimpleDockerCommand cmd = new adams.flow.source.SimpleDockerCommand();
    cmd.setCommand(list);
    result.add(cmd);

    StringJoin join = new StringJoin();
    join.setGlue("\\n");
    result.add(join);

    StringToSpreadSheet s2s = new StringToSpreadSheet();
    AutoWidthTabularSpreadSheetReader reader = new AutoWidthTabularSpreadSheetReader();
    reader.setTextColumns(new Range(Range.ALL));
    reader.setMinSpaces(2);
    s2s.setReader(reader);
    result.add(new Convert(s2s));

    SetStorageValue ssv = new SetStorageValue("sheet");
    result.add(ssv);

    return result;
  }

  /**
   * Creates the flow for stopping docker containers.
   *
   * @param ids 	the IDs of the containers to stop
   * @return		the flow
   */
  protected Flow getStopFlow(String[] ids) {
    Flow 	result;

    result = new Flow();
    result.add(new SimpleDockerConnection());

    result.add(new StringConstants(ids));

    adams.flow.transformer.SimpleDockerCommand cmd = new adams.flow.transformer.SimpleDockerCommand();
    StopContainers stop = new StopContainers();
    stop.setBlocking(true);
    cmd.setCommand(stop);
    result.add(cmd);

    result.add(new SetStorageValue("output"));

    return result;
  }

  /**
   * Creates the flow for deleting docker containers.
   *
   * @param ids 	the IDs of the containers to remove
   * @return		the flow
   */
  protected Flow getDeleteFlow(String[] ids) {
    Flow 	result;

    result = new Flow();
    result.add(new SimpleDockerConnection());

    result.add(new StringConstants(ids));

    adams.flow.transformer.SimpleDockerCommand cmd = new adams.flow.transformer.SimpleDockerCommand();
    RemoveContainers remove = new RemoveContainers();
    remove.setForce(true);
    remove.setBlocking(true);
    cmd.setCommand(remove);
    result.add(cmd);

    result.add(new SetStorageValue("output"));

    return result;
  }

  /**
   * Frees up memory.
   *
   * @param flow	the flow to clean up
   */
  protected void cleanUp(Flow flow) {
    flow.wrapUp();
    flow.cleanUp();
    flow.destroy();
  }

  /**
   * Loads all the objects.
   *
   * @return all available Objects
   */
  @Override
  protected List<DockerContainer> loadAll() {
    List<DockerContainer>	result;
    Flow		flow;
    StorageName		sname;
    String		msg;
    SpreadSheet		sheet;

    result = new ArrayList<>();

    flow  = getListFlow();
    sname = new StorageName("sheet");
    msg = flow.setUp();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, "Failed to list docker containers (flow setup):\n" + msg);
      cleanUp(flow);
      return result;
    }

    msg = flow.execute();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, "Failed to list docker containers (flow execution):\n" + msg);
      cleanUp(flow);
      return result;
    }

    if (!flow.getStorage().has(sname)) {
      GUIHelper.showErrorMessage(this, "No spreadsheet with docker containers generated - parsing of docker output failed?");
      cleanUp(flow);
      return result;
    }

    sheet = (SpreadSheet) flow.getStorage().get(sname);
    for (Row row: sheet.rows()) {
      result.add(new DockerContainer(
	row.getCell(0).getContent(),
	row.getCell(1).getContent(),
	row.getCell(2).getContent(),
	row.getCell(3).getContent(),
	row.getCell(4).getContent(),
	row.getCell(5).getContent(),
	row.getCell(6).getContent()
      ));
    }

    cleanUp(flow);

    return result;
  }

  /**
   * Stops the selected containers.
   */
  protected void stopContainers() {
    int				retVal;
    Flow			flow;
    List<DockerContainer>	selected;
    String[]			ids;
    int				i;
    String			msg;
    StorageName			sname;
    String			output;

    selected = getSelectedValues();

    retVal = GUIHelper.showConfirmMessage(this, "Do you want to stop " + selected.size() + " docker container(s)?");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    ids  = new String[selected.size()];
    for (i = 0; i < selected.size(); i++)
      ids[i] = selected.get(i).getContainerID();

    flow  = getStopFlow(ids);
    sname = new StorageName("output");
    msg = flow.setUp();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, "Failed to stop docker containers (flow setup):\n" + msg);
      cleanUp(flow);
      return;
    }

    msg = flow.execute();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, "Failed to stop docker containers (flow execution):\n" + msg);
      cleanUp(flow);
      return;
    }

    if (flow.getStorage().has(sname)) {
      output = "" + flow.getStorage().get(sname);
      output = output.trim();
      if (output.length() > 0)
	GUIHelper.showInformationMessage(this, "Output of stopping docker containers:\n" + output);
    }

    cleanUp(flow);

    refresh();
  }

  /**
   * Deletes the containers with the specified IDs.
   *
   * @param ids		the IDs to delete
   */
  protected void deleteContainers(String[] ids) {
    SwingWorker worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
        Flow flow  = getDeleteFlow(ids);
        StorageName sname = new StorageName("output");
        String msg = flow.setUp();
        if (msg != null) {
          GUIHelper.showErrorMessage(DockerContainersPanel.this, "Failed to delete docker containers (flow setup):\n" + msg);
          cleanUp(flow);
          return null;
        }

        msg = flow.execute();
        if (msg != null) {
          GUIHelper.showErrorMessage(DockerContainersPanel.this, "Failed to delete docker containers (flow execution):\n" + msg);
          cleanUp(flow);
          return null;
        }

        if (flow.getStorage().has(sname)) {
          String output = "" + flow.getStorage().get(sname);
          output = output.trim();
          if (output.length() > 0)
            GUIHelper.showInformationMessage(DockerContainersPanel.this, "Output of deleting docker containers:\n" + output);
        }

        cleanUp(flow);
        return null;
      }

      @Override
      protected void done() {
        super.done();
        refresh();
      }
    };
    worker.execute();
  }

  /**
   * Deletes the selected containers.
   */
  protected void deleteContainers() {
    int				retVal;
    List<DockerContainer>	selected;
    String[]			ids;
    int				i;

    selected = getSelectedValues();

    retVal = GUIHelper.showConfirmMessage(this, "Do you want to delete " + selected.size() + " docker container(s)?");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    ids  = new String[selected.size()];
    for (i = 0; i < selected.size(); i++)
      ids[i] = selected.get(i).getContainerID();

    deleteContainers(ids);
  }

  /**
   * Checks whether the object already exists.
   *
   * @param value the value to look for
   * @return true if already available
   */
  @Override
  protected boolean exists(DockerContainer value) {
    return false;
  }

  /**
   * Stores the object.
   *
   * @param value the value to store
   * @return true if successfully stored
   */
  @Override
  protected boolean store(DockerContainer value) {
    return false;
  }

  /**
   * Removes the object.
   *
   * @param value the value to remove
   * @return true if successfully removed
   */
  @Override
  protected boolean remove(DockerContainer value) {
    return false;
  }

  /**
   * Returns whether the fields can be cleared, i.e., if there is any input.
   *
   * @return true if input can be cleared
   */
  @Override
  protected boolean canClearFields() {
    return !m_TextContainerID.getText().isEmpty()
      || !m_TextImage.getText().isEmpty()
      || !m_TextCommand.getText().isEmpty()
      || !m_TextCreated.getText().isEmpty()
      || !m_TextStatus.getText().isEmpty()
      || !m_TextPorts.getText().isEmpty()
      || !m_TextNames.getText().isEmpty();
  }

  /**
   * Returns whether modified data cannot be stored.
   *
   * @return true if storing is not available
   */
  @Override
  protected boolean isReadOnly() {
    return true;
  }

  /**
   * Returns whether all the required fields are set to add the object.
   *
   * @return true if required fields are filled in
   */
  @Override
  protected boolean canAddObject() {
    return false;
  }
}
