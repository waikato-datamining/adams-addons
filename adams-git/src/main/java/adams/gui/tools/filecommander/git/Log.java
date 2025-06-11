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
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.filecommander.git;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;

/**
 * Shows the log for the selected file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Log
  extends AbstractFileCommanderGitAction {

  private static final long serialVersionUID = 713334996532169916L;

  /**
   * Instantiates the action.
   */
  public Log() {
    super();
    setName("Git log");
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
	String logMsg = m_Operation.log(getOwner().getActive().getFilePanel().getSelectedFile());
	if (logMsg == null)
	  logMsg = "No log message available!";
	getLogger().info(logMsg);
	showNotification(logMsg);
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
    setEnabled((getOwner().getActive().getFilePanel().getSelectedFiles().length == 1)
      && m_Operation.canLog(getOwner().getActive().getFilePanel().getSelectedFile()));
  }
}
