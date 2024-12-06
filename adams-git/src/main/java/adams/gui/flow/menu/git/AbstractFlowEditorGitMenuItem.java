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
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.menu.git;

import adams.core.git.GitSession;
import adams.core.git.GitSettingsHelper;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.gui.flow.menu.AbstractFlowEditorMenuItem;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.transport.SshTransport;

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
    update();
  }

  /**
   * Adds the transport config callback (with sshd factory) if necessary.
   * If the remote url starts with "git@", then we assume that ssh keys are used.
   *
   * @param cmd		the command to update
   * @return		the updated command
   */
  protected <T  extends TransportCommand> T setTransportConfigCallbackIfNecessary(T cmd) {
    String 	url;

    url = m_Git.getRepository().getConfig().getString("remote", "origin", "url");
    // do we need ssh key?
    if ((url != null) && url.startsWith("git@")) {
      cmd.setTransportConfigCallback(transport -> ((SshTransport) transport).setSshSessionFactory(
	GitSession.getSingleton().getSshdSessionFactory()));
    }

    return cmd;
  }

  /**
   * Checks whether the repository has a remote URL.
   *
   * @return		true if remote URL available
   */
  protected boolean isRemoteRepo() {
    return (m_Git.getRepository().getConfig().getString("remote", "origin", "url") != null);
  }
}
