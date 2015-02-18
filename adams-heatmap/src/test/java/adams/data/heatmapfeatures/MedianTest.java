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
 * MedianTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.heatmapfeatures;

import adams.data.io.input.AbstractHeatmapReader;
import adams.data.io.input.SpreadSheetHeatmapReader;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the Median feature generator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6879 $
 */
public class MedianTest
  extends AbstractHeatmapFeatureGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public MedianTest(String name) {
    super(name);
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
	"simple.csv"
    };
  }

  /**
   * Returns the readers to use on the corresponding input files.
   *
   * @return		the readers
   * @see		#getRegressionInputFiles()
   */
  @Override
  protected AbstractHeatmapReader[] getRegressionInputFileReaders() {
    return new AbstractHeatmapReader[]{
	new SpreadSheetHeatmapReader()
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractHeatmapFeatureGenerator[] getRegressionSetups() {
    return new AbstractHeatmapFeatureGenerator[]{
	new Median()
    };
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MedianTest.class);
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
