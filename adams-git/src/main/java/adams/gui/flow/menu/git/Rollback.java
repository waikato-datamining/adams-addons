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
 * Rollback.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.menu.git;

import adams.core.io.FileUtils;
import adams.gui.action.AbstractBaseAction;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowPanelNotificationArea.NotificationType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Constants;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;

/**
 * Performs a "git commit".
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Rollback
  extends AbstractFlowEditorGitMenuItem {

  private static final long serialVersionUID = -2869403192412073396L;

  /**
   * Creates the action to use.
   *
   * @return		the action
   */
  @Override
  protected AbstractBaseAction newAction() {
    return new AbstractBaseAction("Rollback", "revert") {
      private static final long serialVersionUID = 5856785085545656193L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	String relPath = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), m_Owner.getCurrentFile());
	SwingWorker worker = new SwingWorker() {
	  @Override
	  protected Object doInBackground() throws Exception {
	    try {
	      Status status = m_Git.status()
			 .addPath(relPath)
			 .call();
	      if (status.getAdded().contains(relPath))
		m_Git.reset().setRef(Constants.HEAD).addPath(relPath).call();
	      else
		m_Git.checkout().addPath(relPath).call();
	      String msg = "Rolled back:\n" + m_Owner.getCurrentFile().getAbsolutePath();
	      getLogger().info(msg);
	      getOwner().getCurrentPanel().showNotification(msg, NotificationType.INFO);
	      getOwner().getCurrentPanel().revert();
	    }
	    catch (Exception ex) {
	      getLogger().log(Level.SEVERE, "Failed to roll back: " + relPath, e);
	      GUIHelper.showErrorMessage(m_Owner, "Failed to roll back:\n" + relPath, ex);
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

    file    = m_Owner.getCurrentFile();
    relPath = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), file);
    try {
      status = m_Git.status()
		 .addPath(relPath)
		 .call();
      m_Action.setEnabled(
	status.getModified().contains(relPath)
	  || status.getAdded().contains(relPath));
    }
    catch (Exception e) {
      m_Action.setEnabled(false);
      getLogger().log(Level.SEVERE, "Failed to query status of repo!", e);
    }
  }
}
