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
 * RSinkTest.java
 * Copyright (C) 2013-2021 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.NumberToDouble;
import adams.env.Environment;
import adams.flow.AbstractRFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.transformer.Convert;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for RSink actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class RSinkTest
  extends AbstractRFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public RSinkTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt")
        });
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(RSinkTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;

    Flow flow = new Flow();

    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor1 = new adams.flow.core.Actor[6];

      // Flow.Rserve
      adams.flow.standalone.Rserve rserve2 = new adams.flow.standalone.Rserve();
      abstractactor1[0] = rserve2;

      // Flow.number of random numbers
      adams.flow.standalone.SetVariable setvariable3 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("name");
      setvariable3.setName((java.lang.String) argOption.valueOf("number of random numbers"));

      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableName");
      setvariable3.setVariableName((adams.core.VariableName) argOption.valueOf("num"));

      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableValue");
      setvariable3.setVariableValue((BaseText) argOption.valueOf("200"));

      abstractactor1[1] = setvariable3;

      // Flow.RandomNumberGenerator
      adams.flow.source.RandomNumberGenerator randomnumbergenerator7 = new adams.flow.source.RandomNumberGenerator();
      argOption = (AbstractArgumentOption) randomnumbergenerator7.getOptionManager().findByProperty("generator");
      adams.data.random.JavaRandomInt javarandomint9 = new adams.data.random.JavaRandomInt();
      randomnumbergenerator7.setGenerator(javarandomint9);

      argOption = (AbstractArgumentOption) randomnumbergenerator7.getOptionManager().findByProperty("maxNum");
      argOption.setVariable("@{num}");

      abstractactor1[2] = randomnumbergenerator7;

      Convert conv = new Convert();
      conv.setConversion(new NumberToDouble());
      abstractactor1[3] = conv;

      // Flow.SequenceToArray
      adams.flow.transformer.SequenceToArray sequencetoarray10 = new adams.flow.transformer.SequenceToArray();
      argOption = (AbstractArgumentOption) sequencetoarray10.getOptionManager().findByProperty("arrayLength");
      argOption.setVariable("@{num}");

      abstractactor1[4] = sequencetoarray10;

      // Flow.RSink
      adams.flow.sink.RSink rsink11 = new adams.flow.sink.RSink();
      argOption = (AbstractArgumentOption) rsink11.getOptionManager().findByProperty("inlineScript");
      rsink11.setInlineScript((adams.core.scripting.RScript) argOption.valueOf("\nwrite.table(X, file=\"/tmp/dumpfile.txt\")\n"));

      abstractactor1[5] = rsink11;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener14 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener14);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }

    return flow;
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}

