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
 * NonValidatingSSLConnectionFactory.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.connection;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.logging.LoggingHelper;
import com.rabbitmq.client.ConnectionFactory;

/**
 * For encrypting a connection with SSL (performs no validation).
 * Use only for testing/development!
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NonValidatingSSLConnectionFactory
  extends AbstractConnectionFactory {

  private static final long serialVersionUID = 7600989409175939L;

  /** the base connection factory. */
  protected AbstractConnectionFactory m_ConnectionFactory;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For encrypting a connection with SSL (performs no validation).\n"
      + "Use only for testing/development!";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "connection-factory", "connectionFactory",
      new GuestConnectionFactory());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "connectionFactory", m_ConnectionFactory, "connection: ");
  }

  /**
   * Sets the base connection factory to encrypt.
   *
   * @param value	the factory
   */
  public void setConnectionFactory(AbstractConnectionFactory value) {
    m_ConnectionFactory = value;
    reset();
  }

  /**
   * Returns the base connection factory to encrypt.
   *
   * @return		the factory
   */
  public AbstractConnectionFactory getConnectionFactory() {
    return m_ConnectionFactory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String connectionFactoryTipText() {
    return "The base connection factory to encrypt.";
  }

  /**
   * Returns whether a flow context is required.
   *
   * @return		true if required
   */
  @Override
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
    ConnectionFactory	result;
    String		msg;

    msg    = null;
    result = m_ConnectionFactory.generate(errors);
    if (result == null)
      return null;

    try {
      result.useSslProtocol();
    }
    catch (Exception e) {
      msg = LoggingHelper.handleException(this, "Failed to enable use of SSL!", e);
    }
    if (msg != null)
      errors.add(msg);
    if (!errors.isEmpty())
      return null;

    return result;
  }
}
