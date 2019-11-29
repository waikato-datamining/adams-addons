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
 * EchoClientTransformer.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest.echo;

import adams.flow.rest.AbstractRESTClientSource;
import com.github.fracpete.requests4j.Requests;
import com.github.fracpete.requests4j.response.BasicResponse;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Source client for Echo REST service.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EchoClientSource
  extends AbstractRESTClientSource<String> {

  private static final long serialVersionUID = -4005180585673812548L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Client (source) for Echo REST service.";
  }

  /**
   * Returns the classes that this client generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Performs the actual webservice query.
   *
   * @throws Exception	if accessing webservice fails for some reason
   */
  @Override
  protected void doQuery() throws Exception {
    String		url;
    BasicResponse 	r;

    if (getUseAlternativeURL())
      url = getAlternativeURL();
    else
      url = new EchoServer().getDefaultURL();
    url += "echo/" + URLEncoder.encode(getOwner().getFullName(), "UTF-8");
    r = Requests.get(url).execute();
    if (r.statusCode() == 200)
      setResponseData(URLDecoder.decode(r.text(), "UTF-8"));
    else
      m_LastError = r.statusCode() + ": " + r.statusMessage();
  }
}
