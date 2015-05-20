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
 * HeatmapNormalizeToFieldTest.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.heatmap.Heatmap;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.env.Environment;

/**
 * Test class for the HeatmapNormalizeToField filter. Run from the command line with: <br><br>
 * java adams.data.filter.HeatmapNormalizeToFieldTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HeatmapNormalizeToFieldTest
  extends AbstractHeatmapFilterTestCase {

  /** the field to use. */
  public final static String FIELD = "Blah";
  
  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public HeatmapNormalizeToFieldTest(String name) {
    super(name);
  }
  
  /**
   * Loads the data to process.
   *
   * @param filename	the filename to load (without path)
   * @return		the data, null if it could not be loaded
   */
  @Override
  protected Heatmap load(String filename) {
    Heatmap	result;
    
    result = super.load(filename);
    if (result != null) {
      result.getReport().addField(new Field(FIELD, DataType.NUMERIC));
      result.getReport().setNumericValue(FIELD, 10);
    }
    
    return result;
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
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractFilter[] getRegressionSetups() {
    HeatmapNormalizeToField[]	result;
    
    result    = new HeatmapNormalizeToField[1];
    result[0] = new HeatmapNormalizeToField();
    result[0].setField(new Field(FIELD, DataType.NUMERIC));
    
    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(HeatmapNormalizeToFieldTest.class);
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
