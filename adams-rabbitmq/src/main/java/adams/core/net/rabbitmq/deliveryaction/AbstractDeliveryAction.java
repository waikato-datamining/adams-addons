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
 * AbstractDeliveryAction.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.deliveryaction;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import com.rabbitmq.client.Channel;

/**
 * Ancestor for message delivery actions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDeliveryAction
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = -3800385190201409905L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * The default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method for performing checks before executing the action.
   *
   * @param channel	the channel to check
   * @return		null if successful check, otherwise error message
   */
  protected String check(Channel channel) {
    if (channel == null)
      return "No channel provided!";
    return null;
  }

  /**
   * Performs the action.
   *
   * @param channel	the channel to operate on
   * @param tag 	the delivery tag
   * @return		null if successful, otherwise error message
   */
  protected abstract String doPerformAction(Channel channel, long tag);

  /**
   * Performs the action.
   *
   * @param channel	the channel to operate on
   * @param tag 	the delivery tag
   * @return		null if successful, otherwise error message
   */
  public String performAction(Channel channel, long tag) {
    String	result;

    result = check(channel);
    if (result == null)
      result = doPerformAction(channel, tag);

    return result;
  }
}
