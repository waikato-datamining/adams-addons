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
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.filecommander.git;

import adams.core.Utils;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;

/**
 * Reverts the changes for the file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Rollback
  extends AbstractFileCommanderGitAction {

  private static final long serialVersionUID = 713334996532169916L;

  /**
   * Instantiates the action.
   */
  public Rollback() {
    super();
    setName("Git rollback");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    SwingWorker worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	if (m_Operation.rollback(getOwner().getActive().getFilePanel().getSelectedFiles())) {
	  String msg = "Rolled back:\n" + Utils.flatten(getOwner().getActive().getFilePanel().getSelectedFiles(), "\n");
	  getLogger().info(msg);
	  showNotification(msg);
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
    setEnabled(m_Operation.canRollback(getOwner().getActive().getFilePanel().getSelectedFiles()));
  }
}
