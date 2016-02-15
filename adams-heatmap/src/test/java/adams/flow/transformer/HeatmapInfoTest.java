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
 * HeatmapInfoTest.java
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
 * Test for HeatmapInfo actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class HeatmapInfoTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public HeatmapInfoTest(String name) {
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
    return new TestSuite(HeatmapInfoTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[7];

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
      argOption = (AbstractArgumentOption) simpleimageheatmapreader6.getOptionManager().findByProperty("reader");
      adams.data.io.input.JAIImageReader jaiimagereader8 = new adams.data.io.input.JAIImageReader();
      simpleimageheatmapreader6.setReader(jaiimagereader8);

      heatmapfilereader4.setReader(simpleimageheatmapreader6);

      actors1[1] = heatmapfilereader4;

      // Flow.width
      adams.flow.control.Tee tee9 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee9.getOptionManager().findByProperty("name");
      tee9.setName((java.lang.String) argOption.valueOf("width"));
      argOption = (AbstractArgumentOption) tee9.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors11 = new adams.flow.core.Actor[4];

      // Flow.width.HeatmapInfo
      adams.flow.transformer.HeatmapInfo heatmapinfo12 = new adams.flow.transformer.HeatmapInfo();
      actors11[0] = heatmapinfo12;

      // Flow.width.Convert
      adams.flow.transformer.Convert convert13 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert13.getOptionManager().findByProperty("conversion");
      adams.data.conversion.IntToString inttostring15 = new adams.data.conversion.IntToString();
      convert13.setConversion(inttostring15);

      actors11[1] = convert13;

      // Flow.width.StringInsert
      adams.flow.transformer.StringInsert stringinsert16 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) stringinsert16.getOptionManager().findByProperty("position");
      stringinsert16.setPosition((adams.core.Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert16.getOptionManager().findByProperty("value");
      stringinsert16.setValue((adams.core.base.BaseString) argOption.valueOf("width: "));
      actors11[2] = stringinsert16;

      // Flow.width.DumpFile
      adams.flow.sink.DumpFile dumpfile19 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile19.getOptionManager().findByProperty("outputFile");
      dumpfile19.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile19.setAppend(true);

      actors11[3] = dumpfile19;
      tee9.setActors(actors11);

      actors1[2] = tee9;

      // Flow.height
      adams.flow.control.Tee tee21 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee21.getOptionManager().findByProperty("name");
      tee21.setName((java.lang.String) argOption.valueOf("height"));
      argOption = (AbstractArgumentOption) tee21.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors23 = new adams.flow.core.Actor[4];

      // Flow.height.HeatmapInfo
      adams.flow.transformer.HeatmapInfo heatmapinfo24 = new adams.flow.transformer.HeatmapInfo();
      argOption = (AbstractArgumentOption) heatmapinfo24.getOptionManager().findByProperty("type");
      heatmapinfo24.setType((adams.flow.transformer.HeatmapInfo.InfoType) argOption.valueOf("HEIGHT"));
      actors23[0] = heatmapinfo24;

      // Flow.height.Convert
      adams.flow.transformer.Convert convert26 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert26.getOptionManager().findByProperty("conversion");
      adams.data.conversion.IntToString inttostring28 = new adams.data.conversion.IntToString();
      convert26.setConversion(inttostring28);

      actors23[1] = convert26;

      // Flow.height.StringInsert
      adams.flow.transformer.StringInsert stringinsert29 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) stringinsert29.getOptionManager().findByProperty("position");
      stringinsert29.setPosition((adams.core.Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert29.getOptionManager().findByProperty("value");
      stringinsert29.setValue((adams.core.base.BaseString) argOption.valueOf("height: "));
      actors23[2] = stringinsert29;

      // Flow.height.DumpFile
      adams.flow.sink.DumpFile dumpfile32 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile32.getOptionManager().findByProperty("outputFile");
      dumpfile32.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile32.setAppend(true);

      actors23[3] = dumpfile32;
      tee21.setActors(actors23);

      actors1[3] = tee21;

      // Flow.min
      adams.flow.control.Tee tee34 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee34.getOptionManager().findByProperty("name");
      tee34.setName((java.lang.String) argOption.valueOf("min"));
      argOption = (AbstractArgumentOption) tee34.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors36 = new adams.flow.core.Actor[4];

      // Flow.min.HeatmapInfo
      adams.flow.transformer.HeatmapInfo heatmapinfo37 = new adams.flow.transformer.HeatmapInfo();
      argOption = (AbstractArgumentOption) heatmapinfo37.getOptionManager().findByProperty("type");
      heatmapinfo37.setType((adams.flow.transformer.HeatmapInfo.InfoType) argOption.valueOf("MIN"));
      actors36[0] = heatmapinfo37;

      // Flow.min.Convert
      adams.flow.transformer.Convert convert39 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert39.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToString doubletostring41 = new adams.data.conversion.DoubleToString();
      argOption = (AbstractArgumentOption) doubletostring41.getOptionManager().findByProperty("numDecimals");
      doubletostring41.setNumDecimals((Integer) argOption.valueOf("2"));
      doubletostring41.setFixedDecimals(true);

      convert39.setConversion(doubletostring41);

      actors36[1] = convert39;

      // Flow.min.StringInsert
      adams.flow.transformer.StringInsert stringinsert43 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) stringinsert43.getOptionManager().findByProperty("position");
      stringinsert43.setPosition((adams.core.Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert43.getOptionManager().findByProperty("value");
      stringinsert43.setValue((adams.core.base.BaseString) argOption.valueOf("min: "));
      actors36[2] = stringinsert43;

      // Flow.min.DumpFile
      adams.flow.sink.DumpFile dumpfile46 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile46.getOptionManager().findByProperty("outputFile");
      dumpfile46.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile46.setAppend(true);

      actors36[3] = dumpfile46;
      tee34.setActors(actors36);

      actors1[4] = tee34;

      // Flow.max
      adams.flow.control.Tee tee48 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee48.getOptionManager().findByProperty("name");
      tee48.setName((java.lang.String) argOption.valueOf("max"));
      argOption = (AbstractArgumentOption) tee48.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors50 = new adams.flow.core.Actor[4];

      // Flow.max.HeatmapInfo
      adams.flow.transformer.HeatmapInfo heatmapinfo51 = new adams.flow.transformer.HeatmapInfo();
      argOption = (AbstractArgumentOption) heatmapinfo51.getOptionManager().findByProperty("type");
      heatmapinfo51.setType((adams.flow.transformer.HeatmapInfo.InfoType) argOption.valueOf("MAX"));
      actors50[0] = heatmapinfo51;

      // Flow.max.Convert
      adams.flow.transformer.Convert convert53 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert53.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToString doubletostring55 = new adams.data.conversion.DoubleToString();
      argOption = (AbstractArgumentOption) doubletostring55.getOptionManager().findByProperty("numDecimals");
      doubletostring55.setNumDecimals((Integer) argOption.valueOf("2"));
      doubletostring55.setFixedDecimals(true);

      convert53.setConversion(doubletostring55);

      actors50[1] = convert53;

      // Flow.max.StringInsert
      adams.flow.transformer.StringInsert stringinsert57 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) stringinsert57.getOptionManager().findByProperty("position");
      stringinsert57.setPosition((adams.core.Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert57.getOptionManager().findByProperty("value");
      stringinsert57.setValue((adams.core.base.BaseString) argOption.valueOf("max: "));
      actors50[2] = stringinsert57;

      // Flow.max.DumpFile
      adams.flow.sink.DumpFile dumpfile60 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile60.getOptionManager().findByProperty("outputFile");
      dumpfile60.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile60.setAppend(true);

      actors50[3] = dumpfile60;
      tee48.setActors(actors50);

      actors1[5] = tee48;

      // Flow.values
      adams.flow.control.Tee tee62 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee62.getOptionManager().findByProperty("name");
      tee62.setName((java.lang.String) argOption.valueOf("values"));
      argOption = (AbstractArgumentOption) tee62.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors64 = new adams.flow.core.Actor[5];

      // Flow.values.HeatmapInfo
      adams.flow.transformer.HeatmapInfo heatmapinfo65 = new adams.flow.transformer.HeatmapInfo();
      heatmapinfo65.setOutputArray(true);

      argOption = (AbstractArgumentOption) heatmapinfo65.getOptionManager().findByProperty("type");
      heatmapinfo65.setType((adams.flow.transformer.HeatmapInfo.InfoType) argOption.valueOf("VALUES"));
      actors64[0] = heatmapinfo65;

      // Flow.values.ArrayProcess
      adams.flow.control.ArrayProcess arrayprocess67 = new adams.flow.control.ArrayProcess();
      argOption = (AbstractArgumentOption) arrayprocess67.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors68 = new adams.flow.core.Actor[1];

      // Flow.values.ArrayProcess.Convert
      adams.flow.transformer.Convert convert69 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert69.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DoubleToString doubletostring71 = new adams.data.conversion.DoubleToString();
      argOption = (AbstractArgumentOption) doubletostring71.getOptionManager().findByProperty("numDecimals");
      doubletostring71.setNumDecimals((Integer) argOption.valueOf("2"));
      doubletostring71.setFixedDecimals(true);

      convert69.setConversion(doubletostring71);

      actors68[0] = convert69;
      arrayprocess67.setActors(actors68);

      actors64[1] = arrayprocess67;

      // Flow.values.StringJoin
      adams.flow.transformer.StringJoin stringjoin73 = new adams.flow.transformer.StringJoin();
      argOption = (AbstractArgumentOption) stringjoin73.getOptionManager().findByProperty("glue");
      stringjoin73.setGlue((java.lang.String) argOption.valueOf(","));
      actors64[2] = stringjoin73;

      // Flow.values.StringInsert
      adams.flow.transformer.StringInsert stringinsert75 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) stringinsert75.getOptionManager().findByProperty("position");
      stringinsert75.setPosition((adams.core.Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert75.getOptionManager().findByProperty("value");
      stringinsert75.setValue((adams.core.base.BaseString) argOption.valueOf("values: "));
      actors64[3] = stringinsert75;

      // Flow.values.DumpFile
      adams.flow.sink.DumpFile dumpfile78 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile78.getOptionManager().findByProperty("outputFile");
      dumpfile78.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile78.setAppend(true);

      actors64[4] = dumpfile78;
      tee62.setActors(actors64);

      actors1[6] = tee62;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener81 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener81);

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

