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
 * MekaTrainTestSetEvaluatorTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for MekaTrainTestSetEvaluator actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class MekaTrainTestSetEvaluatorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MekaTrainTestSetEvaluatorTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("Music.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("Music.arff");
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
    return new TestSuite(MekaTrainTestSetEvaluatorTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[9];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[1];

      // Flow.CallableActors.MekaClassifierSetup
      adams.flow.source.MekaClassifierSetup mekaclassifiersetup4 = new adams.flow.source.MekaClassifierSetup();
      argOption = (AbstractArgumentOption) mekaclassifiersetup4.getOptionManager().findByProperty("classifier");
      meka.classifiers.multilabel.BR br6 = new meka.classifiers.multilabel.BR();
      br6.setOptions(OptionUtils.splitOptions("-W weka.classifiers.rules.ZeroR"));
      mekaclassifiersetup4.setClassifier(br6);

      actors3[0] = mekaclassifiersetup4;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier7 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier7.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files8 = new adams.core.io.PlaceholderFile[1];
      files8[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/Music.arff");
      filesupplier7.setFiles(files8);
      actors1[1] = filesupplier7;

      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader9 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader9.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader11 = new weka.core.converters.ArffLoader();
      wekafilereader9.setCustomLoader(arffloader11);
      wekafilereader9.setUseCustomLoader(true);

      actors1[2] = wekafilereader9;

      // Flow.MekaPrepareData
      adams.flow.transformer.MekaPrepareData mekapreparedata12 = new adams.flow.transformer.MekaPrepareData();
      actors1[3] = mekapreparedata12;

      // Flow.WekaRandomSplit
      adams.flow.transformer.WekaRandomSplit wekarandomsplit13 = new adams.flow.transformer.WekaRandomSplit();
      actors1[4] = wekarandomsplit13;

      // Flow.MekaTrainTestSetEvaluator
      adams.flow.transformer.MekaTrainTestSetEvaluator mekatraintestsetevaluator14 = new adams.flow.transformer.MekaTrainTestSetEvaluator();
      actors1[5] = mekatraintestsetevaluator14;

      // Flow.MekaResultSummary
      adams.flow.transformer.MekaResultSummary mekaresultsummary15 = new adams.flow.transformer.MekaResultSummary();
      actors1[6] = mekaresultsummary15;

      // Flow.SubProcess
      adams.flow.control.SubProcess subprocess16 = new adams.flow.control.SubProcess();
      argOption = (AbstractArgumentOption) subprocess16.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors17 = new adams.flow.core.AbstractActor[3];

      // Flow.SubProcess.StringSplit
      adams.flow.transformer.StringSplit stringsplit18 = new adams.flow.transformer.StringSplit();
      argOption = (AbstractArgumentOption) stringsplit18.getOptionManager().findByProperty("expression");
      stringsplit18.setExpression((java.lang.String) argOption.valueOf("\\n"));
      actors17[0] = stringsplit18;

      // Flow.SubProcess.StringMatcher
      adams.flow.transformer.StringMatcher stringmatcher20 = new adams.flow.transformer.StringMatcher();
      argOption = (AbstractArgumentOption) stringmatcher20.getOptionManager().findByProperty("regExp");
      stringmatcher20.setRegExp((adams.core.base.BaseRegExp) argOption.valueOf(".*time.*"));
      stringmatcher20.setInvert(true);

      actors17[1] = stringmatcher20;

      // Flow.SubProcess.StringJoin
      adams.flow.transformer.StringJoin stringjoin22 = new adams.flow.transformer.StringJoin();
      argOption = (AbstractArgumentOption) stringjoin22.getOptionManager().findByProperty("glue");
      stringjoin22.setGlue((java.lang.String) argOption.valueOf("\\n"));
      actors17[2] = stringjoin22;
      subprocess16.setActors(actors17);

      actors1[7] = subprocess16;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile24 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile24.getOptionManager().findByProperty("outputFile");
      dumpfile24.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors1[8] = dumpfile24;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener27 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener27);

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

