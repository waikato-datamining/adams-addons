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
 * IterationListenerConfigurator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.iterationlistener;

import adams.flow.core.FlowContextHandler;
import org.deeplearning4j.optimize.api.IterationListener;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for classes that can configure an {@link IterationListener}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface IterationListenerConfigurator
  extends Serializable, FlowContextHandler {

  /**
   * Returns whether a flow context is required or optional.
   *
   * @return		true if required
   */
  public boolean requiresFlowContext();

  /**
   * Configures the listeners and returns it.
   *
   * @return		the listeners
   */
  public List<IterationListener> configureIterationListeners();
}
