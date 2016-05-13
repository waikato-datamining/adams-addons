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
 * TrailWindowTest.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import adams.core.base.BaseDateTimeMsec;
import adams.data.trail.Trail;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for the TrailWindow filter. Run from the command line with: <br><br>
 * java adams.data.filter.TrailWindowTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TrailWindowTest
  extends AbstractTrailFilterTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public TrailWindowTest(String name) {
    super(name);
  }

  /**
   * Returns the configured filter.
   *
   * @return		the filter
   */
  public Filter<Trail> getFilter() {
    return new TrailWindow();
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"mouse1.trail",
	"mouse1.trail",
	"mouse1.trail",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Filter[] getRegressionSetups() {
    TrailWindow[]	result;

    result = new TrailWindow[3];

    result[0] = new TrailWindow();

    result[1] = new TrailWindow();
    result[1].setStart(new BaseDateTimeMsec("1970-01-01 12:01:00.000"));
    result[1].setEnd(new BaseDateTimeMsec("1970-01-01 12:02:59.999"));

    result[2] = new TrailWindow();
    result[2].setStart(new BaseDateTimeMsec("1970-01-01 12:02:00.000"));
    result[2].setEnd(new BaseDateTimeMsec("1970-01-01 12:10:59.999"));
    result[2].setInvert(true);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(TrailWindowTest.class);
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
