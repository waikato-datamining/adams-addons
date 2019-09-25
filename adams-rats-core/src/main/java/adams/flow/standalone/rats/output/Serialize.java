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
 * Serialize.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.core.SerializationHelper;
import adams.core.io.AbstractFilenameGenerator;
import adams.core.io.SimpleFilenameGenerator;

/**
 <!-- globalinfo-start -->
 * Saves the objects that it receives using serialization under the filename created by the filename generator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-filename-generator &lt;adams.core.io.AbstractFilenameGenerator&gt; (property: filenameGenerator)
 * &nbsp;&nbsp;&nbsp;The filename generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.SimpleFilenameGenerator
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Serialize
  extends AbstractRatOutput {

  /** for serialization. */
  private static final long serialVersionUID = 5871927859523743161L;

  /** the filename generator to use. */
  protected AbstractFilenameGenerator m_FilenameGenerator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Saves the objects that it receives using serialization under the "
	+ "filename created by the filename generator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filename-generator", "filenameGenerator",
	    new SimpleFilenameGenerator());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_FilenameGenerator);
  }

  /**
   * Sets the filename generator.
   *
   * @param value	the generator
   */
  public void setFilenameGenerator(AbstractFilenameGenerator value) {
    m_FilenameGenerator = value;
    reset();
  }

  /**
   * Returns the filename generator.
   *
   * @return		the generator
   */
  public AbstractFilenameGenerator getFilenameGenerator() {
    return m_FilenameGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filenameGeneratorTipText() {
    return "The filename generator to use.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object.class};
  }

  /**
   * Performs the actual transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String	result;
    String	filename;

    result   = null;
    filename = m_FilenameGenerator.generate("");
    try {
      SerializationHelper.write(filename, m_Input);
    }
    catch (Exception e) {
      result = handleException("Failed to write object to " + filename, e);
    }

    return result;
  }
}
