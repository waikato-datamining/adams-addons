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
 * HeatmapDisplayTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for HeatmapDisplay actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class HeatmapDisplayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public HeatmapDisplayTest(String name) {
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
    return new TestSuite(HeatmapDisplayTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[3];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/3666455665_18795f0741_small.jpg");
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

      // Flow.HeatmapDisplay
      adams.flow.sink.HeatmapDisplay heatmapdisplay9 = new adams.flow.sink.HeatmapDisplay();
      argOption = (AbstractArgumentOption) heatmapdisplay9.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter nullwriter11 = new adams.gui.print.NullWriter();
      heatmapdisplay9.setWriter(nullwriter11);

      argOption = (AbstractArgumentOption) heatmapdisplay9.getOptionManager().findByProperty("colorGenerator");
      adams.gui.visualization.core.BiColorGenerator bicolorgenerator13 = new adams.gui.visualization.core.BiColorGenerator();
      heatmapdisplay9.setColorGenerator(bicolorgenerator13);

      actors1[2] = heatmapdisplay9;
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

