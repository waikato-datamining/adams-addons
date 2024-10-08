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
 * MOAClassifyingTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.conversion.WEKAInstancesToMOAInstances;
import adams.flow.source.MOAStream;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.source.FileSupplier;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.test.TmpFile;
import moa.options.ClassOption;
import moa.streams.ExampleStream;

/**
 * Tests the MOAClassifying actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOAClassifyingTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MOAClassifyingTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method. Copies from resource files into
   * the tmp directory
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("iris.arff");
    m_TestHelper.copyResourceToTmp("naivebayes.model");
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris.arff");
    m_TestHelper.deleteFileFromTmp("naivebayes.model");
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    ClassOption arffOption = new ClassOption(
      "stream",
      's',
      "The MOA stream generator to use from within ADAMS.",
      ExampleStream.class,
      "ArffFileStream -f (" + new TmpFile("iris.arff").getAbsolutePath() + ")",
      "moa.streams.ArffFileStream");

    MOAStream stream = new MOAStream();
    stream.setNumExamples(-1);
    stream.setStreamGenerator(arffOption);

    MOAClassifying cls = new MOAClassifying();
    cls.setOutputInstance(true);
    cls.setModelFile(new TmpFile("naivebayes.model"));

    MOAInstanceDumper id = new MOAInstanceDumper();
    id.setOutputPrefix(new TmpFile("dumpfile"));

    Flow flow = new Flow();
    flow.setActors(new Actor[]{stream, cls, id});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.arff"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MOAClassifyingTest.class);
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
