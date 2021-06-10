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
 * RenjinFileWriter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import org.renjin.primitives.io.serialization.HeadlessWriteContext;
import org.renjin.primitives.io.serialization.RDataWriter;
import org.renjin.primitives.io.serialization.Serialization.SerializationType;
import org.renjin.sexp.SEXP;

import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 <!-- globalinfo-start -->
 * Writes R data to disk.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
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
 * &nbsp;&nbsp;&nbsp;default: RenjinFileWriter
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
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file to write the R data to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-serialization-type &lt;ASCII|XDR|BINARY&gt; (property: serializationType)
 * &nbsp;&nbsp;&nbsp;The format of the output file.
 * &nbsp;&nbsp;&nbsp;default: BINARY
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RenjinFileWriter
  extends AbstractFileWriter {

  private static final long serialVersionUID = -8506897965137239463L;

  /** the output format. */
  protected SerializationType m_SerializationType;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes R data to disk.";
  }

  /**
   * Adds to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"serialization-type", "serializationType",
	SerializationType.BINARY);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The file to write the R data to.";
  }

  /**
   * Sets the serialization type, ie output format.
   *
   * @param value	the type
   */
  public void setSerializationType(SerializationType value) {
    m_SerializationType = value;
    reset();
  }

  /**
   * Returns the serialization type, ie output format.
   *
   * @return 		the type
   */
  public SerializationType getSerializationType() {
    return m_SerializationType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String serializationTypeTipText() {
    return "The format of the output file.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "serializationType", m_SerializationType, ", type: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
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
    SEXP		input;
    RDataWriter		writer;
    FileOutputStream	fos;
    GZIPOutputStream	gos;

    result = null;
    input  = null;
    if (m_InputToken.hasPayload(SEXP.class))
      input = m_InputToken.getPayload(SEXP.class);
    else
      result = m_InputToken.unhandledData();

    if (result == null) {
      fos = null;
      gos = null;
      try {
	fos = new FileOutputStream(m_OutputFile.getAbsolutePath());
	gos = new GZIPOutputStream(fos);
	writer = new RDataWriter(HeadlessWriteContext.INSTANCE, gos);
	writer.save(input);
	writer.close();
      }
      catch (Exception e) {
	result = handleException("Failed to write data to: " + m_OutputFile, e);
      }
      finally {
	FileUtils.closeQuietly(gos);
	FileUtils.closeQuietly(fos);
      }
    }

    return result;
  }
}
