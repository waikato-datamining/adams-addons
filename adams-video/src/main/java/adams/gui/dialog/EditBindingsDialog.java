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
 * EditBindingsDialog.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;


import adams.gui.visualization.annotator.Binding;
import adams.gui.visualization.annotator.BindingsEditorPanel;

import java.awt.*;
import java.util.List;

/**
 * A dialog for editing key bindings
 *
 * @author sjb90
 * @version $Revision$
 */
public class EditBindingsDialog extends ApprovalDialog {

  /** the BindingEditorPanel */
  BindingsEditorPanel m_BindignPanel;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public EditBindingsDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public EditBindingsDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public EditBindingsDialog(Dialog owner, String title) {
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
  public EditBindingsDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public EditBindingsDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public EditBindingsDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public EditBindingsDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public EditBindingsDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  @Override
  protected void initialize() {
    super.initialize();
  }

  @Override
  protected void initGUI() {
    super.initGUI();

    m_BindignPanel = new BindingsEditorPanel();
    getContentPane().add(m_BindignPanel, BorderLayout.CENTER);
    setSize(600, 600);
    setTitle("Edit bindings");
  }

  public List<Binding> getBindings() {
    return m_BindignPanel.getBindings();
  }

  public void setBindings(List<Binding> bindings) {
    m_BindignPanel.setBindings(bindings);
  }
}
