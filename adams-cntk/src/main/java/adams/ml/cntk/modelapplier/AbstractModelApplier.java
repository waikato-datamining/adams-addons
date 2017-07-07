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
 * AbstractModelApplier.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.cntk.modelapplier;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.io.ModelFileHandler;
import adams.core.io.PlaceholderFile;
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
import com.microsoft.CNTK.DeviceDescriptor;
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

  /** the source actor. */
  protected CallableActorReference m_Source;

  /** the storage item. */
  protected StorageName m_Storage;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the model to use. */
  protected transient Function m_Model;

  /** the device to use. */
  protected DeviceDescriptor m_Device;

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
      "source", "source",
      new CallableActorReference());

    m_OptionManager.add(
      "storage", "storage",
      new StorageName());
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    resetModel();
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
   * Resets the model.
   */
  public void resetModel() {
    m_Model = null;
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

    switch (m_Location) {
      case FILE:
	if (m_ModelFile.exists() && !m_ModelFile.isDirectory()) {
	  if (isLoggingEnabled())
	    getLogger().info("Loading serialized filter from: " + m_ModelFile);
	  m_Device = DeviceDescriptor.useDefaultDevice(); // TODO option?
	  m_Model = Function.load(m_ModelFile.getAbsolutePath(), m_Device);
	  return null;
	}
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
	    m_Model = (Function) token.getPayload();
	    if (isLoggingEnabled())
	      getLogger().info("Using model from source: " + m_Source);
	    return null;
	  }
	}
	break;

      case STORAGE:
	if (getFlowContext().getStorageHandler().getStorage().has(m_Storage)) {
	  m_Model = (Function) getFlowContext().getStorageHandler().getStorage().get(m_Storage);
	  if (isLoggingEnabled())
	    getLogger().info("Using model from storage: " + m_Storage);
	  return null;
	}
	break;

      default:
	return "Unhandled location type: " + m_Location;
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

    if (m_Model == null) {
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
