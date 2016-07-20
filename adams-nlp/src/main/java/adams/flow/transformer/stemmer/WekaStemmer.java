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
 * WekaStemmer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.stemmer;

import weka.core.stemmers.LovinsStemmer;

/**
 <!-- globalinfo-start -->
 * Uses the selected Weka stemmer.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-stemmer &lt;weka.core.stemmers.Stemmer&gt; (property: stemmer)
 * &nbsp;&nbsp;&nbsp;The Weka stemmer to use.
 * &nbsp;&nbsp;&nbsp;default: weka.core.stemmers.LovinsStemmer
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaStemmer
  extends AbstractStemmer {

  private static final long serialVersionUID = 7151008333967646560L;

  /** the actual stemmer. */
  protected weka.core.stemmers.Stemmer m_Stemmer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the selected Weka stemmer.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "stemmer", "stemmer",
      new LovinsStemmer());
  }

  /**
   * Sets the Weka stemmer to use.
   *
   * @param value	the stemmer
   */
  public void setStemmer(weka.core.stemmers.Stemmer value) {
    m_Stemmer = value;
    reset();
  }

  /**
   * Returns the Weka stemmer to use.
   *
   * @return		the stemmer
   */
  public weka.core.stemmers.Stemmer getStemmer() {
    return m_Stemmer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stemmerTipText() {
    return "The Weka stemmer to use.";
  }

  /**
   * Stems the given word and returns the stemmed version
   *
   * @param word      the unstemmed word
   * @return          the stemmed word
   */
  @Override
  public String stem(String word) {
    return m_Stemmer.stem(word);
  }
}
