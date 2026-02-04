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
 * GitOperation.java
 * Copyright (C) 2025-2026 University of Waikato, Hamilton, New Zealand
 */

package adams.core.git;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.logging.CustomLoggingLevelObject;
import adams.core.logging.LoggingHelper;
import adams.gui.core.GUIHelper;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.SshTransport;

import java.awt.Component;
import java.io.File;
import java.net.URI;
import java.util.logging.Level;

/**
 * Helper class for making git operations simpler.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class GitOperation
  extends CustomLoggingLevelObject {

  private static final long serialVersionUID = -5216944029338909422L;

  /** the current git instance to use. */
  protected Git m_Git;

  /** whether to show errors in the GUI. */
  protected boolean m_ShowErrors;

  /** the component to use as parent for dialogs. */
  protected Component m_Parent;

  /**
   * Sets the git instance to use.
   *
   * @param value	the instance to use
   */
  public void setGit(Git value) {
    m_Git = value;
  }

  /**
   * Returns the git instance in use.
   *
   * @return		the instance in use, null if none set
   */
  public Git getGit() {
    return m_Git;
  }

  /**
   * Sets whether to show error dialogs.
   *
   * @param value	true if to show
   */
  public void setShowErrors(boolean value) {
    m_ShowErrors = value;
  }

  /**
   * Returns whether to show error dialogs.
   *
   * @return		true if to show
   */
  public boolean getShowErrors() {
    return m_ShowErrors;
  }

  /**
   * Checks whether we can proceed with operations.
   *
   * @return		true if able to proceed
   */
  protected boolean canProceed() {
    return (m_Git != null);
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
   * Checks whether the repo can be cloned.
   *
   * @param uri		the URI of the repo
   * @param directory	the directory to clone into
   * @return		true if can be cloned
   */
  public static boolean canClone(String uri, File directory) {
    return canClone(uri, directory, null);
  }

  /**
   * Checks whether the repo can be cloned.
   *
   * @param uri		the URI of the repo
   * @param directory	the directory to clone into
   * @param errors 	for logging errors
   * @return		true if can be committed
   */
  public static boolean canClone(String uri, File directory, MessageCollection errors) {
    if (uri.isEmpty()) {
      if (errors != null)
	errors.add("No URI for cloning!");
      return false;
    }

    try {
      new URI(uri);
    }
    catch (Exception e) {
      if (errors != null)
	errors.add("Invalid repository URI: " + uri);
      return false;
    }

    if (directory.exists()) {
      if (errors != null)
	errors.add("Directory already exists: " + directory);
      return false;
    }

    return true;
  }

  /**
   * Clones the specified repo.
   *
   * @param uri		the URI of the repo
   * @param directory	the directory to clone into
   * @return		the git instance if successful, otherwise null
   */
  public static Git clone(String uri, File directory) {
    return clone(uri, directory, null);
  }

  /**
   * Clones the specified repo.
   *
   * @param uri		the URI of the repo
   * @param directory	the directory to clone into
   * @return		the git instance if successful, otherwise null
   */
  public static Git clone(String uri, File directory, MessageCollection errors) {
    CloneCommand	cmd;
    Git			git;
    String		msg;

    try {
      cmd = Git.cloneRepository()
	      .setURI(uri)
	      .setDirectory(directory.getAbsoluteFile());
      git = cmd.call();
      GitSession.getSingleton().addRepo(git);
      return git;
    }
    catch (Exception e) {
      msg = "Failed to clone: " + uri;
      if (errors != null)
	errors.add(msg, e);
      LoggingHelper.global().log(Level.SEVERE, msg, e);
    }

    return null;
  }

  /**
   * Checks whether a log can be generated.
   *
   * @param file	the file to generate the log for
   * @return		true if log can be generated
   */
  public boolean canLog(File file) {
    return canLog(file, null);
  }

  /**
   * Checks whether a log can be generated.
   *
   * @param file	the file to generate the log for
   * @param errors	for collecting errors, can be null
   * @return		true if log can be generated
   */
  public boolean canLog(File file, MessageCollection errors) {
    String	relPath;
    Status 	status;
    String	msg;

    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return false;
    }

    relPath = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), file);
    try {
      status = m_Git.status()
		 .addPath(relPath)
		 .call();
      return status.getModified().contains(relPath) || status.isClean();
    }
    catch (Exception e) {
      msg = "Failed to query status of repo!";
      if (errors != null)
	errors.add(msg, e);
      getLogger().log(Level.SEVERE, msg, e);
    }
    return false;
  }

  /**
   * Generates a log for the specified file.
   *
   * @param file	the file to get the log for
   * @return		the log output, null if failed
   */
  public String log(File file) {
    return log(file, null);
  }

  /**
   * Generates a log for the specified file.
   *
   * @param file	the file to get the log for
   * @param errors 	for storing errors, can be null
   * @return		the log output, null if failed
   */
  public String log(File file, MessageCollection errors) {
    StringBuilder 		result;
    String			relPath;
    Iterable<RevCommit> 	logResult;
    String			msg;

    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return null;
    }

    relPath = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), file);
    try {
      logResult = m_Git.log()
		    .addPath(relPath)
		    .call();
      result = new StringBuilder();
      for (RevCommit commit: logResult) {
	if (result.length() > 0)
	  result.append("\n");
	result.append(GitHelper.format(commit, GitHelper.FORMAT_REVCOMMIT_LONG));
      }
      return result.toString();
    }
    catch (Exception e) {
      msg = "Failed to commit: " + relPath;
      if (errors != null)
	errors.add(msg, e);
      getLogger().log(Level.SEVERE, msg, e);
      if (getShowErrors())
	GUIHelper.showErrorMessage(m_Parent, msg, e);
    }
    return null;
  }

  /**
   * Checks whether a pull is possible.
   *
   * @return		true if pull possible
   */
  public boolean canPull() {
    return canPull(null);
  }

  /**
   * Checks whether a pull is possible.
   *
   * @param errors	for collecting errors, can be null
   * @return		true if pull possible
   */
  public boolean canPull(MessageCollection errors) {
    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return false;
    }

    return GitHelper.isRemoteRepo(m_Git);
  }

  /**
   * Performs a pull on the repo.
   *
   * @return		the pull result, null if failed
   */
  public String pull() {
    return pull(null);
  }

  /**
   * Performs a pull on the repo.
   *
   * @param errors 	for storing errors, can be null
   * @return		the pull result, null if failed
   */
  public String pull(MessageCollection errors) {
    String 		result;
    PullCommand 	pullCmd;
    PullResult 		pullResult;
    String		msg;

    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return null;
    }

    result = null;
    try {
      pullCmd = setTransportConfigCallbackIfNecessary(m_Git.pull());
      pullResult = pullCmd.call();
      result = GitHelper.format(pullResult);
    }
    catch (Exception e) {
      msg = "Failed to pull: " + m_Git.getRepository().getWorkTree();
      if (errors != null)
	errors.add(msg, e);
      getLogger().log(Level.SEVERE, msg, e);
      if (getShowErrors())
	GUIHelper.showErrorMessage(m_Parent, msg, e);
    }
    return result;
  }

  /**
   * Checks whether a push is possible.
   *
   * @return		true if push possible
   */
  public boolean canPush() {
    return canPush(null);
  }

  /**
   * Checks whether a push is possible.
   *
   * @param errors	for collecting errors, can be null
   * @return		true if push possible
   */
  public boolean canPush(MessageCollection errors) {
    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return false;
    }

    return GitHelper.isRemoteRepo(m_Git);
  }

  /**
   * Performs a push on the repo.
   *
   * @return		the result of the push, null if failed
   */
  public String push() {
    return push(null);
  }

  /**
   * Performs a push on the repo.
   *
   * @param errors 	for collecting errors, can be null
   * @return		the result of the push, null if failed
   */
  public String push(MessageCollection errors) {
    PushCommand 		pushCmd;
    Iterable<PushResult> 	pushResults;
    StringBuilder 		result;
    String			msg;

    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return null;
    }

    try {
      pushCmd     = setTransportConfigCallbackIfNecessary(m_Git.push());
      pushResults = pushCmd.call();
      result      = new StringBuilder();
      for (PushResult pushResult : pushResults) {
	if (result.length() > 0)
	  result.append("\n");
	for (RemoteRefUpdate update: pushResult.getRemoteUpdates())
	  result.append(GitHelper.format(update)).append("\n");
      }
      return result.toString();
    }
    catch (Exception e) {
      msg = "Failed to push: " + m_Git.getRepository().getWorkTree();
      if (errors != null)
	errors.add(msg, e);
      getLogger().log(Level.SEVERE, msg, e);
      if (getShowErrors())
	GUIHelper.showErrorMessage(m_Parent, msg, e);
    }
    return null;
  }

  /**
   * Checks whether the file can be added.
   *
   * @param file	the file to check
   * @return		true if can be added
   */
  public boolean canAdd(File file) {
    return canAdd(file, null);
  }

  /**
   * Checks whether the file can be added.
   *
   * @param file	the file to check
   * @param errors	for collecting errors, can be null
   * @return		true if can be added
   */
  public boolean canAdd(File file, MessageCollection errors) {
    return canAdd(new File[]{file}, errors);
  }

  /**
   * Adds the specified file.
   *
   * @param file	the file to add
   * @return		true if successful
   */
  public boolean add(File file) {
    return add(file, null);
  }

  /**
   * Adds the specified file.
   *
   * @param file	the file to add
   * @param errors 	for storing errors, can be null
   * @return		true if successful
   */
  public boolean add(File file, MessageCollection errors) {
    return add(new File[]{file}, errors);
  }

  /**
   * Checks whether the files can be added.
   *
   * @param files	the files to check
   * @return		true if can be added
   */
  public boolean canAdd(File[] files) {
    return canAdd(files, null);
  }

  /**
   * Checks whether the files can be added.
   *
   * @param files	the files to check
   * @param errors	for collecting errors, can be null
   * @return		true if can be added
   */
  public boolean canAdd(File[] files, MessageCollection errors) {
    StatusCommand	cmd;
    Status 		status;
    String[] 		relPaths;
    String		msg;
    boolean		contained;

    if ((files == null) || (files.length == 0))
      return false;

    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return false;
    }

    relPaths = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), files);
    try {
      cmd = m_Git.status();
      for (String relPath: relPaths)
	cmd.addPath(relPath);
      status = cmd.call();
      contained = false;
      for (String relPath: relPaths)
	contained = contained || status.getUntracked().contains(relPath);
      return contained;
    }
    catch (Exception e) {
      msg = "Failed to query status of repo!";
      if (errors != null)
	errors.add(msg);
      getLogger().log(Level.SEVERE, msg, e);
    }
    return false;
  }

  /**
   * Adds the specified files.
   *
   * @param files	the files to add
   * @return		true if successful
   */
  public boolean add(File[] files) {
    return add(files, null);
  }

  /**
   * Adds the specified files.
   *
   * @param files	the files to add
   * @param errors 	for storing errors, can be null
   * @return		true if successful
   */
  public boolean add(File[] files, MessageCollection errors) {
    String[] 	absPaths;
    String[] 	relPaths;
    AddCommand	cmd;
    String	msg;
    int		i;

    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return false;
    }

    absPaths = new String[files.length];
    for (i = 0; i < files.length; i++)
      absPaths[i] = files[i].getAbsolutePath();
    relPaths = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), files);

    try {
      cmd = m_Git.add();
      for (String relPath: relPaths)
	cmd.addFilepattern(relPath);
      cmd.call();
      return true;
    }
    catch (Exception e) {
      msg = "Failed to add:\n" + Utils.flatten(absPaths, "\n");
      if (errors != null)
	errors.add(msg, e);
      getLogger().log(Level.SEVERE, msg, e);
      if (getShowErrors())
	GUIHelper.showErrorMessage(m_Parent, msg, e);
    }

    return false;
  }

  /**
   * Checks whether file can be committed.
   *
   * @param file	the file to check
   * @return		true if can be committed
   */
  public boolean canCommit(File file) {
    return canCommit(file, null);
  }

  /**
   * Checks whether file can be committed.
   *
   * @param file	the file to check
   * @param errors	for collecting errors, can be null
   * @return		true if can be committed
   */
  public boolean canCommit(File file, MessageCollection errors) {
    return canCommit(new File[]{file}, errors);
  }

  /**
   * Commits the specified file.
   *
   * @param file	the file to commit
   * @param user	the user to use in the commit
   * @param email	the email to use in the commit
   * @param message	the commit message
   * @return		the result string of the commit, null if failed
   */
  public String commit(File file, String user, String email, String message) {
    return commit(file, user, email, message, null);
  }

  /**
   * Commits the specified file.
   *
   * @param file	the file to commit
   * @param user	the user to use in the commit
   * @param email	the email to use in the commit
   * @param message	the commit message
   * @param errors 	for storing errors, can be null
   * @return		the result string of the commit, null if failed
   */
  public String commit(File file, String user, String email, String message, MessageCollection errors) {
    return commit(new File[]{file}, user, email, message, errors);
  }

  /**
   * Checks whether files can be committed.
   *
   * @param files	the files to check
   * @return		true if can be committed
   */
  public boolean canCommit(File[] files) {
    return canCommit(files, null);
  }

  /**
   * Checks whether files can be committed.
   *
   * @param files	the files to check
   * @param errors	for collecting errors, can be null
   * @return		true if can be committed
   */
  public boolean canCommit(File[] files, MessageCollection errors) {
    Status 		status;
    String[] 		relPaths;
    StatusCommand	cmd;
    String		msg;
    boolean		contained;

    if ((files == null) || (files.length == 0))
      return false;

    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return false;
    }

    relPaths = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), files);
    try {
      cmd = m_Git.status();
      for (String relPath: relPaths)
	cmd.addPath(relPath);
      status = cmd.call();
      contained = false;
      for (String relPath: relPaths)
	contained = contained || status.getModified().contains(relPath) || status.getAdded().contains(relPath);
      return contained;
    }
    catch (Exception e) {
      msg = "Failed to query status of repo!";
      if (errors != null)
	errors.add(msg, e);
      getLogger().log(Level.SEVERE, msg, e);
    }
    return false;
  }

  /**
   * Commits the specified files.
   *
   * @param files	the files to commit
   * @param user	the user to use in the commit
   * @param email	the email to use in the commit
   * @param message	the commit message
   * @return		the result string of the commit, null if failed
   */
  public String commit(File[] files, String user, String email, String message) {
    return commit(files, user, email, message, null);
  }

  /**
   * Commits the specified files.
   *
   * @param files	the files to commit
   * @param user	the user to use in the commit
   * @param email	the email to use in the commit
   * @param message	the commit message
   * @param errors 	for storing errors, can be null
   * @return		the result string of the commit, null if failed
   */
  public String commit(File[] files, String user, String email, String message, MessageCollection errors) {
    String		result;
    CommitCommand	cmd;
    String		msg;
    RevCommit 		resultCommit;
    String[] 		relPaths;

    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return null;
    }

    result   = null;
    relPaths = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), files);
    try {
      cmd = m_Git.commit();
      for (String relPath: relPaths)
	cmd.setOnly(relPath);
      resultCommit = cmd
		       .setCommitter(user, email)
		       .setMessage(message)
		       .call();
      result = GitHelper.format(resultCommit, GitHelper.FORMAT_REVCOMMIT_LONG);
    }
    catch (Exception e) {
      msg = "Failed to commit: " + Utils.flatten(relPaths, ", ");
      if (errors != null)
	errors.add(msg, e);
      getLogger().log(Level.SEVERE, msg, e);
      if (getShowErrors())
	GUIHelper.showErrorMessage(m_Parent, msg, e);
    }
    return result;
  }

  /**
   * Checks whether file can be rolled back.
   *
   * @param file	the file to roll back
   * @return		true if rollback possible
   */
  public boolean canRollback(File file) {
    return canRollback(file, null);
  }

  /**
   * Checks whether file can be rolled back.
   *
   * @param file	the file to roll back
   * @param errors	for collecting errors, can be null
   * @return		true if rollback possible
   */
  public boolean canRollback(File file, MessageCollection errors) {
    return canRollback(new File[]{file}, errors);
  }

  /**
   * Performs a rollback on a file,
   *
   * @param file	the file to revert the changes for
   * @return		true if successfully rolled back
   */
  public boolean rollback(File file) {
    return rollback(file, null);
  }

  /**
   * Performs a rollback on a file,
   *
   * @param file	the file to revert the changes for
   * @param errors	for collecting errors, can be null
   * @return		true if successfully rolled back
   */
  public boolean rollback(File file, MessageCollection errors) {
    return rollback(new File[]{file}, errors);
  }

  /**
   * Checks whether files can be rolled back.
   *
   * @param files	the files to roll back
   * @return		true if rollback possible
   */
  public boolean canRollback(File[] files) {
    return canRollback(files, null);
  }

  /**
   * Checks whether files can be rolled back.
   *
   * @param files	the files to roll back
   * @param errors	for collecting errors, can be null
   * @return		true if rollback possible
   */
  public boolean canRollback(File[] files, MessageCollection errors) {
    String[] 		relPaths;
    StatusCommand	cmd;
    Status 		status;
    String		msg;
    boolean		contained;

    if ((files == null) || (files.length == 0))
      return false;

    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return false;
    }

    relPaths = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), files);
    try {
      cmd = m_Git.status();
      for (String relPath: relPaths)
	cmd.addPath(relPath);
      status = cmd.call();
      contained = false;
      for (String relPath: relPaths)
	contained = contained || status.getModified().contains(relPath) || status.getAdded().contains(relPath);
      return contained;
    }
    catch (Exception e) {
      msg = "Failed to query status of repo!";
      if (errors != null)
	errors.add(msg, e);
      getLogger().log(Level.SEVERE, msg, e);
    }
    return false;
  }

  /**
   * Performs a rollback on files.
   *
   * @param files	the files to revert the changes for
   * @return		true if successfully rolled back
   */
  public boolean rollback(File[] files) {
    return rollback(files, null);
  }

  /**
   * Performs a rollback on files.
   *
   * @param files	the files to revert the changes for
   * @param errors	for collecting errors, can be null
   * @return		true if successfully rolled back
   */
  public boolean rollback(File[] files, MessageCollection errors) {
    String[] 		relPaths;
    StatusCommand	cmd;
    Status 		status;
    String		msg;

    if ((files == null) || (files.length == 0))
      return false;

    if (!canProceed()) {
      if (errors != null)
	errors. add("Not configured, cannot proceed!");
      return false;
    }

    relPaths = FileUtils.relativePath(m_Git.getRepository().getWorkTree(), files);

    try {
      cmd = m_Git.status();
      for (String relPath: relPaths)
	cmd.addPath(relPath);
      status = cmd.call();
      for (String relPath: relPaths) {
	if (status.getAdded().contains(relPath))
	  m_Git.reset().setRef(Constants.HEAD).addPath(relPath).call();
	else
	  m_Git.checkout().addPath(relPath).call();
      }
      return true;
    }
    catch (Exception e) {
      msg = "Failed to roll back: " + Utils.flatten(relPaths, ", ");
      if (errors != null)
	errors.add(msg, e);
      getLogger().log(Level.SEVERE, msg, e);
      if (getShowErrors())
	GUIHelper.showErrorMessage(m_Parent, "Failed to roll back:\n" + Utils.flatten(relPaths, ", "), e);
    }
    return false;
  }
}
