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
 * SimpleImageHeatmapWriterTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.data.io.input.SimpleImageHeatmapWriter class. Run from commandline with: <br><br>
 * java adams.data.io.input.SimpleImageHeatmapWriter
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6887 $
 */
public class SimpleImageHeatmapWriterTest
  extends AbstractHeatmapWriterTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SimpleImageHeatmapWriterTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the setup tests.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getInputFiles() {
    return new String[]{
	"simple.csv",
    };
  }

  /**
   * Returns the filenames (without path) of the output data files to use
   * in the setup tests.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getOutputFiles() {
    return new String[]{
	"simple-out.png",
    };
  }

  /**
   * Returns the setups to use in the setup tests.
   *
   * @return		the setups
   */
  @Override
  protected AbstractHeatmapWriter[] getSetups() {
    SimpleImageHeatmapWriter[]	result;
    
    result = new SimpleImageHeatmapWriter[1];
    result[0] = new SimpleImageHeatmapWriter();
    
    return result;
  }

  /**
   * Returns whether a regression can be run.
   *
   * @return		always true
   */
  @Override
  protected boolean hasRegressionTest() {
    return true;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SimpleImageHeatmapWriterTest.class);
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
