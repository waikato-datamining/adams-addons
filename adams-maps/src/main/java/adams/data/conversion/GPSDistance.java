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

/*
 * GPSDistance.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.gps.AbstractGPS;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;

/**
 <!-- globalinfo-start -->
 * Computes the distance in meters between two GPS points.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-type &lt;SPHERICAL_LAW_OF_COSINES|HARVESINE|VINCENTY&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of distance calculation to perform.
 * &nbsp;&nbsp;&nbsp;default: SPHERICAL_LAW_OF_COSINES
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GPSDistance
  extends AbstractConversion {

  private static final long serialVersionUID = 4042881508585524573L;

  /**
   * How to calculate the distance.
   */
  public enum DistanceCalculationType {
    SPHERICAL_LAW_OF_COSINES,
    HARVESINE,
    VINCENTY,
  }

  /** the type of calculation to perform. */
  protected DistanceCalculationType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Computes the distance in meters between two GPS points.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      DistanceCalculationType.SPHERICAL_LAW_OF_COSINES);
  }

  /**
   * Sets the type of distance calculation to perform.
   *
   * @param value	the column
   */
  public void setType(DistanceCalculationType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of distance calculation to perform.
   *
   * @return		the column
   */
  public DistanceCalculationType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of distance calculation to perform.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type);
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return AbstractGPS[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Double.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    AbstractGPS[]	gps;
    Point		point1;
    Point		point2;

    gps = (AbstractGPS[]) m_Input;
    if (gps.length != 2)
      throw new IllegalStateException("Requires two GPS points, received: " + gps.length);

    point1 = Point.at(
      Coordinate.fromDegrees(gps[0].getLatitude().toDecimal()),
      Coordinate.fromDegrees(gps[0].getLongitude().toDecimal()));
    point2 = Point.at(
      Coordinate.fromDegrees(gps[1].getLatitude().toDecimal()),
      Coordinate.fromDegrees(gps[1].getLongitude().toDecimal()));

    switch (m_Type) {
      case SPHERICAL_LAW_OF_COSINES:
        return EarthCalc.gcdDistance(point1, point2);
      case HARVESINE:
        return EarthCalc.harvesineDistance(point1, point2);
      case VINCENTY:
        return EarthCalc.vincentyDistance(point1, point2);
      default:
        throw new IllegalStateException("Unhandled distance type: " + m_Type);
    }
  }
}
