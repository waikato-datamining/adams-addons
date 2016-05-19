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
 * FileSplitConfigurator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.inputsplit;

import adams.core.io.PlaceholderFile;
import org.canova.api.split.FileSplit;
import org.canova.api.split.InputSplit;

/**
 <!-- globalinfo-start -->
 * Configures a org.canova.api.split.FileSplit instance for a single file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-source &lt;adams.core.io.PlaceholderFile&gt; (property: source)
 * &nbsp;&nbsp;&nbsp;The file to use.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SingleFileSplitConfigurator
  extends AbstractInputSplitConfigurator {

  private static final long serialVersionUID = -384107247374795362L;

  /** the file. */
  protected PlaceholderFile m_Source;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures a " + FileSplit.class.getName() + " instance for a single file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "source", "source",
      new PlaceholderFile());
  }

  /**
   * Sets the file to use.
   *
   * @param value	the file
   */
  public void setSource(PlaceholderFile value) {
    m_Source = value;
    reset();
  }

  /**
   * Returns the file to use.
   *
   * @return 		the file
   */
  public PlaceholderFile getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The file to use.";
  }

  /**
   * Configures the actual {@link InputSplit} and returns it.
   *
   * @return		the input split
   */
  @Override
  protected InputSplit doConfigureInputSplit() {
    return new FileSplit(m_Source.getAbsoluteFile());
  }
}
