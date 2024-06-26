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
 * MeanTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featuregenerator;

import adams.data.io.input.AbstractHeatmapReader;
import adams.data.io.input.SpreadSheetHeatmapReader;

/**
 * Tests the Mean feature generator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MeanTest
  extends AbstractFeatureGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public MeanTest(String name) {
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
  protected AbstractFeatureGenerator[] getRegressionSetups() {
    return new AbstractFeatureGenerator[]{
	new Mean()
    };
  }
}
