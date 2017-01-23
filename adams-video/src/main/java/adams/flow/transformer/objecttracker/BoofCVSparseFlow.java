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
 * Copyright (C) 2015-2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.objecttracker;

import boofcv.abst.tracker.TrackerObjectQuad;
import boofcv.factory.tracker.FactoryTrackerObjectQuad;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayF64;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.GrayS64;
import boofcv.struct.image.GrayS8;
import boofcv.struct.image.GrayU16;
import boofcv.struct.image.GrayU8;

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
    // TODO configuration

    switch (m_ImageType) {
      case FLOAT_32:
        return FactoryTrackerObjectQuad.sparseFlow(null, GrayF32.class, null);
      case FLOAT_64:
        return FactoryTrackerObjectQuad.sparseFlow(null, GrayF64.class, null);
      case SIGNED_INT_16:
        return FactoryTrackerObjectQuad.sparseFlow(null, GrayS16.class, null);
      case SIGNED_INT_32:
        return FactoryTrackerObjectQuad.sparseFlow(null, GrayS32.class, null);
      case SIGNED_INT_64:
        return FactoryTrackerObjectQuad.sparseFlow(null, GrayS64.class, null);
      case SIGNED_INT_8:
        return FactoryTrackerObjectQuad.sparseFlow(null, GrayS8.class, null);
      case UNSIGNED_INT_16:
        return FactoryTrackerObjectQuad.sparseFlow(null, GrayU16.class, null);
      case UNSIGNED_INT_8:
        return FactoryTrackerObjectQuad.sparseFlow(null, GrayU8.class, null);
      default:
        throw new IllegalStateException("Unhandled image type: " + m_ImageType);
    }
  }
}
