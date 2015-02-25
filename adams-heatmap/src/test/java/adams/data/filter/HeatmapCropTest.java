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
 * HeatmapCropTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import adams.data.filter.heatmapcrop.CropToCentroid;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for the HeatmapCrop filter. Run from the command line with: <p/>
 * java adams.data.filter.HeatmapCropTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapCropTest
  extends AbstractHeatmapFilterTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public HeatmapCropTest(String name) {
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
	"simple.csv",
	"simple.csv",
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractFilter[] getRegressionSetups() {
    HeatmapCrop[]	result;
    CropToCentroid	crop2centr;
    
    result    = new HeatmapCrop[2];
    result[0] = new HeatmapCrop();
    result[1] = new HeatmapCrop();
    crop2centr = new CropToCentroid();
    crop2centr.setWidth(3);
    crop2centr.setHeight(3);
    result[0].setAlgorithm(crop2centr);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(HeatmapCropTest.class);
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
