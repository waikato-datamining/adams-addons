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
 * HeatmapRenderer.java
 * Copyright (C) 2015-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectrenderer;

import adams.data.heatmap.Heatmap;
import adams.gui.visualization.heatmap.HeatmapPanel;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Renders Heatmap objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HeatmapRenderer
  extends AbstractObjectRenderer {

  private static final long serialVersionUID = -3528006886476495175L;

  /** the cached setup. */
  protected HeatmapPanel m_LastHeatmapPanel;

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(Heatmap.class, cls);
  }

  /**
   * Checks whether the renderer can use a cached setup to render an object.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		true if possible
   */
  @Override
  public boolean canRenderCached(Object obj, JPanel panel) {
    return (m_LastHeatmapPanel != null);
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @param limit       the limit to use for the rendering (if applicable), ignored if null
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRenderCached(Object obj, JPanel panel, Integer limit) {
    m_LastHeatmapPanel.setHeatmap((Heatmap) obj);
    panel.add(m_LastHeatmapPanel, BorderLayout.CENTER);
    return null;
  }

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @param limit       the limit to use for the rendering (if applicable), ignored if null
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doRender(Object obj, JPanel panel, Integer limit) {
    Heatmap 		heatmap;
    HeatmapPanel 	heatmapPanel;

    heatmap = (Heatmap) obj;
    heatmapPanel = new HeatmapPanel(null);
    heatmapPanel.setHeatmap(heatmap);
    panel.add(heatmapPanel, BorderLayout.CENTER);

    m_LastHeatmapPanel = heatmapPanel;

    return null;
  }
}
