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
 * AbstractFileBaseRecorder.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.video;

import adams.core.io.PlaceholderFile;

/**
 * Ancestor for file-based recorders.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFileBaseRecorder
  extends AbstractRecorder {

  private static final long serialVersionUID = 5200164488272190578L;

  /** the output file. */
  protected PlaceholderFile m_Output;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output", "output",
      getDefaultOutput());
  }

  /**
   * Returns the default output file to use.
   *
   * @return		the default
   */
  protected abstract PlaceholderFile getDefaultOutput();

  /**
   * Sets the output file to use.
   *
   * @param value	the output file
   */
  public void setOutput(PlaceholderFile value) {
    m_Output = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return		the output file
   */
  public PlaceholderFile getOutput() {
    return m_Output;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTipText() {
    return "The file to store the recorded output in.";
  }
}
