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
 * CNTKModelWrapper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 * Copyright (C) Microsoft
 */

package adams.ml.cntk;

import adams.core.CleanUpHandler;
import adams.core.License;
import adams.core.Range;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.core.base.BaseString;
import adams.core.option.AbstractOptionHandler;
import com.microsoft.CNTK.DeviceDescriptor;
import com.microsoft.CNTK.Function;
import com.microsoft.CNTK.NDShape;
import com.microsoft.CNTK.Variable;
import gnu.trove.set.hash.TIntHashSet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates a CNTK model for making predictions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@MixedCopyright(
  author = "CNTK",
  copyright = "Microsoft",
  license = License.MIT,
  url = "https://github.com/Microsoft/CNTK/blob/v2.0/Tests/EndToEndTests/EvalClientTests/JavaEvalTest/src/Main.java",
  note = "Original code based on CNTK example"
)
public abstract class AbstractCNTKModelWrapper
  extends AbstractOptionHandler
  implements CleanUpHandler {

  private static final long serialVersionUID = -1508684329565658944L;

  /** the model to use. */
  protected transient Function m_Model;

  /** the device to use. */
  protected transient DeviceDescriptor m_Device;

  /** the inputs. */
  protected Range[] m_Inputs;

  /** the names of the inputs. */
  protected BaseString[] m_InputNames;

  /** the input variables (name / var). */
  protected transient Map<String,Variable> m_InputVars;

  /** the input shapes (name / shape). */
  protected transient Map<String,NDShape> m_InputShapes;

  /** the input variable names. */
  protected transient List<String> m_Names;

  /** the ranges (input var name / indices). */
  protected transient Map<String,TIntHashSet> m_Ranges;

  /** whether the model has been successfully initialized. */
  protected transient boolean m_Initialized;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "input", "inputs",
      getDefaultInputs());

    m_OptionManager.add(
      "input-name", "inputNames",
      getDefaultInputNames());
  }

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();

    cleanUp();

    m_Initialized = false;
  }

  /**
   * Sets the model to use.
   *
   * @param value	the model
   */
  public void setModel(Function value) {
    m_Model = value;
    reset();
  }

  /**
   * Returns the currently set model.
   *
   * @return		the model
   */
  public Function getModel() {
    return m_Model;
  }

  /**
   * Sets the device to use.
   *
   * @param value	the device
   */
  public void setDevice(DeviceDescriptor value) {
    m_Device = value;
    reset();
  }

  /**
   * Returns the currently set device.
   *
   * @return		the device
   */
  public DeviceDescriptor getDevice() {
    return m_Device;
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
    reset();
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
    reset();
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
   * Returns whether the model has been initialized.
   *
   * @return		true if initialized
   * @see		#initModel()
   */
  public boolean isInitialized() {
    return m_Initialized;
  }

  /**
   * Initializes the device.
   *
   * @param device	the type of device to use
   * @param gpu		GPU ID, only used if device type is {@link DeviceType#GPU}
   */
  public void initDevice(DeviceType device, long gpu) {
    switch (device) {
      case DEFAULT:
	m_Device = DeviceDescriptor.useDefaultDevice();
	break;
      case CPU:
	m_Device = DeviceDescriptor.getCPUDevice();
	break;
      case GPU:
	m_Device = DeviceDescriptor.getGPUDevice(gpu);
	break;
      default:
	throw new IllegalStateException("Unhandled device type: " + device);
    }
  }

  /**
   * Loads the model from disk. Device must be available.
   *
   * @param model	the model to load
   * @throws Exception	if loading of model fails
   */
  public void loadModel(File model) throws Exception {
    if (!model.exists())
      throw new IllegalStateException("Model does not exist: " + model);
    if (model.isDirectory())
      throw new IllegalStateException("Model points to directory: " + model);

    m_Model = Function.load(model.getAbsolutePath(), m_Device);
    if (m_Model == null)
      throw new IllegalStateException("Failed to load model: " + model);
  }

  /**
   * Initializes the model.
   *
   * @throws Exception	if loading fails
   */
  protected void initModel() throws Exception {
    if (m_Model == null)
      throw new IllegalStateException("No model present!");
    if (m_Device == null)
      throw new IllegalStateException("No device present!");

    if (isLoggingEnabled()) {
      getLogger().info("Arguments:");
      for (int i = 0; i < m_Model.getArguments().size(); i++)
        getLogger().info("- " + m_Model.getArguments().get(i));
      getLogger().info("Outputs:");
      for (int i = 0; i < m_Model.getOutputs().size(); i++)
        getLogger().info("- " + m_Model.getOutputs().get(i));
    }

    // analyze model structure
    m_InputVars       = new HashMap<>();
    m_InputShapes     = new HashMap<>();
    m_Names           = new ArrayList<>();
    for (Variable var: m_Model.getArguments()) {
      String name = var.getName();
      String uid = var.getUid();
      for (BaseString inputName: m_InputNames) {
        if (inputName.getValue().equals(name) || inputName.getValue().equals(uid)) {
          m_Names.add(inputName.getValue());
          m_InputVars.put(inputName.getValue(), var);
          m_InputShapes.put(inputName.getValue(), var.getShape());
	  if (isLoggingEnabled())
	    getLogger().info("Input var '" + inputName.getValue() + "': " + var);
          break;
	}
      }
    }

    // reset ranges
    m_Ranges = null;

    m_Initialized = true;
  }

  /**
   * Frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_InputVars != null) {
      for (Variable in : m_InputVars.values())
	in.delete();
      m_InputVars.clear();
      m_InputVars = null;
    }
    if (m_InputShapes != null) {
      for (NDShape in: m_InputShapes.values())
        in.delete();
      m_InputShapes.clear();
      m_InputShapes = null;
    }
    if (m_Ranges != null) {
      m_Ranges.clear();
      m_Ranges = null;
    }
  }
}
