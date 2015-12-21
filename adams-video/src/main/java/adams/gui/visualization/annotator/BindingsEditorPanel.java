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

  protected DefaultListModel<Binding> m_Model;

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
	m_EditDialog.setLocationRelativeTo(BindingsEditorPanel.this);
	m_EditDialog.setVisible(true);
	m_Model.addElement(m_EditDialog.getBinding());
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
	int[] indices = m_BindingsList.getSelectedIndices();
	for (int i = indices.length - 1; i >= 0; i--)
	  m_Model.remove(indices[i]);
      }
    };
  }

  @Override
  protected void initialize() {
    super.initialize();
    initActions();
  }

  @Override
  protected void initGUI() {
    super.initGUI();
    m_Model = new DefaultListModel<>();
    m_BindingsList = new BaseListWithButtons(m_Model);
    setLayout(new GridLayout(1,2));

    // Buttons for the list

    m_AddButton = new JButton(m_AddAction);
    m_BindingsList.addToButtonsPanel(m_AddButton);

    m_DeleteButton = new JButton(m_DeleteAction);
    m_BindingsList.addToButtonsPanel(m_DeleteButton);

    m_EditButton = new JButton(m_EditAction);
    m_EditButton.setEnabled(false);
    m_BindingsList.addToButtonsPanel(m_EditButton);
    add(m_BindingsList);
  }

  /**
   * A getter for the m_Bindings
   * @return some key m_Bindings
   */
  public List<Binding> getBindings() {
    List<Binding> result;
    int i;

    result = new ArrayList<>();
    for (i = 0; i < m_Model.size(); i++)
      result.add(m_Model.get(i));

    return result;
  }

  /**
   * Sets the m_Bindings to the ones supplied
   * @param bindings The m_Bindings supplied
   */
  public void setBindings(List<Binding> bindings) {
    m_Model.clear();
    for (Binding binding: bindings)
      m_Model.addElement(binding);
  }
}
