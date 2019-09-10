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
 * DataExchangeServerUpload.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseURL;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import adams.flow.rest.dex.DataExchangeHelper;
import adams.flow.rest.dex.clientauthentication.AbstractClientAuthentication;
import adams.flow.rest.dex.clientauthentication.NoAuthentication;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Uploads the file or byte array to the specified data exchange server and forwards the received token, if successful.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;byte[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: DataExchangeServerUpload
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
 * <pre>-server &lt;adams.core.base.BaseURL&gt; (property: server)
 * &nbsp;&nbsp;&nbsp;The data exchange server to use.
 * &nbsp;&nbsp;&nbsp;default: http:&#47;&#47;localhost:8080&#47;upload
 * </pre>
 *
 * <pre>-authentication &lt;adams.flow.rest.dex.clientauthentication.AbstractClientAuthentication&gt; (property: authentication)
 * &nbsp;&nbsp;&nbsp;The authentication to use for accessing the server.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.rest.dex.clientauthentication.NoAuthentication
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DataExchangeServerUpload
  extends AbstractTransformer {

  private static final long serialVersionUID = 5538665246033999366L;

  /** the data exchange server to use. */
  protected BaseURL m_Server;

  /** the authentication to use. */
  protected AbstractClientAuthentication m_Authentication;

  /** the object mapper in use. */
  protected transient ObjectMapper m_Mapper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uploads the file or byte array to the specified data exchange server and forwards the received token, if successful.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

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

    result = QuickInfoHelper.toString(this, "server", m_Server, "server: ");
    result += QuickInfoHelper.toString(this, "authentication", m_Authentication, ", auth: ");

    return result;
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
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class, byte[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    MessageCollection	errors;
    File		file;
    byte[]		data;
    String		token;

    result = null;
    file   = null;
    data   = new byte[0];
    if (m_InputToken.hasPayload(String.class))
      file = new PlaceholderFile(m_InputToken.getPayload(String.class));
    else if (m_InputToken.hasPayload(File.class))
      file = new PlaceholderFile(m_InputToken.getPayload(File.class));
    else if (m_InputToken.hasPayload(byte[].class))
      data = m_InputToken.getPayload(byte[].class);
    else
      result = m_InputToken.unhandledData();

    if (result == null) {
      errors = new MessageCollection();
      m_Authentication.setFlowContext(this);
      if (file != null)
	token = DataExchangeHelper.upload(file, m_Server, m_Authentication, errors);
      else
	token = DataExchangeHelper.upload(data, m_Server, m_Authentication, errors);
      if (errors.isEmpty() && (token != null))
        m_OutputToken = new Token(token);
      else if (!errors.isEmpty())
        result = errors.toString();
    }

    return result;
  }
}
