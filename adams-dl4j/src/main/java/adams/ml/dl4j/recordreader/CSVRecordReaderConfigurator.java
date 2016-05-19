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
 * CSVRecordReaderConfigurator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.recordreader;

import org.canova.api.records.reader.RecordReader;
import org.canova.api.records.reader.impl.CSVRecordReader;

/**
 <!-- globalinfo-start -->
 * Configures a org.canova.api.records.reader.impl.CSVRecordReader instance.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-input-split &lt;adams.ml.dl4j.inputsplit.InputSplitConfigurator&gt; (property: inputSplit)
 * &nbsp;&nbsp;&nbsp;The input split configurator to use for initializing the iterator.
 * &nbsp;&nbsp;&nbsp;default: adams.ml.dl4j.inputsplit.FileSplitConfigurator
 * </pre>
 * 
 * <pre>-skip-num-lines &lt;int&gt; (property: skipNumLines)
 * &nbsp;&nbsp;&nbsp;The number of lines to skip.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-delimiter &lt;java.lang.String&gt; (property: delimiter)
 * &nbsp;&nbsp;&nbsp;The column delimiter to use.
 * &nbsp;&nbsp;&nbsp;default: ,
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CSVRecordReaderConfigurator
  extends AbstractRecordReaderConfigurator {

  private static final long serialVersionUID = 8914456080710417165L;

  /** the number of lines to skip. */
  protected int m_SkipNumLines;

  /** the column delimiter. */
  protected String m_Delimiter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures a " + CSVRecordReader.class.getName() + " instance.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "skip-num-lines", "skipNumLines",
      0, 0, null);

    m_OptionManager.add(
      "delimiter", "delimiter",
      ",");
  }

  /**
   * Sets the number of lines to skip.
   *
   * @param value	the number of lines
   */
  public void setSkipNumLines(int value) {
    if (getOptionManager().isValid("skipNumLines", value)) {
      m_SkipNumLines = value;
      reset();
    }
  }

  /**
   * Returns the number of lines to skip.
   *
   * @return 		the number of lines
   */
  public int getSkipNumLines() {
    return m_SkipNumLines;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String skipNumLinesTipText() {
    return "The number of lines to skip.";
  }

  /**
   * Sets the column delimiter to use.
   *
   * @param value	the delimiter
   */
  public void setDelimiter(String value) {
    m_Delimiter = value;
    reset();
  }

  /**
   * Returns the column delimiter to use.
   *
   * @return 		the delimiter
   */
  public String getDelimiter() {
    return m_Delimiter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String delimiterTipText() {
    return "The column delimiter to use.";
  }

  /**
   * Configures the actual {@link RecordReader} and returns it.
   *
   * @return		the reader
   */
  @Override
  protected RecordReader doConfigureRecordReader() {
    return new CSVRecordReader(m_SkipNumLines, m_Delimiter);
  }
}
