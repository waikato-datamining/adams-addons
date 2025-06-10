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
 * Commit.java
 * Copyright (C) 2024-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.menu.git;

import adams.core.git.GitSettingsHelper;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;

/**
 * Performs a "git commit".
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Commit
  extends AbstractFlowEditorGitMenuItem {

  private static final long serialVersionUID = -2869403192412073396L;

  /**
   * Creates the action to use.
   *
   * @return		the action
   */
  @Override
  protected AbstractBaseAction newAction() {
    return new AbstractBaseAction("Commit...", "save") {
      private static final long serialVersionUID = 5856785085545656193L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	if (m_Owner.isModified()) {
	  int retVal = GUIHelper.showConfirmMessage(m_Owner, "Flow is modified - save?");
	  if (retVal != ApprovalDialog.APPROVE_OPTION)
	    return;
	  m_Owner.save();
	}

	String msg = GUIHelper.showInputDialog(m_Owner, "Commit message for " + m_Owner.getCurrentFile().getName() + ":");
	if (msg == null)
	  return;
	String user = GitSettingsHelper.getSingleton().getUser();
	if (user.isEmpty())
	  user = GUIHelper.showInputDialog(m_Owner, "Please enter user for commit:");
	if (user == null)
	  return;
	String email = GitSettingsHelper.getSingleton().getEmail();
	if (email.isEmpty())
	  email = GUIHelper.showInputDialog(m_Owner, "Please enter email for commit:");
	if (email == null)
	  return;
	final String fUser = user;
	final String fEmail = email;
	SwingWorker worker = new SwingWorker() {
	  @Override
	  protected Object doInBackground() throws Exception {
	    String commitMsg = m_Operation.commit(m_Owner.getCurrentFile(), fUser, fEmail, msg);
	    if (commitMsg != null) {
	      getLogger().info(commitMsg);
	      getOwner().getCurrentPanel().showNotification(commitMsg, "git.png");
	    }
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
    m_Action.setEnabled(m_Operation.canCommit(m_Owner.getCurrentFile()));
  }
}
