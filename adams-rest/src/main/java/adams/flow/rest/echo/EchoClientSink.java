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
 * EchoClientSink.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.echo;

import adams.core.base.BaseURL;
import adams.core.net.HttpRequestHelper;
import adams.flow.container.HTMLRequestResult;
import adams.flow.rest.AbstractRESTClientSink;
import org.jsoup.Connection.Method;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Sink client for Echo REST service.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EchoClientSink
  extends AbstractRESTClientSink<String> {

  private static final long serialVersionUID = -4005180585673812548L;

  /** the request data. */
  protected String m_RequestData;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Client (sink) for Echo REST service. Check console for result of webservice.";
  }

  /**
   * Returns the classes that are accepted input.
   *
   * @return		the classes that are accepted
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Sets the data for the request, if any.
   *
   * @param value	the request data
   */
  @Override
  public void setRequestData(String value) {
    m_RequestData = value;
  }

  /**
   * Performs the actual webservice query.
   *
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  protected void doQuery() throws Exception {
    String		url;
    HTMLRequestResult 	result;

    if (getUseAlternativeURL())
      url = getAlternativeURL();
    else
      url = new EchoServer().getDefaultURL();
    url += "echo/" + URLEncoder.encode(m_RequestData, "UTF-8");
    result = HttpRequestHelper.send(new BaseURL(url), Method.GET, null, null);
    if (result.getValue(HTMLRequestResult.VALUE_STATUSCODE, Integer.class) == 200)
      getLogger().info(URLDecoder.decode(result.getValue(HTMLRequestResult.VALUE_BODY, String.class), "UTF-8"));
    else
      m_LastError = result.getValue(HTMLRequestResult.VALUE_STATUSCODE) + ": " + result.getValue(HTMLRequestResult.VALUE_STATUSMESSAGE);
  }
}
