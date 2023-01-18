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
 * LocalPathToContainerPath.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.Utils;
import adams.docker.SimpleDockerHelper;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.FlowContextHandler;
import adams.flow.standalone.SimpleDockerConnection;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Converts a local path into a container one
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class LocalPathToContainerPath
  extends AbstractStringConversion
  implements FlowContextHandler {

  private static final long serialVersionUID = -9065222569094253813L;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the docker connection in use. */
  protected transient SimpleDockerConnection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a local path into a container one";
  }

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
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    String[]	paths;

    if (m_Connection == null) {
      m_Connection = (SimpleDockerConnection) ActorUtils.findClosestType(m_FlowContext, SimpleDockerConnection.class, true);
      if (m_Connection == null)
	throw new IllegalStateException("No " + Utils.classToString(SimpleDockerConnection.class) + " actor found!");
    }

    paths = SimpleDockerHelper.toContainerPaths(
      Arrays.asList(m_Connection.getExpandedDirMappings()),
      new String[]{(String) m_Input});

    return paths[0];
  }
}
