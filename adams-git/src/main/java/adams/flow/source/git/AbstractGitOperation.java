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
 * AbstractGitRepo.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.git;

import adams.core.MessageCollection;
import adams.core.git.GitOperation;
import adams.core.option.AbstractOptionHandler;
import adams.flow.standalone.GitRepo;

/**
 * Ancestor for git operations.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractGitOperation
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 1538753872785242893L;

  /** the GitRepo instance to use. */
  protected GitRepo m_GitRepo;

  /** the git operation to use. */
  protected GitOperation m_GitOperation;

  /**
   * Sets the GitRepo instance to use.
   *
   * @param value	the instance to use
   */
  public void setGitRepo(GitRepo value) {
    m_GitRepo      = value;
    m_GitOperation = new GitOperation();
    m_GitOperation.setGit(m_GitRepo.getGit());
  }

  /**
   * Returns the GitRepo instancee in use.
   *
   * @return		the instance in use
   */
  public GitRepo getGitRepo() {
    return m_GitRepo;
  }

  /**
   * Hook method for checking.
   */
  protected void check(MessageCollection errors) {
    if (m_GitRepo == null)
      errors.add("No GitRepo instance set!");
  }

  /**
   * Checks whether the git operation can be executed.
   *
   * @param errors 	for storing errors, can be null
   * @return		whether operation can be executed
   */
  protected abstract boolean doCanExecute(MessageCollection errors);

  /**
   * Checks whether the git operation can be executed.
   *
   * @param errors 	for storing errors, can be null
   * @return		whether operation can be executed
   */
  public boolean canExecute(MessageCollection errors) {
    check(errors);
    if (!errors.isEmpty())
      return false;
    else
      return doCanExecute(errors);
  }

  /**
   * Executes the git operation.
   *
   * @param errors 	for storing errors, can be null
   * @return		the operation output, null if failed
   */
  protected abstract String doExecute(MessageCollection errors);

  /**
   * Executes the git operation.
   *
   * @param errors 	for storing errors, can be null
   * @return		the operation output, null if failed
   */
  public String execute(MessageCollection errors) {
    check(errors);
    if (!errors.isEmpty())
      return null;
    else
      return doExecute(errors);
  }
}
