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
 * GitSession.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.core.git;

import adams.core.CleanUpHandler;
import adams.core.base.BasePassword;
import adams.core.io.FileUtils;
import adams.core.logging.CustomLoggingLevelObject;
import adams.gui.core.GUIHelper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.sshd.IdentityPasswordProvider;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the repositories during a session.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class GitSession
  extends CustomLoggingLevelObject
  implements CleanUpHandler {

  private static final long serialVersionUID = -1973439331943188604L;

  /** the git repos. */
  protected Map<File, Git> m_Repos;

  /** the file repo/false relation. */
  protected Map<File, Object> m_Controlled;

  /** the singleton. */
  protected static GitSession m_Singleton;

  /** for keeping track of passwords per ssh key. */
  protected Map<URIish,char[]> m_Passwords;

  /**
   * Initializes the session object.
   */
  protected GitSession() {
    m_Repos      = new HashMap<>();
    m_Controlled = new HashMap<>();
    m_Passwords  = new HashMap<>();
    setLoggingLevel(GitSettingsHelper.getSingleton().getLoggingLevel());
  }

  /**
   * Adds the git repo.
   *
   * @param git		the repo to add
   */
  public void addRepo(Git git) {
    m_Repos.put(git.getRepository().getWorkTree(), git);
  }

  /**
   * Returns the currently managed repos.
   *
   * @return		the repos
   */
  public Collection<Git> repos() {
    return m_Repos.values();
  }

  /**
   * Returns the repo for the specified dir/file.
   *
   * @param path	the dir/file to get the repo for
   * @return		the repo, null if no repo (yet) available
   */
  public Git repoFor(File path) {
    Git		result;
    String 	pathStr;
    String	parentStr;

    result  = null;
    pathStr = FileUtils.useForwardSlashes(path.getAbsolutePath());
    for (File parent: m_Repos.keySet()) {
      parentStr = FileUtils.useForwardSlashes(parent.getAbsolutePath()) + "/";
      if (pathStr.startsWith(parentStr)) {
	result = m_Repos.get(parent);
	break;
      }
    }

    return result;
  }

  /**
   * Checks whether the dir/file is inside a git-managed directory sub-tree.
   * Automatically calls {@link #addRepo(Git)} when it is within a git-controlled sub-tree.
   *
   * @param path	the dir/file to check
   * @return		true if under control
   * @see		#addRepo(Git) 
   */
  public boolean isWithinRepo(File path) {
    RepositoryBuilder 	builder;
    Git			git;

    if (!m_Controlled.containsKey(path.getAbsoluteFile())) {
      // already a repo present?
      git = repoFor(path);

      // new repo?
      if (git == null) {
	try {
	  if (path.isDirectory())
	    builder = new RepositoryBuilder().findGitDir(path.getAbsoluteFile());
	  else
	    builder = new RepositoryBuilder().findGitDir(path.getParentFile().getAbsoluteFile());
	  git = new Git(builder.build());
	  getLogger().info("path: " + path);
	  getLogger().info("-> git repo dir: " + git.getRepository().getWorkTree());
	  addRepo(git);
	}
	catch (Exception e) {
	  // expecting RepositoryNotFoundException if not a repo
	  m_Controlled.put(path, false);
	}
      }

      // update cache
      if (git != null)
	m_Controlled.put(path.getAbsoluteFile(), git);
      else
	m_Controlled.put(path.getAbsoluteFile(), false);
    }

    return (m_Controlled.get(path.getAbsoluteFile()) instanceof Git);
  }

  /**
   * Returns the password for the ssh key.
   *
   * @param uri		the URI of the ssh key
   * @return		the password or null if none stored yet
   */
  public char[] getPassword(URIish uri) {
    return m_Passwords.get(uri);
  }

  /**
   * Returns the password for the ssh key and prompts the user if not yet stored.
   *
   * @param uri		the URI of the ssh key
   * @return		the key or null if none stored yet
   */
  public char[] getPasswordOrPrompt(URIish uri) {
    char[]		result;
    BasePassword password;

    result = m_Passwords.get(uri);

    if (result == null) {
      password = GUIHelper.showPasswordDialog(null, null, "SSH key:\n" + uri);
      if (password != null)
	setPassword(uri, password.getValue().toCharArray());
      result = getPassword(uri);
    }

    return result.clone();
  }

  /**
   * Returns a sshd session factory that will prompt the user for
   * a password for each ssh key in use.
   *
   * @return		the factory
   */
  public SshdSessionFactory getSshdSessionFactory() {
    return new SshdSessionFactoryBuilder()
	     .setPreferredAuthentications("publickey")
	     .setHomeDirectory(FS.DETECTED.userHome())
	     .setSshDirectory(GitSettingsHelper.getSingleton().getSshDirFile().getAbsoluteFile())
	     .setKeyPasswordProvider(cp -> new IdentityPasswordProvider(cp) {
	       @Override
	       protected char[] getPassword(URIish uri, String message) {
		 getLogger().info("Key password provider for URI: " + uri);
		 char[] result = GitSession.getSingleton().getPasswordOrPrompt(uri);
		 if (result != null)
		   result = result.clone();   // in case it gets zeroed
		 return result;
	       }
	     }).build(null);
  }

  /**
   * Sets the password for the ssh key.
   *
   * @param uri		the URI of the ssh key
   * @param password    the password for the key
   */
  public void setPassword(URIish uri, char[] password) {
    m_Passwords.put(uri, password);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    for (Git git: m_Repos.values())
      git.close();
    for (char[] password: m_Passwords.values())
      Arrays.fill(password, '0');
    m_Repos.clear();
    m_Controlled.clear();
    m_Passwords.clear();
  }

  /**
   * Returns the session singleton.
   *
   * @return		the singleton
   */
  public static synchronized GitSession getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new GitSession();
    return m_Singleton;
  }
}
