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
 * AddTrailStepTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseDateTimeMsec;
import adams.core.base.BaseKeyValuePair;
import adams.core.io.PlaceholderDirectory;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.output.SimpleTrailWriter;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.source.NewTrail;
import adams.flow.transformer.AbstractDataContainerFileWriter.FileNameGeneration;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for AddTrailStep actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class AddTrailStepTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AddTrailStepTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.trail");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.trail");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
      new TmpFile[]{
        new TmpFile("dumpfile.trail")
      });
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[]{0};
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(AddTrailStepTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<Actor>();

      // Flow.NewTrail
      NewTrail newtrail = new NewTrail();
      argOption = (AbstractArgumentOption) newtrail.getOptionManager().findByProperty("ID");
      newtrail.setID((String) argOption.valueOf("newtrail"));
      actors.add(newtrail);

      // Flow.AddTrailStep
      AddTrailStep addtrailstep = new AddTrailStep();
      argOption = (AbstractArgumentOption) addtrailstep.getOptionManager().findByProperty("timestamp");
      addtrailstep.setTimestamp((BaseDateTimeMsec) argOption.valueOf("2015-01-01 00:00:01.000"));
      argOption = (AbstractArgumentOption) addtrailstep.getOptionManager().findByProperty("X");
      addtrailstep.setX((Float) argOption.valueOf("1.0"));
      argOption = (AbstractArgumentOption) addtrailstep.getOptionManager().findByProperty("Y");
      addtrailstep.setY((Float) argOption.valueOf("1.0"));
      actors.add(addtrailstep);

      // Flow.AddTrailStep-1
      AddTrailStep addtrailstep2 = new AddTrailStep();
      argOption = (AbstractArgumentOption) addtrailstep2.getOptionManager().findByProperty("name");
      addtrailstep2.setName((String) argOption.valueOf("AddTrailStep-1"));
      argOption = (AbstractArgumentOption) addtrailstep2.getOptionManager().findByProperty("timestamp");
      addtrailstep2.setTimestamp((BaseDateTimeMsec) argOption.valueOf("2015-01-01 00:00:01.100"));
      argOption = (AbstractArgumentOption) addtrailstep2.getOptionManager().findByProperty("X");
      addtrailstep2.setX((Float) argOption.valueOf("2.0"));
      argOption = (AbstractArgumentOption) addtrailstep2.getOptionManager().findByProperty("Y");
      addtrailstep2.setY((Float) argOption.valueOf("10.0"));
      argOption = (AbstractArgumentOption) addtrailstep2.getOptionManager().findByProperty("metaData");
      List<BaseKeyValuePair> metadata = new ArrayList<BaseKeyValuePair>();
      metadata.add((BaseKeyValuePair) argOption.valueOf("meta1=data1"));
      metadata.add((BaseKeyValuePair) argOption.valueOf("meta2=0.0"));
      metadata.add((BaseKeyValuePair) argOption.valueOf("meta3=hello world"));
      addtrailstep2.setMetaData(metadata.toArray(new BaseKeyValuePair[0]));
      actors.add(addtrailstep2);

      // Flow.TrailFileWriter
      TrailFileWriter trailfilewriter = new TrailFileWriter();
      SimpleTrailWriter simpletrailwriter = new SimpleTrailWriter();
      trailfilewriter.setWriter(simpletrailwriter);

      argOption = (AbstractArgumentOption) trailfilewriter.getOptionManager().findByProperty("outputDir");
      trailfilewriter.setOutputDir((PlaceholderDirectory) argOption.valueOf("${TMP}"));
      argOption = (AbstractArgumentOption) trailfilewriter.getOptionManager().findByProperty("fileNameGeneration");
      trailfilewriter.setFileNameGeneration((FileNameGeneration) argOption.valueOf("SUPPLIED"));
      argOption = (AbstractArgumentOption) trailfilewriter.getOptionManager().findByProperty("suppliedFileName");
      trailfilewriter.setSuppliedFileName((String) argOption.valueOf("dumpfile.trail"));
      actors.add(trailfilewriter);
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
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}

