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
 * Pull.java
 * Copyright (C) 2024-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.menu.git;

import adams.gui.action.AbstractBaseAction;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;

/**
 * Performs a "git pull".
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Pull
  extends AbstractFlowEditorGitMenuItem {

  private static final long serialVersionUID = -2869403192412073396L;

  /**
   * Creates the action to use.
   *
   * @return		the action
   */
  @Override
  protected AbstractBaseAction newAction() {
    return new AbstractBaseAction("Pull", "arrow_skip_down") {
      private static final long serialVersionUID = -242442630510447880L;
      @Override
      protected void doActionPerformed(ActionEvent e) {
	SwingWorker worker = new SwingWorker() {
	  @Override
	  protected Object doInBackground() throws Exception {
	    String pullMsg = m_Operation.pull();
	    if (pullMsg != null) {
	      getLogger().info(pullMsg);
	      getOwner().getCurrentPanel().showNotification(pullMsg, "git.png");
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
    m_Action.setEnabled(m_Operation.canPull());
  }
}
