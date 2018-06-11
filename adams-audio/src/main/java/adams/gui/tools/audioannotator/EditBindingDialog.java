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
 * EditBindingDialog.java
 * Copyright (C) 2015-2018 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.audioannotator;

import adams.gui.dialog.ApprovalDialog;

import java.awt.Dialog;
import java.awt.Frame;

/**
 * Dialog for editing an individual key binding
 *
 * @author sjb90
 */
public class EditBindingDialog extends ApprovalDialog {

  /** the editor panel that does the actual work */
  BindingParameterPanel m_EditorPanel;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public EditBindingDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public EditBindingDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public EditBindingDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified title, modality and the specified
   * owner Dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   * @param modality	the type of modality
   */
  public EditBindingDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public EditBindingDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public EditBindingDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public EditBindingDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public EditBindingDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  @Override
  protected void initialize() {
    super.initialize();
  }

  @Override
  protected void initGUI() {
    super.initGUI();
    m_EditorPanel = new BindingParameterPanel();
    getContentPane().add(m_EditorPanel);
  }

  /**
   * Clears all the parameter fields.
   */
  public void clearFields() {
    m_EditorPanel.clearFields();
  }


  /**
   * a getter for the binding
   * @return a binding made from the last info entered
   */
  public Binding getBinding() {
    return m_EditorPanel.getBinding();
  }

  /**
   * loads a binding into the edit binding panel
   * @param b the binding to load in
   */
  public void loadBinding(Binding b) {
    m_EditorPanel.loadBinding(b);
  }
}

