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
 * QuadrilateralLocationTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.core.base.QuadrilateralLocation class. Run from commandline with: <br><br>
 * java adams.core.base.QuadrilateralLocationTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class QuadrilateralLocationTest
  extends AbstractBaseObjectTestCase<QuadrilateralLocation> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public QuadrilateralLocationTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected QuadrilateralLocation getDefault() {
    return new QuadrilateralLocation();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected QuadrilateralLocation getCustom(String s) {
    return new QuadrilateralLocation(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "276.00 159.00 362.00 163.00 358.00 292.00 273.00 289.00";
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(QuadrilateralLocationTest.class);
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
