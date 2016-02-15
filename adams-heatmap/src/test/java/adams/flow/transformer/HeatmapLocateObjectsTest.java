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
 * HeatmapLocateObjectsTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for HeatmapLocateObjects actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class HeatmapLocateObjectsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public HeatmapLocateObjectsTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("particles.jpg");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("particles.jpg");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    if (m_Headless)
      return;
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
    return new TestSuite(HeatmapLocateObjectsTest.class);
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
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[4];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/particles.jpg");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.HeatmapFileReader
      adams.flow.transformer.HeatmapFileReader heatmapfilereader4 = new adams.flow.transformer.HeatmapFileReader();
      argOption = (AbstractArgumentOption) heatmapfilereader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.SimpleImageHeatmapReader simpleimageheatmapreader6 = new adams.data.io.input.SimpleImageHeatmapReader();
      simpleimageheatmapreader6.setUseAbsoluteSource(false);
      argOption = (AbstractArgumentOption) simpleimageheatmapreader6.getOptionManager().findByProperty("reader");
      adams.data.io.input.JAIImageReader jaiimagereader8 = new adams.data.io.input.JAIImageReader();
      simpleimageheatmapreader6.setReader(jaiimagereader8);

      heatmapfilereader4.setReader(simpleimageheatmapreader6);

      actors1[1] = heatmapfilereader4;

      // Flow.HeatmapLocateObjects
      adams.flow.transformer.HeatmapLocateObjects heatmaplocateobjects9 = new adams.flow.transformer.HeatmapLocateObjects();
      argOption = (AbstractArgumentOption) heatmaplocateobjects9.getOptionManager().findByProperty("conversion");
      adams.data.conversion.HeatmapToBufferedImage heatmaptobufferedimage11 = new adams.data.conversion.HeatmapToBufferedImage();
      argOption = (AbstractArgumentOption) heatmaptobufferedimage11.getOptionManager().findByProperty("generator");
      adams.gui.visualization.core.BiColorGenerator bicolorgenerator13 = new adams.gui.visualization.core.BiColorGenerator();
      heatmaptobufferedimage11.setGenerator(bicolorgenerator13);

      heatmaplocateobjects9.setConversion(heatmaptobufferedimage11);

      argOption = (AbstractArgumentOption) heatmaplocateobjects9.getOptionManager().findByProperty("locator");
      adams.flow.transformer.locateobjects.BinaryContours binarycontours15 = new adams.flow.transformer.locateobjects.BinaryContours();
      heatmaplocateobjects9.setLocator(binarycontours15);

      heatmaplocateobjects9.setGenerateReport(true);

      actors1[2] = heatmaplocateobjects9;

      // Flow.ReportFileWriter
      adams.flow.transformer.ReportFileWriter reportfilewriter16 = new adams.flow.transformer.ReportFileWriter();
      argOption = (AbstractArgumentOption) reportfilewriter16.getOptionManager().findByProperty("writer");
      adams.data.io.output.DefaultSimpleCSVReportWriter defaultsimplecsvreportwriter18 = new adams.data.io.output.DefaultSimpleCSVReportWriter();
      reportfilewriter16.setWriter(defaultsimplecsvreportwriter18);

      argOption = (AbstractArgumentOption) reportfilewriter16.getOptionManager().findByProperty("filenameGenerator");
      adams.core.io.SimpleFixedFilenameGenerator simplefixedfilenamegenerator20 = new adams.core.io.SimpleFixedFilenameGenerator();
      argOption = (AbstractArgumentOption) simplefixedfilenamegenerator20.getOptionManager().findByProperty("name");
      simplefixedfilenamegenerator20.setName((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      reportfilewriter16.setFilenameGenerator(simplefixedfilenamegenerator20);

      actors1[3] = reportfilewriter16;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener23 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener23);

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

