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
 * AbstractModelApplier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelapplier;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.core.option.AbstractOptionHandler;
import adams.flow.control.StorageName;
import adams.flow.core.AbstractModelLoader.ModelLoadingType;
import adams.flow.core.Actor;
import adams.flow.core.CNTKModelLoader;
import adams.flow.core.CallableActorReference;
import adams.flow.core.ModelLoaderSupporter;
import adams.ml.cntk.CNTKPredictionWrapper;
import adams.ml.cntk.DeviceType;
import com.microsoft.CNTK.Function;

/**
 * Ancestor for classes that apply models to the input data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <I> the input data
 * @param <O> the output data
 */
public abstract class AbstractModelApplier<I, O>
  extends AbstractOptionHandler
  implements ModelLoaderSupporter, QuickInfoSupporter {

  private static final long serialVersionUID = 7541008225536782803L;

  /** the device to use. */
  protected DeviceType m_DeviceType;

  /** the GPU device ID. */
  protected long m_GPUDeviceID;

  /** the number of classes. */
  protected int m_NumClasses;

  /** the wrapper to use. */
  protected CNTKPredictionWrapper m_Wrapper;

  /** the model loader. */
  protected CNTKModelLoader m_ModelLoader;

  /**
   * Returns information how the model is loaded in case of {@link ModelLoadingType#AUTO}.
   *
   * @return		the description
   */
  public String automaticOrderInfo() {
    return m_ModelLoader.automaticOrderInfo();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "model-loading-type", "modelLoadingType",
      ModelLoadingType.AUTO);

    m_OptionManager.add(
      "model-file", "modelFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "device-type", "deviceType",
      DeviceType.DEFAULT);

    m_OptionManager.add(
      "gpu-device-id", "GPUDeviceID",
      0L);

    m_OptionManager.add(
      "model-actor", "modelActor",
      new CallableActorReference());

    m_OptionManager.add(
      "model-storage", "modelStorage",
      new StorageName());

    m_OptionManager.add(
      "input", "inputs",
      getDefaultInputs());

    m_OptionManager.add(
      "input-name", "inputNames",
      getDefaultInputNames());

    m_OptionManager.add(
      "class-name", "className",
      getDefaultClassName());

    m_OptionManager.add(
      "output-name", "outputName",
      getDefaultOutputName());

    m_OptionManager.add(
      "num-classes", "numClasses",
      getDefaultNumClasses());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_Wrapper = new CNTKPredictionWrapper();
    m_ModelLoader = new CNTKModelLoader();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    m_Wrapper.reset();
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public synchronized void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_Wrapper.setLoggingLevel(value);
  }

  /**
   * Sets the loading type. In case of {@link ModelLoadingType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @param value	the type
   */
  public void setModelLoadingType(ModelLoadingType value) {
    m_ModelLoader.setModelLoadingType(value);
    reset();
  }

  /**
   * Returns the loading type. In case of {@link ModelLoadingType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @return		the type
   */
  public ModelLoadingType getModelLoadingType() {
    return m_ModelLoader.getModelLoadingType();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelLoadingTypeTipText() {
    return m_ModelLoader.modelLoadingTypeTipText();
  }

  /**
   * Sets the file to load the model from.
   *
   * @param value	the model file
   */
  public void setModelFile(PlaceholderFile value) {
    m_ModelLoader.setModelFile(value);
    reset();
  }

  /**
   * Returns the file to load the model from.
   *
   * @return		the model file
   */
  public PlaceholderFile getModelFile() {
    return m_ModelLoader.getModelFile();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelFileTipText() {
    return m_ModelLoader.modelFileTipText();
  }

  /**
   * Sets the device to use.
   *
   * @param value	the device
   */
  public void setDeviceType(DeviceType value) {
    m_DeviceType = value;
    reset();
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
   * Sets the GPU device ID.
   *
   * @param value	the ID
   */
  public void setGPUDeviceID(long value) {
    m_GPUDeviceID = value;
    reset();
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
   * Sets the filter source actor.
   *
   * @param value	the source
   */
  public void setModelActor(CallableActorReference value) {
    m_ModelLoader.setModelActor(value);
    reset();
  }

  /**
   * Returns the filter source actor.
   *
   * @return		the source
   */
  public CallableActorReference getModelActor() {
    return m_ModelLoader.getModelActor();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelActorTipText() {
    return m_ModelLoader.modelActorTipText();
  }

  /**
   * Sets the filter storage item.
   *
   * @param value	the storage item
   */
  public void setModelStorage(StorageName value) {
    m_ModelLoader.setModelStorage(value);
    reset();
  }

  /**
   * Returns the filter storage item.
   *
   * @return		the storage item
   */
  public StorageName getModelStorage() {
    return m_ModelLoader.getModelStorage();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelStorageTipText() {
    return m_ModelLoader.modelStorageTipText();
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
    reset();
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
    reset();
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
    reset();
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
    reset();
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
   * Returns the default number of classes.
   *
   * @return		the default
   */
  protected int getDefaultNumClasses() {
    return 1;
  }

  /**
   * Sets the number of classes (numeric class = 1, otherwise number of class labels).
   *
   * @param value	the number of classes
   */
  public void setNumClasses(int value) {
    m_NumClasses = value;
    reset();
  }

  /**
   * Returns the number of classes (numeric class = 1, otherwise number of class labels).
   *
   * @return		the number of classes
   */
  public int getNumClasses() {
    return m_NumClasses;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numClassesTipText() {
    return "The number of classes (numeric class = 1, otherwise number of class labels).";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    switch (getModelLoadingType()) {
      case AUTO:
        result = "automatic";
        break;
      case FILE:
	result = QuickInfoHelper.toString(this, "modelFile", getModelFile(), "file: ");
	break;
      case SOURCE_ACTOR:
	result = QuickInfoHelper.toString(this, "modelSource", getModelActor(), "source: ");
	break;
      case STORAGE:
	result = QuickInfoHelper.toString(this, "modelStorage", getModelStorage(), "storage: ");
	break;
      default:
	throw new IllegalStateException("Unhandled location type: " + getModelLoadingType());
    }

    return result;
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_ModelLoader.setFlowContext(value);
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_ModelLoader.getFlowContext();
  }

  /**
   * Returns the class that the applier accepts.
   *
   * @return		the class
   */
  public abstract Class accepts();

  /**
   * Returns the class that the applier generates.
   *
   * @return		the class
   */
  public abstract Class generates();

  /**
   * Initializes the model.
   */
  protected String initModel() {
    MessageCollection	errors;
    Function		model;

    m_Wrapper.initDevice(m_DeviceType, m_GPUDeviceID);

    errors = new MessageCollection();
    m_ModelLoader.setDevice(m_Wrapper.getDevice());
    model = m_ModelLoader.getModel(errors);
    if (model == null)
      return errors.toString();
    m_Wrapper.setModel(model);
    try {
      m_Wrapper.initModel(getNumClasses());
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to initialize model!", e);
    }

    return null;
  }

  /**
   * Performs checks.
   * <br>
   * The default implementation ensures that the flow context is set and the
   * model exists.
   *
   * @param input	the input data
   * @return		null if successful, otherwise error message
   */
  protected String check(I input) {
    String	result;

    if (getFlowContext() == null)
      return "No flow context set!";

    if (!m_Wrapper.isInitialized()) {
      result = initModel();
      if (result != null)
	return result;
    }

    return null;
  }

  /**
   * Performs the actual application of the model.
   *
   * @param input	the input
   * @return		the score
   */
  protected abstract O doApplyModel(I input);

  /**
   * Applies the model to the input data.
   *
   * @param input	the input
   * @return		the score
   */
  public O applyModel(I input) {
    String	msg;

    msg = check(input);
    if (msg != null)
      throw new IllegalStateException("Failed check: " + msg);
    return doApplyModel(input);
  }
}
