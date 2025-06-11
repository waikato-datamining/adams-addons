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
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.filecommander.git;

import adams.core.MessageCollection;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Performs a git add of the selected files.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Add
  extends AbstractFileCommanderGitAction {

  private static final long serialVersionUID = 713334996532169916L;

  /**
   * Instantiates the action.
   */
  public Add() {
    super();
    setName("Git add");
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
	MessageCollection log = new MessageCollection();
	for (File file: getOwner().getActive().getFilePanel().getSelectedFiles())
	  log.add("Added '" + file + "': " + m_Operation.add(file));
	getLogger().info(log.toString());
	showNotification(log.toString());
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
    setEnabled(getOwner().getActive().getFilePanel().getSelectedFiles().length > 0);
  }
}
