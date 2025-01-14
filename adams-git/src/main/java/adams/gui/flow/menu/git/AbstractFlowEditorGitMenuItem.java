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
 * AbstractFlowEditorGitMenuItem.java
 * Copyright (C) 2024-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.menu.git;

import adams.core.git.GitOperation;
import adams.core.git.GitSettingsHelper;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.gui.flow.menu.AbstractFlowEditorMenuItem;
import org.eclipse.jgit.api.Git;

/**
 * Ancestor for menuitems in the git sub-menu.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFlowEditorGitMenuItem
  extends AbstractFlowEditorMenuItem  {

  private static final long serialVersionUID = 3399685601227012815L;

  /** the current git instance to use. */
  protected Git m_Git;

  /** for managing git operations. */
  protected GitOperation m_Operation;

  /**
   * Initializes the menu item.
   */
  @Override
  protected void initialize() {
    LoggingLevel	level;

    super.initialize();

    level = GitSettingsHelper.getSingleton().getLoggingLevel();
    if (!LoggingHelper.isAtLeast(getLogger(), level.getLevel()))
      getLogger().setLevel(level.getLevel());

    m_Operation = new GitOperation();
    m_Operation.setShowErrors(true);
  }

  /**
   * Returns the name of the menu to list this item under.
   *
   * @return		sub-menu, so always null
   */
  @Override
  public String getMenu() {
    return null;
  }

  /**
   * Updating the action/menuitem/submenu, based on the current status of the owner.
   * 
   * @param git		the git instance
   */
  public void update(Git git) {
    m_Git = git;
    m_Operation.setGit(git);
    update();
  }
}
