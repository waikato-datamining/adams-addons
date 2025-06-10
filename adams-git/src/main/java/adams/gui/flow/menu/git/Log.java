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
 * Copyright (C) 2024-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.menu.git;

import adams.gui.action.AbstractBaseAction;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;

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
	SwingWorker worker = new SwingWorker() {
	  @Override
	  protected Object doInBackground() throws Exception {
	    String logMsg = m_Operation.log(m_Owner.getCurrentFile());
	    getLogger().info(logMsg);
	    getOwner().getCurrentPanel().showNotification(logMsg, "git.png");
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
    m_Action.setEnabled(m_Operation.canLog(m_Owner.getCurrentFile()));
  }
}
