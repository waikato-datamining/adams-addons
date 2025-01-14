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
 * Add.java
 * Copyright (C) 2024-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.menu.git;

import adams.gui.action.AbstractBaseAction;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;

/**
 * Performs a "git add".
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Add
  extends AbstractFlowEditorGitMenuItem {

  private static final long serialVersionUID = -2869403192412073396L;

  /**
   * Creates the action to use.
   *
   * @return		the action
   */
  @Override
  protected AbstractBaseAction newAction() {
    return new AbstractBaseAction("Add", "add") {
      private static final long serialVersionUID = -7027399409999957965L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	if (m_Owner.isModified()) {
	  int retVal = GUIHelper.showConfirmMessage(m_Owner, "Flow is modified - save?");
	  if (retVal != ApprovalDialog.APPROVE_OPTION)
	    return;
	  m_Owner.save();
	}

	SwingWorker worker = new SwingWorker() {
	  @Override
	  protected Object doInBackground() throws Exception {
	    m_Operation.add(m_Owner.getCurrentFile());
	    return null;
	  }
	};
	worker.execute();
      }
    };
  }

  /**
   * Updating the action/menuitem/submenu, based on the current status of the owner.
   */
  @Override
  public void update() {
    m_Action.setEnabled(m_Operation.canAdd(m_Owner.getCurrentFile()));
  }
}
