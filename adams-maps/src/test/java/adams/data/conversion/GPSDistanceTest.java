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
 * GPSDistanceTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.data.conversion.GPSDistance.DistanceCalculationType;
import adams.data.gps.AbstractGPS;
import adams.data.gps.GPSDecimalDegrees;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the GPSDistance conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GPSDistanceTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public GPSDistanceTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    return new Object[]{
      new AbstractGPS[]{
        new GPSDecimalDegrees(-36.848448, 174.7600023),   // AKL Sky Tower
        new GPSDecimalDegrees(-36.848448, 174.7600023),   // AKL Sky Tower
      },
      new AbstractGPS[]{
        new GPSDecimalDegrees(-36.848448, 174.7600023),   // AKL Sky Tower
        new GPSDecimalDegrees(-41.2784228, 174.7745033),   // WEL Beehive
      },
      new AbstractGPS[]{
        new GPSDecimalDegrees(-36.848448, 174.7600023),   // AKL Sky Tower
        new GPSDecimalDegrees(-37.7874157, 175.3169905),   // UoW CS
      },
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    GPSDistance[]	result;
    
    result    = new GPSDistance[3];
    
    result[0] = new GPSDistance();

    result[1] = new GPSDistance();
    result[1].setType(DistanceCalculationType.HARVESINE);

    result[2] = new GPSDistance();
    result[2].setType(DistanceCalculationType.VINCENTY);

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(GPSDistanceTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
