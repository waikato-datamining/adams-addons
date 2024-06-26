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
 * MOAClassifierSetupTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import junit.framework.Test;
import junit.framework.TestSuite;
import moa.options.ClassOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.DumpFile;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.MOATrainClassifier;
import adams.flow.transformer.WekaClassSelector;
import adams.flow.transformer.WekaFileReader;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.test.TmpFile;
import moa.streams.ExampleStream;

/**
 * Tests the MOAClassifierSetup actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOAClassifierSetupTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MOAClassifierSetupTest(String name) {
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

    m_TestHelper.copyResourceToTmp("iris.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    CallableActors ga = new CallableActors();
    
    ClassOption option = new ClassOption(
	"classifier",
	'c',
	"The MOA classifier to use from within ADAMS.",
	moa.classifiers.Classifier.class,
	"trees.DecisionStump",
	"moa.classifiers.trees.DecisionStump");

    MOAClassifierSetup cls = new MOAClassifierSetup();
    cls.setName("cls");
    cls.setClassifier(option);
    ga.add(cls);

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

    MOATrainClassifier cts = new MOATrainClassifier();
    cts.setClassifier(new CallableActorReference("cls"));
    cts.setOutputInterval(150);  // 150 is the number of instances in the dataset

    DumpFile df = new DumpFile();
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Flow flow = new Flow();
    flow.setActors(new Actor[]{ga, stream, cts, df});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.txt"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MOAClassifierSetupTest.class);
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
