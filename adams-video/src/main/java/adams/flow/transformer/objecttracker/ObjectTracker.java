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
 * ObjectTracker.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.objecttracker;

import adams.core.base.QuadrilateralLocation;
import adams.data.image.AbstractImageContainer;

import java.util.List;

/**
 * Interface for object trackers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ObjectTracker {

  /**
   * Initializes the tracker.
   *
   * @param cont	the image to use for initializing
   * @return		null if successfully initialized, otherwise error message
   */
  public String initTracking(AbstractImageContainer cont);

  /**
   * Returns whether the tracker has been initialized.
   *
   * @return		true if initialized
   */
  public boolean isInitialized();

  /**
   * Performs the tracking of the object.
   *
   * @param cont	the current image
   * @return		the location of the tracked image, null if failed to track
   */
  public List<QuadrilateralLocation> trackObjects(AbstractImageContainer cont);
}
