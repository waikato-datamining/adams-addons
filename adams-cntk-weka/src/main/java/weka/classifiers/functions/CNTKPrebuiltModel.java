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
 * CNTKPrebuiltModel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.functions;

import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.CNTKSpreadSheetReader;
import adams.env.Environment;
import adams.ml.cntk.DeviceType;
import com.microsoft.CNTK.DeviceDescriptor;
import com.microsoft.CNTK.FloatVector;
import com.microsoft.CNTK.FloatVectorVector;
import com.microsoft.CNTK.Function;
import com.microsoft.CNTK.NDShape;
import com.microsoft.CNTK.UnorderedMapVariableValuePtr;
import com.microsoft.CNTK.Value;
import com.microsoft.CNTK.Variable;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.set.hash.TIntHashSet;
import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaOptionUtils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.SpreadSheetLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Applies the pre-built CNTK model to the data for making predictions.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -${CWD} &lt;value&gt;
 *  The prebuilt CNTK model to use.
 *  (default: ${CWD})</pre>
 *
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 *
 * <pre> -num-decimal-places
 *  The number of decimal places for the output of numbers in the model (default 2).</pre>
 *
 * <pre> -batch-size
 *  The desired batch size for batch prediction  (default 100).</pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CNTKPrebuiltModel
  extends AbstractClassifier {

  private static final long serialVersionUID = 7732345053235983381L;

  protected static String MODEL = "model";

  protected static String DEVICETYPE = "device-type";

  protected static String GPUDEVICEID = "gpu-device-id";

  protected static String INPUTS = "inputs";

  protected static String INPUTNAMES = "input-names";

  protected static String CLASSNAME = "class-name";

  /** the model to load. */
  protected PlaceholderFile m_Model = getDefaultModel();

  /** the device to use. */
  protected DeviceType m_DeviceType = getDefaultDeviceType();

  /** the GPU device ID. */
  protected long m_GPUDeviceID = getDefaultGPUDeviceID();

  /** the model to use. */
  protected transient Function m_ActualModel;

  /** the device to use. */
  protected DeviceDescriptor m_Device;

  /** the inputs. */
  protected Range[] m_Inputs = getDefaultInputs();

  /** the names of the inputs. */
  protected BaseString[] m_InputNames = getDefaultInputNames();

  /** the name of the class attribute in the mode. */
  protected String m_ClassName = getDefaultClassName();

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Applies the pre-built CNTK model to the data for making predictions.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;

    result = new Vector();

    WekaOptionUtils.addOption(result, modelTipText(), "" + getModel(), "" + getDefaultModel());
    WekaOptionUtils.addOption(result, deviceTypeTipText(), "" + getDeviceType(), "" + getDefaultDeviceType());
    WekaOptionUtils.addOption(result, GPUDeviceIDTipText(), "" + getGPUDeviceID(), "" + getDefaultGPUDeviceID());
    WekaOptionUtils.addOption(result, inputsTipText(), Utils.arrayToString(getInputs()), Utils.arrayToString(getDefaultInputs()));
    WekaOptionUtils.addOption(result, inputNamesTipText(), Utils.arrayToString(getInputNames()), Utils.arrayToString(getDefaultInputNames()));
    WekaOptionUtils.addOption(result, classNameTipText(), getClassName(), getDefaultClassName());
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    setModel(WekaOptionUtils.parse(options, MODEL, getDefaultModel()));
    setDeviceType((DeviceType) WekaOptionUtils.parse(options, DEVICETYPE, getDefaultDeviceType()));
    setGPUDeviceID(WekaOptionUtils.parse(options, GPUDEVICEID, getDefaultGPUDeviceID()));
    setInputs(WekaOptionUtils.parse(options, INPUTS, getDefaultInputs()));
    setInputNames((BaseString[]) WekaOptionUtils.parse(options, INPUTNAMES, getDefaultInputNames()));
    setClassName(WekaOptionUtils.parse(options, CLASSNAME, getDefaultClassName()));
    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, MODEL, getModel());
    WekaOptionUtils.add(result, DEVICETYPE, getDeviceType());
    WekaOptionUtils.add(result, GPUDEVICEID, getGPUDeviceID());
    WekaOptionUtils.add(result, INPUTS, getInputs());
    WekaOptionUtils.add(result, INPUTNAMES, getInputNames());
    WekaOptionUtils.add(result, CLASSNAME, getClassName());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns the default model file.
   *
   * @return		the default
   */
  protected PlaceholderFile getDefaultModel() {
    return new PlaceholderFile();
  }

  /**
   * Sets the prebuilt model to use.
   *
   * @param value	the model
   */
  public void setModel(PlaceholderFile value) {
    m_Model = value;
  }

  /**
   * Returns the prebuilt model to use.
   *
   * @return		the model
   */
  public PlaceholderFile getModel() {
    return m_Model;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String modelTipText() {
    return "The prebuilt CNTK model to use.";
  }

  /**
   * Returns the default device to use.
   *
   * @return  		the device
   */
  protected DeviceType getDefaultDeviceType() {
    return DeviceType.DEFAULT;
  }

  /**
   * Sets the device to use.
   *
   * @param value	the device
   */
  public void setDeviceType(DeviceType value) {
    m_DeviceType = value;
  }

  /**
   * Returns the device to use.
   *
   * @return  		the device
   */
  public DeviceType getDeviceType() {
    return m_DeviceType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String deviceTypeTipText() {
    return "The device type to use.";
  }

  /**
   * Returns the default GPU device ID.
   *
   * @return  		the ID
   */
  protected long getDefaultGPUDeviceID() {
    return 0;
  }

  /**
   * Sets the GPU device ID.
   *
   * @param value	the ID
   */
  public void setGPUDeviceID(long value) {
    m_GPUDeviceID = value;
  }

  /**
   * Returns the GPU device ID.
   *
   * @return  		the ID
   */
  public long getGPUDeviceID() {
    return m_GPUDeviceID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String GPUDeviceIDTipText() {
    return "The GPU device ID.";
  }

  /**
   * Returns the default inputs.
   *
   * @return		the default
   */
  protected Range[] getDefaultInputs() {
    return new Range[0];
  }

  /**
   * Sets the column ranges that make up the inputs (eg for 'features' and 'class').
   *
   * @param value	the column
   */
  public void setInputs(Range[] value) {
    m_Inputs     = value;
    m_InputNames = (BaseString[]) Utils.adjustArray(m_InputNames, m_Inputs.length, new BaseString());
  }

  /**
   * Returns the column ranges that make up the inputs (eg for 'features' and 'class').
   *
   * @return 		the ranges
   */
  public Range[] getInputs() {
    return m_Inputs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputsTipText() {
    return "The column ranges determining the inputs (eg for 'features' and 'class').";
  }

  /**
   * Returns the default input names.
   *
   * @return		the default
   */
  protected BaseString[] getDefaultInputNames() {
    return new BaseString[0];
  }

  /**
   * Sets the names for the inputs.
   *
   * @param value	the names
   */
  public void setInputNames(BaseString[] value) {
    m_InputNames = value;
    m_Inputs     = (Range[]) Utils.adjustArray(m_Inputs, m_InputNames.length, new Range());
  }

  /**
   * Returns the names for the inputs.
   *
   * @return 		the names
   */
  public BaseString[] getInputNames() {
    return m_InputNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputNamesTipText() {
    return "The names of the inputs (eg 'features' and 'class').";
  }

  /**
   * Returns the default name of the class attribute in the model.
   *
   * @return		the default
   */
  protected String getDefaultClassName() {
    return "";
  }

  /**
   * Sets the name of the class attribute in the model.
   *
   * @param value	the name
   */
  public void setClassName(String value) {
    m_ClassName = value;
  }

  /**
   * Returns the name of the class attribute in the model.
   *
   * @return		the name
   */
  public String getClassName() {
    return m_ClassName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String classNameTipText() {
    return "The name of the class attribute in the model.";
  }

  /**
   * Returns the Capabilities of this classifier. Maximally permissive
   * capabilities are allowed by default. Derived classifiers should override
   * this method and first disable all capabilities and then enable just those
   * capabilities that make sense for the scheme.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = super.getCapabilities();

    result.disableAll();
    result.enable(Capability.NUMERIC_ATTRIBUTES);

    result.disableAllClasses();
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  /**
   * Initializes the model.
   *
   * @throws Exception	if loading fails
   */
  protected void initModel() throws Exception {
    if (!m_Model.exists())
      throw new IllegalStateException("Model does not exist: " + m_Model);
    if (m_Model.isDirectory())
      throw new IllegalStateException("Model points to directory: " + m_Model);

    switch (m_DeviceType) {
      case DEFAULT:
	m_Device = DeviceDescriptor.useDefaultDevice();
	break;
      case CPU:
	m_Device = DeviceDescriptor.getCPUDevice();
	break;
      case GPU:
	m_Device = DeviceDescriptor.getGPUDevice(m_GPUDeviceID);
	break;
      default:
	throw new IllegalStateException("Unhandled device type: " + m_DeviceType);
    }
    m_ActualModel = Function.load(m_Model.getAbsolutePath(), m_Device);
    if (m_ActualModel == null)
      throw new IllegalStateException("Failed to load model: " + m_Model);
  }

  /**
   * Generates a classifier. Must initialize all fields of the classifier
   * that are not being set via options (ie. multiple calls of buildClassifier
   * must always lead to the same result). Must not change the dataset
   * in any way.
   *
   * @param data set of instances serving as training data
   * @throws Exception if the classifier has not been generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);

    data = new Instances(data);
    data.deleteWithMissingClass();

    initModel();
  }

  /**
   * Performs the actual application of the model.
   *
   * @param input	the input
   * @return		the score
   */
  protected float[] applyModelHardCoded(float[] input) {
    Variable outputVar = m_ActualModel.getOutputs().get(0);
    Variable inputVar0 = m_ActualModel.getArguments().get(1);  // spectrum
    Variable inputVar1 = m_ActualModel.getArguments().get(0);  // ref

    NDShape inputShape0 = inputVar0.getShape();
    NDShape inputShape1 = inputVar1.getShape();  // class

    FloatVector floatVec0 = new FloatVector();
    for (float f : input)
      floatVec0.add(f);
    FloatVector floatVec1 = new FloatVector();
    floatVec1.add(100.0f);  // class

    FloatVectorVector floatVecVec0 = new FloatVectorVector();
    floatVecVec0.add(floatVec0);
    // Create input data map
    Value inputVal0 = Value.createDenseFloat(inputShape0, floatVecVec0, m_Device);
    UnorderedMapVariableValuePtr inputDataMap = new UnorderedMapVariableValuePtr();
    inputDataMap.add(inputVar0, inputVal0);

    FloatVectorVector floatVecVec1 = new FloatVectorVector();
    floatVecVec1.add(floatVec1);
    // Create input data map
    Value inputVal1 = Value.createDenseFloat(inputShape1, floatVecVec1, m_Device);
    inputDataMap.add(inputVar1, inputVal1);

    // Create output data map. Using null as Value to indicate using system allocated memory.
    // Alternatively, create a Value object and add it to the data map.
    UnorderedMapVariableValuePtr outputDataMap = new UnorderedMapVariableValuePtr();
    outputDataMap.add(outputVar, null);

    // Start evaluation on the device
    m_ActualModel.evaluate(inputDataMap, outputDataMap, m_Device);

    // get evaluate result as dense output
    FloatVectorVector outputBuffer = new FloatVectorVector();
    outputDataMap.getitem(outputVar).copyVariableValueToFloat(outputVar, outputBuffer);

    FloatVector results = outputBuffer.get(0);
    float[] result = new float[(int) results.size()];
    for (int i = 0; i < result.length; i++)
      result[i] = results.get(i);

    return result;
  }

  /**
   * Performs the actual application of the model.
   *
   * @param input	the input
   * @return		the score
   */
  protected float[] applyModel(float[] input) {
    // TODO only init once after loading model
    // analyze model structure
    Variable outputVar = null;
    for (Variable var: m_ActualModel.getOutputs()) {
      if (var.getName().equals("rmse")) {
	outputVar = var;
	break;
      }
    }
    Map<String,Variable> inputVars = new HashMap<>();
    Map<String,NDShape> inputShapes = new HashMap<>();
    List<String> names = new ArrayList<>();
    for (int i = 0; i < m_ActualModel.getArguments().size(); i++) {
      String name = m_ActualModel.getArguments().get(i).getName();
      for (BaseString inputName: m_InputNames) {
        if (inputName.getValue().equals(name)) {
          names.add(name);
          inputVars.put(name, m_ActualModel.getArguments().get(i));
          inputShapes.put(name, m_ActualModel.getArguments().get(i).getShape());
          break;
	}
      }
    }
    Map<String,TIntHashSet> ranges = new HashMap<>();
    for (int i = 0; i < m_Inputs.length; i++) {
      m_Inputs[i].setMax(input.length + 1);  // +1 because class is already removed from array
      ranges.put(m_InputNames[i].getValue(), new TIntHashSet(m_Inputs[i].getIntIndices()));
    }

    // assemble input data
    Map<String,FloatVector> floatVecs = new HashMap<>();
    if (names.contains(m_ClassName)) {
      floatVecs.put(m_ClassName, new FloatVector());
      floatVecs.get(m_ClassName).add(0.0f);
    }
    for (int i = 0; i < input.length; i++) {
      for (String name: names) {
        TIntHashSet range = ranges.get(name);
        if ((range != null) && (range.contains(i))) {
          if (!floatVecs.containsKey(name))
            floatVecs.put(name, new FloatVector());
	  floatVecs.get(name).add(input[i]);
	  break;
	}
      }
    }

    UnorderedMapVariableValuePtr inputDataMap = new UnorderedMapVariableValuePtr();
    for (String name: names) {
      FloatVectorVector floatVecVec = new FloatVectorVector();
      floatVecVec.add(floatVecs.get(name));
      // Create input data map
      Value inputVal = Value.createDenseFloat(inputShapes.get(name), floatVecVec, m_Device);
      inputDataMap.add(inputVars.get(name), inputVal);
    }

    // Create output data map. Using null as Value to indicate using system allocated memory.
    // Alternatively, create a Value object and add it to the data map.
    UnorderedMapVariableValuePtr outputDataMap = new UnorderedMapVariableValuePtr();
    outputDataMap.add(outputVar, null);

    // Start evaluation on the device
    m_ActualModel.evaluate(inputDataMap, outputDataMap, m_Device);

    // get evaluate result as dense output
    FloatVectorVector outputBuffer = new FloatVectorVector();
    outputDataMap.getitem(outputVar).copyVariableValueToFloat(outputVar, outputBuffer);

    FloatVector results = outputBuffer.get(0);
    float[] result = new float[(int) results.size()];
    for (int i = 0; i < result.length; i++)
      result[i] = results.get(i);

    return result;
  }

  /**
   * Predicts the class memberships for a given instance. If an instance is
   * unclassified, the returned array elements must be all zero. If the class is
   * numeric, the array must consist of only one element, which contains the
   * predicted value.
   *
   * @param instance the instance to be classified
   * @return an array containing the estimated membership probabilities of the
   *         test instance in each class or the numeric prediction
   * @throws Exception if distribution could not be computed successfully
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    double[]	result;
    float[]	scores;
    TFloatList 	values;
    int		i;
    float	min;
    float	max;

    if (m_ActualModel == null)
      initModel();

    values = new TFloatArrayList();
    for (i = 0; i < instance.numAttributes(); i++) {
      if (i == instance.classIndex())
	continue;
      values.add((float) instance.value(i));
    }

    scores = applyModel(values.toArray());
    result = new double[scores.length];
    if (scores.length > 1) {
      min = Float.POSITIVE_INFINITY;
      max = Float.NEGATIVE_INFINITY;
      for (i = 0; i < scores.length; i++) {
	min = Math.min(min, scores[i]);
	max = Math.max(max, scores[i]);
      }
      if (min != max) {
	for (i = 0; i < scores.length; i++)
	  result[i] = (scores[i] - min) / (max - min);
      }
    }
    else if (scores.length == 1) {
      result[0] = scores[0];
    }

    return result;
  }

  /**
   * Returns a string representation of the built model.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    if (m_ActualModel == null)
      return "No model loaded yet!";
    return m_ActualModel.toString();
  }

  protected static void simple() throws Exception {
    CNTKSpreadSheetReader cntk = new CNTKSpreadSheetReader();
    SpreadSheetLoader loader = new SpreadSheetLoader();
    loader.setReader(cntk);
    loader.setSource(new File("/home/fracpete/temp/cntk/regression_test/ph_train_std.txt"));
    Instances data = DataSource.read(loader);
    data.setClassIndex(data.numAttributes() - 1);

    CNTKPrebuiltModel cls = new CNTKPrebuiltModel();
    cls.setModel(new PlaceholderFile("/home/fracpete/temp/cntk/regression_test/output/Models/RegrSimple_CIFAR10.cmf"));
    //cls.setModel(new PlaceholderFile("/home/fracpete/development/libraries/CNTK.bin-2.2/PretrainedModels/ResNet20_CIFAR10_CNTK.model"));
    cls.setInputNames(new BaseString[]{
      new BaseString("spectrum"),
      new BaseString("ref"),
    });
    cls.setInputs(new Range[]{
      new Range("1-246"),
      new Range("247"),
    });
    cls.setClassName("ref");

    int index = 0;
    for (Instance inst: data) {
      double value = cls.classifyInstance(inst);
      System.out.println((index+1) + ". p=" + value + " a=" + inst.classValue());
      index++;
    }
  }

  protected static void fusion() throws Exception {
    CNTKSpreadSheetReader cntk = new CNTKSpreadSheetReader();
    SpreadSheetLoader loader = new SpreadSheetLoader();
    loader.setReader(cntk);
    loader.setSource(new File("/home/fracpete/temp/cntk/regression2/A_P_M3-clean_fusion_test.txt"));
    Instances data = DataSource.read(loader);
    data.setClassIndex(data.numAttributes() - 1);

    CNTKPrebuiltModel cls = new CNTKPrebuiltModel();
    cls.setModel(new PlaceholderFile("/home/fracpete/temp/cntk/regression2/RegrSimple_CIFAR10.cmf.1168"));
    cls.setInputNames(new BaseString[]{
      new BaseString("xrf"),
      new BaseString("alpha"),
      new BaseString("ref"),
    });
    cls.setInputs(new Range[]{
      new Range(1 + "-" + 1368),
      new Range((1 + 1368) + "-" + (1368 + 1701)),
      new Range("" + (1368 + 1701 + 1)),
    });
    cls.setClassName("ref");

    int index = 0;
    for (Instance inst: data) {
      double value = cls.classifyInstance(inst);
      System.out.println((index+1) + ". p=" + value + " a=" + inst.classValue());
      index++;
    }
  }

  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    fusion();
  }
}
