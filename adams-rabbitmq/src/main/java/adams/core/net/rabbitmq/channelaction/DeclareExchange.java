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
 * DeclareQueue.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.channelaction;

import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

/**
 * Declares a exchange.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DeclareExchange
  extends AbstractChannelActionWithWait {

  private static final long serialVersionUID = 4097038378479166882L;

  /** the name of the exchange. */
  protected String m_Exchange;

  /** the type of the exchange. */
  protected BuiltinExchangeType m_Type;

  /** whether the exchange survives a server restart. */
  protected boolean m_Durable;

  /** declaring an autodelete exchange (server will delete it when no longer in use). */
  protected boolean m_AutoDelete;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Declares the specified exchange.";
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
      "type", "type",
      BuiltinExchangeType.FANOUT);

    m_OptionManager.add(
      "durable", "durable",
      false);

    m_OptionManager.add(
      "auto-delete", "autoDelete",
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
    result += QuickInfoHelper.toString(this, "type", m_Type, ", type: ");
    result += QuickInfoHelper.toString(this, "durable", m_Durable, "durable", ", ");
    result += QuickInfoHelper.toString(this, "autoDelete", m_AutoDelete, "auto-delete", ", ");
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
   * Sets the type of the exchange.
   *
   * @param value	the type
   */
  public void setType(BuiltinExchangeType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of the exchange.
   *
   * @return 		the type
   */
  public BuiltinExchangeType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of the exchange.";
  }

  /**
   * Sets whether declaring a durable exchange (the exchange will survive a server restart).
   *
   * @param value	true if durable
   */
  public void setDurable(boolean value) {
    m_Durable = value;
    reset();
  }

  /**
   * Returns whether declaring a durable exchange (the exchange will survive a server restart).
   *
   * @return 		true if durable
   */
  public boolean getDurable() {
    return m_Durable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String durableTipText() {
    return "If enabled, declaring a durable exchange (the exchange will survive a server restart).";
  }

  /**
   * Sets whether declaring an autodelete exchange (server will delete it when no longer in use).
   *
   * @param value	true if auto-delete
   */
  public void setAutoDelete(boolean value) {
    m_AutoDelete = value;
    reset();
  }

  /**
   * Returns whether declaring an autodelete exchange (server will delete it when no longer in use).
   *
   * @return 		true if auto-delete
   */
  public boolean getAutoDelete() {
    return m_AutoDelete;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String autoDeleteTipText() {
    return "If enabled, declaring an autodelete exchange (server will delete it when no longer in use).";
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
        channel.exchangeDeclare(m_Exchange, m_Type, m_Durable, m_AutoDelete, null);
      else
        channel.exchangeDeclareNoWait(m_Exchange, m_Type, m_Durable, false, m_AutoDelete, null);
    }
    catch (Exception e) {
      result = LoggingHelper.handleException(this, "Failed to declare exchange '" + m_Exchange + "'!", e);
    }

    return result;
  }
}
