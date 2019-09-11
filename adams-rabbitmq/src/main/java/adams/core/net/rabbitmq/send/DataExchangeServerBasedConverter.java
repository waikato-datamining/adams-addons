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
 * DataExchangeServerBasedConverter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.net.rabbitmq.send;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseURL;
import adams.flow.core.ActorUtils;
import adams.flow.rest.dex.DataExchangeHelper;
import adams.flow.rest.dex.clientauthentication.AbstractClientAuthentication;
import adams.flow.rest.dex.clientauthentication.NoAuthentication;
import adams.flow.standalone.DataExchangeServerConnection;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Instead of sending potentially large payloads via a RabbitMQ, this
 * meta-converter uploads the payload to the specified data exchange server
 * and only sends the token for obtaining the data again via RabbitMQ.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DataExchangeServerBasedConverter
  extends AbstractConverter {

  private static final long serialVersionUID = -736244897402323379L;

  /** the base converter. */
  protected AbstractConverter m_Converter;

  /** whether to use a DEX connection from the flow context. */
  protected boolean m_UseFlowContextConnection;

  /** the connection in use. */
  protected transient DataExchangeServerConnection m_Connection;

  /** the data exchange server to use. */
  protected BaseURL m_Server;

  /** the authentication to use. */
  protected AbstractClientAuthentication m_Authentication;

  /** the object mapper in use. */
  protected transient ObjectMapper m_Mapper;

  /** the actual server URL. */
  protected transient BaseURL m_ActualURL;

  /** the actual authentication in use. */
  protected AbstractClientAuthentication m_ActualAuthentication;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Instead of sending potentially large payloads via a RabbitMQ, this "
      + "meta-converter uploads the payload to the specified data exchange server "
      + "and only sends the token for obtaining the data again via RabbitMQ.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "converter", "converter",
      new BinaryConverter());

    m_OptionManager.add(
      "use-flow-context-connection", "useFlowContextConnection",
      false);

    m_OptionManager.add(
      "server", "server",
      new BaseURL("http://localhost:8080/upload"));

    m_OptionManager.add(
      "authentication", "authentication",
      new NoAuthentication());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Mapper = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "converter", m_Converter, "converter: ");
    if (m_UseFlowContextConnection) {
      result += ", connection from flow context";
    }
    else {
      result += QuickInfoHelper.toString(this, "server", m_Server, ", server: ");
      result += QuickInfoHelper.toString(this, "authentication", m_Authentication, ", auth: ");
    }

    return result;
  }

  /**
   * Sets the base converter to use.
   *
   * @param value	the converter
   */
  public void setConverter(AbstractConverter value) {
    m_Converter = value;
    reset();
  }

  /**
   * Returns the base converter to use.
   *
   * @return 		the converter
   */
  public AbstractConverter getConverter() {
    return m_Converter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String converterTipText() {
    return "The base converter for performing the actual conversion.";
  }

  /**
   * Sets whether the data exchange server connection available through the
   * flow context is used rather than the server/authentication defined here.
   *
   * @param value	true if to use connection from context
   */
  public void setUseFlowContextConnection(boolean value) {
    m_UseFlowContextConnection = value;
    reset();
  }

  /**
   * Returns whether the data exchange server connection available through the
   * flow context is used rather than the server/authentication defined here.
   *
   * @return 		true if to use connection from context
   */
  public boolean getUseFlowContextConnection() {
    return m_UseFlowContextConnection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String useFlowContextConnectionTipText() {
    return "If enabled, the data exchange server connection available through the flow context is used rather than the server/authentication defined here.";
  }

  /**
   * Sets the data exchange server to use.
   *
   * @param value	the server
   */
  public void setServer(BaseURL value) {
    m_Server = value;
    reset();
  }

  /**
   * Returns the data exchange server to use.
   *
   * @return 		the server
   */
  public BaseURL getServer() {
    return m_Server;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String serverTipText() {
    return "The data exchange server to use.";
  }

  /**
   * Sets the authentication to use for accessing the server.
   *
   * @param value	the authentication
   */
  public void setAuthentication(AbstractClientAuthentication value) {
    m_Authentication = value;
    reset();
  }

  /**
   * Returns the authentication to use for accessing the server.
   *
   * @return 		the authentication
   */
  public AbstractClientAuthentication getAuthentication() {
    return m_Authentication;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String authenticationTipText() {
    return "The authentication to use for accessing the server.";
  }

  /**
   * Returns the classes that the converter accepts.
   *
   * @return		the classes
   */
  @Override
  public Class[] accepts() {
    return m_Converter.accepts();
  }

  /**
   * Hook method for checks.
   *
   * @param payload	the payload to check
   * @return		null if sucessfully checked, otherwise error message
   */
  public String check(Object payload) {
    String	result;

    result = super.check(payload);

    if (result == null) {
      if (m_UseFlowContextConnection && (m_Connection == null)) {
	m_Connection = (DataExchangeServerConnection) ActorUtils.findClosestType(getFlowContext(), DataExchangeServerConnection.class);
	if (m_Connection == null)
	  result = "No " + DataExchangeServerConnection.class.getName() + " actor found!";
      }
    }

    if (result == null) {
      if (m_ActualURL == null) {
        if (m_Connection != null) {
          m_ActualURL            = m_Connection.buildURL("upload");
          m_ActualAuthentication = m_Connection.getAuthentication();
	}
	else {
          m_ActualURL = DataExchangeHelper.buildURL(m_Server, "upload");
          m_ActualAuthentication = m_Authentication;
	  m_ActualAuthentication.setFlowContext(getFlowContext());
	}
      }
    }

    return result;
  }

  /**
   * Converts the payload.
   *
   * @param payload	the payload
   * @param errors	for recording errors
   * @return		null if failed to convert, otherwise byte array
   */
  @Override
  protected byte[] doConvert(Object payload, MessageCollection errors) {
    byte[]	result;
    byte[]	data;
    String	token;

    data = m_Converter.convert(payload, errors);
    if (!errors.isEmpty())
      return null;

    m_Authentication.setFlowContext(getFlowContext());
    token = DataExchangeHelper.upload(data, m_ActualURL, m_ActualAuthentication, errors);
    if (errors.isEmpty() && (token != null))
      result = new StringConverter().convert(token, errors);
    else
      result = null;

    return result;
  }
}
