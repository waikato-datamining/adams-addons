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
 * DL4JDatasetAppendTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.VariableName;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.core.scripting.Dummy;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Trigger;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.DL4JDatasetIterator;
import adams.flow.source.Start;
import adams.flow.source.StorageValuesArray;
import adams.ml.dl4j.datasetiterator.RecordReaderDataSetIteratorConfigurator;
import adams.ml.dl4j.datasetpreprocessor.DataSetPreProcessorWithScriptedConfiguration;
import adams.ml.dl4j.inputsplit.SingleFileSplitConfigurator;
import adams.ml.dl4j.recordreader.CSVRecordReaderConfigurator;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for DL4JDatasetAppend actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class DL4JDatasetAppendTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DL4JDatasetAppendTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("iris_numeric-train.csv");
    m_TestHelper.copyResourceToTmp("iris_numeric-test.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris_numeric-train.csv");
    m_TestHelper.deleteFileFromTmp("iris_numeric-test.csv");
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
    return new TestSuite(DL4JDatasetAppendTest.class);
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

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.1st dataset
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("1st dataset"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.1st dataset.DL4JDatasetIterator
      DL4JDatasetIterator dl4jdatasetiterator = new DL4JDatasetIterator();
      RecordReaderDataSetIteratorConfigurator recordreaderdatasetiteratorconfigurator = new RecordReaderDataSetIteratorConfigurator();
      CSVRecordReaderConfigurator csvrecordreaderconfigurator = new CSVRecordReaderConfigurator();
      SingleFileSplitConfigurator singlefilesplitconfigurator = new SingleFileSplitConfigurator();
      argOption = (AbstractArgumentOption) singlefilesplitconfigurator.getOptionManager().findByProperty("source");
      singlefilesplitconfigurator.setSource((PlaceholderFile) argOption.valueOf("${TMP}/iris_numeric-train.csv"));
      csvrecordreaderconfigurator.setInputSplit(singlefilesplitconfigurator);

      argOption = (AbstractArgumentOption) csvrecordreaderconfigurator.getOptionManager().findByProperty("skipNumLines");
      csvrecordreaderconfigurator.setSkipNumLines((Integer) argOption.valueOf("1"));
      recordreaderdatasetiteratorconfigurator.setRecordReader(csvrecordreaderconfigurator);

      argOption = (AbstractArgumentOption) recordreaderdatasetiteratorconfigurator.getOptionManager().findByProperty("labelIndex");
      recordreaderdatasetiteratorconfigurator.setLabelIndex((Integer) argOption.valueOf("4"));
      argOption = (AbstractArgumentOption) recordreaderdatasetiteratorconfigurator.getOptionManager().findByProperty("numPossibleLabels");
      recordreaderdatasetiteratorconfigurator.setNumPossibleLabels((Integer) argOption.valueOf("3"));
      dl4jdatasetiterator.setIterator(recordreaderdatasetiteratorconfigurator);

      DataSetPreProcessorWithScriptedConfiguration datasetpreprocessorwithscriptedconfiguration = new DataSetPreProcessorWithScriptedConfiguration();
      Dummy dummy = new Dummy();
      datasetpreprocessorwithscriptedconfiguration.setHandler(dummy);

      dl4jdatasetiterator.setPreProcessor(datasetpreprocessorwithscriptedconfiguration);

      dl4jdatasetiterator.setFullDataset(true);

      actors2.add(dl4jdatasetiterator);

      // Flow.1st dataset.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("entry"));
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableValue");
      setvariable.setVariableValue((BaseText) argOption.valueOf("1st"));
      actors2.add(setvariable);

      // Flow.1st dataset.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("train"));
      actors2.add(setstoragevalue);

      // Flow.1st dataset.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);

      actors2.add(dumpfile);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.2nd dataset
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("2nd dataset"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.2nd dataset.DL4JDatasetIterator
      DL4JDatasetIterator dl4jdatasetiterator2 = new DL4JDatasetIterator();
      RecordReaderDataSetIteratorConfigurator recordreaderdatasetiteratorconfigurator2 = new RecordReaderDataSetIteratorConfigurator();
      CSVRecordReaderConfigurator csvrecordreaderconfigurator2 = new CSVRecordReaderConfigurator();
      SingleFileSplitConfigurator singlefilesplitconfigurator2 = new SingleFileSplitConfigurator();
      argOption = (AbstractArgumentOption) singlefilesplitconfigurator2.getOptionManager().findByProperty("source");
      singlefilesplitconfigurator2.setSource((PlaceholderFile) argOption.valueOf("${TMP}/iris_numeric-test.csv"));
      csvrecordreaderconfigurator2.setInputSplit(singlefilesplitconfigurator2);

      argOption = (AbstractArgumentOption) csvrecordreaderconfigurator2.getOptionManager().findByProperty("skipNumLines");
      csvrecordreaderconfigurator2.setSkipNumLines((Integer) argOption.valueOf("1"));
      recordreaderdatasetiteratorconfigurator2.setRecordReader(csvrecordreaderconfigurator2);

      argOption = (AbstractArgumentOption) recordreaderdatasetiteratorconfigurator2.getOptionManager().findByProperty("labelIndex");
      recordreaderdatasetiteratorconfigurator2.setLabelIndex((Integer) argOption.valueOf("4"));
      argOption = (AbstractArgumentOption) recordreaderdatasetiteratorconfigurator2.getOptionManager().findByProperty("numPossibleLabels");
      recordreaderdatasetiteratorconfigurator2.setNumPossibleLabels((Integer) argOption.valueOf("3"));
      dl4jdatasetiterator2.setIterator(recordreaderdatasetiteratorconfigurator2);

      DataSetPreProcessorWithScriptedConfiguration datasetpreprocessorwithscriptedconfiguration2 = new DataSetPreProcessorWithScriptedConfiguration();
      Dummy dummy2 = new Dummy();
      datasetpreprocessorwithscriptedconfiguration2.setHandler(dummy2);

      dl4jdatasetiterator2.setPreProcessor(datasetpreprocessorwithscriptedconfiguration2);

      dl4jdatasetiterator2.setFullDataset(true);

      actors3.add(dl4jdatasetiterator2);

      // Flow.2nd dataset.SetVariable
      SetVariable setvariable2 = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
      setvariable2.setVariableName((VariableName) argOption.valueOf("entry"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableValue");
      setvariable2.setVariableValue((BaseText) argOption.valueOf("2nd"));
      actors3.add(setvariable2);

      // Flow.2nd dataset.SetStorageValue
      SetStorageValue setstoragevalue2 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue2.getOptionManager().findByProperty("storageName");
      setstoragevalue2.setStorageName((StorageName) argOption.valueOf("test"));
      actors3.add(setstoragevalue2);

      // Flow.2nd dataset.DumpFile
      DumpFile dumpfile2 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
      dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile2.setAppend(true);

      actors3.add(dumpfile2);
      trigger2.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.append datasets
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("append datasets"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.append datasets.StorageValuesArray
      StorageValuesArray storagevaluesarray = new StorageValuesArray();
      argOption = (AbstractArgumentOption) storagevaluesarray.getOptionManager().findByProperty("storageNames");
      List<StorageName> storagenames = new ArrayList<>();
      storagenames.add((StorageName) argOption.valueOf("train"));
      storagenames.add((StorageName) argOption.valueOf("test"));
      storagevaluesarray.setStorageNames(storagenames.toArray(new StorageName[0]));
      actors4.add(storagevaluesarray);

      // Flow.append datasets.DL4JDatasetAppend
      DL4JDatasetAppend dl4jdatasetappend = new DL4JDatasetAppend();
      actors4.add(dl4jdatasetappend);

      // Flow.append datasets.SetVariable
      SetVariable setvariable3 = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableName");
      setvariable3.setVariableName((VariableName) argOption.valueOf("entry"));
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableValue");
      setvariable3.setVariableValue((BaseText) argOption.valueOf("combined"));
      actors4.add(setvariable3);

      // Flow.append datasets.DumpFile
      DumpFile dumpfile3 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile3.getOptionManager().findByProperty("outputFile");
      dumpfile3.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile3.setAppend(true);

      actors4.add(dumpfile3);
      trigger3.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger3);
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

