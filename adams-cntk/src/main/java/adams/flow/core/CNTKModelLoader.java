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
 * CNTKModelLoader.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.MessageCollection;
import adams.flow.container.AbstractContainer;
import com.microsoft.CNTK.DeviceDescriptor;
import com.microsoft.CNTK.Function;

/**
 * Manages CNTK models.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CNTKModelLoader
  extends AbstractModelLoader<Function> {

  private static final long serialVersionUID = 4060060263449859577L;

  /** the device to use. */
  protected transient DeviceDescriptor m_Device;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages CNTK models.";
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
   * Deserializes the model file.
   *
   * @param errors	for collecting errors
   * @return		the object read from the file, null if failed
   */
  @Override
  protected Object deserializeFile(MessageCollection errors) {
    if (m_Device == null) {
      errors.add("No device set!");
      return null;
    }
    return Function.load(m_ModelFile.getAbsolutePath(), m_Device);
  }

  /**
   * Retrieves the model from the container.
   *
   * @param cont	the container to get the model from
   * @param errors	for collecting errors
   * @return		the model, null if not in container
   */
  @Override
  protected Function getModelFromContainer(AbstractContainer cont, MessageCollection errors) {
    unhandledContainer(cont, errors);
    return null;
  }
}
