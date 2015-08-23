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
 * MultiObjectTracker.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.objecttracker;

import adams.core.base.QuadrilateralLocation;
import adams.data.image.AbstractImageContainer;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Applies all the specified trackers to the image container.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-tracker &lt;adams.flow.transformer.objecttracker.ObjectTracker&gt; [-tracker ...] (property: trackers)
 * &nbsp;&nbsp;&nbsp;The trackers to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiObjectTracker
  extends AbstractObjectTracker {

  private static final long serialVersionUID = -5332153959179226936L;

  /** the trackers to use. */
  protected ObjectTracker[] m_Trackers;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies all the specified trackers to the image container.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "tracker", "trackers",
      new ObjectTracker[0]);
  }

  /**
   * Sets the trackers to use.
   *
   * @param value	the trackers
   */
  public void setTrackers(ObjectTracker[] value) {
    m_Trackers = value;
    reset();
  }

  /**
   * Returns the trackers to use.
   *
   * @return		the trackers
   */
  public ObjectTracker[] getTrackers() {
    return m_Trackers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trackersTipText() {
    return "The trackers to use.";
  }

  /**
   * Returns the initial object locations.
   *
   * @param cont	the current image container
   * @return		returns a bogus list
   */
  @Override
  protected List<QuadrilateralLocation> getInitialLocations(AbstractImageContainer cont) {
    List<QuadrilateralLocation>		result;

    result = new ArrayList<>();
    result.add(new QuadrilateralLocation());

    return result;
  }

  /**
   * Performs the actual initialization of the tracking.
   *
   * @param cont	the image to use for initializing
   * @param locations	the initial location(s) of the object(s)
   * @return		true if successfully initialized, error message otherwise
   */
  @Override
  protected String doInitTracking(AbstractImageContainer cont, List<QuadrilateralLocation> locations) {
    String		result;
    int			i;
    ObjectTracker	tracker;

    result = null;

    for (i = 0; i < m_Trackers.length; i++) {
      tracker = m_Trackers[i];
      result  = tracker.initTracking(cont);
      if (result != null)
	result = "Tracker #" + (i+1) + ": " + result;
    }

    return result;
  }

  /**
   * Performs the actual tracking of the object.
   *
   * @param cont	the current image
   * @return		the location(s) of the tracked object(s), null if failed to track
   */
  @Override
  protected List<QuadrilateralLocation> doTrackObjects(AbstractImageContainer cont) {
    List<QuadrilateralLocation>		result;
    List<QuadrilateralLocation>		locations;
    int					i;
    ObjectTracker			tracker;

    result = new ArrayList<>();

    for (i = 0; i < m_Trackers.length; i++) {
      tracker = m_Trackers[i];
      locations = tracker.trackObjects(cont);
      if (locations != null)
	result.addAll(locations);
    }

    return result;
  }
}
