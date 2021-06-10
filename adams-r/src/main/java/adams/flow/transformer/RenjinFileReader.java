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
 * RenjinFileReader.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.io.FileUtils;
import adams.core.io.GzipUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import org.renjin.primitives.io.serialization.RDataReader;
import org.renjin.sexp.SEXP;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;

/**
 <!-- globalinfo-start -->
 * Reads .rdata files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;org.renjin.sexp.SEXP<br>
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
 * &nbsp;&nbsp;&nbsp;default: RDataFileReader
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RenjinFileReader
  extends AbstractTransformer {

  private static final long serialVersionUID = 6309034892839103164L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads .rdata files.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{SEXP.class};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile	input;
    FileInputStream 	fis;
    GZIPInputStream 	gis;
    RDataReader 	reader;
    SEXP		data;

    result = null;
    input  = null;
    if (m_InputToken.hasPayload(String.class))
      input = new PlaceholderFile(m_InputToken.getPayload(String.class));
    else if (m_InputToken.hasPayload(File.class))
      input = new PlaceholderFile(m_InputToken.getPayload(File.class));
    else
      result = m_InputToken.unhandledData();

    if (result == null) {
      fis = null;
      gis = null;
      try {
        if (GzipUtils.isGzipCompressed(input)) {
          fis = new FileInputStream(input.getAbsolutePath());
          gis = new GZIPInputStream(fis);
          reader = new RDataReader(gis);
        }
        else {
          fis = new FileInputStream(input.getAbsolutePath());
          reader = new RDataReader(fis);
        }
	data   = reader.readFile();
	m_OutputToken = new Token(data);
	reader.close();
      }
      catch (Exception e) {
	result = handleException("Failed to read: " + input, e);
      }
      finally {
	FileUtils.closeQuietly(gis);
	FileUtils.closeQuietly(fis);
      }
    }

    return result;
  }
}
