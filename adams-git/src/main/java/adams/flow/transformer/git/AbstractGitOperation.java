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
 * AbstractGitOperation.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.git;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.git.GitOperation;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Compatibility;
import adams.flow.standalone.GitRepo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for git operations.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractGitOperation
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 1538753872785242893L;

  /** the GitRepo instance to use. */
  protected GitRepo m_GitRepo;

  /** the git operation instance to use. */
  protected GitOperation m_GitOperation;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Whether a GitRepo instance is required.
   *
   * @return		true if required
   */
  public boolean requiresGitRepo() {
    return true;
  }

  /**
   * Sets the GitRepo instance to use.
   *
   * @param value	the instance to use
   */
  public void setGitRepo(GitRepo value) {
    m_GitRepo      = value;
    m_GitOperation = new GitOperation();
    m_GitOperation.setGit(m_GitRepo.getGit());
    m_GitOperation.setShowErrors(false);
  }

  /**
   * Returns the GitRepo instance in use.
   *
   * @return		the instance in use
   */
  public GitRepo getGitRepo() {
    return m_GitRepo;
  }

  /**
   * The type of data accepted as input.
   *
   * @return		the accepted input
   */
  public abstract Class[] accepts();

  /**
   * The type of data that is being generated.
   *
   * @return		the generated output
   */
  public abstract Class[] generates();

  /**
   * Hook method for checking.
   *
   * @param input 	the input data
   */
  protected void check(Object input, MessageCollection errors) {
    Compatibility	comp;

    if (requiresGitRepo()) {
      if (m_GitOperation == null)
	errors.add("No GitOperation instance set!");
    }

    if (input == null)
      errors.add("No input data provided!");

    if (input != null) {
      comp = new Compatibility();
      if (comp.isCompatible(new Class[]{input.getClass()}, accepts()))
	errors.add("Only accepts " + Utils.classesToString(accepts()) + " but received " + Utils.classToString(input.getClass()) + " as input!");
    }
  }

  /**
   * Checks whether the git operation can be executed.
   *
   * @param input 	the input data
   * @param errors 	for storing errors, can be null
   * @return		whether operation can be executed
   */
  protected abstract boolean doCanExecute(Object input, MessageCollection errors);

  /**
   * Checks whether the git operation can be executed.
   *
   * @param input 	the input data
   * @param errors 	for storing errors, can be null
   * @return		whether operation can be executed
   */
  public boolean canExecute(Object input, MessageCollection errors) {
    check(input, errors);
    if (!errors.isEmpty())
      return false;
    else
      return doCanExecute(input, errors);
  }

  /**
   * Executes the git operation.
   *
   * @param input 	the input data
   * @param errors 	for storing errors, can be null
   * @return		the result of the operation
   */
  protected abstract Object doExecute(Object input, MessageCollection errors);

  /**
   * Executes the git operation.
   *
   * @param input 	the input data
   * @param errors 	for storing errors, can be null
   * @return		the result of the operation
   */
  public Object execute(Object input,MessageCollection errors) {
    check(input, errors);
    if (!errors.isEmpty())
      return false;
    else
      return doExecute(input, errors);
  }

  /**
   * Converts String/File and String[]/File[] to a file array.
   *
   * @param input	the input to process
   * @return		the generated output
   */
  public static File[] toFiles(Object input) {
    List<File> files;

    files = new ArrayList<>();
    if (input instanceof String) {
      files.add(new PlaceholderFile((String) input).getAbsoluteFile());
    }
    else if (input instanceof String[]) {
      for (String f: (String[]) input)
	files.add(new PlaceholderFile(f).getAbsoluteFile());
    }
    // also covers PlaceholderFile/PlaceholderDirectory
    else if (input instanceof File) {
      files.add(((File) input).getAbsoluteFile());
    }
    else if (input instanceof File[]) {
      for (File f: (File[]) input)
	files.add(f.getAbsoluteFile());
    }

    return files.toArray(new File[0]);
  }
}
