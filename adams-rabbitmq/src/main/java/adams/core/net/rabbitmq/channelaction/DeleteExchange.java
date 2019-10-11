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
 * DeleteQueue.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.channelaction;

import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import com.rabbitmq.client.Channel;

/**
 * Deletes a exchange.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DeleteExchange
  extends AbstractChannelActionWithWait {

  private static final long serialVersionUID = 4097038378479166882L;

  /** the name of the exchange. */
  protected String m_Exchange;

  /** whether the exchange gets only deleted if not used. */
  protected boolean m_IfUnused;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Deletes the specified exchange.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "exchange", "exchange",
      "");

    m_OptionManager.add(
      "if-unused", "ifUnused",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "exchange", (m_Exchange.isEmpty() ? "-empty-" : m_Exchange), "exchange: ");
    result += QuickInfoHelper.toString(this, "ifUnused", m_IfUnused, "if unused", ", ");
    result += QuickInfoHelper.toString(this, "wait", m_Wait, "wait", ", ");

    return result;
  }

  /**
   * Sets the name of the exchange.
   *
   * @param value	the name
   */
  public void setExchange(String value) {
    m_Exchange = value;
    reset();
  }

  /**
   * Returns the name of the exchange.
   *
   * @return 		the name
   */
  public String getExchange() {
    return m_Exchange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String exchangeTipText() {
    return "The name of the exchange.";
  }

  /**
   * Sets whether to delete the exchange only if not used.
   *
   * @param value	true if only if empty
   */
  public void setIfUnused(boolean value) {
    m_IfUnused = value;
    reset();
  }

  /**
   * Returns whether to delete the exchange only if not used.
   *
   * @return 		true if only if empty
   */
  public boolean getIfUnused() {
    return m_IfUnused;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String ifUnusedTipText() {
    return "If enabled, only gets deleted if not used.";
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

    result = null;

    try {
      if (m_Wait)
	channel.exchangeDelete(m_Exchange, m_IfUnused);
      else
	channel.exchangeDeleteNoWait(m_Exchange, m_IfUnused);
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to delete exchange '" + m_Exchange + "'!", e);
    }

    return result;
  }
}
