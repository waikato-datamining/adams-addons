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

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.redis.RedisDataType;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.RedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * Executes the specified Redis channel.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RedisSubscribe
    extends AbstractBufferedRatInput {

  private static final long serialVersionUID = -5856410764771164718L;

  /** the name of the channel to subscribe to. */
  protected String m_Channel;

  /** the data type. */
  protected RedisDataType m_Type;

  /** the redis connection to use. */
  protected transient RedisConnection m_Connection;

  /** the pub/sub connection object. */
  protected transient StatefulRedisPubSubConnection m_PubSubConnection;

  /** the pub/sub listener. */
  protected transient RedisPubSubListener m_PubSubListener;

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
    return "The channel to subscribe to.";
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
   * Returns the type of data this scheme generates.
   *
   * @return the type of data
   */
  @Override
  public Class generates() {
    return m_Type.getDataClass();
  }

  /**
   * Returns a new pub/sub listener for strings.
   *
   * @return		the listener
   */
  protected RedisPubSubListener<String, String> newStringListener() {
    return new RedisPubSubListener<String, String>() {
      @Override
      public void message(String channel, String message) {
	if (isLoggingEnabled())
	  getLogger().info("Message on channel '" + channel + "': " + message);
	bufferData(message);
      }
      @Override
      public void message(String pattern, String channel, String message) {
	message(channel, message);
      }
      @Override
      public void subscribed(String channel, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Subscribed to channel: " + channel);
      }
      @Override
      public void psubscribed(String pattern, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Subscribed to pattern: " + pattern);
      }
      @Override
      public void unsubscribed(String channel, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Unsubscribed from channel: " + channel);
      }
      @Override
      public void punsubscribed(String pattern, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Unsubscribed from pattern: " + pattern);
      }
    };
  }

  /**
   * Returns a new pub/sub listener for byte arrays.
   *
   * @return		the listener
   */
  protected RedisPubSubListener<byte[], byte[]> newBytesListener() {
    return new RedisPubSubListener<byte[], byte[]>() {
      @Override
      public void message(byte[] channel, byte[] message) {
	if (isLoggingEnabled())
	  getLogger().info("Message on channel '" + new String(channel) + "': " + new String(message));
	bufferData(message);
      }
      @Override
      public void message(byte[] pattern, byte[] channel, byte[] message) {
	message(channel, message);
      }
      @Override
      public void subscribed(byte[] channel, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Subscribed to channel: " + new String(channel));
      }
      @Override
      public void psubscribed(byte[] pattern, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Subscribed to pattern: " + new String(pattern));
      }
      @Override
      public void unsubscribed(byte[] channel, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Unsubscribed from channel: " + new String(channel));
      }
      @Override
      public void punsubscribed(byte[] pattern, long count) {
	if (isLoggingEnabled())
	  getLogger().info("Unsubscribed from pattern: " + new String(pattern));
      }
    };
  }

  /**
   * Hook method for performing checks at setup time.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  public String setUp() {
    String	        result;

    result = super.setUp();

    if (result == null) {
      m_Connection = (RedisConnection) ActorUtils.findClosestType(m_Owner, RedisConnection.class, true);
      if (m_Connection == null)
	result = "Failed to locate a " + Utils.classToString(RedisConnection.class) + "!";
    }

    return result;
  }

  /**
   * Initializes the reception.
   *
   * @return		null if successfully initialized, otherwise error message
   */
  @Override
  public String initReception() {
    String	result;

    result = super.initReception();

    if (result == null) {
      switch (m_Type) {
	case STRING:
	  m_PubSubListener   = newStringListener();
	  m_PubSubConnection = m_Connection.getClient().connectPubSub(StringCodec.UTF8);
	  m_PubSubConnection.addListener(m_PubSubListener);
	  m_PubSubConnection.async().subscribe(m_Channel);
	  break;
	case BYTE_ARRAY:
	  m_PubSubListener   = newBytesListener();
	  m_PubSubConnection = m_Connection.getClient().connectPubSub(new ByteArrayCodec());
	  m_PubSubConnection.addListener(m_PubSubListener);
	  m_PubSubConnection.async().subscribe(m_Channel.getBytes());
	  break;
	default:
	  result = "Unhandled redis data type (setting up pub/sub): " + m_Type;
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
    return null;
  }

  /**
   * Cleans up the Redis data structures.
   */
  protected void cleanUpRedis() {
    if (m_PubSubConnection != null) {
      if (m_PubSubListener != null)
	m_PubSubConnection.removeListener(m_PubSubListener);
      switch (m_Type) {
	case STRING:
	  m_PubSubConnection.async().unsubscribe(m_Channel);
	  break;
	case BYTE_ARRAY:
	  m_PubSubConnection.async().unsubscribe(m_Channel.getBytes());
	  break;
	default:
	  throw new IllegalStateException("Unhandled redis data type (unsubscribing): " + m_Type);
      }
    }
    m_PubSubConnection = null;
    m_PubSubListener   = null;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    cleanUpRedis();
    super.stopExecution();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    cleanUpRedis();
    super.cleanUp();
  }
}
