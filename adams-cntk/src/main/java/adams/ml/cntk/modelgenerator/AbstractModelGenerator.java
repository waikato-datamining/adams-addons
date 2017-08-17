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
 * AbstractModelGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelgenerator;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;

import java.util.List;

/**
 * Ancestor for classes that generate CNTK model scripts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractModelGenerator
  extends AbstractOptionHandler
  implements ModelGenerator, QuickInfoSupporter {

  private static final long serialVersionUID = 5451911399666075222L;

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
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method for performing checks.
   * <br>
   * Default implementation only ensures that flow context is set.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    if (m_FlowContext == null)
      return "No flow context set!";
    return null;
  }

  /**
   * Generates the actual models.
   *
   * @param numInput	the number of input nodes
   * @param numOutput	the number of output nodes
   * @return		the models
   */
  protected abstract List<String> doGenerate(int numInput, int numOutput);

  /**
   * Generates models.
   *
   * @param numInput	the number of input nodes
   * @param numOutput	the number of output nodes
   * @return		the models
   */
  public List<String> generate(int numInput, int numOutput) {
    String	msg;

    msg = check();
    if (msg != null)
      throw new IllegalStateException(msg);
    return doGenerate(numInput, numOutput);
  }
}
