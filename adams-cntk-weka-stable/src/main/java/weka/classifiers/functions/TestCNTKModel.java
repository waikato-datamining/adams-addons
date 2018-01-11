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
 * TestCNTKModel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.functions;

import adams.core.Range;
import adams.core.base.BaseKeyValuePair;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.ml.cntk.DeviceType;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Tests the CNTKModel classifier.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TestCNTKModel {

  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);

    System.out.println("Configuring model...");
    String flowsDir = "/home/fracpete/development/projects/adamsfamily/adams-addons/adams-cntk-weka/src/main/flows";
    String cntkTrain = flowsDir + "/output/chem.cs.txt";
    String cntkModelDir = flowsDir + "/output/chem.ca";
    String cntkModel = cntkModelDir + "/checm.ca.cmf";
    CNTKBrainscriptModel cls = new CNTKBrainscriptModel();
    cls.setDebug(true);
    cls.setDeviceType(DeviceType.DEFAULT);
    cls.setScript(new PlaceholderFile(flowsDir + "/data/chem.cs.bs"));
    cls.setVariables(new BaseKeyValuePair[]{
      new BaseKeyValuePair("dir", flowsDir),
      new BaseKeyValuePair("train", cntkTrain),
      new BaseKeyValuePair("numatts", "216"),
    });
    cls.setTrainFile(new PlaceholderFile(cntkTrain));
    cls.setInputs(new Range[]{
      new Range("1-216"),
      new Range("217"),
    });
    cls.setInputNames(new BaseString[]{
      new BaseString("spectrum"),
      new BaseString("ref"),
    });
    cls.setClassName("ref");
    cls.setOutputName("rmse");
    cls.setModelDirectory(new PlaceholderDirectory(cntkModelDir));
    cls.setModel(new PlaceholderFile(cntkModel));
    cls.setModelExtension(".cmf");
    System.out.println(OptionUtils.getCommandLine(cls));

    // load data
    System.out.println("Loading data...");
    String train = flowsDir + "/data/chem.ca.csv";
    Instances data = DataSource.read(train);
    data.setClassIndex(data.numAttributes() - 1);
    data.deleteAttributeAt(0);  // delete sample ID
    System.out.println("Data loaded!");

    // build
    System.out.println("Building model...");
    cls.buildModel(data);

    // make predictions
    System.out.println("Making predictions...");
    for (Instance inst: data)
      System.out.println(inst.classValue() + " -> " + cls.classifyInstance(inst));
  }
}
