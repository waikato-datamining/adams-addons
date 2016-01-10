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

/**
 * AbstractRecordingSetupUser.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.flow.core.ActorUtils;

/**
 * Ancestor for standalones that make use of {@link RecordingSetup}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRecordingSetupUser
  extends AbstractStandalone {

  private static final long serialVersionUID = -6296042384127048214L;

  /** the setup to use. */
  protected transient RecordingSetup m_Setup;

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Setup = null;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_Setup = (RecordingSetup) ActorUtils.findClosestType(this, RecordingSetup.class, true);
      if (m_Setup == null)
	result = "No " + RecordingSetup.class.getName() + " found!";
    }

    return result;
  }
}
