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
 * DL4JModelSetupTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.DL4JModelToYaml;
import adams.env.Environment;
import adams.flow.AbstractDL4JFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.transformer.Convert;
import adams.ml.dl4j.model.SimpleMultiLayerNetwork;
import adams.ml.dl4j.modelgenerator.Configurator;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for DL4JModelGenerator actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class DL4JModelGeneratorTest
  extends AbstractDL4JFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DL4JModelGeneratorTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  // TODO Disabled for now, different results on build server?
  /*
  public void testRegression() {
    performRegressionTest(
      new TmpFile[]{
	new TmpFile("dumpfile.txt")
      });
  }
  */

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DL4JModelGeneratorTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.DL4JModelConfigurator
      DL4JModelGenerator dl4jmodelsetup = new DL4JModelGenerator();
      SimpleMultiLayerNetwork simplemultilayernetwork = new SimpleMultiLayerNetwork();
      Configurator conf = new Configurator();
      conf.setModelConfigurator(simplemultilayernetwork);
      dl4jmodelsetup.setModelGenerator(conf);

      actors.add(dl4jmodelsetup);

      // Flow.Convert
      Convert convert = new Convert();
      DL4JModelToYaml toyaml = new DL4JModelToYaml();
      convert.setConversion(toyaml);

      actors.add(convert);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);
      actors.add(dumpfile);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

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
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}

