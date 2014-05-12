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
 * TextWriter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats;

import adams.core.QuickInfoHelper;
import adams.data.io.output.AbstractTextWriter;
import adams.data.io.output.NullWriter;
import adams.data.text.TextContainer;

/**
 <!-- globalinfo-start -->
 * Writes incoming textual data to a text file.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-content-name &lt;java.lang.String&gt; (property: contentName)
 * &nbsp;&nbsp;&nbsp;The name of the content, might be used in the filename of the output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-writer &lt;adams.data.io.output.AbstractTextWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for ouputting the textual data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.NullWriter
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextWriter
  extends AbstractRatOutput {

  /** for serialization. */
  private static final long serialVersionUID = 5871927859523743161L;

  /** the name of the content (e.g., can be used in the filename). */
  protected String m_ContentName;

  /** the writer to use. */
  protected AbstractTextWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes incoming textual data to a text file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "content-name", "contentName",
	    "");

    m_OptionManager.add(
	    "writer", "writer",
	    new NullWriter());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "writer", m_Writer);
  }

  /**
   * Sets name of the content.
   *
   * @param value 	the content name
   */
  public void setContentName(String value) {
    m_ContentName = value;
    reset();
  }

  /**
   * Returns the name of the content.
   *
   * @return 		the content name
   */
  public String getContentName() {
    return m_ContentName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String contentNameTipText() {
    return "The name of the content, might be used in the filename of the output.";
  }

  /**
   * Sets whether to append to the file or not.
   *
   * @param value 	true if appending to file instead of rewriting it
   */
  public void setWriter(AbstractTextWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns whether files gets only appended or not.
   *
   * @return 		true if appending is turned on
   */
  public AbstractTextWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for ouputting the textual data.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start --><!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, TextContainer.class};
  }

  /**
   * Performs the actual transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String		result;

    try {
      result = null;
      if (m_Input instanceof TextContainer) {
	if (m_Writer.write((TextContainer) m_Input, m_ContentName) == null)
	  result = "Error writing data: " + m_Writer.toCommandLine();
      }
      else {
	if (m_Writer.write(m_Input.toString(), m_ContentName) == null)
	  result = "Error writing data: " + m_Writer.toCommandLine();
      }
    }
    catch (Exception e) {
      result = handleException("Failed to write data:", e);
    }

    return result;
  }
}
