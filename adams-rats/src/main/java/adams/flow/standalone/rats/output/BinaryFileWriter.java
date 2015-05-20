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
 * BinaryFileWriter.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone.rats.output;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.data.blob.BlobContainer;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

/**
 <!-- globalinfo-start -->
 * Writes incoming binary data (byte array or adams.data.blob.BlobContainer) to a binary file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The name of the output file.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BinaryFileWriter
  extends AbstractRatOutput
  implements FileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 5871927859523743161L;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Writes incoming binary data (byte array or " 
	+ BlobContainer.class.getName() + ") to a binary file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output", "outputFile",
	    getDefaultOutputFile());
  }

  /**
   * Returns the default output file.
   *
   * @return		the file
   */
  protected PlaceholderFile getDefaultOutputFile() {
    return new PlaceholderFile(".");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "outputFile", m_OutputFile);
  }

  /**
   * Set output file.
   *
   * @param value	file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Get output file.
   *
   * @return	file
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
    return "The name of the output file.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start --><!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{byte[].class, BlobContainer.class};
  }

  /**
   * Performs the actual transmission.
   * 
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransmit() {
    String			result;
    byte[]			content;
    BufferedOutputStream	out;
    FileOutputStream            fos;

    out = null;
    fos = null;
    try {
      result = null;
      if (m_Input instanceof BlobContainer)
	content = ((BlobContainer) m_Input).getContent();
      else
	content = (byte[]) m_Input;
      fos = new FileOutputStream(m_OutputFile.getAbsolutePath());
      out = new BufferedOutputStream(fos);
      out.write(content);
      out.flush();
    }
    catch (Exception e) {
      result = handleException("Failed to write byte array to " + m_OutputFile, e);
    }
    finally {
      FileUtils.closeQuietly(out);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }
}
