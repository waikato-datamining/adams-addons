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
import adams.data.redis.RedisDataType;
import adams.flow.core.Unknown;
import adams.flow.standalone.RedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * Broadcasts the incoming data to the specified out channel and listens for data to come through on the in channel.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class BroadcastAndListen
    extends AbstractRedisAction {

  private static final long serialVersionUID = -6976434112891561358L;

  /** the channel for sending data. */
  protected String m_ChannelOut;

  /** the data type for the out channel. */
  protected RedisDataType m_TypeOut;

  /** the channel for receiving data. */
  protected String m_ChannelIn;

  /** the data type for the incoming channel. */
  protected RedisDataType m_TypeIn;

  /** the timeout in msec. */
  protected int m_TimeOut;

  /** the pub/sub connection object. */
  protected transient StatefulRedisPubSubConnection m_PubSubConnection;

  /** the pub/sub listener. */
  protected transient RedisPubSubListener m_PubSubListener;

  /** the received data. */
  protected transient Object m_Data;

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
	"type-out", "typeOut",
	RedisDataType.STRING);

    m_OptionManager.add(
	"channel-in", "channelIn",
	"");

    m_OptionManager.add(
	"type-in", "typeIn",
	RedisDataType.STRING);

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
   * Sets the type of the data for the out channel.
   *
   * @param value	the type
   */
  public void setTypeOut(RedisDataType value) {
    m_TypeOut = value;
    reset();
  }

  /**
   * Returns the type of the data for the out channel.
   *
   * @return 		the type
   */
  public RedisDataType getTypeOut() {
    return m_TypeOut;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String typeOutTipText() {
    return "The type of the data for the outgoing data.";
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
   * Sets the type of the data for the in channel.
   *
   * @param value	the type
   */
  public void setTypeIn(RedisDataType value) {
    m_TypeIn = value;
    reset();
  }

  /**
   * Returns the type of the data for the in channel.
   *
   * @return 		the type
   */
  public RedisDataType getTypeIn() {
    return m_TypeIn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String typeInTipText() {
    return "The type of the data for the incoming data.";
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
    result += QuickInfoHelper.toString(this, "typeOut", m_TypeOut, "/");
    result += QuickInfoHelper.toString(this, "channelIn", m_ChannelIn, ", in: ");
    result += QuickInfoHelper.toString(this, "typeIn", m_TypeIn, "/");
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
    return new Class[]{m_TypeOut.getDataClass()};
  }

  /**
   * Returns the classes the action generates as output.
   *
   * @return the classes
   */
  @Override
  public Class generates() {
    return m_TypeIn.getDataClass();
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
	m_Data = message;
	m_PubSubConnection.removeListener(m_PubSubListener);
	m_PubSubConnection.async().unsubscribe(m_ChannelIn);
	m_PubSubConnection = null;
	m_PubSubListener   = null;
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
	m_Data = message;
	m_PubSubConnection.removeListener(m_PubSubListener);
	m_PubSubConnection.async().unsubscribe(m_ChannelIn.getBytes());
	m_PubSubConnection = null;
	m_PubSubListener   = null;
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
   * Performs the action.
   *
   * @param connection the Redis connection
   * @param o          the object to process
   * @param errors     for collecting errors
   * @return the generated object
   */
  @Override
  protected Object doExecute(RedisConnection connection, Object o, MessageCollection errors) {
    long 	start;

    m_Data = null;
    start  = System.currentTimeMillis();
    switch (m_TypeOut) {
      case STRING:
        m_PubSubListener   = newStringListener();
        m_PubSubConnection = connection.getClient().connectPubSub(StringCodec.UTF8);
	m_PubSubConnection.addListener(m_PubSubListener);
	m_PubSubConnection.async().subscribe(m_ChannelIn);
	connection.getConnection(m_TypeOut.getCodecClass()).async().publish(m_ChannelOut, o);
	break;
      case BYTE_ARRAY:
        m_PubSubListener   = newBytesListener();
	m_PubSubConnection = connection.getClient().connectPubSub(new ByteArrayCodec());
	m_PubSubConnection.addListener(m_PubSubListener);
	m_PubSubConnection.async().subscribe(m_ChannelIn.getBytes());
	connection.getConnection(m_TypeOut.getCodecClass()).async().publish(m_ChannelOut.getBytes(), o);
	break;
      default:
	errors.add("Unhandled redis data type (setting up pub/sub): " + m_TypeOut);
	return null;
    }
    while ((m_Data == null) && !isStopped() && (System.currentTimeMillis() - start < m_TimeOut)) {
      Utils.wait(this, 100, 100);
    }

    if (m_PubSubConnection != null) {
      if (m_PubSubListener != null)
	m_PubSubConnection.removeListener(m_PubSubListener);
      switch (m_TypeOut) {
	case STRING:
	  m_PubSubConnection.async().unsubscribe(m_ChannelIn);
	  break;
	case BYTE_ARRAY:
	  m_PubSubConnection.async().unsubscribe(m_ChannelIn.getBytes());
	  break;
	default:
	  errors.add("Unhandled redis data type (unsubscribing): " + m_TypeOut);
	  return null;
      }
    }
    m_PubSubConnection = null;
    m_PubSubListener   = null;

    return m_Data;
  }
}
