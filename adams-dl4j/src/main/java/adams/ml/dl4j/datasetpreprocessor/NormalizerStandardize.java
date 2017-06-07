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
 * NormalizerStandardize.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.datasetpreprocessor;

import org.nd4j.linalg.dataset.api.DataSetPreProcessor;

/**
 <!-- globalinfo-start -->
 * Configures an org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NormalizerStandardize
  extends AbstractDataSetPreProcessorConfigurator {

  private static final long serialVersionUID = 6871564201222898901L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures an " + org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize.class.getName() + ".";
  }

  /**
   * Configures the actual {@link DataSetPreProcessor} and returns it.
   *
   * @return		the preprocessor
   */
  @Override
  protected DataSetPreProcessor doConfigurePreProcessor() {
    return new org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize();
  }
}
