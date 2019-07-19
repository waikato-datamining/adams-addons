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
 * GuestConnectionFactory.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.connection;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Does not use any authentication.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GuestConnectionFactory
  extends AbstractConnectionFactory {

  private static final long serialVersionUID = 1730696755155054710L;

  /** the host. */
  protected String m_Host;

  /** the port. */
  protected int m_Port;

  /** the virtual host. */
  protected String m_VirtualHost;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Does not use any authentication.";
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
      AMQP.PROTOCOL.PORT, 1, 65535);

    m_OptionManager.add(
      "virtual-host", "virtualHost",
      "/");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "host", (m_Host.length() == 0 ? "??" : m_Host), "guest@");
    result += QuickInfoHelper.toString(this, "port", m_Port, ":");
    result += QuickInfoHelper.toString(this, "virtualHost", m_VirtualHost, "");

    return result;
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
   * Returns the host to connect to.
   *
   * @return		the host name/ip
   */
  public String getHost() {
    return m_Host;
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
   * Returns the port to connect to.
   *
   * @return 		the port
   */
  public int getPort() {
    return m_Port;
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
   * Sets the virtual host to use.
   *
   * @param value	the virtual host
   */
  public void setVirtualHost(String value) {
    m_VirtualHost = value;
    reset();
  }

  /**
   * Returns the virtual host to use.
   *
   * @return		the virtual host
   */
  public String getVirtualHost() {
    return m_VirtualHost;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String virtualHostTipText() {
    return "The virtual host to use on the RabbitMQ server.";
  }

  /**
   * Returns whether a flow context is required.
   *
   * @return		true if required
   */
  protected boolean requiresFlowContext() {
    return false;
  }

  /**
   * Generates the connection factory object.
   *
   * @param errors	for collecting errors
   * @return		the factory, null in case of error
   */
  @Override
  protected ConnectionFactory doGenerate(MessageCollection errors) {
    ConnectionFactory 	result;

    result = new ConnectionFactory();
    result.setHost(m_Host);
    result.setPort(m_Port);
    result.setVirtualHost(m_VirtualHost);

    return result;
  }
}
