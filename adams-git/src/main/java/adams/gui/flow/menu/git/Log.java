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
 * Log.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.menu.git;

import adams.core.git.GitHelper;
import adams.core.io.FileUtils;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowPanelNotificationArea.NotificationType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;

/**
 * Performs a "git log".
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Log
  extends AbstractFlowEditorGitMenuItem {

  private static final long serialVersionUID = -2869403192412073396L;

  /**
   * Creates the action to use.
   *
   * @return		the action
   */
  @Override
  protected AbstractBaseAction newAction() {
    return new AbstractBaseAction("Log...", "log") {
      private static final long serialVersionUID = 5856785085545656193L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	String relPath = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), m_Owner.getCurrentFile());
	SwingWorker worker = new SwingWorker() {
	  @Override
	  protected Object doInBackground() throws Exception {
	    try {
	      Iterable<RevCommit> result = m_Git.log()
					     .addPath(relPath)
					     .call();
	      StringBuilder info = new StringBuilder();
	      for (RevCommit commit: result) {
		if (info.length() > 0)
		  info.append("\n");
		info.append(GitHelper.format(commit, GitHelper.FORMAT_REVCOMMIT_LONG));
	      }
	      getLogger().info(info.toString());
	      getOwner().getCurrentPanel().showNotification(info.toString(), NotificationType.INFO);
	    }
	    catch (Exception ex) {
	      getLogger().log(Level.SEVERE, "Failed to commit: " + relPath, ex);
	      GUIHelper.showErrorMessage(m_Owner, "Failed to commit:\n" + relPath, ex);
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
    Status 	status;
    File 	file;
    String 	relPath;

    if (m_Git == null) {
      m_Action.setEnabled(false);
      return;
    }

    file    = m_Owner.getCurrentFile();
    relPath = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), file);
    try {
      status = m_Git.status()
		 .addPath(relPath)
		 .call();
      m_Action.setEnabled(
	status.getModified().contains(relPath)
	  || status.isClean());
    }
    catch (Exception e) {
      m_Action.setEnabled(false);
      getLogger().log(Level.SEVERE, "Failed to query status of repo!", e);
    }
  }
}
