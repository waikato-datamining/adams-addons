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
 * ScriptedRecordReader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.ml.dl4j.recordreader;

import adams.core.scripting.AbstractScriptingHandler;
import adams.core.scripting.Dummy;
import org.canova.api.conf.Configuration;
import org.canova.api.records.reader.RecordReader;
import org.canova.api.split.InputSplit;
import org.canova.api.writable.Writable;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * A meta record reader that uses any scripting handler for managing the record reader in the specified script file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-script &lt;adams.core.io.PlaceholderFile&gt; (property: scriptFile)
 * &nbsp;&nbsp;&nbsp;The script file to load and execute.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-options &lt;adams.core.base.BaseText&gt; (property: scriptOptions)
 * &nbsp;&nbsp;&nbsp;The options for the script; must consist of 'key=value' pairs separated 
 * &nbsp;&nbsp;&nbsp;by blanks; the value of 'key' can be accessed via the 'getAdditionalOptions
 * &nbsp;&nbsp;&nbsp;().getXYZ("key")' method in the script actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-handler &lt;adams.core.scripting.AbstractScriptingHandler&gt; (property: handler)
 * &nbsp;&nbsp;&nbsp;The handler to use for scripting.
 * &nbsp;&nbsp;&nbsp;default: adams.core.scripting.Dummy
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13195 $
 */
public class ScriptedRecordReader
  extends AbstractScriptedRecordReader {

  /** for serialization. */
  private static final long serialVersionUID = 1304903578667689350L;

  /** the loaded script object. */
  protected transient RecordReader m_RecordReaderObject;

  /** the scripting handler to use. */
  protected AbstractScriptingHandler m_Handler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "A meta record reader that uses any scripting handler for managing the "
        + "record reader in the specified script file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "handler", "handler",
      new Dummy());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String scriptOptionsTipText() {
    return
      "The options for the script; must consist of 'key=value' pairs "
        + "separated by blanks; the value of 'key' can be accessed via the "
        + "'getAdditionalOptions().getXYZ(\"key\")' method in the script actor.";
  }

  /**
   * Sets the handler to use for scripting.
   *
   * @param value 	the handler
   */
  public void setHandler(AbstractScriptingHandler value) {
    m_Handler = value;
    reset();
  }

  /**
   * Gets the handler to use for scripting.
   *
   * @return 		the handler
   */
  public AbstractScriptingHandler getHandler() {
    return m_Handler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String handlerTipText() {
    return "The handler to use for scripting.";
  }

  /**
   * Loads the scripts object and sets its options.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String loadScriptObject() {
    Object[]	result;

    result = m_Handler.loadScriptObject(
      RecordReader.class,
      m_ScriptFile,
      m_ScriptOptions,
      getOptionManager().getVariables());
    m_ScriptObject = result[1];

    return (String) result[0];
  }

  /**
   * Checks the script object.
   *
   * @return		null if OK, otherwise the error message
   */
  @Override
  protected String checkScriptObject() {
    // TODO checks?
    return null;
  }

  /**
   * Hook method for checks before the actual execution.
   *
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null)
      m_RecordReaderObject = (RecordReader) m_ScriptObject;

    return result;
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    super.destroy();

    m_RecordReaderObject = null;
  }

  /**
   * Returns the record reader. Raises an {@link IllegalStateException} if not
   * model object loaded.
   *
   * @return		the input splot
   */
  protected synchronized RecordReader getRecordReader() {
    if (m_RecordReaderObject != null)
      return m_RecordReaderObject;
    else
      throw new IllegalStateException("No record reader script loaded!");
  }

  /**
   * Closes this stream and releases any system resources associated
   * with it. If the stream is already closed then invoking this
   * method has no effect.
   *
   * <p> As noted in {@link AutoCloseable#close()}, cases where the
   * close may fail require careful attention. It is strongly advised
   * to relinquish the underlying resources and to internally
   * <em>mark</em> the {@code Closeable} as closed, prior to throwing
   * the {@code IOException}.
   *
   * @throws IOException if an I/O error occurs
   */
  @Override
  public void close() throws IOException {
    getRecordReader().close();
  }

  /**
   * Called once at initialization.
   * @param split the split that defines the range of records to read
   * @throws java.io.IOException
   * @throws InterruptedException
   */
  @Override
  public void initialize(InputSplit split) throws IOException, InterruptedException {
    getRecordReader().initialize(split);
  }

  /**
   * Called once at initialization.
   * @param conf a configuration for initialization
   * @param split the split that defines the range of records to read
   * @throws java.io.IOException
   * @throws InterruptedException
   */
  @Override
  public void initialize(Configuration conf, InputSplit split) throws IOException, InterruptedException {
    getRecordReader().initialize(conf, split);
  }

  /**
   * Get the next record
   * @return
   */
  @Override
  public Collection<Writable> next() {
    return getRecordReader().next();
  }

  @Override
  public boolean hasNext() {
    return getRecordReader().hasNext();
  }

  /**
   * Whether there are anymore records
   * @return
   */
  @Override
  public List<String> getLabels() {
    return getRecordReader().getLabels();
  }

  /**
   * Load the record from the given DataInputStream
   * Unlike {@link #next()} the internal state of the RecordReader is not modified
   * Implementations of this method should not close the DataInputStream
   *
   * @throws IOException if error occurs during reading from the input stream
   */
  @Override
  public Collection<Writable> record(URI uri, DataInputStream dataInputStream) throws IOException {
    return getRecordReader().record(uri, dataInputStream);
  }

  /** Set the configuration to be used by this object. */
  @Override
  public void setConf(Configuration conf) {
    getRecordReader().setConf(conf);
  }

  /** Return the configuration used by this object. */
  @Override
  public Configuration getConf() {
    return getRecordReader().getConf();
  }
}
