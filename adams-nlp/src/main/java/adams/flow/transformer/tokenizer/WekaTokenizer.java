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
 * WekaTokenizer.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.tokenizer;

import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses the specified Weka tokenizer.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-tokenizer &lt;weka.core.tokenizers.Tokenizer&gt; (property: tokenizer)
 * &nbsp;&nbsp;&nbsp;The tokenizer to use.
 * &nbsp;&nbsp;&nbsp;default: weka.core.tokenizers.WordTokenizer -delimiters \" \\r\\n\\t.,;:\\\'\\\"()?!\"
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10826 $
 */
public class WekaTokenizer
  extends AbstractTokenizer {

  /** for serialization. */
  private static final long serialVersionUID = 4043221889853222507L;

  /** the tokenizer to use. */
  protected Tokenizer m_Tokenizer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified Weka tokenizer.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "tokenizer", "tokenizer",
	    new WordTokenizer());
  }

  /**
   * Sets the tokenizer to use.
   *
   * @param value	the tokenizer
   */
  public void setTokenizer(Tokenizer value) {
    m_Tokenizer = value;
    reset();
  }

  /**
   * Returns the tokenizer to use.
   *
   * @return		the tokenizer
   */
  public Tokenizer getTokenizer() {
    return m_Tokenizer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tokenizerTipText() {
    return "The tokenizer to use.";
  }

  /**
   * Performs the actual tokenization.
   * 
   * @param str		the string to tokenize
   * @return		the list of sentence words
   */
  @Override
  protected List<String> doTokenize(String str) {
    List<String>	result;

    result = new ArrayList<>();

    m_Tokenizer.tokenize(str);
    while (m_Tokenizer.hasMoreElements())
      result.add(m_Tokenizer.nextElement());

    return result;
  }
}
