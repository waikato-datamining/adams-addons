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
import adams.ml.cntk.CNTKModelWrapper;
import adams.ml.cntk.DeviceType;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
 * <pre> -model &lt;value&gt;
 *  The prebuilt CNTK model to use.
 *  (default: ${CWD})</pre>
 *
 * <pre> -device-type &lt;value&gt;
 *  The device type to use.
 *  (default: DEFAULT)</pre>
 *
 * <pre> -gpu-device-id &lt;value&gt;
 *  The GPU device ID.
 *  (default: 0)</pre>
 *
 * <pre> -inputs &lt;value&gt;
 *  The column ranges determining the inputs (eg for 'features' and 'class').
 *  (default: )</pre>
 *
 * <pre> -input-names &lt;value&gt;
 *  The names of the inputs (eg 'features' and 'class').
 *  (default: )</pre>
 *
 * <pre> -class-name &lt;value&gt;
 *  The name of the class attribute in the model, in case it cannot be determined automatically.
 *  (default: )</pre>
 *
 * <pre> -output-name &lt;value&gt;
 *  The name of the output variable in the model, in case it cannot be determined automatically based on its dimension.
 *  (default: )</pre>
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

  protected static String OUTPUTNAME = "output-name";

  /** the model to load. */
  protected PlaceholderFile m_Model = getDefaultModel();

  /** the device to use. */
  protected DeviceType m_DeviceType = getDefaultDeviceType();

  /** the GPU device ID. */
  protected long m_GPUDeviceID = getDefaultGPUDeviceID();

  /** the model wrapper. */
  protected CNTKModelWrapper m_Wrapper = new CNTKModelWrapper();

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

    WekaOptionUtils.addOption(result, modelTipText(), "" + getDefaultModel(), MODEL);
    WekaOptionUtils.addOption(result, deviceTypeTipText(), "" + getDefaultDeviceType(), DEVICETYPE);
    WekaOptionUtils.addOption(result, GPUDeviceIDTipText(), "" + getDefaultGPUDeviceID(), GPUDEVICEID);
    WekaOptionUtils.addOption(result, inputsTipText(), Utils.arrayToString(getDefaultInputs()), INPUTS);
    WekaOptionUtils.addOption(result, inputNamesTipText(), Utils.arrayToString(getDefaultInputNames()), INPUTNAMES);
    WekaOptionUtils.addOption(result, classNameTipText(), getDefaultClassName(), CLASSNAME);
    WekaOptionUtils.addOption(result, outputNameTipText(), getDefaultOutputName(), OUTPUTNAME);
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
    setOutputName(WekaOptionUtils.parse(options, OUTPUTNAME, getDefaultOutputName()));
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
    if (getDeviceType() == DeviceType.GPU)
      WekaOptionUtils.add(result, GPUDEVICEID, getGPUDeviceID());
    WekaOptionUtils.add(result, INPUTS, getInputs());
    WekaOptionUtils.add(result, INPUTNAMES, getInputNames());
    WekaOptionUtils.add(result, CLASSNAME, getClassName());
    WekaOptionUtils.add(result, OUTPUTNAME, getOutputName());
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
    m_Wrapper.setInputs(value);
  }

  /**
   * Returns the column ranges that make up the inputs (eg for 'features' and 'class').
   *
   * @return 		the ranges
   */
  public Range[] getInputs() {
    return m_Wrapper.getInputs();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputsTipText() {
    return m_Wrapper.inputsTipText();
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
    m_Wrapper.setInputNames(value);
  }

  /**
   * Returns the names for the inputs.
   *
   * @return 		the names
   */
  public BaseString[] getInputNames() {
    return m_Wrapper.getInputNames();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputNamesTipText() {
    return m_Wrapper.inputNamesTipText();
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
   * Sets the name of the class attribute in the model, in case it cannot
   * be determined automatically.
   *
   * @param value	the name
   */
  public void setClassName(String value) {
    m_Wrapper.setClassName(value);
  }

  /**
   * Returns the name of the class attribute in the model, in case it cannot
   * be determined automatically.
   *
   * @return		the name
   */
  public String getClassName() {
    return m_Wrapper.getClassName();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String classNameTipText() {
    return m_Wrapper.classNameTipText();
  }

  /**
   * Returns the default name of the class attribute in the model.
   *
   * @return		the default
   */
  protected String getDefaultOutputName() {
    return "";
  }

  /**
   * Sets the name of the output variable in the model, in case it cannot be
   * determined automatically based on its dimension.
   *
   * @param value	the name
   */
  public void setOutputName(String value) {
    m_Wrapper.setOutputName(value);
  }

  /**
   * Returns the name of the output variable in the model, in case it cannot
   * be determined automatically based on its dimension.
   *
   * @return		the name
   */
  public String getOutputName() {
    return m_Wrapper.getOutputName();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String outputNameTipText() {
    return m_Wrapper.outputNameTipText();
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
   * @param data	the training data
   * @throws Exception	if loading fails
   */
  protected void initModel(Instances data) throws Exception {
    m_Wrapper.loadModel(m_Model, m_DeviceType, m_GPUDeviceID);
    m_Wrapper.initModel(data.numClasses());
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

    initModel(data);
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

    if (!m_Wrapper.isInitialized())
      initModel(instance.dataset());

    values = new TFloatArrayList();
    for (i = 0; i < instance.numAttributes(); i++)
      values.add((float) instance.value(i));

    scores = m_Wrapper.predict(values.toArray());
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
    if (m_Wrapper.getModel() == null)
      return "No model loaded yet!";
    return m_Wrapper.getModel().toString();
  }
}
