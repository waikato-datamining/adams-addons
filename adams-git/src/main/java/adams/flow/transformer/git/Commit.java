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
 * Commit.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.git;

import adams.core.MessageCollection;
import adams.core.base.BaseText;

import java.io.File;

/**
 * Performs git commit.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Commit
  extends AbstractGitOperation {

  private static final long serialVersionUID = -8077178938362036492L;

  /** the commit message. */
  protected BaseText m_Message;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs 'git commit' on the incoming file(s).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "message", "message",
      new BaseText());
  }

  /**
   * Sets the commit message to use.
   *
   * @param value 	the message
   */
  public void setMessage(BaseText value) {
    m_Message = value;
    reset();
  }

  /**
   * Returns the commit message to use.
   *
   * @return 		the message
   */
  public BaseText getMessage() {
    return m_Message;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String messageTipText() {
    return "The message to use for the commit.";
  }

  /**
   * The type of data accepted as input.
   *
   * @return the accepted input
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class, String[].class, File[].class};
  }

  /**
   * The type of data that is being generated.
   *
   * @return		the generated output
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Checks whether the git operation can be executed.
   *
   * @param input  the input data
   * @param errors for storing errors, can be null
   * @return whether operation can be executed
   */
  @Override
  protected boolean doCanExecute(Object input, MessageCollection errors) {
    File[]	files;

    files = toFiles(input);
    return (files.length > 0) && m_GitOperation.canCommit(files, errors);
  }

  /**
   * Executes the git operation.
   *
   * @param input  	the input data
   * @param errors 	for storing errors, can be null
   * @return		the result of the operation
   */
  @Override
  protected Object doExecute(Object input, MessageCollection errors) {
    File[]	files;

    files = toFiles(input);
    return m_GitOperation.commit(files, m_GitRepo.getUser(), m_GitRepo.getEmail(), m_Message.getValue(), errors);
  }
}
