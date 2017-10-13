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
 * CNTKModelReader.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import adams.ml.cntk.DeviceType;
import com.microsoft.CNTK.DeviceDescriptor;
import com.microsoft.CNTK.Function;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Reads the incoming model from disk.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;com.microsoft.CNTK.Function<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: CNTKModelReader
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-device-type &lt;DEFAULT|CPU|GPU&gt; (property: deviceType)
 * &nbsp;&nbsp;&nbsp;The device type to use.
 * &nbsp;&nbsp;&nbsp;default: DEFAULT
 * </pre>
 *
 * <pre>-gpu-device-id &lt;long&gt; (property: GPUDeviceID)
 * &nbsp;&nbsp;&nbsp;The GPU device ID.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CNTKModelReader
  extends AbstractTransformer {

  private static final long serialVersionUID = -7949607321054894441L;

  /** the device to use. */
  protected DeviceType m_DeviceType;

  /** the GPU device ID. */
  protected long m_GPUDeviceID;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads the incoming model from disk.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "device-type", "deviceType",
      DeviceType.DEFAULT);

    m_OptionManager.add(
      "gpu-device-id", "GPUDeviceID",
      0L);
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "deviceType", m_DeviceType, "device: ");
    if (m_DeviceType == DeviceType.GPU)
      result += QuickInfoHelper.toString(this, "GPUDeviceID", m_GPUDeviceID, ", ID: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{File.class, String.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Function.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    File		file;
    DeviceDescriptor 	device;
    Function		model;

    result = null;

    file = null;
    if (m_InputToken.getPayload() instanceof String)
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    else if (m_InputToken.getPayload() instanceof File)
      file = new PlaceholderFile((File) m_InputToken.getPayload());
    else
      result = "Unhandled input type: " + Utils.classToString(m_InputToken.getPayload());

    if (result == null) {
      switch (m_DeviceType) {
	case DEFAULT:
	  device = DeviceDescriptor.useDefaultDevice();
	  break;
	case CPU:
	  device = DeviceDescriptor.getCPUDevice();
	  break;
	case GPU:
	  device = DeviceDescriptor.getGPUDevice(m_GPUDeviceID);
	  break;
	default:
	  throw new IllegalStateException("Unhandled device type: " + m_DeviceType);
      }
      try {
	model         = Function.load(file.getAbsolutePath(), device);
	m_OutputToken = new Token(model);
      }
      catch (Exception e) {
        result = handleException(
          "Failed to load model " + file + " for device "
	    + m_DeviceType + (m_DeviceType == DeviceType.GPU ? "/" + m_GPUDeviceID : ""), e);
      }
    }

    return result;
  }
}
