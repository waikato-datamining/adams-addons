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
 * RedisConnection.java
 * Copyright (C) 2021-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Defines a connection to a Redis server.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: RedisConnection
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-host &lt;java.lang.String&gt; (property: host)
 * &nbsp;&nbsp;&nbsp;The host (name&#47;IP address) to connect to.
 * &nbsp;&nbsp;&nbsp;default: localhost
 * </pre>
 *
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;The port to connect to.
 * &nbsp;&nbsp;&nbsp;default: 6379
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 65535
 * </pre>
 *
 * <pre>-database &lt;int&gt; (property: database)
 * &nbsp;&nbsp;&nbsp;The database to use (usually 0).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 65535
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RedisConnection
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -1726172998200420556L;

  public final static int DEFAULT_PORT = 6379;

  /** the host. */
  protected String m_Host;

  /** the port. */
  protected int m_Port;

  /** the database. */
  protected int m_Database;

  /** the client object. */
  protected transient RedisClient m_Client;

  /** the connection object. */
  protected transient Map<Class, StatefulRedisConnection> m_Connections;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Defines a connection to a Redis server.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "host", "host",
      "localhost");

    m_OptionManager.add(
      "port", "port",
      DEFAULT_PORT, 1, 65535);

    m_OptionManager.add(
      "database", "database",
      0, 0, 65535);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String  result;

    result = QuickInfoHelper.toString(this, "host", m_Host);
    result += QuickInfoHelper.toString(this, "port", m_Port, ":");
    result += QuickInfoHelper.toString(this, "database", m_Database, "/");

    return result;
  }

  /**
   * Returns the host to connect to.
   *
   * @return		the host name/ip
   */
  public String getHost() {
    return m_Host;
  }

  /**
   * Sets the host to connect to.
   *
   * @param value	the host name/ip
   */
  public void setHost(String value) {
    m_Host = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hostTipText() {
    return "The host (name/IP address) to connect to.";
  }

  /**
   * Returns the port to connect to.
   *
   * @return 		the port
   */
  public int getPort() {
    return m_Port;
  }

  /**
   * Sets the port to connect to.
   *
   * @param value	the port
   */
  public void setPort(int value) {
    if (getOptionManager().isValid("port", value)) {
      m_Port = value;
      reset();
    }
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String portTipText() {
    return "The port to connect to.";
  }

  /**
   * Returns the database to use.
   *
   * @return		the database ID
   */
  public int getDatabase() {
    return m_Database;
  }

  /**
   * Sets the database to use.
   *
   * @param value	the database ID
   */
  public void setDatabase(int value) {
    m_Database = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String databaseTipText() {
    return "The database to use (usually 0).";
  }

  /**
   * Creates a new connection.
   *
   * @param errors    for collecting errors
   * @return          the connection, null if failed to create
   */
  public StatefulRedisConnection<String, String> newConnection(MessageCollection errors) {
    try {
      return m_Client.connect();
    }
    catch (Exception e) {
      errors.add("Failed to create new connection!", e);
      return null;
    }
  }

  /**
   * Returns the client in use.
   *
   * @return		the client object
   */
  public RedisClient getClient() {
    return m_Client;
  }

  /**
   * Returns the connection in use.
   *
   * @param codec	the codec to use
   * @return		the connection object
   */
  public StatefulRedisConnection getConnection(Class codec) {
    if (m_Connections.containsKey(codec))
      return m_Connections.get(codec);
    if (codec == ByteArrayCodec.class) {
      m_Connections.put(codec, m_Client.connect(new ByteArrayCodec()));
      return m_Connections.get(codec);
    }
    if (codec == StringCodec.class) {
      m_Connections.put(codec, m_Client.connect(StringCodec.UTF8));
      return m_Connections.get(codec);
    }
    throw new IllegalStateException("Unhandled codec!");
  }

  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;
    try {
      m_Client = RedisClient.create(RedisURI.Builder.redis(m_Host, m_Port).withDatabase(m_Database).build());
    }
    catch (Exception e) {
      result = handleException("Failed to create Redis client: " + m_Host + ":" + m_Port + "/" + m_Database, e);
    }

    m_Connections = new HashMap<>();

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    if (m_Connections != null) {
      for (Class codec: m_Connections.keySet())
        m_Connections.get(codec).close();
      m_Connections = null;
    }
    if (m_Client != null) {
      m_Client.shutdown();
      m_Client = null;
    }

    super.wrapUp();
  }
}
