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
 * ScoreIterationListenerConfigurator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.ml.dl4j.iterationlistener;

import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;

import java.util.Arrays;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Configures a simple score iteration listener
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-frequency &lt;int&gt; (property: frequency)
 * &nbsp;&nbsp;&nbsp;The update frequency.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScoreIterationListenerConfigurator
  extends AbstractIteratorListenerConfigurator {

  private static final long serialVersionUID = -3325744412079265328L;

  /** the frequency. */
  protected int m_Frequency;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Configures a simple score iteration listener";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "frequency", "frequency",
      10, 1, null);
  }

  /**
   * Sets the update frequency.
   *
   * @param value	the frequency
   */
  public void setFrequency(int value) {
    if (getOptionManager().isValid("frequency", value)) {
      m_Frequency = value;
      reset();
    }
  }

  /**
   * Returns the update frequency.
   *
   * @return 		the frequency
   */
  public int getFrequency() {
    return m_Frequency;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String frequencyTipText() {
    return "The update frequency.";
  }

  /**
   * Configures the actual {@link IterationListener} and returns it.
   *
   * @return		the listeners
   */
  @Override
  protected List<IterationListener> doConfigureIterationListeners() {
    return Arrays.asList(new ScoreIterationListener(m_Frequency));
  }
}
