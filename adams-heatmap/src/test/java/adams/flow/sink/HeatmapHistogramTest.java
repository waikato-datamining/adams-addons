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
 * HeatmapHistogramTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for HeatmapHistogram actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class HeatmapHistogramTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public HeatmapHistogramTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("3666455665_18795f0741_small.jpg");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("3666455665_18795f0741_small.jpg");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HeatmapHistogramTest.class);
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
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[3];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${EXAMPLE_FLOWS}/data/3666455665_18795f0741_small.jpg");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.HeatmapFileReader
      adams.flow.transformer.HeatmapFileReader heatmapfilereader4 = new adams.flow.transformer.HeatmapFileReader();
      argOption = (AbstractArgumentOption) heatmapfilereader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.SimpleImageHeatmapReader simpleimageheatmapreader6 = new adams.data.io.input.SimpleImageHeatmapReader();
      argOption = (AbstractArgumentOption) simpleimageheatmapreader6.getOptionManager().findByProperty("reader");
      adams.data.io.input.JAIImageReader jaiimagereader8 = new adams.data.io.input.JAIImageReader();
      simpleimageheatmapreader6.setReader(jaiimagereader8);

      heatmapfilereader4.setReader(simpleimageheatmapreader6);

      actors1[1] = heatmapfilereader4;

      // Flow.HeatmapHistogram
      adams.flow.sink.HeatmapHistogram heatmaphistogram9 = new adams.flow.sink.HeatmapHistogram();
      heatmaphistogram9.setShortTitle(true);

      argOption = (AbstractArgumentOption) heatmaphistogram9.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter nullwriter11 = new adams.gui.print.NullWriter();
      heatmaphistogram9.setWriter(nullwriter11);

      argOption = (AbstractArgumentOption) heatmaphistogram9.getOptionManager().findByProperty("arrayHistogram");
      adams.data.statistics.ArrayHistogram arrayhistogram13 = new adams.data.statistics.ArrayHistogram();
      heatmaphistogram9.setArrayHistogram(arrayhistogram13);

      actors1[2] = heatmaphistogram9;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener15 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener15);

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

