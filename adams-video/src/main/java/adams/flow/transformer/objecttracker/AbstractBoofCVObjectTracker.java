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

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for BoofCV object trackers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractBoofCVObjectTracker
  extends AbstractSimpleReportBasedObjectTracker {

  private static final long serialVersionUID = -8364076045858032972L;

  /** the image type. */
  protected BoofCVImageType m_ImageType;

  /** the tracker. */
  protected TrackerObjectQuad m_Tracker;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image-type", "imageType",
      BoofCVImageType.UNSIGNED_INT_8);
  }

  /**
   * Sets the image type to use.
   *
   * @param value	the type
   */
  public void setImageType(BoofCVImageType value) {
    m_ImageType = value;
    reset();
  }

  /**
   * Returns the image type to use.
   *
   * @return		the type
   */
  public BoofCVImageType getImageType() {
    return m_ImageType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageTypeTipText() {
    return "The image type to use.";
  }

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
   * @param locations	the initial location(s) of the object(s)
   * @return		true if successfully initialized, error message otherwise
   */
  protected String doInitTracking(AbstractImageContainer cont, List<QuadrilateralLocation> locations) {
    BoofCVImageContainer	bcont;
    ImageBase 			frame;

    bcont     = BoofCVHelper.toBoofCVImageContainer(cont, m_ImageType);
    frame     = bcont.getImage();
    m_Tracker = newTracker();
    if (!m_Tracker.initialize(frame, locations.get(0).quadrilateralValue()))
      return "Failed to initialze tracker!";

    return null;
  }

  /**
   * Performs the actual tracking of the object.
   *
   * @param cont	the current image
   * @return		the location of the tracked image, null if failed to track
   */
  protected List<QuadrilateralLocation> doTrackObjects(AbstractImageContainer cont) {
    BoofCVImageContainer	bcont;
    ImageBase 			frame;
    Quadrilateral_F64		location;
    boolean			visible;

    bcont           = BoofCVHelper.toBoofCVImageContainer(cont, m_ImageType);
    frame           = bcont.getImage();
    location        = m_LastLocations.get(0).quadrilateralValue();
    visible         = m_Tracker.process(frame, location);
    m_LastLocations = new ArrayList<>();
    m_LastLocations.add(new QuadrilateralLocation(location));
    if (isLoggingEnabled())
      getLogger().info("visible=" + visible);

    return m_LastLocations;
  }
}
