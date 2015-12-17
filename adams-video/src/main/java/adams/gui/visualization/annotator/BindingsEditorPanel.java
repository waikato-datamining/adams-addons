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
 * BindingsEditorPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.annotator;

import adams.core.Properties;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.BasePanel;
import adams.gui.dialog.EditBindingDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * TODO: what class does.
 *
 * @author sjb90
 * @version $Revision$
 */
public class BindingsEditorPanel extends BasePanel{

  /**
   * the bindings the user sets
   */
  protected List m_Bindings;

  /**
   * the list of current bindings
   */
  protected BaseListWithButtons m_BindingsList;

  /**
   * the button for adding a binding
   */
  protected JButton m_AddButton;

  /**
   * the button for deleting a binding
   */
  protected JButton m_DeleteButton;

  /**
   * the save button
   */
  protected JButton m_EditButton;

  /**
   * dialog for editing or adding a binding
   */
  protected EditBindingDialog m_EditDialog;

  // actions

  /**
   * the action 'add'
   */
  protected AbstractBaseAction m_AddAction;

  /**
   * the action 'delete'
   */
  protected AbstractBaseAction m_DeleteAction;

  protected AbstractBaseAction m_EditAction;

  protected void initActions() {
    m_AddAction = new AbstractBaseAction("Add", "add.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	if(m_EditDialog == null) {
	  if (getParentDialog() != null) {
	    m_EditDialog = new EditBindingDialog(getParentDialog(), Dialog.ModalityType.DOCUMENT_MODAL);
	    m_EditDialog.setSize(200,200);
	  }
	  else
	    m_EditDialog = new EditBindingDialog(getParentFrame(), true);
	}
	else {
	  m_EditDialog.clearFields();
	}
	m_EditDialog.setVisible(true);
	updateListBox();
      }
    };

    m_EditAction = new AbstractBaseAction("Edit", "edit.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	// TODO: build this
      }
    };

    m_DeleteAction = new AbstractBaseAction("Remove", "remove.gif") {
      @Override
      protected void doActionPerformed(ActionEvent e) {
	// TODO: Rebuild this
	updateListBox();
      }
    };
  }

  @Override
  protected void initialize() {
    super.initialize();
    m_Bindings = new ArrayList<>();
    initActions();
  }

  @Override
  protected void initGUI() {
    super.initGUI();
    m_BindingsList       = new BaseListWithButtons(new DefaultListModel<>());
    setLayout(new GridLayout(1,2));

    // Set up a List Selection Listener that we'll use to change the editor panel
    m_BindingsList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
	updateListBox();
      }
    });

    // Buttons for the list

    m_AddButton = new JButton(m_AddAction);
    m_BindingsList.addToButtonsPanel(m_AddButton);

    m_DeleteButton = new JButton(m_DeleteAction);
    m_BindingsList.addToButtonsPanel(m_DeleteButton);

    m_EditButton = new JButton(m_EditAction);
    m_EditButton.setEnabled(false);
    m_BindingsList.addToButtonsPanel(m_EditButton);
    add(m_BindingsList);

    setVisible(true);
  }

  private void updateListBox() {
    DefaultListModel model = (DefaultListModel)m_BindingsList.getModel();
    // Set the update button to enabled or unenabled depending on if something is selected.
    m_EditButton.setEnabled(m_BindingsList.getSelectedIndex() != -1);
  }

  /**
   * A getter for the bindings
   * @return some key bindings
   */
  public List getBindings() {
    return m_Bindings;
  }

  /**
   * Sets the bindings to the ones supplied
   * @param bindings The bindings supplied
   */
  public void setBindings(java.util.List<Binding> bindings) {
    m_Bindings = bindings;
  }
}
