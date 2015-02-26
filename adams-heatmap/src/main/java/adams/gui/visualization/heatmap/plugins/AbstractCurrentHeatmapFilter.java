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
 * AbstractCurrentHeatmapFilter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap.plugins;

import adams.data.heatmap.Heatmap;
import adams.gui.visualization.heatmap.HeatmapPanel;

import java.io.File;
import java.util.logging.Level;

/**
 * Ancestor for plugins that filter the current heatmap.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCurrentHeatmapFilter
  extends AbstractHeatmapViewerPlugin {

  /** for serialization. */
  private static final long serialVersionUID = 869121794905442017L;

  /** for storing filtering errors. */
  protected String m_FilterError;

  /**
   * Checks whether the plugin can be executed given the specified heatmap panel.
   * <p/>
   * Panel must be non-null and must contain an heatmap.
   *
   * @param panel	the panel to use as basis for decision
   * @return		true if plugin can be executed
   */
  @Override
  public boolean canExecute(HeatmapPanel panel) {
    return (panel != null) && (panel.getHeatmap() != null);
  }

  /**
   * Filters the heatmap.
   *
   * @param heatmap	the heatmap to filter
   * @return		the processed heatmap
   */
  protected abstract Heatmap filter(Heatmap heatmap);

  /**
   * Executes the plugin.
   *
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Heatmap		input;
    Heatmap		output;
    File		file;
    double		scale;

    result = null;

    input         = m_CurrentPanel.getHeatmap();
    scale         = m_CurrentPanel.getZoom();
    m_FilterError = null;
    try {
      output = filter(input);

      // did user abort filtering?
      if (m_CanceledByUser)
	return result;

      if (output == null) {
	result = "Failed to filter heatmap: ";
	if (m_FilterError == null)
	  result += "unknown reason";
	else
	  result += m_FilterError;
      }
      else {
	m_CurrentPanel.setHeatmap(output);
	m_CurrentPanel.setZoom(scale);
      }
    }
    catch (Exception e) {
      m_FilterError = e.toString();
      result = "Failed to filter heatmap: ";
      getLogger().log(Level.SEVERE, result, e);
      result += m_FilterError;
    }

    return result;
  }
}
