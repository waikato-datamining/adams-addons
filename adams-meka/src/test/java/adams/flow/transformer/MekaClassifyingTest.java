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
 * MekaClassifyingTest.java
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
 * Test for MekaClassifying actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class MekaClassifyingTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MekaClassifyingTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("Music.arff");
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
    return new TestSuite(MekaClassifyingTest.class);
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

      // Flow.CallableActors
      adams.flow.standalone.CallableActors callableactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) callableactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors3 = new adams.flow.core.AbstractActor[2];

      // Flow.CallableActors.setup
      adams.flow.source.MekaClassifierSetup mekaclassifiersetup4 = new adams.flow.source.MekaClassifierSetup();
      argOption = (AbstractArgumentOption) mekaclassifiersetup4.getOptionManager().findByProperty("name");
      mekaclassifiersetup4.setName((java.lang.String) argOption.valueOf("setup"));
      mekaclassifiersetup4.setStopFlowOnError(true);

      argOption = (AbstractArgumentOption) mekaclassifiersetup4.getOptionManager().findByProperty("classifier");
      meka.classifiers.multilabel.BR br7 = new meka.classifiers.multilabel.BR();
      br7.setOptions(OptionUtils.splitOptions("-W weka.classifiers.trees.J48 -- -C 0.25 -M 2"));
      mekaclassifiersetup4.setClassifier(br7);

      actors3[0] = mekaclassifiersetup4;

      // Flow.CallableActors.model
      adams.flow.source.StorageValue storagevalue8 = new adams.flow.source.StorageValue();
      argOption = (AbstractArgumentOption) storagevalue8.getOptionManager().findByProperty("name");
      storagevalue8.setName((java.lang.String) argOption.valueOf("model"));
      argOption = (AbstractArgumentOption) storagevalue8.getOptionManager().findByProperty("storageName");
      storagevalue8.setStorageName((adams.flow.control.StorageName) argOption.valueOf("model"));
      actors3[1] = storagevalue8;
      callableactors2.setActors(actors3);

      actors1[0] = callableactors2;

      // Flow.Start
      adams.flow.source.Start start11 = new adams.flow.source.Start();
      actors1[1] = start11;

      // Flow.train classifier
      adams.flow.control.Trigger trigger12 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger12.getOptionManager().findByProperty("name");
      trigger12.setName((java.lang.String) argOption.valueOf("train classifier"));
      argOption = (AbstractArgumentOption) trigger12.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors14 = new adams.flow.core.AbstractActor[6];

      // Flow.train classifier.FileSupplier
      adams.flow.source.FileSupplier filesupplier15 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier15.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files16 = new adams.core.io.PlaceholderFile[1];
      files16[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/Music.arff");
      filesupplier15.setFiles(files16);
      actors14[0] = filesupplier15;

      // Flow.train classifier.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader17 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader17.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader19 = new weka.core.converters.ArffLoader();
      wekafilereader17.setCustomLoader(arffloader19);
      wekafilereader17.setUseCustomLoader(true);

      actors14[1] = wekafilereader17;

      // Flow.train classifier.MekaPrepareData
      adams.flow.transformer.MekaPrepareData mekapreparedata20 = new adams.flow.transformer.MekaPrepareData();
      actors14[2] = mekapreparedata20;

      // Flow.train classifier.MekaTrainClassifier
      adams.flow.transformer.MekaTrainClassifier mekatrainclassifier21 = new adams.flow.transformer.MekaTrainClassifier();
      argOption = (AbstractArgumentOption) mekatrainclassifier21.getOptionManager().findByProperty("classifier");
      mekatrainclassifier21.setClassifier((adams.flow.core.CallableActorReference) argOption.valueOf("setup"));
      actors14[3] = mekatrainclassifier21;

      // Flow.train classifier.ContainerValuePicker
      adams.flow.control.ContainerValuePicker containervaluepicker23 = new adams.flow.control.ContainerValuePicker();
      argOption = (AbstractArgumentOption) containervaluepicker23.getOptionManager().findByProperty("valueName");
      containervaluepicker23.setValueName((java.lang.String) argOption.valueOf("Model"));
      containervaluepicker23.setSwitchOutputs(true);

      actors14[4] = containervaluepicker23;

      // Flow.train classifier.SetStorageValue
      adams.flow.transformer.SetStorageValue setstoragevalue25 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue25.getOptionManager().findByProperty("storageName");
      setstoragevalue25.setStorageName((adams.flow.control.StorageName) argOption.valueOf("model"));
      actors14[5] = setstoragevalue25;
      trigger12.setActors(actors14);

      actors1[2] = trigger12;

      // Flow.perform classification
      adams.flow.control.Trigger trigger27 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger27.getOptionManager().findByProperty("name");
      trigger27.setName((java.lang.String) argOption.valueOf("perform classification"));
      argOption = (AbstractArgumentOption) trigger27.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors29 = new adams.flow.core.AbstractActor[8];

      // Flow.perform classification.FileSupplier
      adams.flow.source.FileSupplier filesupplier30 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier30.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files31 = new adams.core.io.PlaceholderFile[1];
      files31[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/Music.arff");
      filesupplier30.setFiles(files31);
      actors29[0] = filesupplier30;

      // Flow.perform classification.WekaFileReader
      adams.flow.transformer.WekaFileReader wekafilereader32 = new adams.flow.transformer.WekaFileReader();
      argOption = (AbstractArgumentOption) wekafilereader32.getOptionManager().findByProperty("customLoader");
      weka.core.converters.ArffLoader arffloader34 = new weka.core.converters.ArffLoader();
      wekafilereader32.setCustomLoader(arffloader34);
      wekafilereader32.setUseCustomLoader(true);

      actors29[1] = wekafilereader32;

      // Flow.perform classification.MekaPrepareData
      adams.flow.transformer.MekaPrepareData mekapreparedata35 = new adams.flow.transformer.MekaPrepareData();
      actors29[2] = mekapreparedata35;

      // Flow.perform classification.WekaInstanceBuffer
      adams.flow.transformer.WekaInstanceBuffer wekainstancebuffer36 = new adams.flow.transformer.WekaInstanceBuffer();
      argOption = (AbstractArgumentOption) wekainstancebuffer36.getOptionManager().findByProperty("operation");
      wekainstancebuffer36.setOperation((adams.flow.transformer.WekaInstanceBuffer.Operation) argOption.valueOf("INSTANCES_TO_INSTANCE"));
      actors29[3] = wekainstancebuffer36;

      // Flow.perform classification.MekaClassifying
      adams.flow.transformer.MekaClassifying mekaclassifying38 = new adams.flow.transformer.MekaClassifying();
      argOption = (AbstractArgumentOption) mekaclassifying38.getOptionManager().findByProperty("modelActor");
      mekaclassifying38.setModelActor((adams.flow.core.CallableActorReference) argOption.valueOf("model"));
      mekaclassifying38.setOnTheFly(true);

      actors29[4] = mekaclassifying38;

      // Flow.perform classification.ContainerValuePicker
      adams.flow.control.ContainerValuePicker containervaluepicker40 = new adams.flow.control.ContainerValuePicker();
      argOption = (AbstractArgumentOption) containervaluepicker40.getOptionManager().findByProperty("valueName");
      containervaluepicker40.setValueName((java.lang.String) argOption.valueOf("Distribution"));
      containervaluepicker40.setSwitchOutputs(true);

      actors29[5] = containervaluepicker40;

      // Flow.perform classification.Convert
      adams.flow.transformer.Convert convert42 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert42.getOptionManager().findByProperty("conversion");
      adams.data.conversion.AnyToString anytostring44 = new adams.data.conversion.AnyToString();
      convert42.setConversion(anytostring44);

      actors29[6] = convert42;

      // Flow.perform classification.DumpFile
      adams.flow.sink.DumpFile dumpfile45 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile45.getOptionManager().findByProperty("outputFile");
      dumpfile45.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile45.setAppend(true);
      actors29[7] = dumpfile45;
      trigger27.setActors(actors29);

      actors1[3] = trigger27;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener48 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener48);

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

