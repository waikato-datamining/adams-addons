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
 * BoofCVCirculant.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.objecttracker;

import boofcv.abst.tracker.TrackerObjectQuad;
import boofcv.factory.tracker.FactoryTrackerObjectQuad;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Creates the Circulant feature tracker. Texture based tracker which uses the theory of circulant matrices, Discrete Fourier Transform (DCF), and linear classifiers to track a target. Fixed sized rectangular target and only estimates translation. Can't detect when it loses track or re-aquire track.
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
public class BoofCVCirculant
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
      "Creates the Circulant feature tracker. Texture based tracker which uses the theory of circulant matrices, "
        + "Discrete Fourier Transform (DCF), and linear classifiers to track a target. Fixed sized rectangular target "
        + "and only estimates translation. Can't detect when it loses track or re-aquire track.";
  }

  /**
   * Instantiates a new tracker.
   *
   * @return		the tracker
   */
  @Override
  protected TrackerObjectQuad newTracker() {
    return FactoryTrackerObjectQuad.circulant(null, ImageUInt8.class);
  }
}
