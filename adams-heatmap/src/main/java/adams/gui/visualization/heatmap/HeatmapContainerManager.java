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
 * HeatmapContainerManager.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.heatmap;

import adams.data.heatmap.Heatmap;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.NamedContainerManager;

/**
 * Container manager for heatmaps.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapContainerManager
  extends AbstractContainerManager<HeatmapContainer>
  implements NamedContainerManager {

  /** for serialization. */
  private static final long serialVersionUID = 189470224357901714L;

  /**
   * Returns the index of the first ID or display ID that matches the
   * specified one.
   *
   * @param id	the ID/display ID to look for
   * @return		the index or -1 if not found
   */
  public int indexOf(String id) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < count(); i++) {
      if (get(i).getID().equals(id)) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns a new container containing the given payload.
   *
   * @param o		the payload to encapsulate
   * @return		the new container
   */
  @Override
  public HeatmapContainer newContainer(Comparable o) {
    return new HeatmapContainer(this, (Heatmap) o);
  }
}
