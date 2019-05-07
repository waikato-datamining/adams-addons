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
 * MOAClassOptionEditor.java
 * Copyright (C) 2009-2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import moa.gui.ClassOptionEditComponent;
import moa.options.ClassOption;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * An editor for MOA ClassOption objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see ClassOption
 */
public class MOAClassOptionEditor
  extends AbstractPropertyEditorSupport {

  /** the custom editor. */
  protected Component m_CustomEditor;

  /** the component for editing. */
  protected ClassOptionEditComponent m_EditComponent;

  /**
   * Returns true since this editor is paintable.
   *
   * @return 		always true.
   */
  public boolean isPaintable() {
    return false;
  }

  /**
   * Creates the custom editor.
   *
   * @return		the editor
   */
  protected JComponent createCustomEditor() {
    JPanel			panel;

    panel = new JPanel(new BorderLayout());
    m_EditComponent = (ClassOptionEditComponent) ((ClassOption) getValue()).getEditComponent();
    m_EditComponent.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	m_EditComponent.applyState();
	setValue(m_EditComponent.getEditedOption());
      }
    });
    panel.add(m_EditComponent, BorderLayout.CENTER);

    return panel;
  }

}
