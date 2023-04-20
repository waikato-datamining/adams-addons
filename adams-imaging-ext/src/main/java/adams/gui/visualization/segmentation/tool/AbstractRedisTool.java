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
 * AbstractRedisTool.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.segmentation.tool;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.base.BaseHostname;
import adams.data.redis.RedisDataType;
import adams.flow.standalone.RedisConnection;
import adams.gui.core.BaseObjectTextField;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.NumberTextField;
import adams.gui.core.ParameterPanel;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;

/**
 * Ancestor for tools that exchange data via Redis.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @param <O> the outgoing data (String or byte[])
 * @param <I> the incoming data (String or byte[])
 */
public abstract class AbstractRedisTool<O,I>
  extends AbstractToolWithParameterPanel {

  private static final long serialVersionUID = 8374950649752446530L;

  /** the redis host/port. */
  protected BaseObjectTextField<BaseHostname> m_TextRedisHost;

  /** the redis channel for sending. */
  protected BaseTextField m_TextRedisSend;

  /** the redis channel for receiving. */
  protected BaseTextField m_TextRedisReceive;

  /** the redis timeout. */
  protected NumberTextField m_TextRedisTimeout;

  /** the redis host. */
  protected String m_RedisHost;

  /** the redis port. */
  protected int m_RedisPort;

  /** the redis database. */
  protected int m_RedisDB;

  /** the redis send channel. */
  protected String m_RedisSend;

  /** the redis receive channel. */
  protected String m_RedisReceive;

  /** the timeout in milli-seconds. */
  protected int m_RedisTimeout;

  /** the data received via Redis. */
  protected I m_ReceivedData;

  /** the client object. */
  protected transient RedisClient m_Client;

  /** the pub/sub connection object. */
  protected transient StatefulRedisPubSubConnection m_PubSubConnection;

  /** the connection object. */
  protected transient StatefulRedisConnection m_Connection;

  /** the pub/sub listener. */
  protected transient RedisPubSubListener m_PubSubListener;

  /**
   * Checks the parameters before applying them.
   *
   * @return		null if checks passed, otherwise error message (gets displayed in GUI)
   */
  @Override
  protected String checkBeforeApply() {
    String	result;

    result = super.checkBeforeApply();

    if (result == null) {
      if (m_TextRedisSend.getText().trim().isEmpty())
	result = "'Send' channel is empty!";
      else if (m_TextRedisReceive.getText().trim().isEmpty())
	result = "'Receive' channel is empty!";
    }

    return result;
  }

  /**
   * Retrieves the parameters from the GUI.
   */
  protected void retrieveParameters() {
    m_RedisHost    = m_TextRedisHost.getObject().hostnameValue();
    m_RedisPort    = m_TextRedisHost.getObject().portValue(RedisConnection.DEFAULT_PORT);
    m_RedisDB      = 0;
    m_RedisSend    = m_TextRedisSend.getText();
    m_RedisReceive = m_TextRedisReceive.getText();
    m_RedisTimeout = m_TextRedisTimeout.getValue().intValue();
  }

  /**
   * Applies the settings.
   */
  @Override
  protected void doApply() {
    retrieveParameters();

    if (m_Client != null) {
      m_Client.shutdown();
      m_Client = null;
    }

    try {
      m_Client = RedisClient.create(RedisURI.Builder.redis(m_RedisHost, m_RedisPort).withDatabase(m_RedisDB).build());
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(getCanvas(), "Failed to create Redis client: " + m_RedisHost + ":" + m_RedisPort + "/" + m_RedisDB, e);
    }
  }

  /**
   * The channel to send the data on.
   *
   * @return		the redis channel
   */
  protected abstract String getSendChannel();

  /**
   * The channel to receive the data on.
   *
   * @return		the redis channel
   */
  protected abstract String getReceiveChannel();

  /**
   * Returns the default timeout in msec.
   *
   * @return		the timeout
   */
  protected int getDefaultTimeout() {
    return 2000;
  }

  /**
   * Fills the parameter panel with the options.
   *
   * @param paramPanel for adding the options to
   */
  @Override
  protected void addOptions(ParameterPanel paramPanel) {
    m_TextRedisHost = new BaseObjectTextField<>(new BaseHostname("localhost:" + RedisConnection.DEFAULT_PORT));
    m_TextRedisHost.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("Redis host", m_TextRedisHost);

    m_TextRedisSend = new BaseTextField(getSendChannel(), 10);
    m_TextRedisSend.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("- Send", m_TextRedisSend);

    m_TextRedisReceive = new BaseTextField(getReceiveChannel(), 10);
    m_TextRedisReceive.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter("- Receive", m_TextRedisReceive);

    m_TextRedisTimeout = new NumberTextField(NumberTextField.Type.INTEGER, 10);
    m_TextRedisTimeout.setCheckModel(new NumberTextField.BoundedNumberCheckModel(NumberTextField.Type.INTEGER, 1, null, getDefaultTimeout()));
    m_TextRedisTimeout.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    paramPanel.addParameter(" - Timeout (msec)", m_TextRedisTimeout);
  }

  /**
   * Returns the type of data to send.
   *
   * @return		the type of data
   */
  protected abstract RedisDataType getSendType();

  /**
   * Returns the type of data to receive.
   *
   * @return		the type of data
   */
  protected abstract RedisDataType getReceiveType();

  /**
   * Returns a new pub/sub listener for strings.
   *
   * @return		the listener
   */
  protected RedisPubSubListener<String,String> newStringListener() {
    return new RedisPubSubListener<>() {
      @Override
      public void message(String channel, String message) {
	m_ReceivedData = (I) message;
	m_PubSubConnection.removeListener(m_PubSubListener);
	m_PubSubConnection.async().unsubscribe(m_RedisReceive);
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
    return new RedisPubSubListener<>() {
      @Override
      public void message(byte[] channel, byte[] message) {
	if (isLoggingEnabled())
	  getLogger().info("Message on channel '" + new String(channel) + "': " + new String(message));
	m_ReceivedData = (I) message;
	m_PubSubConnection.removeListener(m_PubSubListener);
	m_PubSubConnection.async().unsubscribe(m_RedisReceive.getBytes());
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
   * Method that assembles the data to send.
   *
   * @param errors 	for collecting errors
   * @return		the data to send (String or byte[])
   */
  protected abstract O assembleSendData(MessageCollection errors);

  /**
   * Parses the received data and updates the GUI.
   *
   * @param data	the data to parse (String or byte[])
   * @param errors	for collecting errors
   */
  protected abstract void parseReceivedData(I data, MessageCollection errors);

  /**
   * Finishes up the request.
   */
  protected void finishedRequest() {
    m_ReceivedData = null;
  }

  /**
   * Communicates with DEXTR and updates the canvas.
   */
  protected void sendData() {
    SwingWorker		worker;
    MessageCollection 	errors;
    O 			out;

    m_ReceivedData = null;

    // generate json
    errors = new MessageCollection();
    out    = assembleSendData(errors);
    if (out == null) {
      if (errors.isEmpty())
	GUIHelper.showErrorMessage(getCanvas(), "Failed to generate data to send to Redis!");
      else
	GUIHelper.showErrorMessage(getCanvas(), "Failed to generate data to send to Redis:\n" + errors);
      return;
    }
    else {
      switch (getSendType()) {
	case STRING:
	  if (!(out instanceof String))
	    throw new IllegalStateException("Output data is not of type String, but: " + Utils.classToString(out));
	  break;
	case BYTE_ARRAY:
	  if (!(out instanceof byte[]))
	    throw new IllegalStateException("Output data is not of type byte[], but: " + Utils.classToString(out));
	  break;
	default:
	  throw new IllegalStateException("Unhandled send data type: " + getSendType());
      }
    }

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	long start;

	// send data
	switch (getReceiveType()) {
	  case STRING:
	    m_PubSubListener   = newStringListener();
	    m_PubSubConnection = m_Client.connectPubSub(StringCodec.UTF8);
	    m_PubSubConnection.addListener(m_PubSubListener);
	    m_PubSubConnection.async().subscribe(m_RedisReceive);
	    break;
	  case BYTE_ARRAY:
	    m_PubSubListener   = newBytesListener();
	    m_PubSubConnection = m_Client.connectPubSub(new ByteArrayCodec());
	    m_PubSubConnection.addListener(m_PubSubListener);
	    m_PubSubConnection.async().subscribe(m_RedisReceive.getBytes());
	    break;
	  default:
	    throw new IllegalStateException("Unhandled receive data type: " + getReceiveType());
	}
	switch (getSendType()) {
	  case STRING:
	    m_Connection = m_Client.connect(StringCodec.UTF8);
	    m_Connection.async().publish(m_RedisSend, out);
	    break;
	  case BYTE_ARRAY:
	    m_Connection = m_Client.connect(new ByteArrayCodec());
	    m_Connection.async().publish(m_RedisSend.getBytes(), out);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled send data type: " + getSendType());
	}

	// wait for data to arrive
	errors.clear();
	start = System.currentTimeMillis();
	while ((m_ReceivedData == null) && (System.currentTimeMillis() - start < m_RedisTimeout)) {
	  Utils.wait(AbstractRedisTool.this, 100, 100);
	}
	if (m_ReceivedData != null) {
	  switch (getReceiveType()) {
	    case STRING:
	      if (!(m_ReceivedData instanceof String))
		throw new IllegalStateException("Input data is not of type String, but: " + Utils.classToString(m_ReceivedData));
	      break;
	    case BYTE_ARRAY:
	      if (!(m_ReceivedData instanceof byte[]))
		throw new IllegalStateException("Input data is not of type byte[], but: " + Utils.classToString(m_ReceivedData));
	      break;
	    default:
	      throw new IllegalStateException("Unhandled send data type: " + getReceiveType());
	  }
	  parseReceivedData(m_ReceivedData, errors);
	  if (!errors.isEmpty())
	    GUIHelper.showErrorMessage(getCanvas(), errors.toString());
	}
	return null;
      }

      @Override
      protected void done() {
	super.done();
	finishedRequest();
      }
    };
    worker.execute();
  }

  /**
   * Hook method for when new annotations have been set.
   */
  @Override
  public void annotationsChanged() {
    super.annotationsChanged();
    m_ReceivedData = null;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_Connection != null) {
      m_Connection.close();
      m_Connection = null;
    }

    if (m_PubSubConnection != null) {
      if (m_PubSubListener != null)
	m_PubSubConnection.removeListener(m_PubSubListener);
      switch (getReceiveType()) {
	case STRING:
	  m_PubSubConnection.async().unsubscribe(m_RedisReceive);
	  break;
	case BYTE_ARRAY:
	  m_PubSubConnection.async().unsubscribe(m_RedisReceive.getBytes());
	  break;
	default:
	  throw new IllegalStateException("Unhandled receive data type: " + getReceiveType());
      }
      m_PubSubConnection = null;
      m_PubSubListener   = null;
    }

    if (m_Client != null) {
      m_Client.shutdown();
      m_Client = null;
    }

    super.cleanUp();
  }
}
