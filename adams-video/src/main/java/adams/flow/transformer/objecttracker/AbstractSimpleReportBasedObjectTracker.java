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
 * AbstractSimpleReportBasedObjectTracker.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.objecttracker;

import adams.core.QuickInfoHelper;
import adams.core.base.QuadrilateralLocation;
import adams.data.image.AbstractImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for trackers that use fields from the image report to initialize
 * with an initial position and store the current, tracked position.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSimpleReportBasedObjectTracker
  extends AbstractObjectTracker {

  private static final long serialVersionUID = -3358731361747308499L;

  /** the report field with the location to initialize the tracker with. */
  protected Field m_Init;

  /** the report field to store the tracked location in. */
  protected Field m_Current;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "init", "init",
      new Field("Tracker.Init", DataType.STRING));

    m_OptionManager.add(
      "current", "current",
      new Field("Tracker.Current", DataType.STRING));
  }

  /**
   * Sets the field with the location for initializing the tracker.
   *
   * @param value	the field
   */
  public void setInit(Field value) {
    m_Init = value;
    reset();
  }

  /**
   * Returns the field with the location for initializing the tracker.
   *
   * @return		the field
   */
  public Field getInit() {
    return m_Init;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initTipText() {
    return "The field with the initial object location.";
  }

  /**
   * Sets the field to store the current location of the object in.
   *
   * @param value	the field
   */
  public void setCurrent(Field value) {
    m_Current = value;
    reset();
  }

  /**
   * Returns the field to store the current location of the object in.
   *
   * @return		the field
   */
  public Field getCurrent() {
    return m_Current;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String currentTipText() {
    return "The field to store the current location of the object in.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "init", m_Init, "init: ");
    result += QuickInfoHelper.toString(this, "current", m_Current, ", current: ");

    return result;
  }

  /**
   * Returns the initial object locations.
   *
   * @param cont	the current image container
   * @return		the locations, null if failed to determine
   */
  protected List<QuadrilateralLocation> getInitialLocations(AbstractImageContainer cont) {
    List<QuadrilateralLocation>	result;
    QuadrilateralLocation	location;

    result = new ArrayList<>();

    if (cont.getReport().hasValue(m_Init)) {
      location = new QuadrilateralLocation(cont.getReport().getStringValue(m_Init));
      result.add(location);
    }
    else {
      getLogger().severe("Failed to locate initial location: " + m_Init);
      result = null;
    }

    return result;
  }

  /**
   * Hook method for post-processing the tracked objects.
   *
   * @param cont	the current image
   * @param locations	the location(s) of the tracked object(s)
   * @return		the (potentially) updated location(s) of the tracked object(s)
   */
  protected List<QuadrilateralLocation> postProcessTrackedObjects(AbstractImageContainer cont, List<QuadrilateralLocation> locations) {
    if (locations.size() > 0) {
      cont.getReport().addField(m_Current);
      cont.getReport().setValue(m_Current, locations.get(0).toString());
    }
    return locations;
  }
}
