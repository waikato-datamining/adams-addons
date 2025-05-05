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
 * AbstractNetworkGenerator.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.djl.networkgenerator;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import ai.djl.basicdataset.tabular.TabularDataset;
import ai.djl.nn.Block;

/**
 * Ancestor for network generators.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractNetworkGenerator
  extends AbstractOptionHandler
  implements NetworkGenerator, QuickInfoSupporter {

  private static final long serialVersionUID = -9021412063817679485L;

  /** the flow context. */
  protected transient Actor m_FlowContext;

  /**
   * Sets the flow context.
   *
   * @param value	the context
   */
  @Override
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context.
   *
   * @return		the context, null if not available
   */
  @Override
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns whether flow context is required.
   * <br>
   * Default implementation returns false.
   *
   * @return		true if required
   */
  protected boolean requiresFlowContext() {
    return false;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks the dataset before generating the network.
   *
   * @param dataset	the dataset to generate the network for
   * @return		null if checks passed, otherwise error message
   */
  protected String check(TabularDataset dataset) {
    if (dataset == null)
      return "No dataset provided!";
    if (requiresFlowContext() && (m_FlowContext == null))
      return "No flow context set!";
    return null;
  }

  /**
   * Generates the network using the supplied dataset.
   *
   * @param dataset	the dataset to generate the network for
   * @return		the network
   */
  protected abstract Block doGenerate(TabularDataset dataset);

  /**
   * Generates the network using the supplied dataset.
   *
   * @param dataset	the dataset to generate the network for
   * @return		the network
   */
  public Block generate(TabularDataset dataset) {
    String	msg;

    msg = check(dataset);
    if (msg != null)
      throw new IllegalStateException(msg);
    return doGenerate(dataset);
  }
}
