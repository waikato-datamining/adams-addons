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
 * AbstractPollingRatInput.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone.rats.input;

import adams.core.QuickInfoHelper;

/**
 * Ancestor for rat inputs that perform polling.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPollingRatInput
  extends AbstractRatInput
  implements PollingRatInput {

  private static final long serialVersionUID = -6223623486572377618L;

  /** the waiting period in msec before polling again. */
  protected int m_WaitPoll;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "wait-poll", "waitPoll",
      1000, 0, null);
  }

  /**
   * Sets the number of milli-seconds to wait before polling.
   *
   * @param value	the number of milli-seconds
   */
  public void setWaitPoll(int value) {
    if (getOptionManager().isValid("waitPoll", value)) {
      m_WaitPoll = value;
      reset();
    }
  }

  /**
   * Returns the number of milli-seconds to wait before polling again.
   *
   * @return		the number of milli-seconds
   */
  public int getWaitPoll() {
    return m_WaitPoll;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitPollTipText() {
    return "The number of milli-seconds to wait before polling again.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "waitPoll", m_WaitPoll, "wait poll: ");
  }
}
