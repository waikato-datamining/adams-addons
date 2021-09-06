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
 * RedisSubscribe.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone.rats.input;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.RedisConnection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Executes the specified Redis channel.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RedisSubscribe
  extends AbstractBufferedRatInput {

  /** the name of the channel to subscribe to. */
  protected String m_Channel;

  /** the redis connection to use. */
  protected transient Jedis m_Connection;

  /** the pub/sub handler object. */
  protected transient JedisPubSub m_PubSub;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the specified Redis channel.";
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
    return "The channel to subscribe to.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "channel", m_Channel, "channel: ");
  }

  /**
   * Returns the type of data this scheme generates.
   *
   * @return the type of data
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Hook method for performing checks at setup time.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String setUp() {
    String	        result;
    RedisConnection     conn;
    MessageCollection   errors;

    result = super.setUp();

    if (result == null) {
      conn = (RedisConnection) ActorUtils.findClosestType(m_Owner, RedisConnection.class, true);
      if (conn == null) {
        result = "Failed to locate a " + Utils.classToString(RedisConnection.class) + "!";
      }
      else {
        errors       = new MessageCollection();
        m_Connection = conn.newConnection(errors);
        if (!errors.isEmpty())
          result = errors.toString();
      }
    }

    return result;
  }

  /**
   * Performs the actual reception of data.
   *
   * @return null if successful, otherwise error message
   */
  @Override
  protected String doReceive() {
    m_PubSub = new JedisPubSub() {
      @Override
      public void onSubscribe(String channel, int subscribedChannels) {
        if (isLoggingEnabled())
          getLogger().info("Subscribed to channel: " + channel);
      }
      @Override
      public void onMessage(String channel, String message) {
        if (isLoggingEnabled())
          getLogger().info("Message on channel '" + channel + "': " + message);
        bufferData(message);
      }
      @Override
      public void onUnsubscribe(String channel, int subscribedChannels) {
        if (isLoggingEnabled())
          getLogger().info("Unsubscribed from channel: " + channel);
      }
    };
    new Thread(() -> {
      m_Connection.subscribe(m_PubSub, m_Channel);
    }).start();
    return null;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    if (m_PubSub != null) {
      m_PubSub.unsubscribe(m_Channel);
      m_PubSub = null;
    }
    if (m_Connection != null) {
      m_Connection.close();
      m_Connection = null;
    }
    super.stopExecution();
  }
}
