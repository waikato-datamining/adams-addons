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
 * HeatmapSetValueTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for HeatmapSetValue actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class HeatmapSetValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public HeatmapSetValueTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HeatmapSetValueTest.class);
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

      // Flow.NewHeatmap
      adams.flow.source.NewHeatmap newheatmap2 = new adams.flow.source.NewHeatmap();
      argOption = (AbstractArgumentOption) newheatmap2.getOptionManager().findByProperty("rows");
      newheatmap2.setRows((Integer) argOption.valueOf("24"));
      argOption = (AbstractArgumentOption) newheatmap2.getOptionManager().findByProperty("columns");
      newheatmap2.setColumns((Integer) argOption.valueOf("32"));
      argOption = (AbstractArgumentOption) newheatmap2.getOptionManager().findByProperty("value");
      newheatmap2.setValue((Double) argOption.valueOf("255.0"));
      argOption = (AbstractArgumentOption) newheatmap2.getOptionManager().findByProperty("ID");
      newheatmap2.setID((java.lang.String) argOption.valueOf("dumpfile"));
      actors1[0] = newheatmap2;

      // Flow.HeatmapSetValue
      adams.flow.transformer.HeatmapSetValue heatmapsetvalue7 = new adams.flow.transformer.HeatmapSetValue();
      heatmapsetvalue7.setNoCopy(true);

      argOption = (AbstractArgumentOption) heatmapsetvalue7.getOptionManager().findByProperty("row");
      heatmapsetvalue7.setRow((adams.core.Range) argOption.valueOf("5-20"));
      argOption = (AbstractArgumentOption) heatmapsetvalue7.getOptionManager().findByProperty("column");
      heatmapsetvalue7.setColumn((adams.core.Range) argOption.valueOf("10-23"));
      actors1[1] = heatmapsetvalue7;

      // Flow.HeatmapFileWriter
      adams.flow.transformer.HeatmapFileWriter heatmapfilewriter10 = new adams.flow.transformer.HeatmapFileWriter();
      argOption = (AbstractArgumentOption) heatmapfilewriter10.getOptionManager().findByProperty("writer");
      adams.data.io.output.SpreadSheetHeatmapWriter spreadsheetheatmapwriter12 = new adams.data.io.output.SpreadSheetHeatmapWriter();
      argOption = (AbstractArgumentOption) spreadsheetheatmapwriter12.getOptionManager().findByProperty("writer");
      adams.data.io.output.CsvSpreadSheetWriter csvspreadsheetwriter14 = new adams.data.io.output.CsvSpreadSheetWriter();
      spreadsheetheatmapwriter12.setWriter(csvspreadsheetwriter14);

      heatmapfilewriter10.setWriter(spreadsheetheatmapwriter12);

      argOption = (AbstractArgumentOption) heatmapfilewriter10.getOptionManager().findByProperty("outputDir");
      heatmapfilewriter10.setOutputDir((adams.core.io.PlaceholderDirectory) argOption.valueOf("${TMP}"));
      actors1[2] = heatmapfilewriter10;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener17 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener17);

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

