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
 * TrailFilterTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseDateTimeMsec;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.filter.TrailWindow;
import adams.data.io.input.SimpleTrailReader;
import adams.data.io.output.SimpleTrailWriter;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.execution.NullListener;
import adams.flow.source.FileSupplier;
import adams.flow.transformer.AbstractDataContainerFileWriter.FileNameGeneration;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for TrailFilter actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TrailFilterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TrailFilterTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("mouse1.trail");
    m_TestHelper.deleteFileFromTmp("dumpfile.trail");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("mouse1.trail");
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
    return new TestSuite(TrailFilterTest.class);
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
      List<AbstractActor> actors = new ArrayList<AbstractActor>();

      // Flow.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<PlaceholderFile>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/mouse1.trail"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.TrailFileReader
      TrailFileReader trailfilereader = new TrailFileReader();
      SimpleTrailReader simpletrailreader = new SimpleTrailReader();
      trailfilereader.setReader(simpletrailreader);

      actors.add(trailfilereader);

      // Flow.TrailFilter
      TrailFilter trailfilter = new TrailFilter();
      TrailWindow trailwindow = new TrailWindow();
      argOption = (AbstractArgumentOption) trailwindow.getOptionManager().findByProperty("start");
      trailwindow.setStart((BaseDateTimeMsec) argOption.valueOf("1970-01-01 12:01:25.200"));
      argOption = (AbstractArgumentOption) trailwindow.getOptionManager().findByProperty("end");
      trailwindow.setEnd((BaseDateTimeMsec) argOption.valueOf("1970-01-01 12:01:38.831"));
      trailfilter.setFilter(trailwindow);

      actors.add(trailfilter);

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
      flow.setActors(actors.toArray(new AbstractActor[0]));

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

