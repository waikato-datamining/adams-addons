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

/**
 * AbstractIteratorListenerConfigurator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.iterationlistener;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import org.deeplearning4j.optimize.api.IterationListener;

import java.util.List;

/**
 * Ancestor for iterationlistener configurators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractIteratorListenerConfigurator
  extends AbstractOptionHandler
  implements IterationListenerConfigurator, QuickInfoSupporter {

  private static final long serialVersionUID = -5049221729823530346L;

  /** the flow context. */
  protected Actor m_FlowContext;

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * The default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method before configuring the listener.
   * <br>
   * Default implementation only ensures that flow context is set.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    if (requiresFlowContext()) {
      if (m_FlowContext == null)
        return "No flow context set!";
    }
    return null;
  }

  /**
   * Configures the actual {@link IterationListener} and returns it.
   *
   * @return		the listener
   */
  protected abstract List<IterationListener> doConfigureIterationListeners();

  /**
   * Configures the listener and returns it.
   *
   * @return		the listener
   */
  public List<IterationListener> configureIterationListeners() {
    List<IterationListener>	result;
    String			msg;

    msg = check();
    if (msg != null)
      throw new IllegalStateException(msg);

    result = doConfigureIterationListeners();

    return result;
  }
}
