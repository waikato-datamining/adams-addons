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
 * Log.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.git;

import adams.core.MessageCollection;

import java.io.File;

/**
 * Performs git log on the specified dir/file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Log
  extends AbstractGitOperation {

  private static final long serialVersionUID = -4283436351630966419L;

  /** the file/dir to perform the log on. */
  protected String m_Target;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs 'git log' on the specified dir/file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "target", "target",
      ".");
  }

  /**
   * Sets the target to create the log for.
   *
   * @param value 	the dir/file
   */
  public void setTarget(String value) {
    m_Target = value;
    reset();
  }

  /**
   * Returns the target to create the log for.
   *
   * @return 		the dir/file
   */
  public String getTarget() {
    return m_Target;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String targetTipText() {
    return "The dir/file to create the log for.";
  }

  /**
   * Checks whether the git operation can be executed.
   *
   * @param errors 	for storing errors, can be null
   * @return 		whether operation can be executed
   */
  @Override
  protected boolean doCanExecute(MessageCollection errors) {
    return m_GitOperation.canLog(new File(m_Target), errors);
  }

  /**
   * Executes the git operation.
   *
   * @param errors 	for storing errors, can be null
   * @return 		the operation output, null if failed
   */
  @Override
  protected String doExecute(MessageCollection errors) {
    return m_GitOperation.log(new File(m_Target), errors);
  }
}
