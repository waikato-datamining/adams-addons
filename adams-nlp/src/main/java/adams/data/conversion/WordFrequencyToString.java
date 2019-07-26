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
 * WordFrequencyToString.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import com.kennycason.kumo.WordFrequency;

/**
 <!-- globalinfo-start -->
 * Converts word frequencies into strings, using the following format: word&lt;DELIMITER&gt;frequency
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-delimiter &lt;java.lang.String&gt; (property: delimiter)
 * &nbsp;&nbsp;&nbsp;The delimiter to use in the output.
 * &nbsp;&nbsp;&nbsp;default: ,
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WordFrequencyToString
  extends AbstractConversionToString {

  private static final long serialVersionUID = 4439083872054918544L;

  /** the delimiter to use. */
  protected String m_Delimiter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts word frequencies into strings, using the following format: word<DELIMITER>frequency";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "delimiter", "delimiter",
      ",");
  }

  /**
   * Sets the delimiter to use.
   *
   * @param value	the delimiter
   */
  public void setDelimiter(String value) {
    m_Delimiter = value;
    reset();
  }

  /**
   * Returns the delimiter to use.
   *
   * @return		the delimiter
   */
  public String getDelimiter() {
    return m_Delimiter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String delimiterTipText() {
    return "The delimiter to use in the output.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return WordFrequency.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    WordFrequency	freq;

    freq = (WordFrequency) m_Input;
    return freq.getWord() + m_Delimiter + freq.getFrequency();
  }
}
