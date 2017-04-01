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
 * XMLLoggingGenerator.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.webservice.interceptor.incoming;

import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.core.io.PrettyPrintingSupporter;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingLevel;

/**
 * Generator for {@link XMLLoggingInInterceptor}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XMLLoggingGenerator
  extends AbstractInInterceptorGenerator<XMLLoggingInInterceptor>
  implements PrettyPrintingSupporter, FileWriter {

  /** for serialization. */
  private static final long serialVersionUID = -8109018608359183466L;

  /** whether to use pretty-printing. */
  protected boolean m_PrettyPrinting;

  /** the optional output file to write the XML messages to. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a " + XMLLoggingInInterceptor.class.getName() + " instance.\n"
	+ "Logs the messages using its logger instance in XML.\n";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "pretty-printing", "prettyPrinting",
      false);

    m_OptionManager.add(
      "output-file", "outputFile",
      new PlaceholderFile());
  }

  /**
   * Returns the default logging level to use.
   *
   * @return		the logging level
   */
  @Override
  protected LoggingLevel getDefaultLoggingLevel() {
    return LoggingLevel.INFO;
  }

  /**
   * Sets whether to use pretty-printing or not.
   *
   * @param value	true if to use pretty-printing
   */
  public void setPrettyPrinting(boolean value) {
    m_PrettyPrinting = value;
    reset();
  }

  /**
   * Returns whether pretty-printing is used or not.
   *
   * @return		true if to use pretty-printing
   */
  public boolean getPrettyPrinting() {
    return m_PrettyPrinting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prettyPrintingTipText() {
    return "If enabled, XML is output is 'pretty printed' format, i.e., nicely nested.";
  }

  /**
   * Sets the output file in use. Ignored if pointing to a directory.
   *
   * @param value	the output file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file in use. Ignored if pointing to a directory.
   *
   * @return		the output file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "Optional file to store the XML messages in; ignored if pointing to a directory.";
  }

  /**
   * Generates the actual interceptor for incoming messages.
   * 
   * @return		the interceptor
   */
  @Override
  protected XMLLoggingInInterceptor doGenerate() {
    Logger logger;

    logger = LoggingHelper.getLogger(XMLLoggingInInterceptor.class);
    logger.setLevel(getLoggingLevel().getLevel());

    return new XMLLoggingInInterceptor(logger, m_PrettyPrinting, m_OutputFile.isDirectory() ? null : m_OutputFile);
  }
}
