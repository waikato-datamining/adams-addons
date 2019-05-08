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
 * AbstractChannelActionWithWait.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.channelaction;

/**
 * Ancestor for actions that can wait or not.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractChannelActionWithWait
  extends AbstractChannelAction {

  private static final long serialVersionUID = 1931449723571176538L;

  /** whether to wait. */
  protected boolean m_Wait;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "wait", "wait",
      true);
  }

  /**
   * Sets whether to wait for call to finish.
   *
   * @param value	true if to wait
   */
  public void setWait(boolean value) {
    m_Wait = value;
    reset();
  }

  /**
   * Returns whether to wait for call to finish.
   *
   * @return 		true if to wait
   */
  public boolean getWait() {
    return m_Wait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String waitTipText() {
    return "If enabled, executes the call and waits for it to finish.";
  }
}
