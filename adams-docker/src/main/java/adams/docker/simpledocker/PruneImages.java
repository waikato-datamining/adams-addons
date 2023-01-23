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

import java.util.List;

/**
 * For pruning images.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PruneImages
  extends AbstractDockerCommand {

  private static final long serialVersionUID = 34785283711877518L;

  /** for filtering the containers. */
  protected String m_Filter;

  /** whether to prune all images, not just dangling ones. */
  protected boolean m_All;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For pruning images ('docker image prune').\n"
      + "For more information see:\n"
      + "https://docs.docker.com/engine/reference/commandline/image_prune/";
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

    m_OptionManager.add(
      "all", "all",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String  	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "filter", (m_Filter.isEmpty() ? "-none-" : m_Filter), ", filter: ");
    result += QuickInfoHelper.toString(this, "all", m_All, "all", ", ");

    return result;
  }

  /**
   * Sets the image filter.
   *
   * @param value	the filter
   */
  public void setFilter(String value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the image filter.
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
    return "The filter to apply (e.g. 'until=<timestamp>'), ignored if empty; variables get expanded automatically.";
  }

  /**
   * Sets whether to prune all images, not just dangling ones.
   *
   * @param value	true if to prune all
   */
  public void setAll(boolean value) {
    m_All = value;
    reset();
  }

  /**
   * Returns whether to prune all images, not just dangling ones.
   *
   * @return		true if to prune all
   */
  public boolean getAll() {
    return m_All;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allTipText() {
    return "If enabled, prunes all images not just dangling ones.";
  }

  /**
   * Assembles the command to run.
   *
   * @return		the command
   */
  @Override
  protected List<String> buildCommand() {
    List<String> result;

    result = super.buildCommand();
    result.add("image");
    result.add("prune");
    result.add("--force");
    if (m_All)
      result.add("--all");
    if (!m_Filter.isEmpty()) {
      result.add("--filter");
      result.add(getFlowContext().getVariables().expand(m_Filter));
    }

    return result;
  }
}
