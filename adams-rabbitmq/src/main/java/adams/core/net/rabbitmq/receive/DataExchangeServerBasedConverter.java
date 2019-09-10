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

package adams.core.net.rabbitmq.receive;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseURL;
import adams.flow.container.HttpRequestResult;
import adams.flow.rest.dex.DataExchangeHelper;
import adams.flow.rest.dex.clientauthentication.AbstractClientAuthentication;
import adams.flow.rest.dex.clientauthentication.NoAuthentication;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Instead of sending potentially large payloads via a RabbitMQ, this
 * meta-converter loads the actual payload from the the specified data exchange server
 * using the token received via RabbitMQ.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DataExchangeServerBasedConverter
  extends AbstractConverter {

  private static final long serialVersionUID = -736244897402323379L;

  /** the base converter. */
  protected AbstractConverter m_Converter;

  /** the data exchange server to use. */
  protected BaseURL m_Server;

  /** the authentication to use. */
  protected AbstractClientAuthentication m_Authentication;

  /** whether to delete the data after use. */
  protected boolean m_Remove;

  /** the object mapper in use. */
  protected transient ObjectMapper m_Mapper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Instead of sending potentially large payloads via a RabbitMQ, this "
      + "meta-converter loads the actual payload from the specified data exchange server "
      + "using the token received via RabbitMQ.";
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
      "server", "server",
      new BaseURL("http://localhost:8080/download"));

    m_OptionManager.add(
      "authentication", "authentication",
      new NoAuthentication());

    m_OptionManager.add(
      "remove", "remove",
      false);
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
    result += QuickInfoHelper.toString(this, "server", m_Server, ", server: ");
    result += QuickInfoHelper.toString(this, "authentication", m_Authentication, ", auth: ");
    result += QuickInfoHelper.toString(this, "remove", m_Remove ? "remove" : "leave", ", data: ");

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
   * Sets whether to immediately remove the data from the server once
   * successfully retrieved.
   *
   * @param value	true if to remove
   */
  public void setRemove(boolean value) {
    m_Remove = value;
    reset();
  }

  /**
   * Returns whether to immediately remove the data from the server once
   * successfully retrieved.
   *
   * @return 		true if to remove
   */
  public boolean getRemove() {
    return m_Remove;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String removeTipText() {
    return "If enabled, the data gets removed after successfully retrieving it.";
  }

  /**
   * Returns the class that the converter generates.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return m_Converter.generates();
  }

  /**
   * Converts the payload.
   *
   * @param payload	the payload
   * @param errors	for recording errors
   * @return		null if failed to convert, otherwise byte array
   */
  @Override
  protected Object doConvert(byte[] payload, MessageCollection errors) {
    Object		result;
    String 		token;
    byte[]		data;
    HttpRequestResult	response;

    token = (String) new StringConverter().convert(payload, errors);
    if (!errors.isEmpty())
      return null;

    m_Authentication.setFlowContext(getFlowContext());
    data = DataExchangeHelper.download(token, m_Server, m_Authentication, errors);
    if (errors.isEmpty() && (data != null))
      result = m_Converter.convert(data, errors);
    else
      result = null;

    if ((result != null) && m_Remove) {
      errors.clear();
      response = DataExchangeHelper.remove(token, m_Server, m_Authentication, errors);
      if ((response != null) && (response.getValue(HttpRequestResult.VALUE_STATUSCODE, Integer.class) != 200))
	errors.add("Failed to remove data for token '" + token + "': " + response);
    }

    return result;
  }
}
