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
 * Clone.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.git;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.git.GitOperation;
import adams.core.io.PlaceholderDirectory;

/**
 * Performs git clone of the specified repo.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Clone
  extends AbstractGitOperation {

  private static final long serialVersionUID = -4283436351630966419L;

  /** the repository to clone. */
  protected String m_Repository;
  
  /** the directory to clone into. */
  protected PlaceholderDirectory m_Directory;
  
  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs 'git clone' of the specified repository.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "repository", "repository",
      "");

    m_OptionManager.add(
      "directory", "directory",
      new PlaceholderDirectory());
  }

  /**
   * Sets the URI of the repository to clone.
   *
   * @param value 	the URI
   */
  public void setRepository(String value) {
    m_Repository = value;
    reset();
  }

  /**
   * Returns the URI of the repository to clone.
   *
   * @return 		the URI
   */
  public String getRepository() {
    return m_Repository;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String repositoryTipText() {
    return "The URI of the repository to clone.";
  }

  /**
   * Sets the directory to clone into.
   *
   * @param value 	the directory
   */
  public void setDirectory(PlaceholderDirectory value) {
    m_Directory = value;
    reset();
  }

  /**
   * Returns the directory to clone into.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getDirectory() {
    return m_Directory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String directoryTipText() {
    return "The local directory to clone into, must not exit.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "repository", (m_Repository.isEmpty() ? "-none-" : m_Repository), "repo: ");
    result += QuickInfoHelper.toString(this, "directory", m_Directory, ", dir: ");

    return result;
  }

  /**
   * Whether a GitRepo instance is required.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresGitRepo() {
    return false;
  }

  /**
   * Returns the type of data of the output.
   *
   * @return		the type of data
   */
  @Override
  public Class[] generates() {
    return new Class[]{Boolean.class};
  }

  /**
   * Checks whether the git operation can be executed.
   *
   * @param errors 	for storing errors, can be null
   * @return 		whether operation can be executed
   */
  @Override
  protected boolean doCanExecute(MessageCollection errors) {
    return GitOperation.canClone(m_Repository, m_Directory, errors);
  }

  /**
   * Executes the git operation.
   *
   * @param errors 	for storing errors, can be null
   * @return 		the operation output, null if failed
   */
  @Override
  protected Boolean doExecute(MessageCollection errors) {
    org.eclipse.jgit.api.Git	git;

    git = GitOperation.clone(m_Repository, m_Directory, errors);

    if (git != null)
      return true;
    else
      return null;
  }
}
