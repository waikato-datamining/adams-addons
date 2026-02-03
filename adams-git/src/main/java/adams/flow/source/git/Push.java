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
 * Push.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.git;

import adams.core.MessageCollection;

/**
 * Performs git push.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Push
  extends AbstractGitOperation {

  private static final long serialVersionUID = -4283436351630966419L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs 'git push'.";
  }

  /**
   * Checks whether the git operation can be executed.
   *
   * @param errors for storing errors, can be null
   * @return whether operation can be executed
   */
  @Override
  protected boolean doCanExecute(MessageCollection errors) {
    return m_Operation.canPush(errors);
  }

  /**
   * Executes the git operation.
   *
   * @param errors for storing errors, can be null
   * @return the log output, null if failed
   */
  @Override
  protected String doExecute(MessageCollection errors) {
    return m_Operation.push(errors);
  }
}
