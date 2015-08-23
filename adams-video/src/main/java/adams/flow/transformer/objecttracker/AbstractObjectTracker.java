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

import adams.core.QuickInfoSupporter;
import adams.core.base.QuadrilateralLocation;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.AbstractImageContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for object trackers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractObjectTracker
  extends AbstractOptionHandler
  implements ObjectTracker, QuickInfoSupporter {

  private static final long serialVersionUID = -7912135967034523506L;

  /** whether the tracker has been intialized. */
  protected boolean m_Initialized;

  /** the last location(s). */
  protected List<QuadrilateralLocation> m_LastLocations;

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Initialized   = false;
    m_LastLocations = new ArrayList<>();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * <br>
   * Default implementation does nothing.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the initial object locations.
   *
   * @param cont	the current image container
   * @return		the locations, null if failed to determine
   */
  protected abstract List<QuadrilateralLocation> getInitialLocations(AbstractImageContainer cont);

  /**
   * Performs checks before the tracking is initialized.
   *
   * @param cont	the image to use for initializing
   * @param locations	the initial location(s) of the object(s)
   * @return		true if successfully initialized, error message otherwise
   */
  protected String checkInitTracking(AbstractImageContainer cont, List<QuadrilateralLocation> locations) {
    if (cont == null)
      return "No image provided for tracking initialization!";
    if ((locations == null) || (locations.size() == 0))
      return "No initial object locations available!";
    return null;
  }

  /**
   * Performs the actual initialization of the tracking.
   *
   * @param cont	the image to use for initializing
   * @param locations	the initial location(s) of the object(s)
   * @return		true if successfully initialized, error message otherwise
   */
  protected abstract String doInitTracking(AbstractImageContainer cont, List<QuadrilateralLocation> locations);

  /**
   * Initializes the tracker.
   *
   * @param cont	the image to use for initializing
   * @return		true if successfully initialized, error message otherwise
   */
  public String initTracking(AbstractImageContainer cont) {
    String			result;
    List<QuadrilateralLocation>	locations;

    m_LastLocations.clear();

    locations = getInitialLocations(cont);
    result    = checkInitTracking(cont, locations);

    if (result == null) {
      if (isLoggingEnabled())
	getLogger().info("Initializing with location=" + locations);
      result = doInitTracking(cont, locations);
    }

    m_Initialized = (result == null);
    if (!m_Initialized) {
      m_LastLocations.clear();
    }
    else {
      m_LastLocations.clear();
      m_LastLocations.addAll(locations);
    }

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
  protected String checkTrackObjects(AbstractImageContainer cont) {
    if (cont == null)
      return "No image provided for tracking!";
    return null;
  }

  /**
   * Performs the actual tracking of the object.
   *
   * @param cont	the current image
   * @return		the location(s) of the tracked object(s), null if failed to track
   */
  protected abstract List<QuadrilateralLocation> doTrackObjects(AbstractImageContainer cont);

  /**
   * Hook method for post-processing the tracked objects.
   *
   * @param cont	the current image
   * @param locations	the location(s) of the tracked object(s)
   * @return		the (potentially) updated location(s) of the tracked object(s)
   */
  protected List<QuadrilateralLocation> postProcessTrackedObjects(AbstractImageContainer cont, List<QuadrilateralLocation> locations) {
    return locations;
  }

  /**
   * Performs the tracking of the object.
   *
   * @param cont	the current image
   * @return		the location of the tracked image, null if failed to track
   */
  public List<QuadrilateralLocation> trackObjects(AbstractImageContainer cont) {
    List<QuadrilateralLocation>	result;
    String			msg;

    msg = checkTrackObjects(cont);
    if (msg != null) {
      getLogger().severe(msg);
      return null;
    }

    result = doTrackObjects(cont);

    if (result != null) {
      result          = postProcessTrackedObjects(cont, result);
      m_LastLocations = result;
    }

    if (isLoggingEnabled())
      getLogger().info("Tracked locations=" + result);

    return result;
  }
}
