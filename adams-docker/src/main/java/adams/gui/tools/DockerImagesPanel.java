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
 * DockerImagesPanel.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import adams.core.Range;
import adams.data.conversion.StringToSpreadSheet;
import adams.data.io.input.AutoWidthTabularSpreadSheetReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.docker.DockerImage;
import adams.docker.simpledocker.ListImages;
import adams.docker.simpledocker.RemoveImages;
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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Management panel for Docker images.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DockerImagesPanel
  extends AbstractManagementPanel<DockerImage> {

  public static class TableModel
    extends AbstractManagementTableModel<DockerImage> {

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
     * @param values	the images to display
     */
    public TableModel(DockerImage[] values) {
      super(values);
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return the number of columns
     */
    @Override
    public int getColumnCount() {
      return 6;
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
	  return "Repository";
	case 2:
	  return "Tag";
	case 3:
	  return "Image ID";
	case 4:
	  return "Created";
	case 5:
	  return "Size";
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
      DockerImage	image;

      image = m_Values.get(row);

      switch (column) {
	case 0:
	  return (row+1);
	case 1:
	  return image.getRepository();
	case 2:
	  return image.getTag();
	case 3:
	  return image.getImageID();
	case 4:
	  return image.getCreated();
	case 5:
	  return image.getSize();
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
      // repository
      if (params.matches(m_Values.get(row).getRepository()))
	return true;
      // tag
      if (params.matches(m_Values.get(row).getTag()))
	return true;
      // image ID
      if (params.matches(m_Values.get(row).getImageID()))
	return true;
      // created
      if (params.matches(m_Values.get(row).getCreated()))
	return true;
      // size
      if (params.matches(m_Values.get(row).getSize()))
	return true;

      return false;
    }
  }

  /** the repository field. */
  protected BaseTextField m_TextRepository;

  /** the tag field. */
  protected BaseTextField m_TextTag;

  /** the image ID field. */
  protected BaseTextField m_TextImageID;

  /** the created field. */
  protected BaseTextField m_TextCreated;

  /** the size field. */
  protected BaseTextField m_TextSize;

  /** the button for deleting selected images. */
  protected BaseButton m_ButtonDelete;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_ButtonDelete = new BaseButton("Delete");
    m_ButtonDelete.addActionListener((ActionEvent e) -> deleteImages());
    m_TableValues.addToButtonsPanel(m_ButtonDelete);
  }

  /**
   * For adding all the fields.
   *
   * @param panel the panel to add the fields to
   */
  @Override
  protected void addFields(ParameterPanelWithButtons panel) {
    m_TextRepository = new BaseTextField();
    panel.addParameter("Repository", m_TextRepository);

    m_TextTag = new BaseTextField();
    panel.addParameter("Tag", m_TextTag);

    m_TextImageID = new BaseTextField();
    panel.addParameter("Image ID", m_TextImageID);

    m_TextCreated = new BaseTextField();
    panel.addParameter("Created", m_TextCreated);

    m_TextSize = new BaseTextField();
    panel.addParameter("Size", m_TextSize);
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
    return DockerImage.class;
  }

  /**
   * Resets the input fields.
   */
  @Override
  protected void clear() {
    m_TextRepository.setText("");
    m_TextTag.setText("");
    m_TextImageID.setText("");
    m_TextCreated.setText("");
    m_TextSize.setText("");
  }

  /**
   * Turns the fields into an object.
   *
   * @return the generated object
   */
  @Override
  protected DockerImage fieldsToObject() {
    return new DockerImage(
      m_TextRepository.getText(),
      m_TextTag.getText(),
      m_TextImageID.getText(),
      m_TextCreated.getText(),
      m_TextSize.getText()
    );
  }

  /**
   * Updates the field with the specified object.
   *
   * @param value the object to display
   */
  @Override
  protected void objectToFields(DockerImage value) {
    m_TextRepository.setText(value.getRepository());
    m_TextTag.setText(value.getTag());
    m_TextImageID.setText(value.getImageID());
    m_TextCreated.setText(value.getCreated());
    m_TextSize.setText(value.getSize());
  }

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

    ListImages list = new ListImages();
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
   * Creates the flow for deleting docker images.
   *
   * @param ids 	the IDs of the images to remove
   * @return		the flow
   */
  protected Flow getDeleteFlow(String[] ids) {
    Flow 	result;

    result = new Flow();
    result.add(new SimpleDockerConnection());

    result.add(new StringConstants(ids));

    adams.flow.transformer.SimpleDockerCommand cmd = new adams.flow.transformer.SimpleDockerCommand();
    RemoveImages remove = new RemoveImages();
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
  protected List<DockerImage> loadAll() {
    List<DockerImage>	result;
    Flow		flow;
    StorageName		sname;
    String		msg;
    SpreadSheet		sheet;

    result = new ArrayList<>();

    flow  = getListFlow();
    sname = new StorageName("sheet");
    msg = flow.setUp();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, "Failed to list docker images (flow setup):\n" + msg);
      cleanUp(flow);
      return result;
    }

    msg = flow.execute();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, "Failed to list docker images (flow execution):\n" + msg);
      cleanUp(flow);
      return result;
    }

    if (!flow.getStorage().has(sname)) {
      GUIHelper.showErrorMessage(this, "No spreadsheet with docker images generated - parsing of docker output failed?");
      cleanUp(flow);
      return result;
    }

    sheet = (SpreadSheet) flow.getStorage().get(sname);
    for (Row row: sheet.rows()) {
      result.add(new DockerImage(
	row.getCell(0).getContent(),
	row.getCell(1).getContent(),
	row.getCell(2).getContent(),
	row.getCell(3).getContent(),
	row.getCell(4).getContent()
      ));
    }

    cleanUp(flow);

    return result;
  }

  /**
   * Deletes the selected images.
   */
  protected void deleteImages() {
    int			retVal;
    Flow		flow;
    List<DockerImage>	selected;
    String[]		ids;
    int			i;
    String		msg;
    StorageName		sname;
    String		output;

    selected = getSelectedValues();

    retVal = GUIHelper.showConfirmMessage(this, "Do you want to delete " + selected.size() + " docker image(s)?");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    ids  = new String[selected.size()];
    for (i = 0; i < selected.size(); i++)
      ids[i] = selected.get(i).getImageID();

    flow  = getDeleteFlow(ids);
    sname = new StorageName("output");
    msg = flow.setUp();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, "Failed to delete docker images (flow setup):\n" + msg);
      cleanUp(flow);
      return;
    }

    msg = flow.execute();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, "Failed to delete docker images (flow execution):\n" + msg);
      cleanUp(flow);
      return;
    }

    if (flow.getStorage().has(sname)) {
      output = "" + flow.getStorage().get(sname);
      output = output.trim();
      if (output.length() > 0)
        GUIHelper.showInformationMessage(this, "Output of deleting docker images:\n" + output);
    }

    cleanUp(flow);

    refresh();
  }

  /**
   * Checks whether the object already exists.
   *
   * @param value the value to look for
   * @return true if already available
   */
  @Override
  protected boolean exists(DockerImage value) {
    return false;
  }

  /**
   * Stores the object.
   *
   * @param value the value to store
   * @return true if successfully stored
   */
  @Override
  protected boolean store(DockerImage value) {
    return false;
  }

  /**
   * Removes the object.
   *
   * @param value the value to remove
   * @return true if successfully removed
   */
  @Override
  protected boolean remove(DockerImage value) {
    return false;
  }

  /**
   * Returns whether the fields can be cleared, i.e., if there is any input.
   *
   * @return true if input can be cleared
   */
  @Override
  protected boolean canClearFields() {
    return !m_TextRepository.getText().isEmpty()
      || !m_TextTag.getText().isEmpty()
      || !m_TextImageID.getText().isEmpty()
      || !m_TextCreated.getText().isEmpty()
      || !m_TextSize.getText().isEmpty();
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
