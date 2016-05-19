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
 * SVMLightRecordReaderConfigurator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.recordreader;

import org.canova.api.records.reader.RecordReader;
import org.canova.api.records.reader.impl.SVMLightRecordReader;

/**
 <!-- globalinfo-start -->
 * Configures a org.canova.api.records.reader.impl.SVMLightRecordReader instance.
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SVMLightRecordReaderConfigurator
  extends AbstractRecordReaderConfigurator {

  private static final long serialVersionUID = 8914456080710417165L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures a " + SVMLightRecordReader.class.getName() + " instance.";
  }

  /**
   * Configures the actual {@link RecordReader} and returns it.
   *
   * @return		the reader
   */
  @Override
  protected RecordReader doConfigureRecordReader() {
    return new SVMLightRecordReader();
  }
}
