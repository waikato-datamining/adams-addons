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
 * PruneContainers.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.docker.simpledocker;

import adams.core.QuickInfoHelper;
import adams.docker.SimpleDockerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * For pruning stopped containers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PruneContainers
  extends AbstractDockerCommand {

  private static final long serialVersionUID = 34785283711877518L;

  /** for filtering the containers. */
  protected String m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For pruning stopped containers ('docker container prune').\n"
      + "For more information see:\n"
      + "https://docs.docker.com/engine/reference/commandline/container_prune/";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filter",
      "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "filter", (m_Filter.isEmpty() ? "-none-" : m_Filter), "filter: ");
  }

  /**
   * Sets the container filter.
   *
   * @param value	the filter
   */
  public void setFilter(String value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the container filter.
   *
   * @return		the filter
   */
  public String getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to apply (e.g. 'until=<timestamp>'), ignored if empty.";
  }

  /**
   * Whether the command is used in a blocking or async fashion.
   *
   * @return		true if blocking, false if async
   */
  @Override
  public boolean isUsingBlocking() {
    return true;
  }

  /**
   * Executes the command.
   *
   * @return		the result of the command, either a CommandResult or a String object (= error message)
   */
  @Override
  protected Object doBlockingExecute() {
    List<String> result;

    result = new ArrayList<>();
    result.add("container");
    result.add("prune");
    result.add("--force");
    if (!m_Filter.isEmpty()) {
      result.add("--filter");
      result.add(m_Filter);
    }
    log(result);

    return SimpleDockerHelper.command(m_Connection.getAcualBinary(), result);
  }

  /**
   * Returns the class of the output the command generates.
   *
   * @return		the type
   */
  @Override
  public Class generates() {
    return String.class;
  }
}
