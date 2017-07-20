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

/**
 * CNTKPrebuiltModel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.functions;

import adams.core.io.PlaceholderFile;
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
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CNTKPrebuiltModel
  extends AbstractClassifier {

  private static final long serialVersionUID = 7732345053235983381L;

  protected static String MODEL = "model";

  /** the model to load. */
  protected PlaceholderFile m_Model = getDefaultModel();

  /** the model to use. */
  protected transient Function m_ActualModel;

  /** the device to use. */
  protected DeviceDescriptor m_Device;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Generates a classifier cascade, with each deeper level of classifiers "
	+ "being built on the input data and either the class distributions "
	+ "(nominal class) or classification (numeric class) of the classifiers "
	+ "of the previous level in the cascade.\n"
	+ "The build process is stopped when either the maximum number of levels "
	+ "is reached, the termination criterion is satisfied or no further "
	+ "improvement is achieved.\n"
	+ "In case of a level performing worse than the prior one, the build "
	+ "process is terminated immediately and the current level discarded.";
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

    m_Device      = DeviceDescriptor.useDefaultDevice(); // TODO option?
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
  protected float[] applyModel(float[] input) {
    Variable outputVar = m_ActualModel.getOutputs().get(0);
    Variable inputVar = m_ActualModel.getArguments().get(0);

    NDShape inputShape = inputVar.getShape();
    int width;
    int height = 1;
    int channels = 1;
    width = (int)inputShape.getDimensions()[0];
    if (inputShape.getDimensions().length > 1)
      height = (int)inputShape.getDimensions()[1];
    if (inputShape.getDimensions().length > 2)
      channels = (int)inputShape.getDimensions()[2];
    if (width*height*channels != input.length)
      throw new IllegalStateException(
	"Input length and model dimensions differ: "
	  + input.length + " != " + (width*height*channels));

    FloatVector floatVec = new FloatVector();
    for (float f : input)
      floatVec.add(f);

    FloatVectorVector floatVecVec = new FloatVectorVector();
    floatVecVec.add(floatVec);
    // Create input data map
    Value inputVal = Value.createDenseFloat(inputShape, floatVecVec, m_Device);
    UnorderedMapVariableValuePtr inputDataMap = new UnorderedMapVariableValuePtr();
    inputDataMap.add(inputVar, inputVal);

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
}
