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
 * BoofCVSparseFlow.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.objecttracker;

import boofcv.abst.tracker.TrackerObjectQuad;
import boofcv.factory.tracker.FactoryTrackerObjectQuad;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Uses a pyramidal KLT tracker to track features inside the user selected region. The motion of the region is found robustly using LeastMedianOfSquares and a translation + rotation model. Drift is a problem since motion is estimated relative to the previous frame and it will eventually drift away from the original target. When it works well it is very smooth and can handle partially obscured objects. Can't recover after the target has been lost. Runs very fast.
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoofCVSparseFlow
  extends AbstractBoofCVObjectTracker {

  private static final long serialVersionUID = 7061565466109634695L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses a pyramidal KLT tracker to track features inside the user selected region. The motion of the region "
        + "is found robustly using LeastMedianOfSquares and a translation + rotation model. Drift is a problem "
        + "since motion is estimated relative to the previous frame and it will eventually drift away from the original target. "
        + "When it works well it is very smooth and can handle partially obscured objects. Can't recover after the target "
        + "has been lost. Runs very fast.";
  }

  /**
   * Instantiates a new tracker.
   *
   * @return		the tracker
   */
  @Override
  protected TrackerObjectQuad newTracker() {
    return FactoryTrackerObjectQuad.sparseFlow(null, ImageUInt8.class, null);
  }
}
