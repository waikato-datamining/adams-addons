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
 * StanfordPTBTokenizer.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.tokenizer;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses Stanford's PTBTokenizer.<br>
 * <br>
 * For more details on the options see:<br>
 * http:&#47;&#47;nlp.stanford.edu&#47;software&#47;tokenizer.shtml
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-tokenizer-options &lt;java.lang.String&gt; (property: tokenizerOptions)
 * &nbsp;&nbsp;&nbsp;The tokenizer options to use.
 * &nbsp;&nbsp;&nbsp;default: americanize=false,unicodeQuotes=true,unicodeEllipsis=true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 11956 $
 */
@MixedCopyright(
    copyright = "2013 StackExchange",
    author = "Yaniv.H",
    license = License.CC_BY_SA_25,
    url = "http://stackoverflow.com/a/19464001"
)
public class StanfordPTBTokenizer
  extends AbstractTokenizer {

  /** for serialization. */
  private static final long serialVersionUID = 4043221889853222507L;

  /** the options for the tokenizer. */
  protected String m_TokenizerOptions;

  /** the tokenizer factory to use. */
  protected transient TokenizerFactory m_TokenizerFactory;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses Stanford's PTBTokenizer.\n\n"
      + "For more details on the options see:\n"
      + "http://nlp.stanford.edu/software/tokenizer.shtml";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "tokenizer-options", "tokenizerOptions",
	    "americanize=false,unicodeQuotes=true,unicodeEllipsis=true");
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_TokenizerFactory = null;
  }

  /**
   * Sets the tokenizer options to use.
   *
   * @param value	the options
   */
  public void setTokenizerOptions(String value) {
    m_TokenizerOptions = value;
    reset();
  }

  /**
   * Returns the tokenizer options to use.
   *
   * @return		the options
   */
  public String getTokenizerOptions() {
    return m_TokenizerOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tokenizerOptionsTipText() {
    return "The tokenizer options to use.";
  }

  /**
   * Returns the tokenizer factory to use.
   * 
   * @return		the factory
   */
  protected TokenizerFactory getTokenizerFactory() {
    if (m_TokenizerFactory == null) {
      m_TokenizerFactory = PTBTokenizer.factory(
	  new CoreLabelTokenFactory(),
          m_TokenizerOptions);
    }
    return m_TokenizerFactory;
  }
  
  /**
   * Performs the actual tokenization.
   * 
   * @param str		the string to tokenize
   * @return		the list of sentence words
   */
  @Override
  protected List<String> doTokenize(String str) {
    List<String>		result;
    StringReader		reader;
    Tokenizer<CoreLabel> 	tokenizer;

    result = new ArrayList<String>();

    reader = new StringReader(str);
    tokenizer = getTokenizerFactory().getTokenizer(reader);
    while (tokenizer.hasNext())
      result.add(tokenizer.next().value());

    return result;
  }
}
