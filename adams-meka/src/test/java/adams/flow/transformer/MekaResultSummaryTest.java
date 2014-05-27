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
 * MekaResultSummaryTest.java
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
 * Test for MekaResultSummary actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class MekaResultSummaryTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MekaResultSummaryTest(String name) {
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
    return new TestSuite(MekaResultSummaryTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[8];

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

      // Flow.MekaCrossValidationEvaluator
      adams.flow.transformer.MekaCrossValidationEvaluator mekacrossvalidationevaluator13 = new adams.flow.transformer.MekaCrossValidationEvaluator();
      actors1[4] = mekacrossvalidationevaluator13;

      // Flow.MekaResultSummary
      adams.flow.transformer.MekaResultSummary mekaresultsummary14 = new adams.flow.transformer.MekaResultSummary();
      actors1[5] = mekaresultsummary14;

      // Flow.SubProcess
      adams.flow.control.SubProcess subprocess15 = new adams.flow.control.SubProcess();
      argOption = (AbstractArgumentOption) subprocess15.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors16 = new adams.flow.core.AbstractActor[3];

      // Flow.SubProcess.StringSplit
      adams.flow.transformer.StringSplit stringsplit17 = new adams.flow.transformer.StringSplit();
      argOption = (AbstractArgumentOption) stringsplit17.getOptionManager().findByProperty("expression");
      stringsplit17.setExpression((java.lang.String) argOption.valueOf("\\n"));
      actors16[0] = stringsplit17;

      // Flow.SubProcess.StringMatcher
      adams.flow.transformer.StringMatcher stringmatcher19 = new adams.flow.transformer.StringMatcher();
      argOption = (AbstractArgumentOption) stringmatcher19.getOptionManager().findByProperty("regExp");
      stringmatcher19.setRegExp((adams.core.base.BaseRegExp) argOption.valueOf(".*time.*"));
      stringmatcher19.setInvert(true);

      actors16[1] = stringmatcher19;

      // Flow.SubProcess.StringJoin
      adams.flow.transformer.StringJoin stringjoin21 = new adams.flow.transformer.StringJoin();
      argOption = (AbstractArgumentOption) stringjoin21.getOptionManager().findByProperty("glue");
      stringjoin21.setGlue((java.lang.String) argOption.valueOf("\\n"));
      actors16[2] = stringjoin21;
      subprocess15.setActors(actors16);

      actors1[6] = subprocess15;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile23 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile23.getOptionManager().findByProperty("outputFile");
      dumpfile23.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors1[7] = dumpfile23;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener26 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener26);

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

