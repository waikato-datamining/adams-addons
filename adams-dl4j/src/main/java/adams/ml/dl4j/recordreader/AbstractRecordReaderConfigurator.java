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
 * AbstractRecordReaderConfigurator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.recordreader;

import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.ml.dl4j.inputsplit.FileSplitConfigurator;
import adams.ml.dl4j.inputsplit.InputSplitConfigurator;
import org.canova.api.records.reader.RecordReader;

/**
 * Ancestor for record reader configurators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractRecordReaderConfigurator
  extends AbstractOptionHandler
  implements RecordReaderConfigurator {

  private static final long serialVersionUID = -5049221729823530346L;

  /** the input split. */
  protected InputSplitConfigurator m_InputSplit;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "input-split", "inputSplit",
      new FileSplitConfigurator());
  }

  /**
   * Sets the input split configurator to use.
   *
   * @param value	the configurator
   */
  public void setInputSplit(InputSplitConfigurator value) {
    m_InputSplit = value;
    reset();
  }

  /**
   * Returns the input split configurator to use.
   *
   * @return 		the configurator
   */
  public InputSplitConfigurator getInputSplit() {
    return m_InputSplit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String inputSplitTipText() {
    return "The input split configurator to use for initializing the iterator.";
  }

  /**
   * Hook method before configuring the record reader.
   * <br>
   * Default implementation does nothing.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    return null;
  }

  /**
   * Configures the actual {@link RecordReader} and returns it.
   *
   * @return		the reader
   */
  protected abstract RecordReader doConfigureRecordReader();

  /**
   * Configures the {@link RecordReader} and returns it.
   *
   * @return		the reader
   */
  public RecordReader configureRecordReader() {
    RecordReader	result;

    check();
    result = doConfigureRecordReader();
    try {
      result.initialize(m_InputSplit.configureInputSplit());
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to initialize with input split: " + OptionUtils.getCommandLine(m_InputSplit), e);
    }

    return result;
  }
}
