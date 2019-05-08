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
 * MultiAction.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.channelaction;

import com.rabbitmq.client.Channel;

/**
 * Applies the actions sequentially as long as they are successful.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiAction
  extends AbstractChannelAction {

  private static final long serialVersionUID = -5974936447821777420L;

  /** the actions. */
  protected AbstractChannelAction[] m_Actions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the actions sequentially as long as they are successful.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "action", "actions",
      new AbstractChannelAction[0]);
  }

  /**
   * Sets the actions to run.
   *
   * @param value	the actions
   */
  public void setActions(AbstractChannelAction[] value) {
    m_Actions = value;
    reset();
  }

  /**
   * Returns the actions to run.
   *
   * @return 		the actions
   */
  public AbstractChannelAction[] getActions() {
    return m_Actions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String actionsTipText() {
    return "The channel actions to execute.";
  }

  /**
   * Performs the action.
   *
   * @param channel	the channel to operate on
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doPerformAction(Channel channel) {
    String	result;
    int		i;

    result = null;

    for (i = 0; i < m_Actions.length; i++) {
      result = m_Actions[i].performAction(channel);
      if (result != null) {
	result = "Action #" + (i + 1) + " failed: " + result;
	break;
      }
    }

    return result;
  }
}
