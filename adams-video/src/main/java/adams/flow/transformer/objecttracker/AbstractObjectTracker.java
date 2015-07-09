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
 * AbstractObjectTracker.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.objecttracker;

import adams.core.base.QuadrilateralLocation;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.AbstractImageContainer;

/**
 * Ancestor for object trackers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractObjectTracker
  extends AbstractOptionHandler
  implements ObjectTracker {

  private static final long serialVersionUID = -7912135967034523506L;

  /** whether the tracker has been intialized. */
  protected boolean m_Initialized;

  /** the last location. */
  protected QuadrilateralLocation m_LastLocation;

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Initialized  = false;
    m_LastLocation = null;
  }

  /**
   * Performs checks before the tracking is initialized.
   *
   * @param cont	the image to use for initializing
   * @param location	the initial location of the object
   * @return		true if successfully initialized, error message otherwise
   */
  protected String checkInitTracking(AbstractImageContainer cont, QuadrilateralLocation location) {
    if (cont == null)
      return "No image provided for tracking initialization!";
    if (location == null)
      return "No object location provided for tracking initialization!";
    return null;
  }

  /**
   * Performs the actual initialization of the tracking.
   *
   * @param cont	the image to use for initializing
   * @param location	the initial location of the object
   * @return		true if successfully initialized, error message otherwise
   */
  protected abstract String doInitTracking(AbstractImageContainer cont, QuadrilateralLocation location);

  /**
   * Initializes the tracker.
   *
   * @param cont	the image to use for initializing
   * @param location	the initial location of the object
   * @return		true if successfully initialized, error message otherwise
   */
  public String initTracking(AbstractImageContainer cont, QuadrilateralLocation location) {
    String	result;

    if (isLoggingEnabled())
      getLogger().info("Initializing with location=" + m_LastLocation);

    result = checkInitTracking(cont, location);
    if (result == null)
      result = doInitTracking(cont, location);

    m_Initialized = (result == null);
    if (m_Initialized)
      m_LastLocation = location;

    if (result != null)
      getLogger().severe(result);

    return result;
  }

  /**
   * Returns whether the tracker has been initialized.
   *
   * @return		true if initialized
   */
  public boolean isInitialized() {
    return m_Initialized;
  }

  /**
   * Performs checks before tracking an object.
   *
   * @param cont	the current image
   * @return		null if checks passed, otherwise error message
   */
  protected String checkTrackObject(AbstractImageContainer cont) {
    if (cont == null)
      return "No image provided for tracking!";
    return null;
  }

  /**
   * Performs the actual tracking of the object.
   *
   * @param cont	the current image
   * @return		the location of the tracked image, null if failed to track
   */
  protected abstract QuadrilateralLocation doTrackObject(AbstractImageContainer cont);

  /**
   * Performs the tracking of the object.
   *
   * @param cont	the current image
   * @return		the location of the tracked image, null if failed to track
   */
  public QuadrilateralLocation trackObject(AbstractImageContainer cont) {
    QuadrilateralLocation	result;
    String			msg;

    msg = checkTrackObject(cont);
    if (msg != null) {
      getLogger().severe(msg);
      return null;
    }

    result = doTrackObject(cont);

    if (isLoggingEnabled())
      getLogger().info("Tracked location=" + result);

    return result;
  }
}
