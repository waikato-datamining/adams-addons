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
 * AbstractRESTClient.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.QuickInfoHelper;
import adams.flow.rest.RESTClient;
import adams.flow.rest.RESTClientConsumer;
import adams.flow.rest.RESTClientProducer;

/**
 * Ancestor for REST webservice client actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRESTClient
  extends AbstractActor {

  /** for serialization. */
  private static final long serialVersionUID = -1226032219173406368L;

  /** the webservice client to use. */
  protected RESTClient m_Client;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "client", "client",
      getDefaultClient());
  }

  /**
   * Returns the default client to use.
   *
   * @return		the client
   */
  protected abstract RESTClient getDefaultClient();

  /**
   * Checks the client.
   *
   * @param value	the client to check
   * @return		null if accepted, otherwise error message
   */
  protected abstract String checkClient(RESTClient value);

  /**
   * Sets the webservice client to use.
   *
   * @param value	the webservice client to use
   */
  public void setClient(RESTClient value) {
    String	msg;

    msg = checkClient(value);
    if (msg == null) {
      m_Client = value;
      m_Client.setOwner(this);
      reset();
    }
    else {
      getLogger().severe("Failed to set client: " + msg);
    }
  }

  /**
   * Returns the webservice client in use.
   *
   * @return		the webservice client in use
   */
  public RESTClient getClient() {
    return m_Client;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String clientTipText() {
    return "The webservice client to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "client", m_Client);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    Class[]	classes;

    result = super.setUp();

    if (result == null) {
      if (m_Client instanceof RESTClientConsumer) {
        if (ActorUtils.isSink(this) || ActorUtils.isTransformer(this)) {
          classes = ((RESTClientConsumer) m_Client).accepts();
          if ((classes == null) || (classes.length == 0))
            result = "Client '" + m_Client.getClass().getName() + "' defines no input type?";
        }
      }
      else if (m_Client instanceof RESTClientProducer) {
        if (ActorUtils.isSource(this) || ActorUtils.isTransformer(this)) {
          classes = ((RESTClientProducer) m_Client).generates();
          if ((classes == null) || (classes.length == 0))
            result = "Client '" + m_Client.getClass().getName() + "' defines no output type?";
        }
      }
    }

    if (result == null)
      m_Client.setOwner(this);

    return result;
  }

  /**
   * Hook method before the webservice gets queried.
   * <br><br>
   * Default implementation does nothing.
   *
   * @return		null if successful, otherwise error message
   */
  protected String preQuery() {
    return null;
  }

  /**
   * Queries the webservice.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doQuery() {
    String	result;

    try {
      m_Client.query();
      result = null;
    }
    catch (Exception e) {
      result = handleException("Failed to query webservice: ", e);
    }

    return result;
  }

  /**
   * Hook method after the webservice got queried.
   * <br><br>
   * Default implementation does nothing.
   *
   * @return		null if successful, otherwise error message
   */
  protected String postQuery() {
    return null;
  }

  /**
   * Queries the webservice.
   *
   * @return		null if successful, otherwise error message
   * @see		#preQuery()
   * @see		#doQuery()
   * @see		#postQuery()
   */
  protected String query() {
    String	result;

    result = preQuery();
    if (isLoggingEnabled())
      getLogger().info("preQuery: " + result);

    if (result == null) {
      result = doQuery();
      if (isLoggingEnabled())
        getLogger().info("doQuery: " + result);
    }

    if (result == null) {
      result = postQuery();
      if (isLoggingEnabled())
        getLogger().info("postQuery: " + result);
    }

    if (result == null) {
      if (m_Client.hasLastError())
        result = m_Client.getLastError();
    }

    return result;
  }

  /**
   * Cleans up the actor.
   */
  @Override
  public void cleanUp() {
    m_Client.cleanUp();
    super.cleanUp();
  }
}
