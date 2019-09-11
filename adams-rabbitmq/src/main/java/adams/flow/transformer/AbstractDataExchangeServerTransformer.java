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
 * AbstractDataExchangeServerTransformer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.base.BaseURL;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.DataExchangeServerConnection;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Ancestor for transformers that access a Data Exchange Server.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDataExchangeServerTransformer
  extends AbstractTransformer {

  private static final long serialVersionUID = -2903084298983787897L;

  /** the connection in use. */
  protected transient DataExchangeServerConnection m_Connection;

  /** the object mapper in use. */
  protected transient ObjectMapper m_Mapper;

  /** the actual url to use. */
  protected transient BaseURL m_ActualURL;

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Mapper    = null;
    m_ActualURL = null;
  }

  /**
   * Returns the mapper to use.
   *
   * @return		the mapper
   */
  protected ObjectMapper getMapper() {
    if (m_Mapper == null)
      m_Mapper = new ObjectMapper();
    return m_Mapper;
  }

  /**
   * Returns the path for the actual URL.
   *
   * @return		the path
   */
  protected abstract String getPath();

  /**
   * Returns the actual URL to use.
   *
   * @return		the URL
   */
  protected BaseURL getActualUrl() {
    if (m_ActualURL == null)
      m_ActualURL = m_Connection.buildURL(getPath());
    return m_ActualURL;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;

    result = super.setUp();

    if (result == null) {
      m_Connection = (DataExchangeServerConnection) ActorUtils.findClosestType(this, DataExchangeServerConnection.class);
      if (m_Connection == null)
	result = "No " + DataExchangeServerConnection.class.getName() + " actor found!";
    }

    return result;
  }
}
