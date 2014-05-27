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
 * MekaPrepareDataTest.java
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
 * Test for MekaPrepareData actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class MekaPrepareDataTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MekaPrepareDataTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("Music.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.arff")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MekaPrepareDataTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[4];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/Music.arff");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader4 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader4.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader6 = new weka.core.converters.ArffLoader();
      wekafilereader4.setCustomLoader(arffloader6);
      wekafilereader4.setUseCustomLoader(true);

      actors1[1] = wekafilereader4;

      // Flow.MekaPrepareData
      adams.flow.transformer.MekaPrepareData mekapreparedata7 = new adams.flow.transformer.MekaPrepareData();
      actors1[2] = mekapreparedata7;

      // Flow.WekaFileWriter
      adams.flow.sink.WekaFileWriter wekafilewriter8 = new adams.flow.sink.WekaFileWriter();
      argOption = (AbstractArgumentOption) wekafilewriter8.getOptionManager().findByProperty("outputFile");
      wekafilewriter8.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.arff"));
      argOption = (AbstractArgumentOption) wekafilewriter8.getOptionManager().findByProperty("customSaver");
      weka.core.converters.ArffSaver arffsaver11 = new weka.core.converters.ArffSaver();
      arffsaver11.setOptions(OptionUtils.splitOptions("-decimal 6"));
      wekafilewriter8.setCustomSaver(arffsaver11);
      wekafilewriter8.setUseCustomSaver(true);

      actors1[3] = wekafilewriter8;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener13 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener13);

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

