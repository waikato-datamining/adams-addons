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
 * AbstractBoofCVObjectTracker.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.objecttracker;

import adams.core.base.QuadrilateralLocation;
import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import adams.data.image.AbstractImageContainer;
import boofcv.abst.tracker.TrackerObjectQuad;
import boofcv.struct.image.ImageBase;
import georegression.struct.shapes.Quadrilateral_F64;

/**
 * Ancestor for BoofCV object trackers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBoofCVObjectTracker
  extends AbstractObjectTracker {

  private static final long serialVersionUID = -8364076045858032972L;

  /** the tracker. */
  protected TrackerObjectQuad m_Tracker;

  /**
   * Instantiates a new tracker.
   *
   * @return		the tracker
   */
  protected abstract TrackerObjectQuad newTracker();

  /**
   * Performs the actual initialization of the tracking.
   *
   * @param cont	the image to use for initializing
   * @param location	the initial location of the object
   * @return		true if successfully initialized, error message otherwise
   */
  protected String doInitTracking(AbstractImageContainer cont, QuadrilateralLocation location) {
    BoofCVImageContainer	bcont;
    ImageBase 			frame;

    bcont     = BoofCVHelper.toBoofCVImageContainer(cont, BoofCVImageType.UNSIGNED_INT_8);
    frame     = bcont.getImage();
    m_Tracker = newTracker();
    if (!m_Tracker.initialize(frame, location.quadrilateralValue()))
      return "Failed to initialze tracker!";

    return null;
  }

  /**
   * Performs the actual tracking of the object.
   *
   * @param cont	the current image
   * @return		the location of the tracked image, null if failed to track
   */
  protected QuadrilateralLocation doTrackObject(AbstractImageContainer cont) {
    BoofCVImageContainer	bcont;
    ImageBase 			frame;
    Quadrilateral_F64		location;
    boolean			visible;

    bcont          = BoofCVHelper.toBoofCVImageContainer(cont, BoofCVImageType.UNSIGNED_INT_8);
    frame          = bcont.getImage();
    location       = m_LastLocation.quadrilateralValue();
    visible        = m_Tracker.process(frame, location);
    m_LastLocation = new QuadrilateralLocation(location);
    if (isLoggingEnabled())
      getLogger().info("visible=" + visible);

    return m_LastLocation;
  }
}
