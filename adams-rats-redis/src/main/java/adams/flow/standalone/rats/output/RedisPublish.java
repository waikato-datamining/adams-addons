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
 * RedisPublish.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.redis.RedisDataType;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.RedisConnection;

/**
 * Publishes the incoming message to the specified channel.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RedisPublish
  extends AbstractRatOutput {

  private static final long serialVersionUID = 2421975872152713034L;

  /** the name of the channel. */
  protected String m_Channel;

  /** the data type. */
  protected RedisDataType m_Type;

  /** the redis connection to use. */
  protected transient RedisConnection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Publishes the incoming message to the specified channel.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"channel", "channel",
	"");

    m_OptionManager.add(
        "type", "type",
        RedisDataType.STRING);
  }

  /**
   * Sets the name of the channel.
   *
   * @param value	the name
   */
  public void setChannel(String value) {
    m_Channel = value;
    reset();
  }

  /**
   * Returns the name of the channel.
   *
   * @return 		the name
   */
  public String getChannel() {
    return m_Channel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String channelTipText() {
    return "The name of the channel to publish on.";
  }

  /**
   * Sets the type of the data.
   *
   * @param value	the type
   */
  public void setType(RedisDataType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of the data.
   *
   * @return 		the type
   */
  public RedisDataType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of the data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "channel", (m_Channel.isEmpty() ? "-empty-" : m_Channel), "channel: ");
    result += QuickInfoHelper.toString(this, "type", m_Type, ", type: ");

    return result;
  }

  /**
   * Returns the type of data that gets accepted.
   *
   * @return the type of data
   */
  @Override
  public Class[] accepts() {
    return new Class[]{m_Type.getDataClass()};
  }

  /**
   * Hook method for performing checks at setup time.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_Connection = (RedisConnection) ActorUtils.findClosestType(m_Owner, RedisConnection.class, true);
      if (m_Connection == null)
        result = "Failed to locate a " + Utils.classToString(RedisConnection.class) + "!";
    }

    return result;
  }

  /**
   * Performs the actual transmission.
   *
   * @return null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String    result;

    try {
      switch (m_Type) {
        case STRING:
          m_Connection.getConnection(m_Type.getCodecClass()).sync().publish(m_Channel, "" + m_Input);
          return null;
        case BYTE_ARRAY:
          m_Connection.getConnection(m_Type.getCodecClass()).sync().publish(m_Channel.getBytes(), ("" + m_Input).getBytes());
          return null;
        default:
          return "Unhandled redis data type: " + m_Type;
      }
    }
    catch (Exception e) {
      result = handleException("Failed to publish on channel '" + m_Channel + "': " + m_Input, e);
    }

    return result;
  }
}
