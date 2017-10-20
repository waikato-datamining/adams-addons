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

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.Range;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.ModelFileHandler;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.core.option.AbstractOptionHandler;
import adams.data.report.Report;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Compatibility;
import adams.flow.core.FlowContextHandler;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
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
  implements FlowContextHandler, ModelFileHandler, StorageUser,
  QuickInfoSupporter {

  private static final long serialVersionUID = 7541008225536782803L;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the model location. */
  protected ModelLocation m_Location;

  /** file containing the model. */
  protected PlaceholderFile m_ModelFile;

  /** the device to use. */
  protected DeviceType m_DeviceType;

  /** the GPU device ID. */
  protected long m_GPUDeviceID;

  /** the source actor. */
  protected CallableActorReference m_Source;

  /** the storage item. */
  protected StorageName m_Storage;

  /** the number of classes. */
  protected int m_NumClasses;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the wrapper to use. */
  protected CNTKPredictionWrapper m_Wrapper;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "location", "location",
      ModelLocation.FILE);

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
      "source", "source",
      new CallableActorReference());

    m_OptionManager.add(
      "storage", "storage",
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
   * Sets the where to get the model from.
   *
   * @param value	the location
   */
  public void setLocation(ModelLocation value) {
    m_Location = value;
    reset();
  }

  /**
   * Returns where to get the model from.
   *
   * @return  		the location
   */
  public ModelLocation getLocation() {
    return m_Location;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String locationTipText() {
    return "Determines where to get the model from.";
  }

  /**
   * Sets the model file to use.
   *
   * @param value	the model file
   */
  public void setModelFile(PlaceholderFile value) {
    m_ModelFile = value;
    reset();
  }

  /**
   * Returns the model file to use.
   *
   * @return  		the model file
   */
  public PlaceholderFile getModelFile() {
    return m_ModelFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelFileTipText() {
    return "The file containing the model.";
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
   * Sets the model source actor.
   *
   * @param value	the source
   */
  public void setSource(CallableActorReference value) {
    m_Source = value;
    reset();
  }

  /**
   * Returns the model source actor.
   *
   * @return		the source
   */
  public CallableActorReference getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The source actor to obtain the model from.";
  }

  /**
   * Sets the model storage item.
   *
   * @param value	the storage item
   */
  public void setStorage(StorageName value) {
    m_Storage = value;
    reset();
  }

  /**
   * Returns the model storage item.
   *
   * @return		the storage item
   */
  public StorageName getStorage() {
    return m_Storage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageTipText() {
    return "The storage item to obtain the model from.";
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

    switch (m_Location) {
      case FILE:
	result = QuickInfoHelper.toString(this, "modelFile", m_ModelFile, "file: ");
	break;
      case SOURCE:
	result = QuickInfoHelper.toString(this, "source", m_Source, "source: ");
	break;
      case STORAGE:
	result = QuickInfoHelper.toString(this, "storage", m_Storage, "storage: ");
	break;
      default:
	throw new IllegalStateException("Unhandled location type: " + m_Location);
    }

    return result;
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
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
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected Actor findCallableActor() {
    return m_Helper.findCallableActorRecursive(getFlowContext(), getSource());
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
    return (m_Location == ModelLocation.STORAGE);
  }

  /**
   * Initializes the model.
   */
  protected String initModel() {
    String		result;
    Actor 		source;
    Token 		token;
    Compatibility 	comp;

    result = null;

    m_Wrapper.initDevice(m_DeviceType, m_GPUDeviceID);

    try {
      switch (m_Location) {
	case FILE:
	  m_Wrapper.loadModel(m_ModelFile);
	  m_Wrapper.initModel(getNumClasses());
	  break;

	case SOURCE:
	  source = findCallableActor();
	  if (source != null) {
	    if (source instanceof OutputProducer) {
	      comp = new Compatibility();
	      if (!comp.isCompatible(new Class[]{Report.class}, ((OutputProducer) source).generates()))
		result = "Callable actor '" + m_Source + "' does not produce output that is compatible with '" + Report.class.getName() + "'!";
	    }
	    else {
	      result = "Callable actor '" + m_Source + "' does not produce any output!";
	    }
	    token = null;
	    if (result == null) {
	      result = source.execute();
	      if (result != null) {
		result = "Callable actor '" + m_Source + "' execution failed:\n" + result;
	      }
	      else {
		if (((OutputProducer) source).hasPendingOutput())
		  token = ((OutputProducer) source).output();
		else
		  result = "Callable actor '" + m_Source + "' did not generate any output!";
	      }
	    }
	    if (result != null)
	      return result;
	    if (token != null) {
	      m_Wrapper.setModel((Function) token.getPayload());
	      m_Wrapper.initModel(getNumClasses());
	      if (isLoggingEnabled())
		getLogger().info("Using model from source: " + m_Source);
	      return null;
	    }
	  }
	  break;

	case STORAGE:
	  if (getFlowContext().getStorageHandler().getStorage().has(m_Storage)) {
	    m_Wrapper.setModel((Function) getFlowContext().getStorageHandler().getStorage().get(m_Storage));
	    m_Wrapper.initModel(getNumClasses());
	    if (isLoggingEnabled())
	      getLogger().info("Using model from storage: " + m_Storage);
	    return null;
	  }
	  break;

	default:
	  return "Unhandled location type: " + m_Location;
      }
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

    if (m_FlowContext == null)
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
