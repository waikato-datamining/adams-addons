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
 * AbstractFileCommanderGitAction.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.filecommander.git;

import adams.core.git.GitOperation;
import adams.core.git.GitSession;
import adams.core.git.GitSettingsHelper;
import adams.core.io.PlaceholderFile;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingLevelHandler;
import adams.gui.core.GUIHelper;
import adams.gui.tools.filecommander.AbstractFileCommanderAction;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.util.logging.Level;

/**
 * Ancestor for git actions in the file commander.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFileCommanderGitAction
  extends AbstractFileCommanderAction
  implements LoggingLevelHandler {

  private static final long serialVersionUID = 3418947893040481574L;

  /** the logging level. */
  protected LoggingLevel m_LoggingLevel;

  /** the logger in use. */
  protected transient Logger m_Logger;

  /** whether logging is enabled. */
  protected transient Boolean m_LoggingIsEnabled;

  /** the current git instance to use. */
  protected Git m_Git;

  /** for managing git operations. */
  protected GitOperation m_Operation;

  /**
   * Initializes the menu item.
   */
  @Override
  protected void initialize() {
    LoggingLevel level;

    super.initialize();

    initializeLogging();

    level = GitSettingsHelper.getSingleton().getLoggingLevel();
    if (!LoggingHelper.isAtLeast(getLogger(), level.getLevel()))
      getLogger().setLevel(level.getLevel());

    m_Operation = new GitOperation();
    m_Operation.setShowErrors(true);
  }

  /**
   * Pre-configures the logging.
   */
  protected void initializeLogging() {
    m_LoggingLevel = LoggingHelper.getLoggingLevel(getClass());
  }

  /**
   * Initializes the logger.
   * <br><br>
   * Default implementation uses the class name.
   */
  protected void configureLogger() {
    m_Logger = LoggingHelper.getLogger(getClass());
    m_Logger.setLevel(m_LoggingLevel.getLevel());
  }

  /**
   * Returns the logger in use.
   *
   * @return		the logger
   */
  public synchronized Logger getLogger() {
    if (m_Logger == null)
      configureLogger();
    return m_Logger;
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public synchronized void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel     = value;
    m_Logger           = null;
    m_LoggingIsEnabled = null;
  }

  /**
   * Returns the logging level.
   *
   * @return 		the level
   */
  public LoggingLevel getLoggingLevel() {
    return m_LoggingLevel;
  }

  /**
   * Returns whether logging is enabled.
   *
   * @return		true if at least {@link Level#INFO}
   */
  public boolean isLoggingEnabled() {
    if (m_LoggingIsEnabled == null)
      m_LoggingIsEnabled = LoggingHelper.isAtLeast(m_LoggingLevel.getLevel(), Level.INFO);
    return m_LoggingIsEnabled;
  }

  /**
   * Displays the message using the name of the action as title.
   *
   * @param msg		the message to display
   */
  public void showNotification(String msg) {
    showNotification(getName(), msg);
  }

  /**
   * Displays the message.
   *
   * @param title	the title of the dialog
   * @param msg		the message to display
   */
  public void showNotification(String title, String msg) {
    GUIHelper.showInformationMessage(m_Owner, msg, title);
  }

  /**
   * Updates the action.
   */
  protected abstract void doUpdate();

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    File 	dir;
    Git		git;

    dir = new PlaceholderFile(getOwner().getActive().getFilePanel().getCurrentDir()).getAbsoluteFile();

    if (dir.exists()) {
      git = null;
      if (GitSession.getSingleton().isWithinRepo(dir)) {
	git = GitSession.getSingleton().repoFor(dir);
	if (git != null)
	  getLogger().info("git repo dir: " + git.getRepository().getWorkTree());
      }
      m_Git = git;
    }
    m_Operation.setGit(m_Git);

    doUpdate();
  }
}
