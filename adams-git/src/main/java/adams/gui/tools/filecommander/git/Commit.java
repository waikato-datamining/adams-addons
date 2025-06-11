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
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.filecommander.git;

import adams.core.git.GitSettingsHelper;
import adams.gui.core.GUIHelper;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Performs a git commit of the selected file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Commit
  extends AbstractFileCommanderGitAction {

  private static final long serialVersionUID = 713334996532169916L;

  /**
   * Instantiates the action.
   */
  public Commit() {
    super();
    setName("Git commit");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    File[]	files;
    String	filesMsg;
    String 	msg;
    String 	user;
    String 	email;

    files = getOwner().getActive().getFilePanel().getSelectedFiles();
    if (files.length == 1)
      filesMsg = files[0].getName();
    else
      filesMsg = files.length + " files";
    msg = GUIHelper.showInputDialog(m_Owner, "Commit message for " + filesMsg + ":");
    if (msg == null)
      return;

    user = GitSettingsHelper.getSingleton().getUser();
    if (user.isEmpty())
      user = GUIHelper.showInputDialog(m_Owner, "Please enter user for commit:");
    if (user == null)
      return;

    email = GitSettingsHelper.getSingleton().getEmail();
    if (email.isEmpty())
      email = GUIHelper.showInputDialog(m_Owner, "Please enter email for commit:");
    if (email == null)
      return;

    final String fUser = user;
    final String fEmail = email;
    final File[] fFiles = files;
    SwingWorker worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	String commitMsg = m_Operation.commit(fFiles, fUser, fEmail, msg);
	if (commitMsg != null) {
	  getLogger().info(commitMsg);
	  showNotification(commitMsg);
	}
	return null;
      }
    };
    worker.execute();
  }

  /**
   * Updates the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_Operation.canCommit(getOwner().getActive().getFilePanel().getSelectedFiles()));
  }
}
