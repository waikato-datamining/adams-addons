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
 * AddTrailBackgroundTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.JAIImageReader;
import adams.data.io.output.SimpleTrailWriter;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.execution.NullListener;
import adams.flow.source.FileSupplier;
import adams.flow.source.NewTrail;
import adams.flow.source.SequenceSource;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.AbstractDataContainerFileWriter.FileNameGeneration;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for AddTrailBackground actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class AddTrailBackgroundTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AddTrailBackgroundTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("mouse1.png");
    m_TestHelper.deleteFileFromTmp("dumpfile.trail");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("mouse1.png");
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
    return new TestSuite(AddTrailBackgroundTest.class);
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

      // Flow.CallableActors
      CallableActors callableactors = new CallableActors();
      List<Actor> actors2 = new ArrayList<Actor>();

      // Flow.CallableActors.bg
      SequenceSource sequencesource = new SequenceSource();
      argOption = (AbstractArgumentOption) sequencesource.getOptionManager().findByProperty("name");
      sequencesource.setName((String) argOption.valueOf("bg"));
      List<Actor> actors3 = new ArrayList<Actor>();

      // Flow.CallableActors.bg.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<PlaceholderFile>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/mouse1.png"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors3.add(filesupplier);

      // Flow.CallableActors.bg.ImageReader
      ImageReader imagereader = new ImageReader();
      JAIImageReader jaiimagereader = new JAIImageReader();
      imagereader.setReader(jaiimagereader);

      actors3.add(imagereader);
      sequencesource.setActors(actors3.toArray(new Actor[0]));

      actors2.add(sequencesource);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.NewTrail
      NewTrail newtrail = new NewTrail();
      argOption = (AbstractArgumentOption) newtrail.getOptionManager().findByProperty("width");
      newtrail.setWidth((Float) argOption.valueOf("1280.0"));
      argOption = (AbstractArgumentOption) newtrail.getOptionManager().findByProperty("height");
      newtrail.setHeight((Float) argOption.valueOf("720.0"));
      actors.add(newtrail);

      // Flow.AddTrailBackground
      AddTrailBackground addtrailbackground = new AddTrailBackground();
      argOption = (AbstractArgumentOption) addtrailbackground.getOptionManager().findByProperty("background");
      addtrailbackground.setBackground((CallableActorReference) argOption.valueOf("bg"));
      actors.add(addtrailbackground);

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

