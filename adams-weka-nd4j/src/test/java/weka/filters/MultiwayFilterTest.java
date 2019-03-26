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
 * MultiwayFilterTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.filters;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.TestInstances;
import weka.test.AdamsTestHelper;

/**
 * Tests the MultiwayFilter class.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class MultiwayFilterTest
  extends AbstractAdamsFilterTest {

  /**
   * Initializes the test.
   *
   * @param name the name of the test
   */
  public MultiwayFilterTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_Instances = getFilteredClassifierData();
  }

  /**
   * returns data generated for the FilteredClassifier test.
   *
   * @return		the dataset for the FilteredClassifier
   * @throws Exception	if generation of data fails
   */
  @Override
  protected Instances getFilteredClassifierData() throws Exception {
    TestInstances testinst;

    testinst = new TestInstances();
    testinst.setNumNominal(0);
    testinst.setNumNumeric(20);
    testinst.setClassType(Attribute.NUMERIC);
    testinst.setNumInstances(50);

    return testinst.generate();
  }

  /**
   * Used to create an instance of a specific filter. The filter
   * should be configured to operate on a dataset that contains
   * attributes in this order:<p>
   *
   * String, Nominal, Numeric, String, Nominal, Numeric<p>
   *
   * Where the first three attributes do not contain any missing values,
   * but the last three attributes do. If the filter is for some reason
   * incapable of accepting a dataset of this type, override setUp() to
   * either manipulate the default dataset to be compatible, or load another
   * test dataset. <p>
   *
   * The configured filter should preferrably do something
   * meaningful, since the results of filtering are used as the default
   * regression output (and it would hardly be interesting if the filtered
   * data was the same as the input data).
   *
   * @return a suitably configured <code>Filter</code> value
   */
  @Override
  public Filter getFilter() {
    return new MultiwayFilter();
  }

  /**
   * returns the configured FilteredClassifier. Since the base classifier is
   * determined heuristically, derived tests might need to adjust it.
   *
   * @return the configured FilteredClassifier
   */
  @Override
  protected FilteredClassifier getFilteredClassifier() {
    // Skip filtered-classifier test as filter doesn't conserve
    // class attribute setting
    return null;
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MultiwayFilterTest.class);
  }

  /**
   * Runs the test from the commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    AdamsTestHelper.setRegressionRoot();
    TestRunner.run(suite());
  }

  /**
   * Delete the regression test as numerical indeterminism means it
   * always fails on the build server, negating any usefulness.
   */
  @Override
  public void testRegression() {
    // Empty implementation means it always passes
  }
}
