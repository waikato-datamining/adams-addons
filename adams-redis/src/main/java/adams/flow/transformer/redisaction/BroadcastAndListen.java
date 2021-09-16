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
 * BroadcastAndListen.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.redisaction;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.ActorUtils;
import adams.flow.core.Unknown;
import adams.flow.standalone.RedisConnection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Broadcasts the incoming data to the specified out channel and listens for data to come through on the in channel.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class BroadcastAndListen
  extends AbstractRedisAction {

  /** the channel for sending data. */
  protected String m_ChannelOut;

  /** the channel for receiving data. */
  protected String m_ChannelIn;

  /** the timeout in msec. */
  protected int m_TimeOut;

  /** the pub/sub handler object. */
  protected transient JedisPubSub m_PubSub;

  /** the redis connection to use for subscription. */
  protected transient Jedis m_ConnectionSub;

  /** the redis connection to use for publishing. */
  protected transient Jedis m_ConnectionPub;

  /** the received data. */
  protected transient String m_Data;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Broadcasts the incoming data to the specified out channel and listens for data to come through on the in channel.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"channel-out", "channelOut",
	"");

    m_OptionManager.add(
	"channel-in", "channelIn",
	"");

    m_OptionManager.add(
        "time-out", "timeOut",
        1000, 1, null);
  }

  /**
   * Sets the channel for sending data.
   *
   * @param value	the channel
   */
  public void setChannelOut(String value) {
    m_ChannelOut = value;
    reset();
  }

  /**
   * Returns the channel for sending data.
   *
   * @return 		the channel
   */
  public String getChannelOut() {
    return m_ChannelOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String channelOutTipText() {
    return "The channel to send data to.";
  }

  /**
   * Sets the channel for receiving data.
   *
   * @param value	the channel
   */
  public void setChannelIn(String value) {
    m_ChannelIn = value;
    reset();
  }

  /**
   * Returns the channel for receiving data.
   *
   * @return 		the channel
   */
  public String getChannelIn() {
    return m_ChannelIn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String channelInTipText() {
    return "The channel to receive data from.";
  }

  /**
   * Sets the timeout in msec.
   *
   * @param value	the timeout
   */
  public void setTimeOut(int value) {
    m_TimeOut = value;
    reset();
  }

  /**
   * Returns the timeout in msec.
   *
   * @return 		the timeout
   */
  public int getTimeOut() {
    return m_TimeOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String timeOutTipText() {
    return "The timeout in milli-second for waiting on a response.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String    result;

    result = QuickInfoHelper.toString(this, "channelOut", m_ChannelOut, "out: ");
    result += QuickInfoHelper.toString(this, "channelIn", m_ChannelIn, ", in: ");
    result += QuickInfoHelper.toString(this, "timeOut", m_TimeOut, ", timeout: ");

    return result;
  }

  /**
   * Returns the classes the action accepts as input.
   *
   * @return the classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the classes the action generates as output.
   *
   * @return the classes
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Performs the action.
   *
   * @param connection the Redis connection
   * @param o          the object to process
   * @param errors     for collecting errors
   * @return the generated object
   */
  @Override
  protected Object doExecute(Jedis connection, Object o, MessageCollection errors) {
    RedisConnection   redisConn;
    long              start;

    redisConn = (RedisConnection) ActorUtils.findClosestType(m_FlowContext, RedisConnection.class);
    if (redisConn == null) {
      errors.add("Failed to locate " + Utils.classToString(RedisConnection.class) + " instance!");
      return null;
    }
    m_ConnectionSub = redisConn.newConnection(errors);
    if (!errors.isEmpty())
      return null;
    m_ConnectionPub = redisConn.newConnection(errors);
    if (!errors.isEmpty())
      return null;

    m_Data   = null;
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
	m_Data = message;
	m_PubSub.unsubscribe(m_ChannelIn);
	m_PubSub = null;
      }
      @Override
      public void onUnsubscribe(String channel, int subscribedChannels) {
	if (isLoggingEnabled())
	  getLogger().info("Unsubscribed from channel: " + channel);
      }
    };
    new Thread(() -> {
      m_ConnectionSub.subscribe(m_PubSub, m_ChannelIn);
    }).start();

    start = System.currentTimeMillis();
    m_ConnectionPub.publish(m_ChannelOut, "" + o);
    while ((m_Data == null) && !isStopped() && (System.currentTimeMillis() - start < m_TimeOut)) {
      Utils.wait(this, 100, 100);
    }

    if (m_PubSub != null) {
      m_PubSub.unsubscribe(m_ChannelIn);
      m_PubSub = null;
    }
    if (m_ConnectionPub != null) {
      m_ConnectionPub.close();
      m_ConnectionPub = null;
    }
    if (m_ConnectionSub != null) {
      m_ConnectionSub.close();
      m_ConnectionSub = null;
    }

    return m_Data;
  }
}
