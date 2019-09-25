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
 * RatsServer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.rest;

import adams.core.Utils;
import adams.flow.core.FlowContextHandler;
import adams.flow.rest.text.RatsTextUpload;
import adams.flow.standalone.rats.input.RatInput;
import adams.flow.standalone.rats.input.RatInputUser;

/**
 <!-- globalinfo-start -->
 * Generic REST service provider for the RATS framework, which allows you to assemble the REST plugins that should make up the service.<br>
 * Automatically sets the flow context of plugins, if they should implement the adams.flow.core.FlowContextHandler interface, and for rat input context, if they should implement the adams.flow.standalone.rats.input.RatInputUser interface.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-url &lt;java.lang.String&gt; (property: URL)
 * &nbsp;&nbsp;&nbsp;The URL of the service.
 * &nbsp;&nbsp;&nbsp;default: http:&#47;&#47;localhost:8080&#47;
 * </pre>
 *
 * <pre>-in-interceptor &lt;adams.flow.rest.interceptor.incoming.AbstractInInterceptorGenerator&gt; (property: inInterceptor)
 * &nbsp;&nbsp;&nbsp;The interceptor to use for incoming messages.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.rest.interceptor.incoming.NullGenerator
 * </pre>
 *
 * <pre>-out-interceptor &lt;adams.flow.rest.interceptor.outgoing.AbstractOutInterceptorGenerator&gt; (property: outInterceptor)
 * &nbsp;&nbsp;&nbsp;The interceptor to use for outgoing messages.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.rest.interceptor.outgoing.NullGenerator
 * </pre>
 *
 * <pre>-plugin &lt;adams.flow.rest.RESTPlugin&gt; [-plugin ...] (property: plugins)
 * &nbsp;&nbsp;&nbsp;The plugins that make up the REST service.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.rest.text.RatsTextUpload
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RatsServer
  extends GenericServer
  implements RatInputUser {

  private static final long serialVersionUID = -4108489800411310125L;

  /** the rat input. */
  protected RatInput m_RatInput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generic REST service provider for the RATS framework, which allows you to assemble the REST "
	+ "plugins that should make up the service.\n"
	+ "Automatically sets the flow context of plugins, if they should implement "
	+ "the " + Utils.classToString(FlowContextHandler.class) + " interface, "
	+ "and for rat input context, if they should implement the "
	+ Utils.classToString(RatInputUser.class) + " interface.";
  }

  /**
   * Returns the default plugins to use.
   *
   * @return		the default
   */
  protected RESTPlugin[] getDefaultPlugins() {
    return new RESTPlugin[]{new RatsTextUpload()};
  }

  /**
   * Sets the rat input to use.
   *
   * @param value	the rat input
   */
  @Override
  public void setRatInput(RatInput value) {
    m_RatInput = value;
  }

  /**
   * Returns the rat input in use.
   *
   * @return		the rat input, null if none set
   */
  @Override
  public RatInput getRatInput() {
    return m_RatInput;
  }

  /**
   * For configuring the plugins, e.g., setting the flow context.
   *
   * @param plugins	the plugins to configure
   */
  protected void configurePlugins(RESTPlugin[] plugins) {
    int		i;

    super.configurePlugins(plugins);

    for (i = 0; i < plugins.length; i++) {
      if (plugins[i] instanceof RatInputUser)
	((RatInputUser) plugins[i]).setRatInput(m_RatInput);
    }
  }
}
